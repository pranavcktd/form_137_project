import ExcelJS from "exceljs";
import type { DdoMasterFormInput } from "@/lib/validation/ddoMaster";

// Mirrors the fields on the manual "Add DDO" form (client-side ddo-master
// form only exposes address1/address2, not 3/4) — TAN and Name are the only
// two required by ddoMasterSchema, everything else is optional.
export const DDO_MASTER_TEMPLATE_COLUMNS: {
  header: string;
  property: keyof DdoMasterFormInput;
  required: boolean;
  notes: string;
}[] = [
  { header: "TAN", property: "tan", required: true, notes: "4 letters/digits, 5 digits, 1 letter, e.g. MUMD12345A." },
  { header: "Name", property: "name", required: true, notes: "Name of the DDO." },
  { header: "DDO Registration No.", property: "ddoRegNo", required: false, notes: "From Central Record Keeping Agency, if available." },
  { header: "DDO Code", property: "ddoCode", required: false, notes: "" },
  { header: "Address Line 1", property: "address1", required: false, notes: "Street/office address." },
  { header: "Address Line 2", property: "address2", required: false, notes: "" },
  { header: "City", property: "city", required: false, notes: "" },
  { header: "State", property: "state", required: false, notes: "" },
  { header: "PIN Code", property: "pin", required: false, notes: "6-digit PIN." },
  { header: "Email", property: "email", required: false, notes: "" },
];

/** Blank template (with a Field Notes tab) or, when `masters` is passed, an export of the current DDO Master list. */
export async function generateDdoMasterWorkbook(masters?: (DdoMasterFormInput & { id?: string })[]): Promise<Buffer> {
  const workbook = new ExcelJS.Workbook();
  const sheet = workbook.addWorksheet("DDO Master");

  sheet.columns = DDO_MASTER_TEMPLATE_COLUMNS.map((c) => ({
    header: c.header,
    key: c.property,
    width: Math.min(Math.max(c.header.length, 15), 40),
  }));
  sheet.getRow(1).font = { bold: true };

  for (const master of masters ?? []) {
    sheet.addRow(Object.fromEntries(DDO_MASTER_TEMPLATE_COLUMNS.map((c) => [c.property, master[c.property] ?? ""])));
  }

  const notesSheet = workbook.addWorksheet("Field Notes");
  notesSheet.columns = [
    { header: "Field", key: "field", width: 30 },
    { header: "Mandatory", key: "mandatory", width: 12 },
    { header: "Notes", key: "notes", width: 70 },
  ];
  notesSheet.getRow(1).font = { bold: true };
  for (const c of DDO_MASTER_TEMPLATE_COLUMNS) {
    notesSheet.addRow({ field: c.header, mandatory: c.required ? "Required" : "Optional", notes: c.notes });
  }

  const buffer = await workbook.xlsx.writeBuffer();
  return Buffer.from(buffer);
}
