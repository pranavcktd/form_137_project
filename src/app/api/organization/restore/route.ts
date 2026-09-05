import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { restoreRequestSchema } from "@/lib/validation/backup";
import { parseUploadedBackup } from "@/lib/backup/parseUploadedBackup";
import { restoreOrganizationBackup } from "@/lib/backup/restoreOrganization";

/** A firm's own ADMIN restoring a backup into their own data — never another firm's. */
export async function POST(request: Request) {
  const session = await auth();
  if (!session?.user || session.user.role !== "ADMIN") {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

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
    const summary = await restoreOrganizationBackup(
      session.user.organizationId,
      backup,
      parsedStrategy.data.strategy,
    );
    return NextResponse.json(summary);
  } catch (err) {
    console.error("Backup restore failed:", err);
    const message = err instanceof Error ? err.message : "Restore failed.";
    return NextResponse.json({ error: { formErrors: [message] } }, { status: 500 });
  }
}
