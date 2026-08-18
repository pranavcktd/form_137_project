export type GovtCategory = "CENTRAL" | "STATE";
export type DdoMode = "ADD" | "UPDATE" | "DELETE" | "NO_CHANGE";
export type StatementType = "ORIGINAL" | "CORRECTION_M" | "CORRECTION_X";

export interface OrganizationInput {
  ain: string;
  tan: string | null;
  ministryName: string | null;
  subMinistryName: string | null;
  departmentName: string;
  govtCategory: GovtCategory;
  countryCode: string | null;
  responsiblePersonName: string | null;
  responsiblePersonFirstName: string | null;
  responsiblePersonMiddleName: string | null;
  responsiblePersonLastName: string | null;
  responsiblePersonDesignation: string;
  responsiblePersonAddress1: string;
  responsiblePersonAddress2: string | null;
  responsiblePersonAddress3: string | null;
  responsiblePersonAddress4: string | null;
  responsiblePersonCity: string;
  responsiblePersonState: string;
  responsiblePersonPin: string;
  responsiblePersonStdCode: string | null;
  responsiblePersonPhone: string | null;
  responsiblePersonMobile: string | null;
  responsiblePersonEmail: string;
}

export interface FilingPeriodInput {
  financialYear: number;
  month: number;
  statementType: StatementType;
  /** Required for Correction_M/X: the PRN of the return being corrected. */
  originalRrrNo?: string;
  previousRrrNo?: string;
  /** Correction_M only: whether the batch itself needs updating (1) or not (0). */
  batchUpdationIndicator?: 0 | 1;
}

export interface DdoRecordInput {
  serialNo: number;
  tan: string;
  name: string;
  address1: string | null;
  address2: string | null;
  address3: string | null;
  address4: string | null;
  city: string | null;
  state: string | null;
  pin: string | null;
  ddoRegNo: string | null;
  ddoCode: string | null;
  email: string | null;
  taxDeducted: number;
  formType: string | null;
  totalRemitted: number;
  natureOfDeduction: string | null;
  mode: DdoMode;
  /** Correction_M "update" rows carry the pre-update values being replaced. */
  lastDdoTan?: string | null;
  lastTotalRemitted?: number | null;
  lastDdoRegNo?: string | null;
  lastDdoCode?: string | null;
  lastTaxDeducted?: number | null;
  lastNatureOfDeduction?: string | null;
}
