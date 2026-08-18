import type { DdoRecord } from "@prisma/client";

/**
 * Prisma's Decimal fields serialize to strings over JSON (decimal.js's
 * toJSON() returns a string, not a number), which breaks re-submitting an
 * unedited amount field through a zod `z.number()` schema. Normalize to
 * plain numbers before sending a DdoRecord to the client.
 */
export function serializeDdoRecord<T extends DdoRecord>(record: T) {
  return {
    ...record,
    taxDeducted: Number(record.taxDeducted),
    totalRemitted: Number(record.totalRemitted),
  };
}
