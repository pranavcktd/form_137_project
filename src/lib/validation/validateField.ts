import type { FieldDef } from "@/schemas/24g-f137/types";
import { getAnnexureCodes, type AnnexureKey } from "./annexureLookup";

export interface FieldError {
  fieldIndex: number;
  fieldName: string;
  message: string;
}

/**
 * Validates one raw value against a resolved field definition. Constants
 * and NA/reserved fields are never user-editable, so they're skipped here —
 * the generator is what's responsible for emitting them correctly.
 */
export function validateField(field: FieldDef, rawValue: string | null | undefined): FieldError[] {
  if (field.name === "Line Number" || field.constant !== undefined || field.mandatory === "NA") {
    return [];
  }

  const value = (rawValue ?? "").trim();
  const errors: FieldError[] = [];

  if (!value) {
    if (field.mandatory === "M") {
      errors.push(fieldError(field, `${field.name} is required.`));
    }
    return errors;
  }

  if (typeof field.length === "number" && field.dataType !== "DECIMAL") {
    if (value.length > field.length) {
      errors.push(
        fieldError(
          field,
          `${field.name} must be at most ${field.length} characters. You entered "${value}" (${value.length} characters).`,
        ),
      );
    }
  }

  if (field.dataType === "INTEGER") {
    if (!/^\d+$/.test(value)) {
      errors.push(
        fieldError(field, `${field.name} must be a whole number. You entered "${value}".`),
      );
    } else if (field.minValue !== undefined && Number(value) < field.minValue) {
      errors.push(
        fieldError(field, `${field.name} must be >= ${field.minValue}. You entered ${value}.`),
      );
    }
  }

  if (field.dataType === "DECIMAL") {
    if (!/^\d+(\.\d{1,2})?$/.test(value)) {
      errors.push(
        fieldError(
          field,
          `${field.name} must be a decimal with up to 2 decimal places. You entered "${value}".`,
        ),
      );
    } else if (field.minValue !== undefined && Number(value) < field.minValue) {
      errors.push(
        fieldError(
          field,
          `${field.name} must be >= ${field.minValue.toFixed(2)}. You entered ${value}.`,
        ),
      );
    }
  }

  if (field.pattern && !new RegExp(field.pattern).test(value)) {
    errors.push(
      fieldError(field, `${field.name} is not in a valid format. You entered "${value}".`),
    );
  }

  if (field.annexureRef) {
    const validCodes = getAnnexureCodes(field.annexureRef as AnnexureKey);
    if (!validCodes.includes(value)) {
      errors.push(
        fieldError(
          field,
          `${field.name} must be one of: ${validCodes.join(", ")}. You entered "${value}".`,
        ),
      );
    }
  }

  return errors;
}

function fieldError(field: FieldDef, message: string): FieldError {
  return { fieldIndex: field.index, fieldName: field.name, message };
}
