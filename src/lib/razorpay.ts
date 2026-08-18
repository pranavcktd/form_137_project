import crypto from "crypto";

interface RazorpayConfig {
  keyId: string;
  keySecret: string;
}

async function getRazorpayConfig(): Promise<RazorpayConfig | null> {
  const { prisma } = await import("@/lib/prisma");
  const settings = await prisma.platformSettings.findUnique({ where: { id: "singleton" } });
  if (!settings?.razorpayKeyId || !settings?.razorpayKeySecret) return null;
  return { keyId: settings.razorpayKeyId, keySecret: settings.razorpayKeySecret };
}

export async function isRazorpayConfigured(): Promise<boolean> {
  return (await getRazorpayConfig()) !== null;
}

/**
 * Creates a Razorpay hosted Payment Link for a one-off subscription charge
 * (onboarding or a manual renewal) — no recurring mandate is set up, matching
 * the "admin/super-admin renews explicitly each period" billing model. Uses
 * Razorpay's plain REST API over fetch (Basic Auth with the key id/secret)
 * rather than their SDK, since this is the only call site.
 */
export async function createPaymentLink(params: {
  amountRupees: number;
  description: string;
  customerName: string;
  customerEmail: string;
  referenceId: string;
}): Promise<{ id: string; shortUrl: string }> {
  const config = await getRazorpayConfig();
  if (!config) {
    throw new Error("Razorpay isn't configured yet — add API keys from Platform Settings first.");
  }

  const auth = Buffer.from(`${config.keyId}:${config.keySecret}`).toString("base64");
  const res = await fetch("https://api.razorpay.com/v1/payment_links", {
    method: "POST",
    headers: { "Content-Type": "application/json", Authorization: `Basic ${auth}` },
    body: JSON.stringify({
      amount: Math.round(params.amountRupees * 100),
      currency: "INR",
      description: params.description,
      customer: { name: params.customerName, email: params.customerEmail },
      notify: { sms: false, email: true },
      reference_id: params.referenceId,
    }),
  });

  if (!res.ok) {
    const body = await res.text();
    throw new Error(`Razorpay payment link creation failed: ${body}`);
  }

  const data = await res.json();
  return { id: data.id, shortUrl: data.short_url };
}

/** Verifies the `x-razorpay-signature` header on an incoming webhook against the
 *  configured webhook secret (set separately from the API key/secret on Razorpay's
 *  dashboard) — must be checked against the exact raw request body, before parsing. */
export async function verifyWebhookSignature(rawBody: string, signature: string): Promise<boolean> {
  const { prisma } = await import("@/lib/prisma");
  const settings = await prisma.platformSettings.findUnique({ where: { id: "singleton" } });
  const webhookSecret = settings?.razorpayWebhookSecret;
  if (!webhookSecret) return false;

  const expected = crypto.createHmac("sha256", webhookSecret).update(rawBody).digest("hex");
  const expectedBuf = Buffer.from(expected);
  const signatureBuf = Buffer.from(signature);
  if (expectedBuf.length !== signatureBuf.length) return false;
  return crypto.timingSafeEqual(expectedBuf, signatureBuf);
}
