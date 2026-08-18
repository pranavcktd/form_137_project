import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { requireFilingPeriod } from "@/lib/authz";
import { ddoRecordSchema } from "@/lib/validation/ddoRecord";
import { serializeDdoRecord } from "@/lib/serialize";

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const ddoRecords = await prisma.ddoRecord.findMany({
    where: { filingPeriodId: id },
    orderBy: { serialNo: "asc" },
  });
  return NextResponse.json(ddoRecords.map(serializeDdoRecord));
}

export async function POST(
  request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });
  if (filingPeriod.status === "LOCKED") {
    return NextResponse.json(
      { error: { formErrors: ["This filing period is locked. Click Edit Return to make changes."] } },
      { status: 403 },
    );
  }

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

  const maxSerial = await prisma.ddoRecord.aggregate({
    where: { filingPeriodId: id },
    _max: { serialNo: true },
  });

  const record = await prisma.ddoRecord.create({
    data: {
      ...parsed.data,
      filingPeriodId: id,
      serialNo: (maxSerial._max.serialNo ?? 0) + 1,
    },
  });

  return NextResponse.json(serializeDdoRecord(record), { status: 201 });
}
