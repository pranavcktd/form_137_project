import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { firmEditSchema } from "@/lib/validation/organization";
import { syncFirmSubscriptions } from "@/lib/subscriptions";

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
      subscriptions: {
        include: { payments: { orderBy: { createdAt: "desc" } } },
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

  const { firmName, contactEmail, contactPhone, status, subscriptions } = parsed.data;

  await prisma.organization.update({
    where: { id },
    data: {
      name: firmName,
      contactEmail: contactEmail || null,
      contactPhone: contactPhone || null,
      status,
    },
  });

  await syncFirmSubscriptions(
    id,
    subscriptions.map((s) => ({
      application: s.application,
      price: s.price,
      billingCycle: s.billingCycle,
      startDate: s.startDate ? new Date(s.startDate) : null,
      endDate: s.endDate ? new Date(s.endDate) : null,
    })),
  );

  return NextResponse.json({ ok: true });
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
