import { z } from "zod";
import { isValidAin } from "./ain";
import { isValidTan } from "./tan";

export const ainSchema = z
  .string()
  .regex(/^\d{7}$/, "AIN must be exactly 7 digits")
  .refine(isValidAin, "Invalid AIN: the 7th digit must be (first 6 digits mod 7)");

export const tanSchema = z
  .string()
  .regex(/^[A-Z0-9]{4}[0-9]{5}[A-Z]$/, "TAN must be 4 letters/digits, 5 digits, 1 letter (e.g. MUMD12345A)")
  .refine(isValidTan, "Invalid TAN: unrecognized jurisdiction code or bad check letter");

/** Profile fields for a Client (the actual AIN/TAN filing entity a firm files returns for). */
export const clientProfileSchema = z.object({
  ain: ainSchema,
  tan: tanSchema.optional().or(z.literal("")),
  ministryName: z.string().optional().or(z.literal("")),
  subMinistryName: z.string().optional().or(z.literal("")),
  departmentName: z.string().min(1, "Department name is required"),
  govtCategory: z.enum(["CENTRAL", "STATE"]),

  countryCode: z.string().optional().or(z.literal("")),
  responsiblePersonName: z.string().optional().or(z.literal("")),
  responsiblePersonFirstName: z.string().optional().or(z.literal("")),
  responsiblePersonMiddleName: z.string().optional().or(z.literal("")),
  responsiblePersonLastName: z.string().optional().or(z.literal("")),
  responsiblePersonDesignation: z.string().min(1, "Designation is required"),
  responsiblePersonAddress1: z.string().min(1, "Address line 1 is required"),
  responsiblePersonAddress2: z.string().optional().or(z.literal("")),
  responsiblePersonAddress3: z.string().optional().or(z.literal("")),
  responsiblePersonAddress4: z.string().optional().or(z.literal("")),
  responsiblePersonCity: z.string().min(1, "City is required"),
  responsiblePersonState: z.string().min(1, "State is required"),
  responsiblePersonPin: z
    .string()
    .regex(/^\d{6}$/, "PIN must be 6 digits")
    .refine((v) => v !== "000000" && v !== "999999", "PIN cannot be 000000 or 999999"),
  responsiblePersonStdCode: z.string().optional().or(z.literal("")),
  responsiblePersonPhone: z.string().optional().or(z.literal("")),
  responsiblePersonMobile: z
    .string()
    .regex(/^\d{10}$/, "Mobile number must be 10 digits")
    .optional()
    .or(z.literal("")),
  responsiblePersonEmail: z.string().email("Enter a valid email address"),
});

export type ClientProfileInput = z.infer<typeof clientProfileSchema>;

/** Platform-admin-only: onboarding a new Tax Professional firm + its first admin user. */
export const firmOnboardingSchema = z.object({
  firmName: z.string().min(1, "Firm name is required"),
  contactEmail: z.string().email("Enter a valid email address").optional().or(z.literal("")),
  contactPhone: z.string().optional().or(z.literal("")),
  adminName: z.string().min(1, "Admin name is required"),
  adminEmail: z.string().email("Enter a valid email address"),
  adminPassword: z.string().min(8, "Password must be at least 8 characters"),
});

export type FirmOnboardingInput = z.infer<typeof firmOnboardingSchema>;
