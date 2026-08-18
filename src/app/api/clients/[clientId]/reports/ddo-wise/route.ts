import { NextResponse } from "next/server";
import ExcelJS from "exceljs";
import { prisma } from "@/lib/prisma";
import { requireClient } from "@/lib/authz";

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

async function loadReportRows(clientId: string) {
  const ddoMasters = await prisma.ddoMaster.findMany({
    where: { clientId },
    orderBy: { name: "asc" },
    include: {
      ddoRecords: {
        include: { filingPeriod: true },
        orderBy: [{ filingPeriod: { financialYear: "desc" } }, { filingPeriod: { month: "desc" } }],
      },
    },
  });

  return ddoMasters.map((ddo) => ({
    ddoMasterId: ddo.id,
    tan: ddo.tan,
    name: ddo.name,
    transactions: ddo.ddoRecords.map((r) => ({
      financialYear: r.filingPeriod.financialYear,
      month: r.filingPeriod.month,
      statementType: r.filingPeriod.statementType,
      formType: r.formType,
      taxDeducted: Number(r.taxDeducted),
      totalRemitted: Number(r.totalRemitted),
    })),
    totalDeducted: ddo.ddoRecords.reduce((sum, r) => sum + Number(r.taxDeducted), 0),
    totalRemitted: ddo.ddoRecords.reduce((sum, r) => sum + Number(r.totalRemitted), 0),
  }));
}

export async function GET(
  request: Request,
  { params }: { params: Promise<{ clientId: string }> },
) {
  const { clientId } = await params;
  const { session, client } = await requireClient(clientId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!client) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const rows = await loadReportRows(clientId);

  const format = new URL(request.url).searchParams.get("format");
  if (format !== "xlsx") {
    return NextResponse.json(rows);
  }

  const workbook = new ExcelJS.Workbook();
  const sheet = workbook.addWorksheet("DDO-wise Entries");
  sheet.addRow([
    "TAN", "DDO Name", "FY", "Month", "Statement Type", "Form Type", "Tax Deducted", "Total Remitted",
  ]);
  sheet.getRow(1).font = { bold: true };

  for (const ddo of rows) {
    if (ddo.transactions.length === 0) {
      sheet.addRow([ddo.tan, ddo.name, "", "", "", "", "", ""]);
      continue;
    }
    for (const t of ddo.transactions) {
      sheet.addRow([
        ddo.tan,
        ddo.name,
        `${t.financialYear}-${String((t.financialYear + 1) % 100).padStart(2, "0")}`,
        MONTHS[t.month - 1],
        t.statementType,
        t.formType ?? "",
        t.taxDeducted,
        t.totalRemitted,
      ]);
    }
  }
  sheet.columns = [
    { width: 14 }, { width: 28 }, { width: 10 }, { width: 12 }, { width: 14 }, { width: 10 }, { width: 14 }, { width: 14 },
  ];

  const buffer = await workbook.xlsx.writeBuffer();
  return new NextResponse(new Uint8Array(buffer), {
    status: 200,
    headers: {
      "Content-Type": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "Content-Disposition": `attachment; filename="ddo-wise-report-${client.departmentName.replace(/\s+/g, "-")}.xlsx"`,
    },
  });
}
