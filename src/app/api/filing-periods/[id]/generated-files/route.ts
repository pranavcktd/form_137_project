import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { requireFilingPeriod } from "@/lib/authz";

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const generatedFiles = await prisma.generatedFile.findMany({
    where: { filingPeriodId: id },
    orderBy: { createdAt: "desc" },
  });

  return NextResponse.json(generatedFiles);
}
