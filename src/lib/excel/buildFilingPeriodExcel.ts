import ExcelJS from "exceljs";
import { formTypeLabel } from "@/lib/formTypeLabels";

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

export function monthLabel(month: number): string {
  return MONTHS[month - 1];
}

interface DdoRecordLike {
  serialNo: number;
  tan: string;
  name: string;
  formType: string | null;
  // Accepts a Prisma Decimal too (its toString() is what Number() ends up
  // calling) without this file needing to import the Prisma runtime type.
  taxDeducted: number | string | { toString(): string };
  totalRemitted: number | string | { toString(): string };
}

/** One filing period's DDO transactions as an Excel workbook buffer — shared by
 *  the "Email Client" action and firm backups, so both produce the identical sheet. */
export async function buildFilingPeriodExcel(
  financialYear: number,
  month: number,
  ddoRecords: DdoRecordLike[],
): Promise<Buffer> {
  const workbook = new ExcelJS.Workbook();
  const sheet = workbook.addWorksheet(`${monthLabel(month)} ${financialYear}`);
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
  return Buffer.from(await workbook.xlsx.writeBuffer());
}
