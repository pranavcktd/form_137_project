import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { buildBackupZip } from "@/lib/backup/buildBackupZip";

/** A firm's own ADMIN downloading a backup of their own data. */
export async function GET() {
  const session = await auth();
  if (!session?.user || session.user.role !== "ADMIN") {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  const organization = await prisma.organization.findUnique({ where: { id: session.user.organizationId } });
  if (!organization) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const zipBuffer = await buildBackupZip(session.user.organizationId);
  const dateStamp = new Date().toISOString().slice(0, 10);

  return new NextResponse(new Uint8Array(zipBuffer), {
    status: 200,
    headers: {
      "Content-Type": "application/zip",
      "Content-Disposition": `attachment; filename="nex-backup-${organization.name.replace(/\s+/g, "-")}-${dateStamp}.zip"`,
    },
  });
}
