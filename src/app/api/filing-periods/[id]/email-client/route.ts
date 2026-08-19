import { NextResponse } from "next/server";
import { readFile } from "fs/promises";
import ExcelJS from "exceljs";
import { prisma } from "@/lib/prisma";
import { requireFilingPeriod } from "@/lib/authz";
import { formTypeLabel } from "@/lib/formTypeLabels";
import { sendFilingReturnEmail } from "@/lib/alerts/notify";

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

function responsiblePersonDisplayName(client: {
  responsiblePersonName: string | null;
  responsiblePersonFirstName: string | null;
  responsiblePersonMiddleName: string | null;
  responsiblePersonLastName: string | null;
}): string {
  if (client.responsiblePersonName) return client.responsiblePersonName;
  const parts = [client.responsiblePersonFirstName, client.responsiblePersonMiddleName, client.responsiblePersonLastName].filter(
    Boolean,
  );
  return parts.length > 0 ? parts.join(" ") : "Sir/Madam";
}

/**
 * Emails the Client's own responsible person (the government office contact,
 * not a Nex login) the details of one filed return — receipt/ack number and
 * date, plus an Excel export of that period's DDO transactions, and the FVU
 * tool's receipt.html if the most recent generation attempt for this period
 * produced one. Requires a receipt number/date to already be saved, since
 * that's the whole point of what's being communicated.
 */
export async function POST(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });

  if (!filingPeriod.receiptNumber || !filingPeriod.receiptDate) {
    return NextResponse.json(
      { error: { formErrors: ["Save a Receipt / Acknowledgement Number and Date before emailing the client."] } },
      { status: 400 },
    );
  }

  const [client, ddoRecords, latestPassedGeneration] = await Promise.all([
    prisma.client.findUnique({ where: { id: filingPeriod.clientId } }),
    prisma.ddoRecord.findMany({ where: { filingPeriodId: id }, orderBy: { serialNo: "asc" } }),
    prisma.generatedFile.findFirst({
      where: { filingPeriodId: id, status: "FVU_PASSED" },
      orderBy: { createdAt: "desc" },
    }),
  ]);
  if (!client) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const monthLabel = MONTHS[filingPeriod.month - 1];

  const workbook = new ExcelJS.Workbook();
  const sheet = workbook.addWorksheet(`${monthLabel} ${filingPeriod.financialYear}`);
  sheet.addRow(["Serial No.", "TAN", "DDO Name", "Form Type", "Tax Deducted", "Total Remitted", "Difference"]);
  sheet.getRow(1).font = { bold: true };
  for (const r of ddoRecords) {
    const taxDeducted = Number(r.taxDeducted);
    const totalRemitted = Number(r.totalRemitted);
    sheet.addRow([
      r.serialNo,
      r.tan,
      r.name,
      formTypeLabel(r.formType),
      taxDeducted,
      totalRemitted,
      Math.round((taxDeducted - totalRemitted) * 100) / 100,
    ]);
  }
  sheet.columns = [{ width: 10 }, { width: 14 }, { width: 30 }, { width: 44 }, { width: 14 }, { width: 14 }, { width: 14 }];
  const excelBuffer = Buffer.from(await workbook.xlsx.writeBuffer());

  const attachments = [
    {
      filename: `form137-${filingPeriod.financialYear}-${String(filingPeriod.month).padStart(2, "0")}-${client.departmentName.replace(/\s+/g, "-")}.xlsx`,
      content: excelBuffer,
    },
  ];

  if (latestPassedGeneration?.receiptPath) {
    try {
      attachments.push({
        filename: "filing-receipt.html",
        content: await readFile(latestPassedGeneration.receiptPath),
      });
    } catch {
      // The FVU-generated receipt file may have been moved/cleaned up since —
      // not fatal, the email still goes out with the ack details + Excel.
    }
  }

  const emailed = await sendFilingReturnEmail({
    toEmail: client.responsiblePersonEmail,
    toName: responsiblePersonDisplayName(client),
    organizationId: session.user.organizationId,
    departmentName: client.departmentName,
    ain: client.ain,
    financialYear: filingPeriod.financialYear,
    month: filingPeriod.month,
    monthLabel,
    statementType: filingPeriod.statementType,
    receiptNumber: filingPeriod.receiptNumber,
    receiptDate: new Date(filingPeriod.receiptDate).toLocaleDateString(),
    attachments,
  });

  if (!emailed) {
    return NextResponse.json(
      { error: { formErrors: ["No SMTP is configured — set one up from Profile before emailing clients."] } },
      { status: 422 },
    );
  }

  return NextResponse.json({ ok: true, sentTo: client.responsiblePersonEmail });
}
