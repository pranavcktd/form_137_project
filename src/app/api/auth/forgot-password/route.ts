import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { forgotPasswordSchema } from "@/lib/validation/passwordReset";
import { generateResetToken, RESET_TOKEN_TTL_MS } from "@/lib/passwordReset";
import { sendPasswordResetLinkEmail } from "@/lib/alerts/notify";

/**
 * Always returns the same generic response regardless of whether the email
 * exists, whether the account is disabled, or whether delivery succeeded —
 * so this endpoint can't be used to enumerate accounts (same principle as
 * /api/auth/login-status).
 */
const GENERIC_RESPONSE = NextResponse.json({
  message: "If that email is registered, a password reset link has been sent.",
});

export async function POST(request: Request) {
  const body = await request.json();
  const parsed = forgotPasswordSchema.safeParse(body);
  if (!parsed.success) return GENERIC_RESPONSE;

  const user = await prisma.user.findUnique({
    where: { email: parsed.data.email },
    include: { organization: true },
  });

  if (!user || user.disabled || user.organization.status === "DISABLED") {
    return GENERIC_RESPONSE;
  }

  const { rawToken, tokenHash } = generateResetToken();

  await prisma.$transaction([
    prisma.passwordResetToken.deleteMany({ where: { userId: user.id, usedAt: null } }),
    prisma.passwordResetToken.create({
      data: {
        userId: user.id,
        tokenHash,
        expiresAt: new Date(Date.now() + RESET_TOKEN_TTL_MS),
      },
    }),
  ]);

  const origin = new URL(request.url).origin;
  const resetUrl = `${origin}/reset-password?token=${rawToken}`;
  await sendPasswordResetLinkEmail(user.email, user.name, resetUrl, user.organizationId);

  return GENERIC_RESPONSE;
}
