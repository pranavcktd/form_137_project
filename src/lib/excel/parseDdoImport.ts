import ExcelJS from "exceljs";
import { ddoEditableFields } from "./ddoFields";
import { FIELD_NAME_TO_PROPERTY } from "./fieldNameMap";
import { validateField, type FieldError } from "@/lib/validation/validateField";
import type { StatementType } from "@/schemas/24g-f137/v1_9";
import type { DdoRecordFormInput } from "@/lib/validation/ddoRecord";

export interface ImportedRow {
  rowNumber: number;
  data: Partial<DdoRecordFormInput>;
  errors: FieldError[];
}

export async function parseDdoWorkbook(
  buffer: Buffer,
  financialYear: number,
  statementType: StatementType,
): Promise<ImportedRow[]> {
  const fields = ddoEditableFields(financialYear, statementType);

  const workbook = new ExcelJS.Workbook();
  await workbook.xlsx.load(buffer as unknown as ArrayBuffer);
  const sheet = workbook.worksheets[0];
  if (!sheet) return [];

  const headerRow = sheet.getRow(1);
  const columnIndexByFieldName = new Map<string, number>();
  headerRow.eachCell((cell, colNumber) => {
    const header = String(cell.value ?? "").trim();
    if (header) columnIndexByFieldName.set(header, colNumber);
  });

  const rows: ImportedRow[] = [];

  for (let rowNumber = 2; rowNumber <= sheet.rowCount; rowNumber++) {
    const row = sheet.getRow(rowNumber);
    const isBlank = row.values === undefined || (Array.isArray(row.values) && row.values.every((v) => v === undefined || v === null || v === ""));
    if (isBlank) continue;

    const errors: FieldError[] = [];
    const data: Record<string, unknown> = { mode: "ADD" };

    for (const field of fields) {
      const colIndex = columnIndexByFieldName.get(field.name);
      const rawValue = colIndex ? row.getCell(colIndex).value : undefined;
      const stringValue =
        rawValue === undefined || rawValue === null ? "" : String(rawValue).trim();

      errors.push(...validateField(field, stringValue));

      const property = FIELD_NAME_TO_PROPERTY[field.name];
      if (!property) continue;

      if (property === "taxDeducted" || property === "totalRemitted") {
        data[property] = stringValue ? Number(stringValue) : 0;
      } else {
        data[property] = stringValue;
      }
    }

    rows.push({ rowNumber, data: data as Partial<DdoRecordFormInput>, errors });
  }

  return rows;
}
