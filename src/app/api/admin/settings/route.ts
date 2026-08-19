import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { platformSettingsSchema } from "@/lib/validation/platformSettings";

async function requireSuperAdmin() {
  const session = await auth();
  if (!session?.user || session.user.role !== "SUPER_ADMIN") return null;
  return session;
}

export async function GET() {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const settings = await prisma.platformSettings.findUnique({ where: { id: "singleton" } });

  return NextResponse.json({
    smtpHost: settings?.smtpHost ?? "",
    smtpPort: settings?.smtpPort ?? "",
    smtpUser: settings?.smtpUser ?? "",
    hasSmtpPassword: Boolean(settings?.smtpPassword),
    alertEmailTo: settings?.alertEmailTo ?? "",
    slackWebhookUrl: settings?.slackWebhookUrl ?? "",
    hideUnsubscribedModules: settings?.hideUnsubscribedModules ?? false,
    razorpayKeyId: settings?.razorpayKeyId ?? "",
    hasRazorpayKeySecret: Boolean(settings?.razorpayKeySecret),
    hasRazorpayWebhookSecret: Boolean(settings?.razorpayWebhookSecret),
  });
}

export async function PATCH(request: Request) {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const body = await request.json();
  const parsed = platformSettingsSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const {
    smtpHost,
    smtpPort,
    smtpUser,
    smtpPassword,
    alertEmailTo,
    slackWebhookUrl,
    hideUnsubscribedModules,
    razorpayKeyId,
    razorpayKeySecret,
    razorpayWebhookSecret,
  } = parsed.data;

  // Each field is only touched when the caller actually sent it, so a
  // partial PATCH (e.g. just the display toggle from its own settings card)
  // can't accidentally null out SMTP fields saved from a different form.
  const data = {
    ...(smtpHost !== undefined && { smtpHost: smtpHost || null }),
    ...(smtpPort !== undefined && { smtpPort: smtpPort ? Number(smtpPort) : null }),
    ...(smtpUser !== undefined && { smtpUser: smtpUser || null }),
    ...(alertEmailTo !== undefined && { alertEmailTo: alertEmailTo || null }),
    ...(slackWebhookUrl !== undefined && { slackWebhookUrl: slackWebhookUrl || null }),
    ...(smtpPassword ? { smtpPassword } : {}),
    ...(hideUnsubscribedModules !== undefined && { hideUnsubscribedModules }),
    ...(razorpayKeyId !== undefined && { razorpayKeyId: razorpayKeyId || null }),
    ...(razorpayKeySecret ? { razorpayKeySecret } : {}),
    ...(razorpayWebhookSecret ? { razorpayWebhookSecret } : {}),
  };

  await prisma.platformSettings.upsert({
    where: { id: "singleton" },
    create: { id: "singleton", ...data },
    update: data,
  });

  return NextResponse.json({ ok: true });
}
