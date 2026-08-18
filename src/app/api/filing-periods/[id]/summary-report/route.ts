import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { requireFilingPeriod } from "@/lib/authz";
import { generateSummaryReport } from "@/lib/excel/summaryReport";

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const [client, ddoRecords] = await Promise.all([
    prisma.client.findUniqueOrThrow({ where: { id: filingPeriod.clientId } }),
    prisma.ddoRecord.findMany({ where: { filingPeriodId: id }, orderBy: { serialNo: "asc" } }),
  ]);

  const buffer = await generateSummaryReport({
    client: { departmentName: client.departmentName, ain: client.ain },
    filingPeriod: {
      financialYear: filingPeriod.financialYear,
      month: filingPeriod.month,
      statementType: filingPeriod.statementType,
    },
    ddoRecords: ddoRecords.map((r) => ({
      tan: r.tan,
      name: r.name,
      formType: r.formType,
      taxDeducted: Number(r.taxDeducted),
      totalRemitted: Number(r.totalRemitted),
    })),
  });

  return new NextResponse(new Uint8Array(buffer), {
    status: 200,
    headers: {
      "Content-Type": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "Content-Disposition": `attachment; filename="summary-${filingPeriod.financialYear}-${String(filingPeriod.month).padStart(2, "0")}.xlsx"`,
    },
  });
}
