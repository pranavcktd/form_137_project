import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { buildPlatformBackupZip } from "@/lib/backup/buildBackupZip";

/** Super admin downloading every firm's data in one zip — archival/disaster
 *  recovery only; there's no matching "restore the whole platform" endpoint,
 *  since restoring always targets one specific, already-existing firm. */
export async function GET() {
  const session = await auth();
  if (!session?.user || session.user.role !== "SUPER_ADMIN") {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  const zipBuffer = await buildPlatformBackupZip();
  const dateStamp = new Date().toISOString().slice(0, 10);

  return new NextResponse(new Uint8Array(zipBuffer), {
    status: 200,
    headers: {
      "Content-Type": "application/zip",
      "Content-Disposition": `attachment; filename="nex-platform-backup-${dateStamp}.zip"`,
    },
  });
}
