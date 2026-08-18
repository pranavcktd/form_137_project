import { NextResponse } from "next/server";
import ExcelJS from "exceljs";
import { prisma } from "@/lib/prisma";
import { requireClient } from "@/lib/authz";

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

async function loadReportRows(clientId: string) {
  const filingPeriods = await prisma.filingPeriod.findMany({
    where: { clientId },
    orderBy: [{ financialYear: "desc" }, { month: "desc" }],
    include: { _count: { select: { ddoRecords: true } } },
  });

  return filingPeriods.map((p) => ({
    id: p.id,
    financialYear: p.financialYear,
    month: p.month,
    statementType: p.statementType,
    status: p.status,
    ddoCount: p._count.ddoRecords,
    receiptNumber: p.receiptNumber,
    receiptDate: p.receiptDate,
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
  const sheet = workbook.addWorksheet("Filing History");
  sheet.addRow(["FY", "Month", "Statement Type", "Status", "DDO Count", "Receipt No.", "Receipt Date"]);
  sheet.getRow(1).font = { bold: true };
  for (const p of rows) {
    sheet.addRow([
      `${p.financialYear}-${String((p.financialYear + 1) % 100).padStart(2, "0")}`,
      MONTHS[p.month - 1],
      p.statementType,
      p.status,
      p.ddoCount,
      p.receiptNumber ?? "",
      p.receiptDate ? new Date(p.receiptDate).toLocaleDateString() : "",
    ]);
  }
  sheet.columns = [
    { width: 10 }, { width: 12 }, { width: 14 }, { width: 10 }, { width: 10 }, { width: 18 }, { width: 14 },
  ];

  const buffer = await workbook.xlsx.writeBuffer();
  return new NextResponse(new Uint8Array(buffer), {
    status: 200,
    headers: {
      "Content-Type": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "Content-Disposition": `attachment; filename="filing-history-${client.departmentName.replace(/\s+/g, "-")}.xlsx"`,
    },
  });
}
