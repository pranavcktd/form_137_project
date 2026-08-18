import { NextResponse } from "next/server";
import { z } from "zod";
import { prisma } from "@/lib/prisma";
import { requireClient } from "@/lib/authz";
import { ddoMasterSchema } from "@/lib/validation/ddoMaster";

const commitSchema = z.object({
  rows: z.array(ddoMasterSchema),
  // "keep_last": last occurrence of a repeated TAN in the file wins.
  // "skip": drop every row for a TAN that repeats, so the sheet can be
  // fixed and re-imported instead of guessing which row is correct.
  duplicateStrategy: z.enum(["keep_last", "skip"]).default("keep_last"),
  // Only meaningful for TANs that already have a DDO Master saved.
  existingDataStrategy: z.enum(["upsert", "skip_existing"]).default("skip_existing"),
});

export async function POST(
  request: Request,
  { params }: { params: Promise<{ clientId: string }> },
) {
  const { clientId } = await params;
  const { session, client } = await requireClient(clientId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!client) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const body = await request.json();
  const parsed = commitSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const { rows, duplicateStrategy, existingDataStrategy } = parsed.data;

  const groupedByTan = new Map<string, typeof rows>();
  for (const row of rows) {
    const group = groupedByTan.get(row.tan);
    if (group) group.push(row);
    else groupedByTan.set(row.tan, [row]);
  }

  const resolvedRows: typeof rows = [];
  let ignoredCount = 0;
  for (const group of groupedByTan.values()) {
    if (group.length === 1) {
      resolvedRows.push(group[0]);
    } else if (duplicateStrategy === "skip") {
      ignoredCount += group.length;
    } else {
      resolvedRows.push(group[group.length - 1]);
    }
  }

  let createdCount = 0;
  let updatedCount = 0;
  let skippedExistingCount = 0;

  try {
    await prisma.$transaction(async (tx) => {
      for (const row of resolvedRows) {
        const existing = await tx.ddoMaster.findUnique({
          where: { clientId_tan: { clientId, tan: row.tan } },
        });

        if (existing) {
          if (existingDataStrategy === "skip_existing") {
            skippedExistingCount++;
            continue;
          }
          await tx.ddoMaster.update({ where: { id: existing.id }, data: row });
          updatedCount++;
        } else {
          await tx.ddoMaster.create({ data: { ...row, clientId } });
          createdCount++;
        }
      }
    });
  } catch (err) {
    console.error("DDO Master import commit failed:", err);
    const message = err instanceof Error ? err.message : "Could not save these rows.";
    return NextResponse.json({ error: message }, { status: 500 });
  }

  return NextResponse.json(
    { created: createdCount, updated: updatedCount, skipped: skippedExistingCount, ignored: ignoredCount },
    { status: 201 },
  );
}
