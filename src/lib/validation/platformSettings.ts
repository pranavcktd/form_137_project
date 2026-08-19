import { z } from "zod";

export const platformSettingsSchema = z.object({
  smtpHost: z.string().optional().or(z.literal("")),
  smtpPort: z.coerce.number().int().min(1).max(65535).optional().or(z.literal("")),
  smtpUser: z.string().optional().or(z.literal("")),
  // Empty string means "leave the currently saved password unchanged" —
  // there's no way to tell "clear it" from "didn't touch this field"
  // apart, and clearing isn't a meaningful action here anyway.
  smtpPassword: z.string().optional().or(z.literal("")),
  alertEmailTo: z.string().email("Enter a valid email address").optional().or(z.literal("")),
  slackWebhookUrl: z.string().url("Enter a valid URL").optional().or(z.literal("")),
  hideUnsubscribedModules: z.boolean().optional(),
  razorpayKeyId: z.string().optional().or(z.literal("")),
  // Same "blank means leave unchanged" rule as smtpPassword — these are never
  // round-tripped back to the client once saved, only a "configured" flag is.
  razorpayKeySecret: z.string().optional().or(z.literal("")),
  razorpayWebhookSecret: z.string().optional().or(z.literal("")),
});

export type PlatformSettingsInput = z.infer<typeof platformSettingsSchema>;
