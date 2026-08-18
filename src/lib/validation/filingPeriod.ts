import { z } from "zod";
import { isFutureFilingPeriod } from "@/lib/financialYear";

export const filingPeriodSchema = z
  .object({
    financialYear: z
      .number()
      .int()
      .min(2005, "Financial year must be 2005 or later"),
    month: z.number().int().min(1).max(12),
    statementType: z.enum(["ORIGINAL", "CORRECTION_M", "CORRECTION_X"]),
  })
  .refine((data) => !isFutureFilingPeriod(data.financialYear, data.month), {
    message:
      "This filing period is in the future — a return can't be filed for a month that hasn't happened yet.",
    path: ["month"],
  });

export type FilingPeriodInput = z.infer<typeof filingPeriodSchema>;
