import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { restoreRequestSchema } from "@/lib/validation/backup";
import { parseUploadedBackup } from "@/lib/backup/parseUploadedBackup";
import { restoreOrganizationBackup } from "@/lib/backup/restoreOrganization";

async function requireSuperAdmin() {
  const session = await auth();
  if (!session?.user || session.user.role !== "SUPER_ADMIN") return null;
  return session;
}

/** Super admin restoring a backup into one specific, existing firm. */
export async function POST(
  request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const { id } = await params;
  const organization = await prisma.organization.findUnique({ where: { id } });
  if (!organization) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const formData = await request.formData();
  const file = formData.get("file");
  const strategyRaw = formData.get("strategy");
  if (!(file instanceof File)) {
    return NextResponse.json({ error: { formErrors: ["No backup file uploaded."] } }, { status: 400 });
  }

  const parsedStrategy = restoreRequestSchema.safeParse({ strategy: strategyRaw });
  if (!parsedStrategy.success) {
    return NextResponse.json({ error: parsedStrategy.error.flatten() }, { status: 400 });
  }

  let backup;
  try {
    backup = parseUploadedBackup(file.name, Buffer.from(await file.arrayBuffer()));
  } catch (err) {
    const message = err instanceof Error ? err.message : "Could not read that backup file.";
    return NextResponse.json({ error: { formErrors: [message] } }, { status: 400 });
  }

  try {
    const summary = await restoreOrganizationBackup(id, backup, parsedStrategy.data.strategy);
    return NextResponse.json(summary);
  } catch (err) {
    console.error("Backup restore failed:", err);
    const message = err instanceof Error ? err.message : "Restore failed.";
    return NextResponse.json({ error: { formErrors: [message] } }, { status: 500 });
  }
}
