import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { requireClient } from "@/lib/authz";
import { ddoMasterSchema } from "@/lib/validation/ddoMaster";

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ clientId: string }> },
) {
  const { clientId } = await params;
  const { session, client } = await requireClient(clientId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!client) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const ddoMasters = await prisma.ddoMaster.findMany({
    where: { clientId },
    orderBy: { name: "asc" },
  });

  return NextResponse.json(ddoMasters);
}

export async function POST(
  request: Request,
  { params }: { params: Promise<{ clientId: string }> },
) {
  const { clientId } = await params;
  const { session, client } = await requireClient(clientId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!client) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const body = await request.json();
  const parsed = ddoMasterSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const existing = await prisma.ddoMaster.findUnique({
    where: { clientId_tan: { clientId, tan: parsed.data.tan } },
  });
  if (existing) {
    return NextResponse.json(
      { error: { formErrors: ["A DDO with this TAN already exists for this client"] } },
      { status: 409 },
    );
  }

  const ddoMaster = await prisma.ddoMaster.create({
    data: { ...parsed.data, clientId },
  });

  return NextResponse.json(ddoMaster, { status: 201 });
}
