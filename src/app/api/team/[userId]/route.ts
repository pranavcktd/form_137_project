import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { firmUserEditSchema } from "@/lib/validation/organization";

async function requireFirmAdmin() {
  const session = await auth();
  if (!session?.user || session.user.role !== "ADMIN") return null;
  return session;
}

/** Only ever operate on a user within the caller's own firm — never another firm's. */
async function requireOwnFirmUser(session: NonNullable<Awaited<ReturnType<typeof requireFirmAdmin>>>, userId: string) {
  const target = await prisma.user.findUnique({ where: { id: userId } });
  if (!target || target.organizationId !== session.user.organizationId) return null;
  return target;
}

export async function PATCH(
  request: Request,
  { params }: { params: Promise<{ userId: string }> },
) {
  const session = await requireFirmAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const { userId } = await params;
  const target = await requireOwnFirmUser(session, userId);
  if (!target) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const body = await request.json();
  const parsed = firmUserEditSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  if (parsed.data.email !== target.email) {
    const existing = await prisma.user.findUnique({ where: { email: parsed.data.email } });
    if (existing) {
      return NextResponse.json(
        { error: { formErrors: ["Another account already uses this email"] } },
        { status: 409 },
      );
    }
  }

  const losingAdminAccess =
    target.role === "ADMIN" && (parsed.data.role !== "ADMIN" || parsed.data.disabled);
  if (losingAdminAccess) {
    const otherActiveAdmins = await prisma.user.count({
      where: { organizationId: session.user.organizationId, role: "ADMIN", disabled: false, id: { not: userId } },
    });
    if (otherActiveAdmins === 0) {
      return NextResponse.json(
        { error: { formErrors: ["Your firm needs at least one active Admin — promote someone else first"] } },
        { status: 400 },
      );
    }
  }

  const updated = await prisma.user.update({
    where: { id: userId },
    data: parsed.data,
    select: { id: true, name: true, email: true, role: true, disabled: true },
  });

  return NextResponse.json(updated);
}

export async function DELETE(
  _request: Request,
  { params }: { params: Promise<{ userId: string }> },
) {
  const session = await requireFirmAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const { userId } = await params;
  if (userId === session.user.id) {
    return NextResponse.json(
      { error: { formErrors: ["You can't delete your own account"] } },
      { status: 400 },
    );
  }

  const target = await requireOwnFirmUser(session, userId);
  if (!target) return NextResponse.json({ error: "Not found" }, { status: 404 });

  if (target.role === "ADMIN") {
    const otherActiveAdmins = await prisma.user.count({
      where: { organizationId: session.user.organizationId, role: "ADMIN", disabled: false, id: { not: userId } },
    });
    if (otherActiveAdmins === 0) {
      return NextResponse.json(
        { error: { formErrors: ["Your firm needs at least one active Admin — promote someone else first"] } },
        { status: 400 },
      );
    }
  }

  await prisma.user.delete({ where: { id: userId } });

  return NextResponse.json({ ok: true });
}
