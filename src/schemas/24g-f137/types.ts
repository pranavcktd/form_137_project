export type FieldDataType =
  | "CHAR"
  | "INTEGER"
  | "DECIMAL"
  | "ALPHANUMERIC"
  | "DATE"
  | "NA";

export type Mandatory = "M" | "O" | "NA";

/**
 * Which financial-year regime a field (or an override) applies to.
 * "legacy"  -> FY2025-26 and earlier (File Type "24G")
 * "f137"    -> FY2026-27 onward (File Type "F137")
 * "always"  -> applies under both regimes
 */
export type FyScope = "legacy" | "f137" | "always";

export interface FieldDef {
  index: number;
  name: string;
  dataType: FieldDataType;
  /** Field length; a tuple represents [precision, scale] for DECIMAL fields (e.g. 15,2). */
  length?: number | [number, number];
  mandatory: Mandatory;
  /** Use when a field exists under both regimes but its mandatory/optional/NA strength differs by regime. */
  mandatoryByFyScope?: Partial<Record<Exclude<FyScope, "always">, Mandatory>>;
  /** Fixed literal value the field must hold (e.g. Record Type "FH"). */
  constant?: string;
  /** Use instead of `constant` when the fixed value itself differs by FY regime (e.g. File Type "24G" vs "F137"). */
  constantByFyScope?: Partial<Record<Exclude<FyScope, "always">, string>>;
  fyScope: FyScope;
  /** Key into the /annexures/*.json lookup tables, for enum-style validation. */
  annexureRef?: string;
  /** Regex the field value must satisfy, when not driven by an annexure. */
  pattern?: string;
  /** Minimum numeric value, for INTEGER/DECIMAL fields with an explicit floor (e.g. count >= 1). */
  minValue?: number;
  remarks: string;
}

export type RecordFieldDefs = FieldDef[];

export interface RecordTypeOverride {
  /** Overrides keyed by field index; only fields that differ from the Original variant need an entry. */
  fields: Record<number, Partial<FieldDef>>;
}

export interface StatementVariantConfig {
  fh: RecordTypeOverride;
  bh: RecordTypeOverride;
  /** Correction_X carries no TD records at all. */
  td: RecordTypeOverride | null;
}
