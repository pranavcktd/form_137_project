import { NextResponse } from "next/server";
import ExcelJS from "exceljs";
import { prisma } from "@/lib/prisma";
import { requireClient } from "@/lib/authz";
import { currentFinancialYear } from "@/lib/financialYear";
import { formTypeLabel } from "@/lib/formTypeLabels";

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

async function loadReportRows(clientId: string, financialYear: number) {
  const filingPeriods = await prisma.filingPeriod.findMany({
    where: { clientId, financialYear },
    orderBy: { month: "asc" },
    include: { ddoRecords: { include: { ddoMaster: true }, orderBy: { serialNo: "asc" } } },
  });

  return filingPeriods.flatMap((period) =>
    period.ddoRecords.map((r) => ({
      month: period.month,
      statementType: period.statementType,
      periodStatus: period.status,
      tan: r.tan,
      name: r.name,
      formType: r.formType,
      taxDeducted: Number(r.taxDeducted),
      totalRemitted: Number(r.totalRemitted),
    })),
  );
}

export async function GET(
  request: Request,
  { params }: { params: Promise<{ clientId: string }> },
) {
  const { clientId } = await params;
  const { session, client } = await requireClient(clientId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!client) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const url = new URL(request.url);
  const financialYear = Number(url.searchParams.get("financialYear")) || currentFinancialYear();
  const rows = await loadReportRows(clientId, financialYear);

  const format = url.searchParams.get("format");
  if (format !== "xlsx") {
    return NextResponse.json({ financialYear, rows });
  }

  const workbook = new ExcelJS.Workbook();
  const sheet = workbook.addWorksheet(`FY ${financialYear}-${String((financialYear + 1) % 100).padStart(2, "0")}`);
  sheet.addRow(["Month", "Statement Type", "TAN", "DDO Name", "Form Type", "Tax Deducted", "Total Remitted", "Difference"]);
  sheet.getRow(1).font = { bold: true };
  for (const r of rows) {
    sheet.addRow([
      MONTHS[r.month - 1],
      r.statementType,
      r.tan,
      r.name,
      formTypeLabel(r.formType),
      r.taxDeducted,
      r.totalRemitted,
      Math.round((r.taxDeducted - r.totalRemitted) * 100) / 100,
    ]);
  }
  sheet.columns = [
    { width: 12 }, { width: 14 }, { width: 14 }, { width: 28 }, { width: 44 }, { width: 14 }, { width: 14 }, { width: 14 },
  ];

  const buffer = await workbook.xlsx.writeBuffer();
  return new NextResponse(new Uint8Array(buffer), {
    status: 200,
    headers: {
      "Content-Type": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "Content-Disposition": `attachment; filename="fy-wise-report-${financialYear}-${client.departmentName.replace(/\s+/g, "-")}.xlsx"`,
    },
  });
}
