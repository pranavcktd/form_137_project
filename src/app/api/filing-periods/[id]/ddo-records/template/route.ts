import { NextResponse } from "next/server";
import { requireFilingPeriod } from "@/lib/authz";
import { generateDdoTemplate } from "@/lib/excel/ddoTemplate";

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const buffer = await generateDdoTemplate(filingPeriod.financialYear, filingPeriod.statementType);

  return new NextResponse(new Uint8Array(buffer), {
    status: 200,
    headers: {
      "Content-Type": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "Content-Disposition": `attachment; filename="ddo-template-${filingPeriod.financialYear}-${String(filingPeriod.month).padStart(2, "0")}.xlsx"`,
    },
  });
}
