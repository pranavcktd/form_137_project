import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { clientProfileSchema } from "@/lib/validation/organization";

export async function GET() {
  const session = await auth();
  if (!session?.user) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  const clients = await prisma.client.findMany({
    where: { organizationId: session.user.organizationId },
    orderBy: { createdAt: "desc" },
    include: { _count: { select: { filingPeriods: true } } },
  });

  return NextResponse.json(clients);
}

export async function POST(request: Request) {
  const session = await auth();
  if (!session?.user) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  const body = await request.json();
  const parsed = clientProfileSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const existing = await prisma.client.findUnique({ where: { ain: parsed.data.ain } });
  if (existing) {
    return NextResponse.json(
      { error: { formErrors: ["A client with this AIN already exists"] } },
      { status: 409 },
    );
  }

  const { tan, ministryName, ...profile } = parsed.data;

  const client = await prisma.client.create({
    data: {
      ...profile,
      tan: tan || null,
      ministryName: ministryName || null,
      organizationId: session.user.organizationId,
    },
  });

  return NextResponse.json(client, { status: 201 });
}
