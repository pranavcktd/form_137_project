import nodemailer from "nodemailer";
import type { FvuVersionCheckResult } from "./checkFvuVersion";

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

  await Promise.all([sendEmailAlert(subject, body), sendSlackAlert(subject, body)]);
}

async function sendEmailAlert(subject: string, body: string): Promise<void> {
  const { SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASSWORD, ALERT_EMAIL_TO } = process.env;
  if (!SMTP_HOST || !ALERT_EMAIL_TO) return;

  const transport = nodemailer.createTransport({
    host: SMTP_HOST,
    port: SMTP_PORT ? Number(SMTP_PORT) : 587,
    secure: false,
    auth: SMTP_USER ? { user: SMTP_USER, pass: SMTP_PASSWORD } : undefined,
  });

  await transport.sendMail({
    from: SMTP_USER || "no-reply@form137-efiling.local",
    to: ALERT_EMAIL_TO,
    subject,
    text: body,
  });
}

async function sendSlackAlert(subject: string, body: string): Promise<void> {
  const webhookUrl = process.env.SLACK_WEBHOOK_URL;
  if (!webhookUrl) return;

  await fetch(webhookUrl, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ text: `*${subject}*\n${body}` }),
  });
}
