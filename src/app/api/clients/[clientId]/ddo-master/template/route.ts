import { NextResponse } from "next/server";
import { requireClient } from "@/lib/authz";
import { generateDdoMasterWorkbook } from "@/lib/excel/ddoMasterTemplate";

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ clientId: string }> },
) {
  const { clientId } = await params;
  const { session, client } = await requireClient(clientId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!client) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const buffer = await generateDdoMasterWorkbook();

  return new NextResponse(new Uint8Array(buffer), {
    status: 200,
    headers: {
      "Content-Type": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "Content-Disposition": `attachment; filename="ddo-master-template.xlsx"`,
    },
  });
}
