import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { firmEditSchema } from "@/lib/validation/organization";

async function requireSuperAdmin() {
  const session = await auth();
  if (!session?.user || session.user.role !== "SUPER_ADMIN") return null;
  return session;
}

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const { id } = await params;
  const firm = await prisma.organization.findUnique({
    where: { id },
    include: {
      _count: { select: { clients: true } },
      users: {
        orderBy: { createdAt: "asc" },
        select: {
          id: true,
          name: true,
          email: true,
          role: true,
          disabled: true,
          lastLoginAt: true,
          createdAt: true,
        },
      },
    },
  });
  if (!firm) return NextResponse.json({ error: "Not found" }, { status: 404 });

  return NextResponse.json(firm);
}

export async function PATCH(
  request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const { id } = await params;
  const body = await request.json();
  const parsed = firmEditSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const { firmName, contactEmail, contactPhone, status, enabledApplications, subscriptionStartDate, subscriptionEndDate } =
    parsed.data;

  const firm = await prisma.organization.update({
    where: { id },
    data: {
      name: firmName,
      contactEmail: contactEmail || null,
      contactPhone: contactPhone || null,
      status,
      enabledApplications,
      subscriptionStartDate: subscriptionStartDate ? new Date(subscriptionStartDate) : null,
      subscriptionEndDate: subscriptionEndDate ? new Date(subscriptionEndDate) : null,
    },
  });

  return NextResponse.json(firm);
}

export async function DELETE(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const { id } = await params;
  // Cascades to every user, client, filing period, DDO record, and
  // generated file this firm owns — the client confirms this explicitly
  // before calling it.
  await prisma.organization.delete({ where: { id } });

  return NextResponse.json({ ok: true });
}
