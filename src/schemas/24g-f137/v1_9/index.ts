import type { FieldDef, FyScope, RecordFieldDefs } from "../types";
import { fhFields } from "./original/fh";
import { bhFields } from "./original/bh";
import { tdFields } from "./original/td";
import { correctionM } from "./variants/correctionM";
import { correctionX } from "./variants/correctionX";

export type StatementType = "ORIGINAL" | "CORRECTION_M" | "CORRECTION_X";
export type RecordType = "FH" | "BH" | "TD";

const BASE_FIELDS: Record<RecordType, RecordFieldDefs> = {
  FH: fhFields,
  BH: bhFields,
  TD: tdFields,
};

const VARIANT_OVERLAYS = {
  ORIGINAL: null,
  CORRECTION_M: correctionM,
  CORRECTION_X: correctionX,
} as const;

/**
 * Resolves the effective field list for a record type under a given
 * statement type (Original / Correction_M / Correction_X) and FY regime
 * (legacy 24G vs F137), collapsing the per-FY `constantByFyScope` /
 * `mandatoryByFyScope` overrides into a single concrete `constant` /
 * `mandatory` value per field.
 *
 * Returns null when the record type doesn't exist for the statement type
 * (Correction_X carries no TD records).
 */
export function resolveFields(
  recordType: RecordType,
  statementType: StatementType,
  fyScope: Exclude<FyScope, "always">,
): RecordFieldDefs | null {
  const overlay = VARIANT_OVERLAYS[statementType];
  const overlayForRecord =
    recordType === "FH"
      ? overlay?.fh
      : recordType === "BH"
        ? overlay?.bh
        : overlay?.td;

  if (statementType === "CORRECTION_X" && recordType === "TD") {
    return null;
  }

  const base = BASE_FIELDS[recordType];

  // Every field occupies a fixed position in the record regardless of FY
  // regime (this is a positional, ^-delimited format) — a field whose scope
  // doesn't match the requested regime isn't dropped, it's just forced blank
  // (mandatory "NA") for that regime while still holding its slot.
  return base.map((field): FieldDef => {
    const override = overlayForRecord?.fields[field.index] ?? {};
    const merged: FieldDef = { ...field, ...override };

    if (merged.fyScope !== "always" && merged.fyScope !== fyScope) {
      return { ...merged, mandatory: "NA", constant: undefined };
    }

    const constant = merged.constantByFyScope
      ? (merged.constantByFyScope[fyScope] ?? merged.constant)
      : merged.constant;

    const mandatory = merged.mandatoryByFyScope
      ? (merged.mandatoryByFyScope[fyScope] ?? merged.mandatory)
      : merged.mandatory;

    return { ...merged, constant, mandatory };
  });
}

export function fyScopeForFinancialYear(financialYear: number): Exclude<FyScope, "always"> {
  return financialYear >= 2026 ? "f137" : "legacy";
}
