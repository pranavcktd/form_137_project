import type { DdoRecordFormInput } from "@/lib/validation/ddoRecord";

/** Maps a TD schema field's spec name to the DdoRecord domain property it feeds. */
export const FIELD_NAME_TO_PROPERTY: Record<string, keyof DdoRecordFormInput> = {
  "TAN of the DDO": "tan",
  "Name of the DDO": "name",
  "DDO_Address Line 1": "address1",
  "DDO_Address Line 2": "address2",
  "DDO_Address Line 3": "address3",
  "DDO_Address Line 4": "address4",
  "DDO_City": "city",
  "DDO_State": "state",
  "DDO_Address PIN": "pin",
  "TDS/TCS deducted Amount": "taxDeducted",
  "Form Type": "formType",
  "DDO registration no.": "ddoRegNo",
  "DDO code": "ddoCode",
  "E-mail ID of DDO": "email",
  "Total TDS/TCS remitted to Government account (AG/Pr CCA)": "totalRemitted",
  "Nature of deduction": "natureOfDeduction",
};
