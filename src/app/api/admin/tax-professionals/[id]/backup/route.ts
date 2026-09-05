import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { buildBackupZip } from "@/lib/backup/buildBackupZip";

async function requireSuperAdmin() {
  const session = await auth();
  if (!session?.user || session.user.role !== "SUPER_ADMIN") return null;
  return session;
}

/** Super admin downloading a backup of one specific firm's data. */
export async function GET(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const { id } = await params;
  const organization = await prisma.organization.findUnique({ where: { id } });
  if (!organization) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const zipBuffer = await buildBackupZip(id);
  const dateStamp = new Date().toISOString().slice(0, 10);

  return new NextResponse(new Uint8Array(zipBuffer), {
    status: 200,
    headers: {
      "Content-Type": "application/zip",
      "Content-Disposition": `attachment; filename="nex-backup-${organization.name.replace(/\s+/g, "-")}-${dateStamp}.zip"`,
    },
  });
}
