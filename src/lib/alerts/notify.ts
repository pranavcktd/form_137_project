import nodemailer from "nodemailer";
import type { FvuVersionCheckResult } from "./checkFvuVersion";

interface ResolvedSmtpConfig {
  host: string;
  port: number;
  user: string | null;
  password: string | null;
}

/** Platform-level SMTP (super admin's own settings, via Profile), falling back to env vars. */
async function getPlatformSmtpConfig(): Promise<ResolvedSmtpConfig | null> {
  const { prisma } = await import("@/lib/prisma");
  const settings = await prisma.platformSettings.findUnique({ where: { id: "singleton" } });

  const host = settings?.smtpHost || process.env.SMTP_HOST;
  if (!host) return null;

  return {
    host,
    port: settings?.smtpPort || (process.env.SMTP_PORT ? Number(process.env.SMTP_PORT) : 587),
    user: settings?.smtpUser || process.env.SMTP_USER || null,
    password: settings?.smtpPassword || process.env.SMTP_PASSWORD || null,
  };
}

/**
 * A firm's own SMTP (set by their ADMIN via Profile) — used for mail the
 * firm itself sends (password resets for their own staff, and any future
 * mail to their own Clients). Falls back to the platform's SMTP, then env
 * vars, so a firm that hasn't configured its own still gets email delivery.
 */
async function getOrgSmtpConfig(organizationId: string): Promise<ResolvedSmtpConfig | null> {
  const { prisma } = await import("@/lib/prisma");
  const org = await prisma.organization.findUnique({ where: { id: organizationId } });
  if (org?.smtpHost) {
    return {
      host: org.smtpHost,
      port: org.smtpPort || 587,
      user: org.smtpUser || null,
      password: org.smtpPassword || null,
    };
  }
  return getPlatformSmtpConfig();
}

async function getAlertEmailTo(): Promise<string | null> {
  const { prisma } = await import("@/lib/prisma");
  const settings = await prisma.platformSettings.findUnique({ where: { id: "singleton" } });
  return settings?.alertEmailTo || process.env.ALERT_EMAIL_TO || null;
}

async function getSlackWebhookUrl(): Promise<string | null> {
  const { prisma } = await import("@/lib/prisma");
  const settings = await prisma.platformSettings.findUnique({ where: { id: "singleton" } });
  return settings?.slackWebhookUrl || process.env.SLACK_WEBHOOK_URL || null;
}

interface EmailAttachment {
  filename: string;
  content: Buffer;
}

async function sendViaSmtp(
  config: ResolvedSmtpConfig,
  to: string,
  subject: string,
  text: string,
  attachments?: EmailAttachment[],
): Promise<void> {
  const transport = nodemailer.createTransport({
    host: config.host,
    port: config.port,
    secure: false,
    auth: config.user ? { user: config.user, pass: config.password ?? undefined } : undefined,
  });

  await transport.sendMail({
    from: config.user || "no-reply@nex.local",
    to,
    subject,
    text,
    attachments,
  });
}

/** FVU version-update alerts are a platform-level concern — always uses the platform's own SMTP/Slack config. */
export async function sendVersionAlert(result: FvuVersionCheckResult): Promise<void> {
  const subject = `Form 137/24G FVU update available: v${result.detectedVersion} (currently supporting v${result.supportedVersion})`;
  const body =
    `A newer FVU version has been detected on Protean's Form 137 page.\n\n` +
    `Detected version: ${result.detectedVersion}\n` +
    `Currently supported version: ${result.supportedVersion}\n` +
    `Source: ${result.sourceUrl}\n\n` +
    `Review the new file format/FVU before it becomes mandatory, and add a new ` +
    `/src/schemas/24g-f137/v${(result.detectedVersion ?? "").replace(/\./g, "_")}/ ` +
    `config + generator per the versioning strategy.`;

  await Promise.all([sendPlatformAlertEmail(subject, body), sendSlackAlert(subject, body)]);
}

/**
 * A firm self-initiated a renewal but no payment gateway is configured yet —
 * a platform-level concern (like the FVU alert), since it's the super admin
 * who needs to collect payment offline and confirm it via Record Payment.
 */
