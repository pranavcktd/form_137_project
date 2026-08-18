import { resolveFields, fyScopeForFinancialYear } from "@/schemas/24g-f137/v1_9";
import type {
  DdoRecordInput,
  FilingPeriodInput,
  OrganizationInput,
} from "@/lib/generator/types";
import { buildBhValues, buildFhValues, buildTdValues } from "@/lib/generator/buildValues";
import { validateRecord } from "./validateRecord";
import type { FieldError } from "./validateField";

export interface StatementValidationResult {
  fhErrors: FieldError[];
  bhErrors: FieldError[];
  /** Errors per DDO record, indexed the same as the input `ddoRecords` array. */
  tdErrors: FieldError[][];
  isValid: boolean;
}

export function validateStatement(
  organization: OrganizationInput,
  filingPeriod: FilingPeriodInput,
  ddoRecords: DdoRecordInput[],
): StatementValidationResult {
  const fyScope = fyScopeForFinancialYear(filingPeriod.financialYear);
  const statementType = filingPeriod.statementType;

  const fhFields = resolveFields("FH", statementType, fyScope)!;
  const bhFields = resolveFields("BH", statementType, fyScope)!;
  const tdFields = resolveFields("TD", statementType, fyScope);

  const fhErrors = validateRecord(fhFields, buildFhValues(organization));
  const bhErrors = validateRecord(
    bhFields,
    buildBhValues(organization, filingPeriod, ddoRecords, tdFields),
  );

  const tdErrors = tdFields
    ? ddoRecords.map((record) => validateRecord(tdFields, buildTdValues(record)))
    : [];

  if (statementType !== "CORRECTION_X" && ddoRecords.length === 0) {
    bhErrors.push({
      fieldIndex: 0,
      fieldName: "DDO Records",
      message: "At least one DDO record is required.",
    });
  }

  // Cross-field rules the FVU enforces that aren't expressible as a single
  // field's mandatory/optional flag. The "State name" one was found by
  // running a generated statement through the real FVU (error F137/F24G-FV-2134).
  if (organization.govtCategory === "STATE" && !organization.responsiblePersonState) {
    bhErrors.push({
      fieldIndex: 47,
      fieldName: "State name",
      message: "State name is required when AO Category is State Government.",
    });
  }
  if (organization.govtCategory === "CENTRAL" && !organization.ministryName) {
    bhErrors.push({
      fieldIndex: 48,
      fieldName: "Ministry/ Department name",
      message: "Ministry/ Department name is required when AO Category is Central Government.",
    });
  }
  if (organization.govtCategory === "CENTRAL" && !organization.subMinistryName) {
    bhErrors.push({
      fieldIndex: 49,
      fieldName: "Sub Ministry name",
      message: "Sub Ministry name is required when AO Category is Central Government.",
    });
  }

  const isValid =
    fhErrors.length === 0 &&
    bhErrors.length === 0 &&
    tdErrors.every((errors) => errors.length === 0);

  return { fhErrors, bhErrors, tdErrors, isValid };
}
