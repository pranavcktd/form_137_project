import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { requireClient } from "@/lib/authz";

export async function GET(
  request: Request,
  { params }: { params: Promise<{ clientId: string }> },
) {
  const { clientId } = await params;
  const { session, client } = await requireClient(clientId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!client) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const q = new URL(request.url).searchParams.get("q")?.trim() ?? "";
  if (q.length === 0) {
    return NextResponse.json([]);
  }

  const results = await prisma.ddoMaster.findMany({
    where: {
      clientId,
      OR: [
        { tan: { contains: q, mode: "insensitive" } },
        { name: { contains: q, mode: "insensitive" } },
      ],
    },
    orderBy: { name: "asc" },
    take: 10,
  });

  return NextResponse.json(results);
}
