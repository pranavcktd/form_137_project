import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { checkDiscrepancies } from "@/lib/analytics/discrepancies";

export async function GET(request: Request) {
  const session = await auth();
  if (!session?.user) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  const url = new URL(request.url);
  const financialYear = Number(url.searchParams.get("financialYear"));
  const month = Number(url.searchParams.get("month"));
  if (!financialYear || !month) {
    return NextResponse.json({ error: "financialYear and month are required" }, { status: 400 });
  }

  const clients = await prisma.client.findMany({
    where: { organizationId: session.user.organizationId },
    orderBy: { departmentName: "asc" },
    include: {
      filingPeriods: {
        where: { financialYear, month, statementType: "ORIGINAL" },
        include: { ddoRecords: true },
      },
    },
  });

  const rows = clients.map((client) => {
    const filingPeriod = client.filingPeriods[0];
    const discrepancies = filingPeriod
      ? checkDiscrepancies(
          filingPeriod.ddoRecords.map((r) => ({
            tan: r.tan,
            name: r.name,
            formType: r.formType,
            taxDeducted: Number(r.taxDeducted),
            totalRemitted: Number(r.totalRemitted),
          })),
        )
      : [];

    return {
      clientId: client.id,
      departmentName: client.departmentName,
      ain: client.ain,
      filingPeriodId: filingPeriod?.id ?? null,
      status: filingPeriod ? filingPeriod.status : "NOT_STARTED",
      ddoCount: filingPeriod?.ddoRecords.length ?? 0,
      discrepancyCount: discrepancies.length,
    };
  });

  return NextResponse.json(rows);
}
