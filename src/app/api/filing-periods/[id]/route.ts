import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { requireFilingPeriod } from "@/lib/authz";
import { serializeDdoRecord } from "@/lib/serialize";
import { filingPeriodSchema } from "@/lib/validation/filingPeriod";

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const [client, ddoRecords] = await Promise.all([
    prisma.client.findUnique({ where: { id: filingPeriod.clientId } }),
    prisma.ddoRecord.findMany({
      where: { filingPeriodId: id },
      orderBy: { serialNo: "asc" },
    }),
  ]);

  return NextResponse.json({
    filingPeriod,
    client,
    ddoRecords: ddoRecords.map(serializeDdoRecord),
  });
}

export async function PATCH(
  request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const body = await request.json();

  if (body.action === "unlock") {
    const updated = await prisma.filingPeriod.update({
      where: { id },
      data: { status: "DRAFT" },
    });
    return NextResponse.json(updated);
  }

  // Receipt/acknowledgement details are a record of what was actually
  // uploaded to the government portal — independent of whether the return's
  // DDO data is currently locked, and always editable (e.g. to correct a
  // typo in the receipt number after the fact).
  if ("receiptNumber" in body || "receiptDate" in body) {
    const receiptNumber = typeof body.receiptNumber === "string" ? body.receiptNumber.trim() : undefined;
    // An empty string means "clear it" (→ null), not "leave it alone" (→
    // undefined, skipped below) — those need to stay distinct, otherwise
    // clearing the date silently keeps whatever was saved before.
    const receiptDate =
      typeof body.receiptDate === "string" ? (body.receiptDate ? new Date(body.receiptDate) : null) : undefined;

    const updated = await prisma.filingPeriod.update({
      where: { id },
      data: {
        ...(receiptNumber !== undefined && { receiptNumber }),
        ...(receiptDate !== undefined && { receiptDate }),
      },
    });
    return NextResponse.json(updated);
  }

  // Editing the period's own FY/month/statement type — only while DRAFT;
  // once LOCKED, unlock first (that's a deliberate data-change boundary,
  // separate from the always-editable receipt fields above).
  if (filingPeriod.status === "LOCKED") {
    return NextResponse.json(
      { error: "Unlock this return (Edit Return) before changing its financial year, month, or statement type." },
      { status: 403 },
    );
  }

  const parsed = filingPeriodSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const duplicate = await prisma.filingPeriod.findFirst({
    where: {
      clientId: filingPeriod.clientId,
      financialYear: parsed.data.financialYear,
      month: parsed.data.month,
      statementType: parsed.data.statementType,
      NOT: { id },
    },
  });
  if (duplicate) {
    return NextResponse.json(
      { error: { formErrors: ["A filing period with this year, month, and statement type already exists"] } },
      { status: 409 },
    );
  }

  const updated = await prisma.filingPeriod.update({
    where: { id },
    data: parsed.data,
  });
  return NextResponse.json(updated);
}

export async function DELETE(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });

  if (filingPeriod.status === "LOCKED") {
    return NextResponse.json(
      { error: { formErrors: ["This filing period is locked. Click Edit Return (on its detail page) to unlock it before deleting."] } },
      { status: 403 },
    );
  }
  // Same rule as deleting an individual DDO record: the receipt fields
  // record what was actually uploaded to the government portal and stay
  // editable independent of the lock, so this is the only remaining guard
  // once a period has been unlocked again after being acknowledged.
  if (filingPeriod.receiptNumber || filingPeriod.receiptDate) {
    return NextResponse.json(
      {
        error: {
          formErrors: [
            "This return already has a Receipt/Acknowledgement Number and Date recorded. Clear those fields (on its detail page) first if you still want to delete it.",
          ],
        },
      },
      { status: 403 },
    );
  }

  await prisma.filingPeriod.delete({ where: { id } });

  return NextResponse.json({ ok: true });
}
