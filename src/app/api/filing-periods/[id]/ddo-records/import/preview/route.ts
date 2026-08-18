import { NextResponse } from "next/server";
import { requireFilingPeriod } from "@/lib/authz";
import { prisma } from "@/lib/prisma";
import { parseDdoWorkbook, type ImportedRow } from "@/lib/excel/parseDdoImport";

export async function POST(
  request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const formData = await request.formData();
  const file = formData.get("file");
  if (!(file instanceof File)) {
    return NextResponse.json({ error: "No file uploaded" }, { status: 400 });
  }

  const buffer = Buffer.from(await file.arrayBuffer());

  let rows;
  try {
    rows = await parseDdoWorkbook(buffer, filingPeriod.financialYear, filingPeriod.statementType);
  } catch {
    return NextResponse.json(
      { error: "Could not read that file. Make sure it's a valid .xlsx export of the template." },
      { status: 400 },
    );
  }

  // Surface same-DDO/same-Form-Type duplicates within the file up front so
  // the user can decide how to handle them before anything is saved, rather
  // than have commit silently sum (or reject) them.
  const groups = new Map<string, ImportedRow[]>();
  for (const row of rows) {
    if (row.errors.length > 0) continue;
    const key = `${row.data.tan}::${row.data.formType ?? ""}`;
    const group = groups.get(key);
    if (group) group.push(row);
    else groups.set(key, [row]);
  }
  const duplicates = Array.from(groups.values())
    .filter((group) => group.length > 1)
    .map((group) => ({
      tan: group[0].data.tan ?? "",
      name: group[0].data.name ?? "",
      formType: group[0].data.formType ?? "",
      rows: group.map((r) => ({
        rowNumber: r.rowNumber,
        taxDeducted: r.data.taxDeducted ?? 0,
        totalRemitted: r.data.totalRemitted ?? 0,
      })),
      mergedTaxDeducted: group.reduce((sum, r) => sum + (r.data.taxDeducted ?? 0), 0),
      mergedTotalRemitted: group.reduce((sum, r) => sum + (r.data.totalRemitted ?? 0), 0),
    }));

  const existingRecordCount = await prisma.ddoRecord.count({ where: { filingPeriodId: id } });

  return NextResponse.json({ rows, duplicates, existingRecordCount });
}
