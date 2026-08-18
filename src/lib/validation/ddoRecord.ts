import { z } from "zod";
import { isValidTan } from "./tan";

export const ddoRecordSchema = z.object({
  ddoMasterId: z.string().min(1, "Select a DDO from the master list"),
  tan: z
    .string()
    .regex(/^[A-Z0-9]{4}[0-9]{5}[A-Z]$/, "TAN must be 4 letters/digits, 5 digits, 1 letter")
    .refine(isValidTan, "Invalid TAN: unrecognized jurisdiction code or bad check letter"),
  name: z.string().min(1, "Name is required"),
  address1: z.string().optional().or(z.literal("")),
  address2: z.string().optional().or(z.literal("")),
  address3: z.string().optional().or(z.literal("")),
  address4: z.string().optional().or(z.literal("")),
  city: z.string().optional().or(z.literal("")),
  state: z.string().optional().or(z.literal("")),
  pin: z.string().optional().or(z.literal("")),
  ddoRegNo: z.string().optional().or(z.literal("")),
  ddoCode: z.string().optional().or(z.literal("")),
  email: z.string().optional().or(z.literal("")),
  // Coerced rather than a plain z.number(): values loaded back from the API
  // for editing arrive as strings (Prisma's Decimal type serializes to a
  // string over JSON), so a strict z.number() would reject an unedited
  // amount field on save.
  taxDeducted: z.coerce.number().min(0, "Must be >= 0"),
  formType: z.string().optional().or(z.literal("")),
  totalRemitted: z.coerce.number().min(0, "Must be >= 0"),
  natureOfDeduction: z.string().optional().or(z.literal("")),
  mode: z.enum(["ADD", "UPDATE", "DELETE", "NO_CHANGE"]),
});

export type DdoRecordFormInput = z.infer<typeof ddoRecordSchema>;
