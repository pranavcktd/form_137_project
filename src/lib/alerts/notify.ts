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

async function sendViaSmtp(config: ResolvedSmtpConfig, to: string, subject: string, text: string): Promise<void> {
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
 */
export async function sendPasswordResetEmail(
  toEmail: string,
  toName: string,
  temporaryPassword: string,
  organizationId: string,
): Promise<boolean> {
  const config = await getOrgSmtpConfig(organizationId);
  if (!config) return false;

  await sendViaSmtp(
    config,
    toEmail,
    "Your Nex password has been reset",
    `Hi ${toName},\n\n` +
      `Your password has been reset by your administrator.\n\n` +
      `Email: ${toEmail}\n` +
      `Temporary password: ${temporaryPassword}\n\n` +
      `You'll be required to set a new password the next time you sign in.`,
  );

  return true;
}

/**
 * Emails a self-service "forgot password" link, using the user's own
 * firm's SMTP config (falling back to the platform's, then env vars).
 * Returns whether an email was actually sent — if no SMTP is configured
 * anywhere, self-service reset simply isn't available and the caller
 * should tell the user to contact an admin instead.
 */
export async function sendPasswordResetLinkEmail(
  toEmail: string,
  toName: string,
  resetUrl: string,
  organizationId: string,
): Promise<boolean> {
  const config = await getOrgSmtpConfig(organizationId);
  if (!config) return false;

  await sendViaSmtp(
    config,
    toEmail,
    "Reset your Nex password",
    `Hi ${toName},\n\n` +
      `Someone requested a password reset for your Nex account (${toEmail}).\n\n` +
      `Reset your password: ${resetUrl}\n\n` +
      `This link expires in 30 minutes and can only be used once. If you didn't request this, you can ignore this email — your password won't be changed.`,
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
