import ExcelJS from "exceljs";
import { ddoMasterSchema, type DdoMasterFormInput } from "@/lib/validation/ddoMaster";

export interface DdoMasterImportRow {
  rowNumber: number;
  data: Partial<DdoMasterFormInput>;
  errors: string[];
}

// A handful of header spellings map to each property so a client's existing
// export — including "DDO Reg No" / "DDO OFFICE ADDRESS" style headers —
// can be uploaded as-is without renaming columns first.
const HEADER_ALIASES: Record<string, keyof DdoMasterFormInput> = {
  "TAN": "tan",
  "TAN*": "tan",
  "DDO TAN": "tan",
  "TAN of the DDO": "tan",
  "Name": "name",
  "Name*": "name",
  "DDO Name": "name",
  "Name of the DDO": "name",
  "DDO Registration No.": "ddoRegNo",
  "DDO Registration No": "ddoRegNo",
  "DDO Reg No": "ddoRegNo",
  "DDO Reg. No.": "ddoRegNo",
  "DDO Reg No.": "ddoRegNo",
  "DDO Code": "ddoCode",
  "Address Line 1": "address1",
  "Address": "address1",
  "DDO OFFICE ADDRESS": "address1",
  "DDO Office Address": "address1",
  "Address Line 2": "address2",
  "Address Line 3": "address3",
  "Address Line 4": "address4",
  "City": "city",
  "State": "state",
  "PIN Code": "pin",
  "PIN": "pin",
  "Email": "email",
  "E-mail": "email",
  "Email ID": "email",
};

export async function parseDdoMasterWorkbook(buffer: Buffer): Promise<DdoMasterImportRow[]> {
  const workbook = new ExcelJS.Workbook();
  await workbook.xlsx.load(buffer as unknown as ArrayBuffer);
  const sheet = workbook.worksheets[0];
  if (!sheet) return [];

  const headerRow = sheet.getRow(1);
  const columnByProperty = new Map<keyof DdoMasterFormInput, number>();
  headerRow.eachCell((cell, colNumber) => {
    const header = String(cell.value ?? "").trim();
    const property = HEADER_ALIASES[header];
    if (property && !columnByProperty.has(property)) columnByProperty.set(property, colNumber);
  });

  const rows: DdoMasterImportRow[] = [];

  for (let rowNumber = 2; rowNumber <= sheet.rowCount; rowNumber++) {
    const row = sheet.getRow(rowNumber);
    const isBlank =
      row.values === undefined ||
      (Array.isArray(row.values) && row.values.every((v) => v === undefined || v === null || v === ""));
    if (isBlank) continue;

    const data: Record<string, string> = {};
    for (const [property, colIndex] of columnByProperty) {
      const rawValue = row.getCell(colIndex).value;
      const value = rawValue === undefined || rawValue === null ? "" : String(rawValue).trim();
      data[property] = property === "tan" ? value.toUpperCase() : value;
    }

    const parsed = ddoMasterSchema.safeParse(data);
    const errors = parsed.success
      ? []
      : Object.entries(parsed.error.flatten().fieldErrors).flatMap(([field, msgs]) =>
          (msgs ?? []).map((m) => `${field}: ${m}`),
        );

    rows.push({ rowNumber, data, errors });
  }

  return rows;
}
