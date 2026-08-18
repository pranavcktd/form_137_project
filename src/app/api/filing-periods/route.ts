import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { requireClient } from "@/lib/authz";
import { filingPeriodSchema } from "@/lib/validation/filingPeriod";

export async function GET(request: Request) {
  const clientId = new URL(request.url).searchParams.get("clientId");
  if (!clientId) {
    return NextResponse.json({ error: "clientId is required" }, { status: 400 });
  }

  const { session, client } = await requireClient(clientId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!client) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const filingPeriods = await prisma.filingPeriod.findMany({
    where: { clientId },
    orderBy: [{ financialYear: "desc" }, { month: "desc" }],
    include: { _count: { select: { ddoRecords: true } } },
  });

  return NextResponse.json(filingPeriods);
}

export async function POST(request: Request) {
  const body = await request.json();
  const { clientId, ...rest } = body;
  if (!clientId) {
    return NextResponse.json({ error: "clientId is required" }, { status: 400 });
  }

  const { session, client } = await requireClient(clientId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!client) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const parsed = filingPeriodSchema.safeParse(rest);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const existing = await prisma.filingPeriod.findFirst({
    where: {
      clientId,
      financialYear: parsed.data.financialYear,
      month: parsed.data.month,
      statementType: parsed.data.statementType,
    },
  });
  if (existing) {
    return NextResponse.json(
      { error: { formErrors: ["A filing period with this year, month, and statement type already exists"] } },
      { status: 409 },
    );
  }

  const filingPeriod = await prisma.filingPeriod.create({
    data: {
      clientId,
      financialYear: parsed.data.financialYear,
      month: parsed.data.month,
      statementType: parsed.data.statementType,
    },
  });

  return NextResponse.json(filingPeriod, { status: 201 });
}
