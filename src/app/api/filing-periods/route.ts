import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { requireClient } from "@/lib/authz";
import { filingPeriodSchema } from "@/lib/validation/filingPeriod";

interface FormTypeSummaryRow {
  formType: string | null;
  count: number;
  taxDeducted: number;
  totalRemitted: number;
}

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
    include: { _count: { select: { ddoRecords: true, generatedFiles: true } } },
  });

  // Per-period, per-Form-Type DDO count + amount totals — one query for every
  // period on the page rather than one per card.
  const formTypeGroups = await prisma.ddoRecord.groupBy({
    by: ["filingPeriodId", "formType"],
    where: { filingPeriodId: { in: filingPeriods.map((p) => p.id) } },
    _count: { _all: true },
    _sum: { taxDeducted: true, totalRemitted: true },
  });

  const formTypeSummaryByPeriod = new Map<string, FormTypeSummaryRow[]>();
  for (const group of formTypeGroups) {
    const rows = formTypeSummaryByPeriod.get(group.filingPeriodId) ?? [];
    rows.push({
      formType: group.formType,
      count: group._count._all,
      taxDeducted: Number(group._sum.taxDeducted ?? 0),
      totalRemitted: Number(group._sum.totalRemitted ?? 0),
    });
    formTypeSummaryByPeriod.set(group.filingPeriodId, rows);
  }

  return NextResponse.json(
    filingPeriods.map((p) => ({ ...p, formTypeSummary: formTypeSummaryByPeriod.get(p.id) ?? [] })),
  );
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
