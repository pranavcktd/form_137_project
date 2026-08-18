import { resolveFields, fyScopeForFinancialYear } from "@/schemas/24g-f137/v1_9";
import type { StatementType } from "@/schemas/24g-f137/v1_9";
import type { FieldDef } from "@/schemas/24g-f137/types";

// Structural/mode fields handled outside the plain data columns: Line
// Number and Serial No. are server-assigned, Revision Mode / DDO
// Mapping/update are driven by the record's "mode" rather than typed in
// directly.
const NON_DATA_FIELDS = new Set([
  "Line Number",
  "Serial No.",
  "Revision Mode",
  "DDO Mapping/ update",
]);

/**
 * The TD fields a preparer actually fills in for a given filing period —
 * derived from the resolved schema (no hardcoded indices), so the Excel
 * template and importer automatically track FY2025-26 vs FY2026-27 field
 * differences.
 */
export function ddoEditableFields(financialYear: number, statementType: StatementType): FieldDef[] {
  const fyScope = fyScopeForFinancialYear(financialYear);
  const fields = resolveFields("TD", statementType, fyScope) ?? [];
  return fields.filter(
    (field) => field.mandatory !== "NA" && field.constant === undefined && !NON_DATA_FIELDS.has(field.name),
  );
}
