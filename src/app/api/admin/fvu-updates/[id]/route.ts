import { NextResponse } from "next/server";
import { rm } from "fs/promises";
import path from "path";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";

async function requireSuperAdmin() {
  const session = await auth();
  if (!session?.user || session.user.role !== "SUPER_ADMIN") return null;
  return session;
}

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const { id } = await params;
  const upload = await prisma.fvuPackageUpload.findUnique({ where: { id } });
  if (!upload) return NextResponse.json({ error: "Not found" }, { status: 404 });

  return NextResponse.json(upload);
}

export async function DELETE(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const { id } = await params;
  const upload = await prisma.fvuPackageUpload.findUnique({ where: { id } });
  if (!upload) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const workDir = path.join(process.cwd(), "storage", "fvu-uploads", id);
  // Windows can briefly hold a file handle open right after a decompile process
  // exits (including one just killed by the analysis timeout) — retry rather
  // than fail the whole delete on a transient EBUSY.
  await rm(workDir, { recursive: true, force: true, maxRetries: 5, retryDelay: 300 });
  await prisma.fvuPackageUpload.delete({ where: { id } });

  return NextResponse.json({ ok: true });
}
