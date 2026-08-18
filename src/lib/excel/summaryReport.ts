import ExcelJS from "exceljs";

export interface SummaryReportInput {
  client: { departmentName: string; ain: string };
  filingPeriod: { financialYear: number; month: number; statementType: string };
  ddoRecords: Array<{
    tan: string;
    name: string;
    formType: string | null;
    taxDeducted: number;
    totalRemitted: number;
  }>;
}

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

export async function generateSummaryReport(input: SummaryReportInput): Promise<Buffer> {
  const { client, filingPeriod, ddoRecords } = input;
  const workbook = new ExcelJS.Workbook();

  const summary = workbook.addWorksheet("Summary");
  summary.addRow(["Client", client.departmentName]);
  summary.addRow(["AIN", client.ain]);
  summary.addRow([
    "Period",
    `FY ${filingPeriod.financialYear}-${String((filingPeriod.financialYear + 1) % 100).padStart(2, "0")} — ${MONTHS[filingPeriod.month - 1]}`,
  ]);
  summary.addRow(["Statement Type", filingPeriod.statementType]);
  summary.addRow([]);

  const headerRowIndex = summary.rowCount + 1;
  summary.addRow(["TAN", "DDO Name", "Form Type", "Tax Deducted", "Total Remitted", "Difference"]);
  summary.getRow(headerRowIndex).font = { bold: true };

  let totalDeducted = 0;
  let totalRemitted = 0;
  for (const r of ddoRecords) {
    const difference = Math.round((r.taxDeducted - r.totalRemitted) * 100) / 100;
    summary.addRow([r.tan, r.name, r.formType ?? "", r.taxDeducted, r.totalRemitted, difference]);
    totalDeducted += r.taxDeducted;
    totalRemitted += r.totalRemitted;
  }

  summary.addRow([]);
  const totalRow = summary.addRow([
    "",
    "",
    "Total",
    Math.round(totalDeducted * 100) / 100,
    Math.round(totalRemitted * 100) / 100,
    Math.round((totalDeducted - totalRemitted) * 100) / 100,
  ]);
  totalRow.font = { bold: true };

  summary.columns = [{ width: 14 }, { width: 30 }, { width: 12 }, { width: 16 }, { width: 16 }, { width: 14 }];

  const byFormType = workbook.addWorksheet("By Form Type");
  byFormType.addRow(["Form Type", "Count", "Total Deducted", "Total Remitted"]);
  byFormType.getRow(1).font = { bold: true };

  const totals = new Map<string, { count: number; deducted: number; remitted: number }>();
  for (const r of ddoRecords) {
    const key = r.formType ?? "(none)";
    const entry = totals.get(key) ?? { count: 0, deducted: 0, remitted: 0 };
    entry.count += 1;
    entry.deducted += r.taxDeducted;
    entry.remitted += r.totalRemitted;
    totals.set(key, entry);
  }
  for (const [formType, entry] of totals) {
    byFormType.addRow([
      formType,
      entry.count,
      Math.round(entry.deducted * 100) / 100,
      Math.round(entry.remitted * 100) / 100,
    ]);
  }
  byFormType.columns = [{ width: 14 }, { width: 10 }, { width: 16 }, { width: 16 }];

  const buffer = await workbook.xlsx.writeBuffer();
  return Buffer.from(buffer);
}
