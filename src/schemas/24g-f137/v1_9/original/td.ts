import type { RecordFieldDefs } from "../../types";

/**
 * DDO Transaction Detail Record — Original statement.
 * Source: "File format_Regular" sheet, Form 24G_File_Format_version 1.9.
 */
export const tdFields: RecordFieldDefs = [
  { index: 1, name: "Line Number", dataType: "INTEGER", length: 9, mandatory: "M", fyScope: "always", remarks: "Running sequence number for each line in the file." },
  { index: 2, name: "Record Type", dataType: "CHAR", length: 2, mandatory: "M", constant: "TD", fyScope: "always", remarks: "Value should be 'TD' signifying the Transaction Detail (DDO) Record." },
  { index: 3, name: "Batch Number", dataType: "INTEGER", length: 9, mandatory: "M", constant: "1", fyScope: "always", remarks: 'Must match "Batch Number" in the Batch Header record.' },
  { index: 4, name: "Revision Mode", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified for Original statements. For future use." },
  { index: 5, name: "Serial No.", dataType: "INTEGER", length: 9, mandatory: "M", fyScope: "always", remarks: "Starts at 1, strictly increasing." },
  { index: 6, name: "Filler 6", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified for Original statements. For future use." },
  { index: 7, name: "TAN of the DDO", dataType: "CHAR", length: 10, mandatory: "M", fyScope: "always", pattern: "^[A-Z0-9]{4}[0-9]{5}[A-Z]$", remarks: "Ten digit valid TAN, capital letters." },
  { index: 8, name: "Name of the DDO", dataType: "CHAR", length: 75, mandatory: "M", fyScope: "always", remarks: "e.g. 'Asstt. Labour Commissioner'." },
  { index: 9, name: "DDO_Address Line 1", dataType: "CHAR", length: 25, mandatory: "M", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", remarks: "Applicable for FY2025-26 and before only." },
  { index: 10, name: "DDO_Address Line 2", dataType: "CHAR", length: 25, mandatory: "O", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", remarks: "Applicable for FY2025-26 and before only." },
  { index: 11, name: "DDO_Address Line 3", dataType: "CHAR", length: 25, mandatory: "O", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", remarks: "Applicable for FY2025-26 and before only." },
  { index: 12, name: "DDO_Address Line 4", dataType: "CHAR", length: 25, mandatory: "O", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", remarks: "Applicable for FY2025-26 and before only." },
  { index: 13, name: "DDO_City", dataType: "CHAR", length: 25, mandatory: "M", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", remarks: "Applicable for FY2025-26 and before only." },
  { index: 14, name: "DDO_State", dataType: "CHAR", length: 2, mandatory: "M", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", annexureRef: "state", remarks: "Applicable for FY2025-26 and before only." },
  { index: 15, name: "DDO_Address PIN", dataType: "CHAR", length: 6, mandatory: "M", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", pattern: "^\\d{6}$", remarks: "Applicable for FY2025-26 and before only. Must be >= 110001." },
  { index: 16, name: "TDS/TCS deducted Amount", dataType: "DECIMAL", length: 15, mandatory: "M", fyScope: "always", remarks: "Sum of TDS/TCS, education cess and surcharge for the nature of deduction, per DDO per month. Must be >= 0.00." },
  { index: 17, name: "Form Type", dataType: "CHAR", mandatory: "M", fyScope: "f137", annexureRef: "formType", remarks: "One of F138, F140, F143, F144. Applicable from FY2026-27 onward." },
  { index: 18, name: "DDO registration no.", dataType: "CHAR", length: 10, mandatory: "O", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", remarks: "From Central Record Keeping Agency, if available. FY2025-26 and before only." },
  { index: 19, name: "DDO code", dataType: "CHAR", length: 20, mandatory: "O", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", remarks: "FY2025-26 and before only." },
  { index: 20, name: "E-mail ID of DDO", dataType: "CHAR", length: 75, mandatory: "O", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", remarks: "FY2025-26 and before only." },
  { index: 21, name: "Total TDS/TCS remitted to Government account (AG/Pr CCA)", dataType: "DECIMAL", length: 15, mandatory: "M", fyScope: "always", remarks: "Sum of TDS/TCS, cess and surcharge remitted, per DDO per month. Must be >= 0.00." },
  { index: 22, name: "Nature of deduction", dataType: "CHAR", length: 4, mandatory: "M", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", annexureRef: "natureOfPayment", remarks: "Per Annexure 5. FY2025-26 and before only." },
  { index: 23, name: "DDO Mapping/ update", dataType: "CHAR", length: 1, mandatory: "O", mandatoryByFyScope: { f137: "NA" }, fyScope: "always", annexureRef: "ddoMapping", remarks: "Per Annexure 6 (A/D/U). FY2025-26 and before only." },
  { index: 24, name: "DDO serial no.", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified. For future use." },
  { index: 25, name: "Filler 8", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified for Original statements. For future use." },
  { index: 26, name: "Filler 9", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified for Original statements. For future use." },
  { index: 27, name: "Filler 10", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified for Original statements. For future use." },
  { index: 28, name: "Filler 11", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified for Original statements. For future use." },
  { index: 29, name: "Filler 12", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified for Original statements. For future use." },
  { index: 30, name: "Filler 13", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified. For future use." },
  { index: 31, name: "Filler 14", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified. For future use." },
  { index: 32, name: "Filler 15", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified. For future use." },
  { index: 33, name: "Transaction Detail Record Hash", dataType: "NA", mandatory: "NA", fyScope: "always", remarks: "No value should be specified. Computed by the FVU." },
];
