import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";

/**
 * Explains *why* a login failed, for accounts/firms an admin has explicitly
 * blocked — called only after a normal sign-in attempt has already failed,
 * so it never confirms whether an unrelated email/password guess was close.
 * Does not check the password itself.
 */
export async function POST(request: Request) {
  const { email } = await request.json();
  if (!email || typeof email !== "string") {
    return NextResponse.json({ status: "unknown" });
  }

  const user = await prisma.user.findUnique({
    where: { email },
    include: {
      organization: {
        include: { users: { where: { role: "ADMIN" }, take: 1, select: { name: true, email: true } } },
      },
    },
  });
  if (!user) return NextResponse.json({ status: "unknown" });

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

  return NextResponse.json({ status: "unknown" });
}
