import { NextResponse } from "next/server";
import bcrypt from "bcryptjs";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { firmOnboardingSchema } from "@/lib/validation/organization";

async function requireSuperAdmin() {
  const session = await auth();
  if (!session?.user || session.user.role !== "SUPER_ADMIN") return null;
  return session;
}

export async function GET() {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const firms = await prisma.organization.findMany({
    where: { users: { some: { role: { in: ["ADMIN", "PREPARER"] } } } },
    orderBy: { createdAt: "desc" },
    include: {
      _count: { select: { clients: true, users: true } },
      users: { where: { role: "ADMIN" }, take: 1, select: { email: true, name: true } },
    },
  });

  return NextResponse.json(firms);
}

export async function POST(request: Request) {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const body = await request.json();
  const parsed = firmOnboardingSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const { firmName, contactEmail, contactPhone, adminName, adminEmail, adminPassword } =
    parsed.data;

  const existingUser = await prisma.user.findUnique({ where: { email: adminEmail } });
  if (existingUser) {
    return NextResponse.json(
      { error: { formErrors: ["An account with this email already exists"] } },
      { status: 409 },
    );
  }

  const passwordHash = await bcrypt.hash(adminPassword, 12);

  const firm = await prisma.organization.create({
    data: {
      name: firmName,
      contactEmail: contactEmail || null,
      contactPhone: contactPhone || null,
      users: {
        create: {
          name: adminName,
          email: adminEmail,
          passwordHash,
          role: "ADMIN",
        },
      },
    },
  });

  return NextResponse.json({ id: firm.id }, { status: 201 });
}
