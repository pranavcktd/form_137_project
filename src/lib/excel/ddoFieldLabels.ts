import type { DdoRecordFormInput } from "@/lib/validation/ddoRecord";

/** Reverse of FIELD_NAME_TO_PROPERTY, for looking up a spec field name by domain property. */
export const PROPERTY_TO_FIELD_NAME: Partial<Record<keyof DdoRecordFormInput, string>> = {
  tan: "TAN of the DDO",
  name: "Name of the DDO",
  address1: "DDO_Address Line 1",
  address2: "DDO_Address Line 2",
  address3: "DDO_Address Line 3",
  address4: "DDO_Address Line 4",
  city: "DDO_City",
  state: "DDO_State",
  pin: "DDO_Address PIN",
  taxDeducted: "TDS/TCS deducted Amount",
  formType: "Form Type",
  ddoRegNo: "DDO registration no.",
  ddoCode: "DDO code",
  email: "E-mail ID of DDO",
  totalRemitted: "Total TDS/TCS remitted to Government account (AG/Pr CCA)",
  natureOfDeduction: "Nature of deduction",
};
