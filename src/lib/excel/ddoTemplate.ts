import ExcelJS from "exceljs";
import { ddoEditableFields } from "./ddoFields";
import type { StatementType } from "@/schemas/24g-f137/v1_9";

export async function generateDdoTemplate(
  financialYear: number,
  statementType: StatementType,
): Promise<Buffer> {
  const fields = ddoEditableFields(financialYear, statementType);

  const workbook = new ExcelJS.Workbook();
  const sheet = workbook.addWorksheet("DDO Records");

  sheet.columns = fields.map((field) => ({
    header: field.name,
    key: field.name,
    width: Math.min(Math.max(field.name.length, 15), 45),
  }));

  const headerRow = sheet.getRow(1);
  headerRow.font = { bold: true };

  const notesSheet = workbook.addWorksheet("Field Notes");
  notesSheet.columns = [
    { header: "Field", key: "field", width: 45 },
    { header: "Mandatory", key: "mandatory", width: 12 },
    { header: "Notes", key: "notes", width: 90 },
  ];
  notesSheet.getRow(1).font = { bold: true };
  for (const field of fields) {
    notesSheet.addRow({
      field: field.name,
      mandatory: field.mandatory === "M" ? "Required" : "Optional",
      notes: field.remarks,
    });
  }

  const buffer = await workbook.xlsx.writeBuffer();
  return Buffer.from(buffer);
}
