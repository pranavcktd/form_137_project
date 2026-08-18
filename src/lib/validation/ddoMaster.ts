import { z } from "zod";
import { isValidTan } from "./tan";

export const ddoMasterSchema = z.object({
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
});

export type DdoMasterFormInput = z.infer<typeof ddoMasterSchema>;
