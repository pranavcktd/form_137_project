import type { RecordFieldDefs } from "@/schemas/24g-f137/types";
import { formatDateDDMMYYYY } from "./formatters";
import type { FieldValues } from "./render";
import type {
  DdoMode,
  DdoRecordInput,
  FilingPeriodInput,
  OrganizationInput,
} from "./types";

const GOVT_CATEGORY_CODE: Record<OrganizationInput["govtCategory"], string> = {
  CENTRAL: "A",
  STATE: "S",
};

const REVISION_MODE_CODE: Record<DdoMode, string> = {
  ADD: "N",
  UPDATE: "U",
  DELETE: "D",
  NO_CHANGE: "",
};

const DDO_MAPPING_CODE: Record<DdoMode, string> = {
  ADD: "A",
  UPDATE: "U",
  DELETE: "D",
  NO_CHANGE: "",
};

/**
 * Each TD "Form Type" (F138/F140/F143/F144, from FY2026-27 onward) rolls up
 * into one of the legacy TDS/TCS return-type buckets BH reports control
 * totals for. Confirmed against the real sample file: F138 tax/remitted
 * amounts land exactly in the 24Q bucket, F140 in 26Q, F144 in 27Q, F143 in
 * 27EQ — there is no other documented mapping for this in the spec text.
 */
const FORM_TYPE_TO_BUCKET: Record<string, "24Q" | "26Q" | "27Q" | "27EQ"> = {
  F138: "24Q",
  F140: "26Q",
  F144: "27Q",
  F143: "27EQ",
};

export function buildFhValues(organization: OrganizationInput): FieldValues {
  return {
    "Accounts Office Identification No. (AIN)": organization.ain,
    "File Creation Date": formatDateDDMMYYYY(new Date()),
  };
}

export function buildBhValues(
  organization: OrganizationInput,
  filingPeriod: FilingPeriodInput,
  ddoRecords: DdoRecordInput[],
  tdFields: RecordFieldDefs | null,
): FieldValues {
  const totalTax = ddoRecords.reduce((sum, r) => sum + r.taxDeducted, 0);
  const totalRemitted = ddoRecords.reduce((sum, r) => sum + r.totalRemitted, 0);
  const distinctTans = new Set(ddoRecords.map((r) => r.tan)).size;

  // "Added/updated/deleted" DDO-mapping counts only mean something when a
  // mode-carrying field is actually active on the TD record for this
  // fyScope/statementType: Revision Mode (Correction_M) or the legacy DDO
  // Mapping/update field. An Original F137 statement has neither active
  // (confirmed against the real sample file, where these three counts are
  // always 0 even with DDO records present), so they're 0 there.
  const revisionModeActive =
    !!tdFields && tdFields.find((f) => f.name === "Revision Mode")?.mandatory !== "NA";
  const ddoMappingActive =
    !!tdFields && tdFields.find((f) => f.name === "DDO Mapping/ update")?.mandatory !== "NA";
  const modeCountingActive = revisionModeActive || ddoMappingActive;

  const countByMode = (mode: DdoMode) =>
    modeCountingActive ? ddoRecords.filter((r) => r.mode === mode).length : 0;

  const bucketTotals = {
    "24Q": { count: 0, tax: 0, remitted: 0 },
    "26Q": { count: 0, tax: 0, remitted: 0 },
    "27Q": { count: 0, tax: 0, remitted: 0 },
    "27EQ": { count: 0, tax: 0, remitted: 0 },
  };
  for (const record of ddoRecords) {
    const bucket = record.formType ? FORM_TYPE_TO_BUCKET[record.formType] : undefined;
    if (!bucket) continue;
    bucketTotals[bucket].count += 1;
    bucketTotals[bucket].tax += record.taxDeducted;
    bucketTotals[bucket].remitted += record.totalRemitted;
  }

  return {
    "Accounts Office Identification No. (AIN)": organization.ain,
    "AO_Name": organization.departmentName,
    "AO_Address Line 1 OR Flat/Door/Block Number": organization.responsiblePersonAddress1,
    "AO_Address Line 2 OR Name of Premises / Building": organization.responsiblePersonAddress2 ?? "",
    "AO_Address Line 3 OR Road/ Street/ Lane": organization.responsiblePersonAddress3 ?? "",
    "AO_Address Line 4 OR Area/ Location": organization.responsiblePersonAddress4 ?? "",
    "AO_City": organization.responsiblePersonCity,
    "AO_State": organization.responsiblePersonState,
    "AO_PIN": organization.responsiblePersonPin,
    "STD Code": organization.responsiblePersonStdCode ?? "",
    "Phone No.": organization.responsiblePersonPhone ?? "",
    "Email ID": organization.responsiblePersonEmail,
    "Responsible Person Name": organization.responsiblePersonName ?? "",
    "Responsible Person Designation": organization.responsiblePersonDesignation,
    "Financial Year": String(filingPeriod.financialYear),
    "AO Category": GOVT_CATEGORY_CODE[organization.govtCategory],
    "No. of Transactions": String(ddoRecords.length),
    "Total TDS/TCS amount reported": totalTax.toFixed(2),
    "Original RRR No.": filingPeriod.originalRrrNo ?? "",
    "Previous RRR No.": filingPeriod.previousRrrNo ?? "",
    "Month of Transfer voucher": String(filingPeriod.month).padStart(2, "0"),
    "Responsible person Address Line 1": organization.responsiblePersonAddress1,
    "Responsible person Address Line 2": organization.responsiblePersonAddress2 ?? "",
    "Responsible person Address Line 3": organization.responsiblePersonAddress3 ?? "",
    "Responsible person Address Line 4": organization.responsiblePersonAddress4 ?? "",
    "Responsible person_City": organization.responsiblePersonCity,
    "Responsible person_State": organization.responsiblePersonState,
    "Responsible person_PIN": organization.responsiblePersonPin,
    "Responsible person STD Code": organization.responsiblePersonStdCode ?? "",
    "Responsible person Phone No.": organization.responsiblePersonPhone ?? "",
    "Responsible person E-mail id": organization.responsiblePersonEmail,
    "Mobile no. of Responsible person": organization.responsiblePersonMobile ?? "",
    "Mobile Number": organization.responsiblePersonMobile ?? "",
    // Mandatory when AO Category = State Government (confirmed by running the
    // real FVU validator against a generated statement missing this field).
    "State name": organization.govtCategory === "STATE" ? organization.responsiblePersonState : "",
    "Ministry/ Department name": organization.ministryName ?? "",
    "Sub Ministry name": organization.subMinistryName ?? "",
    "Country Code": organization.countryCode ?? "",
    "First Name of responsible person": organization.responsiblePersonFirstName ?? "",
    "Middle Name of responsible person": organization.responsiblePersonMiddleName ?? "",
    "Last Name of responsible person": organization.responsiblePersonLastName ?? "",
    "Count of 24Q transaction": String(bucketTotals["24Q"].count),
    "Control total of tax deducted/collected for 24Q": bucketTotals["24Q"].tax.toFixed(2),
    "Total TDS/TCS remitted to Government account (AG/Pr CCA) for 24Q":
      bucketTotals["24Q"].remitted.toFixed(2),
    "Count of 26Q transaction": String(bucketTotals["26Q"].count),
    "Control total of tax deducted/collected for 26Q": bucketTotals["26Q"].tax.toFixed(2),
    "Total TDS/TCS remitted to Government account (AG/Pr CCA) for 26Q":
      bucketTotals["26Q"].remitted.toFixed(2),
    "Count of 27Q transaction": String(bucketTotals["27Q"].count),
    "Control total of tax deducted/collected for 27Q": bucketTotals["27Q"].tax.toFixed(2),
    "Total TDS/TCS remitted to Government account (AG/Pr CCA) for 27Q":
      bucketTotals["27Q"].remitted.toFixed(2),
    "Count of 27EQ transaction": String(bucketTotals["27EQ"].count),
    "Control total of tax deducted/collected for 27EQ": bucketTotals["27EQ"].tax.toFixed(2),
    "Total TDS/TCS remitted to Government account (AG/Pr CCA) for 27EQ":
      bucketTotals["27EQ"].remitted.toFixed(2),
    "Count of Distinct DDOs": String(distinctTans),
    "Total TDS/TCS remitted to Government account (AG/Pr CCA)": totalRemitted.toFixed(2),
    "Count of DDO records added": String(countByMode("ADD")),
    "Count of DDO records updated": String(countByMode("UPDATE")),
    "Count of DDO records deleted": String(countByMode("DELETE")),
    "TAN of Accounts Office": organization.tan ?? "",
    "Batch Updation Indicator":
      filingPeriod.batchUpdationIndicator !== undefined
        ? String(filingPeriod.batchUpdationIndicator)
        : "",
  };
}

