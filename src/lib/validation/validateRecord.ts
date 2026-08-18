import type { RecordFieldDefs } from "@/schemas/24g-f137/types";
import type { FieldValues } from "@/lib/generator/render";
import { validateField, type FieldError } from "./validateField";

export function validateRecord(fields: RecordFieldDefs, values: FieldValues): FieldError[] {
  return fields.flatMap((field) => validateField(field, values[field.name]));
}
