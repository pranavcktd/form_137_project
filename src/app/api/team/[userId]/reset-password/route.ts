import { NextResponse } from "next/server";
import bcrypt from "bcryptjs";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { sendPasswordResetEmail } from "@/lib/alerts/notify";

const DEFAULT_RESET_PASSWORD = "Client@123";

async function requireFirmAdmin() {
  const session = await auth();
  if (!session?.user || session.user.role !== "ADMIN") return null;
  return session;
}

export async function POST(
  _request: Request,
  { params }: { params: Promise<{ userId: string }> },
) {
  const session = await requireFirmAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const { userId } = await params;
  const user = await prisma.user.findUnique({ where: { id: userId } });
  if (!user || user.organizationId !== session.user.organizationId) {
    return NextResponse.json({ error: "Not found" }, { status: 404 });
  }

  const passwordHash = await bcrypt.hash(DEFAULT_RESET_PASSWORD, 12);
  await prisma.user.update({
    where: { id: userId },
    data: { passwordHash, mustChangePassword: true },
  });

  let emailed = false;
  try {
    emailed = await sendPasswordResetEmail(user.email, user.name, DEFAULT_RESET_PASSWORD, user.organizationId);
  } catch (err) {
    console.error("Password reset email failed:", err);
  }

  return NextResponse.json({
    ok: true,
    emailed,
    temporaryPassword: emailed ? null : DEFAULT_RESET_PASSWORD,
  });
}
