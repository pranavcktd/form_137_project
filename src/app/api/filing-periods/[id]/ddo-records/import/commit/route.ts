import { NextResponse } from "next/server";
import { z } from "zod";
import { prisma } from "@/lib/prisma";
import { requireFilingPeriod } from "@/lib/authz";
import { ddoRecordSchema } from "@/lib/validation/ddoRecord";

// Imported rows only carry TAN/name (+ filed amounts) from the spreadsheet —
// there's no "select a DDO" step like the manual entry form, so ddoMasterId
// isn't known yet. It's resolved (or the DDO Master is created on the fly)
// per row below instead of being required up front.
const importRowSchema = ddoRecordSchema.extend({ ddoMasterId: z.string().optional() });
const commitSchema = z.object({
  rows: z.array(importRowSchema),
  // "merge": sum amounts for rows sharing a TAN + Form Type (per the FVU
  // spec's "Sum ... per DDO per month"). "skip": drop that DDO/Form Type
  // combination entirely so the sheet can be fixed and re-imported.
  duplicateStrategy: z.enum(["merge", "skip"]).default("merge"),
  // Only meaningful when the filing period already has saved DDO records.
  existingDataStrategy: z.enum(["replace", "upsert", "skip_existing"]).default("upsert"),
});

type ImportRow = z.infer<typeof importRowSchema>;

export async function POST(
  request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const body = await request.json();
  const parsed = commitSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const { rows, duplicateStrategy, existingDataStrategy } = parsed.data;
  const clientId = filingPeriod.clientId;

  // A DDO files at most one TD record per Form Type per period. Group rows
  // that collide on TAN + Form Type and either sum them or drop them,
  // depending on what the user chose in the import preview.
  const groupedByKey = new Map<string, ImportRow[]>();
  for (const row of rows) {
    const key = `${row.tan}::${row.formType ?? ""}`;
    const group = groupedByKey.get(key);
    if (group) group.push(row);
    else groupedByKey.set(key, [row]);
  }

  const mergedRows: ImportRow[] = [];
  let mergedCount = 0;
  let ignoredCount = 0;
  for (const group of groupedByKey.values()) {
    if (group.length === 1) {
      mergedRows.push(group[0]);
      continue;
    }
    if (duplicateStrategy === "skip") {
      ignoredCount += group.length;
      continue;
    }
    mergedCount += group.length - 1;
    mergedRows.push({
      ...group[0],
      taxDeducted: group.reduce((sum, r) => sum + r.taxDeducted, 0),
      totalRemitted: group.reduce((sum, r) => sum + r.totalRemitted, 0),
    });
  }

  const maxSerial = await prisma.ddoRecord.aggregate({
    where: { filingPeriodId: id },
    _max: { serialNo: true },
  });
  let nextSerial = (maxSerial._max.serialNo ?? 0) + 1;

  let createdCount = 0;
  let updatedCount = 0;
  let skippedExistingCount = 0;

  try {
    await prisma.$transaction(async (tx) => {
      if (existingDataStrategy === "replace") {
        await tx.ddoRecord.deleteMany({ where: { filingPeriodId: id } });
        nextSerial = 1;
      }

      for (const row of mergedRows) {
        // The DDO Master (matched by TAN) is the single source of truth for
        // identity/contact fields — whatever the spreadsheet's Name/address
        // columns say is only used to seed a brand-new master, never to
        // override an existing one.
        const existingMaster = await tx.ddoMaster.findUnique({
          where: { clientId_tan: { clientId, tan: row.tan } },
        });
        const master =
          existingMaster ??
          (await tx.ddoMaster.create({
            data: {
              clientId,
              tan: row.tan,
              name: row.name,
              address1: row.address1 || undefined,
              address2: row.address2 || undefined,
              address3: row.address3 || undefined,
              address4: row.address4 || undefined,
              city: row.city || undefined,
              state: row.state || undefined,
              pin: row.pin || undefined,
              ddoRegNo: row.ddoRegNo || undefined,
              ddoCode: row.ddoCode || undefined,
              email: row.email || undefined,
            },
          }));

        // Prisma's compound-unique lookup requires a definite string here
        // (nullable columns can't be matched via `null` in a whereUnique),
        // so empty is normalized to "" consistently on both read and write.
        const formType = row.formType ?? "";

        let existingRecord = null;
        if (existingDataStrategy !== "replace") {
          existingRecord = await tx.ddoRecord.findUnique({
            where: {
              filingPeriodId_ddoMasterId_formType: {
                filingPeriodId: id,
                ddoMasterId: master.id,
                formType,
              },
            },
          });
        }

        if (existingRecord) {
          if (existingDataStrategy === "skip_existing") {
            skippedExistingCount++;
            continue;
          }
          await tx.ddoRecord.update({
            where: { id: existingRecord.id },
            data: {
              tan: master.tan,
              name: master.name,
              address1: master.address1,
              address2: master.address2,
              address3: master.address3,
              address4: master.address4,
              city: master.city,
              state: master.state,
              pin: master.pin,
              ddoRegNo: master.ddoRegNo,
              ddoCode: master.ddoCode,
              email: master.email,
              taxDeducted: row.taxDeducted,
              formType,
              totalRemitted: row.totalRemitted,
              natureOfDeduction: row.natureOfDeduction || undefined,
              mode: row.mode,
            },
          });
          updatedCount++;
        } else {
          await tx.ddoRecord.create({
            data: {
              tan: master.tan,
              name: master.name,
              address1: master.address1,
              address2: master.address2,
              address3: master.address3,
              address4: master.address4,
              city: master.city,
              state: master.state,
              pin: master.pin,
              ddoRegNo: master.ddoRegNo,
              ddoCode: master.ddoCode,
              email: master.email,
              taxDeducted: row.taxDeducted,
              formType,
              totalRemitted: row.totalRemitted,
              natureOfDeduction: row.natureOfDeduction || undefined,
              mode: row.mode,
              ddoMasterId: master.id,
              filingPeriodId: id,
              serialNo: nextSerial++,
            },
          });
          createdCount++;
        }
      }
    });
  } catch (err) {
    console.error("DDO import commit failed:", err);
    const message = err instanceof Error ? err.message : "Could not save these rows.";
    return NextResponse.json({ error: message }, { status: 500 });
  }

  return NextResponse.json(
    {
      created: createdCount,
      updated: updatedCount,
      skipped: skippedExistingCount,
      merged: mergedCount,
      ignored: ignoredCount,
    },
    { status: 201 },
  );
}
