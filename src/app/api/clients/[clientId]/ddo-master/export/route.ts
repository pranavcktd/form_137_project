import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
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

  const masters = await prisma.ddoMaster.findMany({
    where: { clientId },
    orderBy: { name: "asc" },
  });

  const buffer = await generateDdoMasterWorkbook(
    masters.map((m) => ({
      tan: m.tan,
      name: m.name,
      address1: m.address1 ?? "",
      address2: m.address2 ?? "",
      address3: m.address3 ?? "",
      address4: m.address4 ?? "",
      city: m.city ?? "",
      state: m.state ?? "",
      pin: m.pin ?? "",
      ddoRegNo: m.ddoRegNo ?? "",
      ddoCode: m.ddoCode ?? "",
      email: m.email ?? "",
    })),
  );

  return new NextResponse(new Uint8Array(buffer), {
    status: 200,
    headers: {
      "Content-Type": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "Content-Disposition": `attachment; filename="ddo-master-export.xlsx"`,
    },
  });
}
