import type { FvuFieldError } from "./parseErrorHtml";
import type { ClientProfileValues } from "@/components/client-profile-fields";
import type { DdoRecordFormInput } from "@/lib/validation/ddoRecord";

/**
 * Maps an FVU error's record type + field index back to where it's actually
 * editable in this app. Keyed by field INDEX (the number the FVU itself puts
 * in parentheses after the field name, e.g. "Responsible Person First
 * Name(76)") rather than by the field name text — the index is stable and
 * unambiguous, whereas the label text turned out to differ from what our own
 * schema docs (src/schemas/24g-f137/v1_9/original/*.ts) transcribe from the
 * spec sheet. These arrays are copied verbatim from the real FVU jar's own
 * source (TBAF_BH_FIELD/TBAF_TD_FIELD/TBAF_FH_FIELD in
 * fvu-wrapper/src/com/tin/etbaf/form24G/fvu/TBAFInterface.java), which is
 * what actually gets written into err.html — confirmed against a real error
 * ("Responsible Person First Name(76)"), which does NOT match this app's own
 * schema doc's name for the same field ("First Name of responsible person").
 * Several distinct FVU field indexes collapse onto the same app field, since
 * buildBhValues() (src/lib/generator/buildValues.ts) reuses one Client field
 * for what the FVU spec treats as two separate slots (e.g. AO address vs
 * Responsible Person address both come from responsiblePersonAddress1).
 */
const BH_INDEX_TO_CLIENT_KEY: Record<number, keyof ClientProfileValues> = {
  5: "ain", // AIN(5)
  7: "departmentName", // AO Name(7)
  8: "responsiblePersonAddress1", // AO Address1(8)
  9: "responsiblePersonAddress2", // AO Address2(9)
  10: "responsiblePersonAddress3", // AO Address3(10)
  11: "responsiblePersonAddress4", // AO Address4(11)
  12: "responsiblePersonCity", // AO City(12)
  13: "responsiblePersonState", // AO State(13)
  14: "responsiblePersonPin", // AO Pin Code(14)
  15: "responsiblePersonStdCode", // AO STD Code(15)
  16: "responsiblePersonPhone", // Phone No.(16)
  17: "responsiblePersonEmail", // Email ID(17)
  18: "responsiblePersonName", // Responsible Person Name(18)
  19: "responsiblePersonDesignation", // Responsible Person Designation(19)
  22: "govtCategory", // Deductor Category(22)
  35: "responsiblePersonAddress1", // Responsible Person Address1(35)
  36: "responsiblePersonAddress2", // Responsible Person Address2(36)
  37: "responsiblePersonAddress3", // Responsible Person Address3(37)
  38: "responsiblePersonAddress4", // Responsible Person Address4(38)
  39: "responsiblePersonCity", // Responsible Person City(39)
  40: "responsiblePersonState", // Responsible Person State(40)
  41: "responsiblePersonPin", // Responsible Person Pin Code(41)
  42: "responsiblePersonStdCode", // Responsible Person STD Code(42)
  43: "responsiblePersonPhone", // Responsible Person Phone No.(43)
  44: "responsiblePersonEmail", // Responsible Person Email ID(44)
  45: "responsiblePersonMobile", // Responsible Person Mobile(45)
  47: "responsiblePersonState", // State Name(47)
  48: "ministryName", // Ministry Name(48)
  49: "subMinistryName", // Sub Ministry Name(49)
  72: "tan", // TAN of the Accounts Office(72)
  76: "responsiblePersonFirstName", // Responsible Person First Name(76)
  77: "responsiblePersonMiddleName", // Responsible Person Middle Name(77)
  78: "responsiblePersonLastName", // Responsible Person Last Name(78)
  79: "countryCode", // Responsible Person Country Code(79)
};

/** File Header errors are rare and almost entirely computed/constant fields —
 *  AIN is the one that's genuinely a Client field, same as on the Batch Header. */
const FH_INDEX_TO_CLIENT_KEY: Record<number, keyof ClientProfileValues> = {
  7: "ain", // AIN/Organization/TFC ID(7)
};

const TD_INDEX_TO_DDO_KEY: Record<number, keyof DdoRecordFormInput> = {
  7: "tan", // TAN(7)
  8: "name", // Name(8)
  9: "address1", // Address1(9)
  10: "address2", // Address2(10)
  11: "address3", // Address3(11)
  12: "address4", // Address4(12)
  13: "city", // Address City(13)
  14: "state", // Address State(14)
  15: "pin", // Address PIN(15)
  16: "taxDeducted", // Tax Amount(16)
  17: "formType", // Form Type(17)
  18: "ddoRegNo", // DDO Registration no.(18)
  19: "ddoCode", // DDO Code(19)
  20: "email", // Email ID(20)
  21: "totalRemitted", // Remitted Amt(21)
  22: "natureOfDeduction", // Nature of Deduction(22)
};

export type FvuErrorTarget =
  | { kind: "client"; fieldKey: keyof ClientProfileValues }
  | { kind: "ddoRecord"; fieldKey: keyof DdoRecordFormInput; serialNo: number }
  | { kind: "none" };

/** The FVU's own labels for BH/FH/TD, verbatim as they appear in its error table
 *  (see TBAFInterface.java: TBAF_BHREC/TBAF_FHREC/TBAF_TDREC) — not the internal
 *  "BH"/"FH"/"TD" codes. */
export function resolveFvuErrorTarget(error: FvuFieldError): FvuErrorTarget {
  if (error.fieldIndex === null) return { kind: "none" };

  if (error.recordType === "Batch Record") {
    const fieldKey = BH_INDEX_TO_CLIENT_KEY[error.fieldIndex];
    return fieldKey ? { kind: "client", fieldKey } : { kind: "none" };
  }

  if (error.recordType === "File Header Record") {
    const fieldKey = FH_INDEX_TO_CLIENT_KEY[error.fieldIndex];
    return fieldKey ? { kind: "client", fieldKey } : { kind: "none" };
  }

  if (error.recordType === "Transaction Detail Record") {
    const fieldKey = TD_INDEX_TO_DDO_KEY[error.fieldIndex];
    const serialNo = Number(error.transactionDetailNo);
    return fieldKey && Number.isFinite(serialNo) ? { kind: "ddoRecord", fieldKey, serialNo } : { kind: "none" };
  }

  return { kind: "none" };
}
