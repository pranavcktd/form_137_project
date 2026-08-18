import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { requireClient } from "@/lib/authz";
import { parseDdoMasterWorkbook } from "@/lib/excel/parseDdoMasterImport";

export async function POST(
  request: Request,
  { params }: { params: Promise<{ clientId: string }> },
) {
  const { clientId } = await params;
  const { session, client } = await requireClient(clientId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!client) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const formData = await request.formData();
  const file = formData.get("file");
  if (!(file instanceof File)) {
    return NextResponse.json({ error: "No file uploaded" }, { status: 400 });
  }

  const buffer = Buffer.from(await file.arrayBuffer());

  let rows;
  try {
    rows = await parseDdoMasterWorkbook(buffer);
  } catch {
    return NextResponse.json(
      { error: "Could not read that file. Make sure it's a valid .xlsx export of the template." },
      { status: 400 },
    );
  }

  // A client can only have one DDO Master per TAN — surface same-TAN
  // repeats within the file itself before anything is saved.
  const groups = new Map<string, typeof rows>();
  for (const row of rows) {
    if (row.errors.length > 0 || !row.data.tan) continue;
    const group = groups.get(row.data.tan);
    if (group) group.push(row);
    else groups.set(row.data.tan, [row]);
  }
  const duplicates = Array.from(groups.values())
    .filter((group) => group.length > 1)
    .map((group) => ({
      tan: group[0].data.tan ?? "",
      rows: group.map((r) => ({ rowNumber: r.rowNumber, name: r.data.name ?? "" })),
    }));

  const tans = rows.map((r) => r.data.tan).filter((tan): tan is string => Boolean(tan));
  const existingCount = tans.length
    ? await prisma.ddoMaster.count({ where: { clientId, tan: { in: tans } } })
    : 0;

  return NextResponse.json({ rows, duplicates, existingCount });
}
