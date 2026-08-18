import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { requireFilingPeriod } from "@/lib/authz";
import { ddoRecordSchema } from "@/lib/validation/ddoRecord";
import { serializeDdoRecord } from "@/lib/serialize";

export async function PATCH(
  request: Request,
  { params }: { params: Promise<{ id: string; recordId: string }> },
) {
  const { id, recordId } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });
  if (filingPeriod.status === "LOCKED") {
    return NextResponse.json(
      { error: { formErrors: ["This filing period is locked. Click Edit Return to make changes."] } },
      { status: 403 },
    );
  }

  const existing = await prisma.ddoRecord.findFirst({
    where: { id: recordId, filingPeriodId: id },
  });
  if (!existing) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const body = await request.json();
  const parsed = ddoRecordSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const duplicate = await prisma.ddoRecord.findFirst({
    where: {
      filingPeriodId: id,
      ddoMasterId: parsed.data.ddoMasterId,
      formType: parsed.data.formType || null,
      NOT: { id: recordId },
    },
  });
  if (duplicate) {
    return NextResponse.json(
      {
        error: {
          formErrors: [
            `This DDO already has a ${parsed.data.formType || "(blank form type)"} transaction for this filing period.`,
          ],
        },
      },
      { status: 409 },
    );
  }

  const record = await prisma.ddoRecord.update({
    where: { id: recordId },
    data: parsed.data,
  });

  return NextResponse.json(serializeDdoRecord(record));
}

export async function DELETE(
  _request: Request,
  { params }: { params: Promise<{ id: string; recordId: string }> },
) {
  const { id, recordId } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });
  if (filingPeriod.status === "LOCKED") {
    return NextResponse.json(
      { error: { formErrors: ["This filing period is locked. Click Edit Return to make changes."] } },
      { status: 403 },
    );
  }

  const existing = await prisma.ddoRecord.findFirst({
    where: { id: recordId, filingPeriodId: id },
  });
  if (!existing) return NextResponse.json({ error: "Not found" }, { status: 404 });

  await prisma.ddoRecord.delete({ where: { id: recordId } });

  return NextResponse.json({ ok: true });
}
