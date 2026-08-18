/**
 * What each Form Type code actually means, per the FVU spec (F138/F140/F143/F144
 * map to the familiar 24Q/26Q/27EQ/27Q quarterly TDS/TCS return types) — shown
 * alongside the raw code everywhere it's reported so it's readable without
 * having to memorize the mapping.
 */
export const FORM_TYPE_LABELS: Record<string, string> = {
  F138: "TDS on Salary",
  F140: "TDS on Non-Salary",
  F143: "TCS",
  F144: "TDS on Payments to Non-Residents / Foreign Entities",
};

/** e.g. "F138 — TDS on Salary" for a recognized code; the bare code (or "") otherwise. */
export function formTypeLabel(code: string | null | undefined): string {
  if (!code) return "";
  const label = FORM_TYPE_LABELS[code];
  return label ? `${code} — ${label}` : code;
}