export function buildTdValues(record: DdoRecordInput): FieldValues {
  return {
    "Serial No.": String(record.serialNo),
    "Revision Mode": REVISION_MODE_CODE[record.mode],
    "LAST DDO TAN": record.lastDdoTan ?? "",
    "TAN of the DDO": record.tan,
    "Name of the DDO": record.name,
    "DDO_Address Line 1": record.address1 ?? "",
    "DDO_Address Line 2": record.address2 ?? "",
    "DDO_Address Line 3": record.address3 ?? "",
    "DDO_Address Line 4": record.address4 ?? "",
    "DDO_City": record.city ?? "",
    "DDO_State": record.state ?? "",
    "DDO_Address PIN": record.pin ?? "",
    "TDS/TCS deducted Amount": record.taxDeducted.toFixed(2),
    "Form Type": record.formType ?? "",
    "DDO registration no.": record.ddoRegNo ?? "",
    "DDO code": record.ddoCode ?? "",
    "E-mail ID of DDO": record.email ?? "",
    "Total TDS/TCS remitted to Government account (AG/Pr CCA)": record.totalRemitted.toFixed(2),
    "Nature of deduction": record.natureOfDeduction ?? "",
    "DDO Mapping/ update": DDO_MAPPING_CODE[record.mode],
    "LAST Total TDS/TCS remitted to Government account (AG/Pr CCA)":
      record.lastTotalRemitted !== null && record.lastTotalRemitted !== undefined
        ? record.lastTotalRemitted.toFixed(2)
        : "",
    "LAST DDO registration no.": record.lastDdoRegNo ?? "",
    "LAST DDO code": record.lastDdoCode ?? "",
    "Last TDS/TCS deducted Amount":
      record.lastTaxDeducted !== null && record.lastTaxDeducted !== undefined
        ? record.lastTaxDeducted.toFixed(2)
        : "",
    "Last NAT of DED": record.lastNatureOfDeduction ?? "",
  };
}
