import type { FieldDef, RecordFieldDefs } from "@/schemas/24g-f137/types";
import { formatDecimal2 } from "./formatters";

export type FieldValues = Record<string, string>;

/**
 * Renders one ^-delimited record line (no trailing caret beyond what a
 * blank final field naturally produces, no CRLF) from a resolved field
 * list and a map of value-bearing field name -> raw value.
 *
 * Fields with a `constant` (from the schema config or FY overlay) always
 * win; fields marked `mandatory: "NA"` are always blank; everything else is
 * looked up from `values` by field name.
 */
export function renderRecord(
  fields: RecordFieldDefs,
  lineNumber: number,
  values: FieldValues,
): string {
  const cells = fields.map((field) => renderCell(field, lineNumber, values));
  return cells.join("^");
}

function renderCell(field: FieldDef, lineNumber: number, values: FieldValues): string {
  if (field.name === "Line Number") {
    return String(lineNumber);
  }
  if (field.constant !== undefined) {
    return field.constant;
  }
  if (field.mandatory === "NA") {
    return "";
  }

  const raw = values[field.name];
  if (raw === undefined || raw === null || raw === "") return "";

  if (field.dataType === "DECIMAL") {
    return formatDecimal2(raw);
  }
  return raw;
}

export function terminateLine(line: string): string {
  return `${line}\r\n`;
}
