import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { platformSettingsSchema } from "@/lib/validation/platformSettings";

async function requireFirmAdmin() {
  const session = await auth();
  if (!session?.user || session.user.role !== "ADMIN") return null;
  return session;
}

export async function GET() {
  const session = await requireFirmAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const org = await prisma.organization.findUnique({ where: { id: session.user.organizationId } });

  return NextResponse.json({
    smtpHost: org?.smtpHost ?? "",
    smtpPort: org?.smtpPort ?? "",
    smtpUser: org?.smtpUser ?? "",
    hasSmtpPassword: Boolean(org?.smtpPassword),
  });
}

// Reuses the same shape as the platform settings form; alertEmailTo/slackWebhookUrl
// are platform-only concepts and simply ignored here if present in the body.
export async function PATCH(request: Request) {
  const session = await requireFirmAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const body = await request.json();
  const parsed = platformSettingsSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const { smtpHost, smtpPort, smtpUser, smtpPassword } = parsed.data;

  await prisma.organization.update({
    where: { id: session.user.organizationId },
    data: {
      smtpHost: smtpHost || null,
      smtpPort: smtpPort ? Number(smtpPort) : null,
      smtpUser: smtpUser || null,
      ...(smtpPassword ? { smtpPassword } : {}),
    },
  });

  return NextResponse.json({ ok: true });
}
