import { NextResponse } from "next/server";
import bcrypt from "bcryptjs";
import { prisma } from "@/lib/prisma";
import { forgotPasswordSchema } from "@/lib/validation/passwordReset";
import { generateTemporaryPassword, PENDING_PASSWORD_TTL_MS } from "@/lib/passwordReset";
import { sendPasswordResetEmail } from "@/lib/alerts/notify";

/**
 * Unlike /api/auth/login-status (which only ever fires after a failed sign-in,
 * so it can't be used to fish for valid emails), this endpoint is reachable by
 * anyone typing any email — so telling the caller whether that email is
 * registered is a deliberate choice to trade account-enumeration resistance
 * for clearer self-service UX, made explicitly by the platform owner.
 */
export async function POST(request: Request) {
  const body = await request.json();
  const parsed = forgotPasswordSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ status: "not_found" });
  }

  const user = await prisma.user.findUnique({
    where: { email: parsed.data.email },
    include: {
      organization: {
        include: { users: { where: { role: "ADMIN" }, take: 1, select: { name: true, email: true } } },
      },
    },
  });

  if (!user) {
    return NextResponse.json({ status: "not_found" });
  }

  if (user.organization.status === "DISABLED") {
    const superAdmin = await prisma.user.findFirst({
      where: { role: "SUPER_ADMIN" },
      select: { name: true, email: true },
    });
    return NextResponse.json({
      status: "org_suspended",
      contactName: superAdmin?.name ?? "the platform administrator",
      contactEmail: superAdmin?.email ?? null,
    });
  }

  if (user.disabled) {
    const firmAdmin = user.organization.users[0];
    return NextResponse.json({
      status: "account_disabled",
      contactName: user.organization.contactEmail || user.organization.contactPhone ? user.organization.name : (firmAdmin?.name ?? user.organization.name),
      contactEmail: user.organization.contactEmail ?? firmAdmin?.email ?? null,
      contactPhone: user.organization.contactPhone ?? null,
    });
  }

  const temporaryPassword = generateTemporaryPassword();

  let emailed = false;
  try {
    emailed = await sendPasswordResetEmail(user.email, user.name, temporaryPassword, user.organizationId, true);
  } catch (err) {
    console.error("[forgot-password] Failed to send temporary password email:", err);
  }

  // Only actually issue the temp password once delivery is confirmed — otherwise
  // a failed send (bad SMTP creds, provider outage) would leave the requester with
  // a password change they never received. It's stored alongside — not instead of
  // — the existing passwordHash: the old password keeps working until this one is
  // actually used (see authorize() in lib/auth.ts), so a delayed or missed email
  // can't lock anyone out either.
  if (emailed) {
    const pendingPasswordHash = await bcrypt.hash(temporaryPassword, 12);
    await prisma.user.update({
      where: { id: user.id },
      data: { pendingPasswordHash, pendingPasswordExpiresAt: new Date(Date.now() + PENDING_PASSWORD_TTL_MS) },
    });
  }

  return NextResponse.json({ status: emailed ? "sent" : "send_failed" });
}