export async function sendRenewalRequestAlert(
  organizationName: string,
  applicationLabel: string,
  amount: number,
  billingCycle: string,
): Promise<void> {
  const subject = `Renewal requested: ${organizationName} — ${applicationLabel}`;
  const body =
    `${organizationName} requested to renew their ${applicationLabel} subscription from their dashboard.\n\n` +
    `Amount due: ₹${amount.toFixed(2)} (${billingCycle === "MONTHLY" ? "monthly" : "yearly"})\n\n` +
    `No payment gateway is configured yet, so this needs to be collected offline (bank transfer/UPI/etc) ` +
    `and confirmed from that firm's page in the admin panel — Products & Subscriptions → Record Payment.`;

  await Promise.all([sendPlatformAlertEmail(subject, body), sendSlackAlert(subject, body)]);
}

async function sendPlatformAlertEmail(subject: string, body: string): Promise<void> {
  const [config, alertEmailTo] = await Promise.all([getPlatformSmtpConfig(), getAlertEmailTo()]);
  if (!config || !alertEmailTo) return;
  await sendViaSmtp(config, alertEmailTo, subject, body);
}

/**
 * Emails a user's reset temporary password to their own address, using
 * their own firm's SMTP config (falling back to the platform's, then env
 * vars). Returns whether an email was actually sent — SMTP is optional in
 * this app, so callers should fall back to showing the temporary password
 * directly in the UI when this returns false.
 *
 * `keepsOldPasswordValid` reflects how the caller actually stored it: an
 * admin-triggered reset overwrites the password immediately, but a
 * self-service "forgot password" one is held as a pending alternative — the
 * old password still works until this temporary one is actually used (see
 * lib/auth.ts) — so the email should say so accurately rather than falsely
 * claiming the old password stopped working right away.
 */
export async function sendPasswordResetEmail(
  toEmail: string,
  toName: string,
  temporaryPassword: string,
  organizationId: string,
  keepsOldPasswordValid = false,
): Promise<boolean> {
  const config = await getOrgSmtpConfig(organizationId);
  if (!config) return false;

  const validityNote = keepsOldPasswordValid
    ? "Your existing password still works until you sign in with this one — once you do, you'll be required to set a new password."
    : "You'll be required to set a new password the next time you sign in.";

  await sendViaSmtp(
    config,
    toEmail,
    "Your Nex password has been reset",
    `Hi ${toName},\n\n` +
      `A temporary password was issued for your Nex account (either by your administrator, or via "Forgot password?" at login).\n\n` +
      `Email: ${toEmail}\n` +
      `Temporary password: ${temporaryPassword}\n\n` +
      `${validityNote} If you didn't request this, contact your administrator.`,
  );

  return true;
}

/**
 * Emails a filed return's details to the Client's own responsible-person address
 * (the government office contact, not a Nex login) — the firm's own SMTP, since
 * this is correspondence the firm sends to the entity it files for, same as a
 * staff password reset. Returns whether it actually sent, so the caller (an
 * explicit button click, not a background job) can show a real error rather
 * than silently claiming success.
 */
export async function sendFilingReturnEmail(params: {
  toEmail: string;
  toName: string;
  organizationId: string;
  departmentName: string;
  ain: string;
  financialYear: number;
  month: number;
  monthLabel: string;
  statementType: string;
  receiptNumber: string;
  receiptDate: string;
  attachments: EmailAttachment[];
}): Promise<boolean> {
  const config = await getOrgSmtpConfig(params.organizationId);
  if (!config) return false;

  const fyLabel = `FY ${params.financialYear}-${String((params.financialYear + 1) % 100).padStart(2, "0")}`;

  await sendViaSmtp(
    config,
    params.toEmail,
    `Form 137/24G filed — ${fyLabel}, ${params.monthLabel} (${params.departmentName})`,
    `Hi ${params.toName},\n\n` +
      `This is to inform you that the Form 137/24G statement for ${params.departmentName} (AIN ${params.ain}) ` +
      `has been filed for ${fyLabel}, ${params.monthLabel} (${params.statementType}).\n\n` +
      `Receipt / Acknowledgement Number: ${params.receiptNumber}\n` +
      `Receipt Date: ${params.receiptDate}\n\n` +
      `The attached Excel sheet lists every DDO transaction included in this filing` +
      `${params.attachments.length > 1 ? ", along with the filing receipt" : ""}.`,
    params.attachments,
  );

  return true;
}

async function sendSlackAlert(subject: string, body: string): Promise<void> {
  const webhookUrl = await getSlackWebhookUrl();
  if (!webhookUrl) return;

  await fetch(webhookUrl, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ text: `*${subject}*\n${body}` }),
  });
}
