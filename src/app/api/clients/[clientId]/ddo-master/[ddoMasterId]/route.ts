import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { requireClient } from "@/lib/authz";
import { ddoMasterSchema } from "@/lib/validation/ddoMaster";

async function requireDdoMaster(clientId: string, ddoMasterId: string) {
  const { session, client } = await requireClient(clientId);
  if (!session || !client) return { session, client, ddoMaster: null };

  const ddoMaster = await prisma.ddoMaster.findFirst({
    where: { id: ddoMasterId, clientId },
  });
  return { session, client, ddoMaster };
}

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ clientId: string; ddoMasterId: string }> },
) {
  const { clientId, ddoMasterId } = await params;
  const { session, ddoMaster } = await requireDdoMaster(clientId, ddoMasterId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!ddoMaster) return NextResponse.json({ error: "Not found" }, { status: 404 });

  return NextResponse.json(ddoMaster);
}

export async function PATCH(
  request: Request,
  { params }: { params: Promise<{ clientId: string; ddoMasterId: string }> },
) {
  const { clientId, ddoMasterId } = await params;
  const { session, ddoMaster } = await requireDdoMaster(clientId, ddoMasterId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!ddoMaster) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const body = await request.json();
  const parsed = ddoMasterSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  if (parsed.data.tan !== ddoMaster.tan) {
    const existing = await prisma.ddoMaster.findUnique({
      where: { clientId_tan: { clientId, tan: parsed.data.tan } },
    });
    if (existing) {
      return NextResponse.json(
        { error: { formErrors: ["A DDO with this TAN already exists for this client"] } },
        { status: 409 },
      );
    }
  }

  const updated = await prisma.ddoMaster.update({
    where: { id: ddoMasterId },
    data: parsed.data,
  });

  return NextResponse.json(updated);
}

export async function DELETE(
  _request: Request,
  { params }: { params: Promise<{ clientId: string; ddoMasterId: string }> },
) {
  const { clientId, ddoMasterId } = await params;
  const { session, ddoMaster } = await requireDdoMaster(clientId, ddoMasterId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!ddoMaster) return NextResponse.json({ error: "Not found" }, { status: 404 });

  await prisma.ddoMaster.delete({ where: { id: ddoMasterId } });

  return NextResponse.json({ ok: true });
}
