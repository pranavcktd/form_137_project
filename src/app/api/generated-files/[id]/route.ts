import { NextResponse } from "next/server";
import { rm } from "fs/promises";
import path from "path";
import { prisma } from "@/lib/prisma";
import { requireFilingPeriod } from "@/lib/authz";

export async function DELETE(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;

  const generatedFile = await prisma.generatedFile.findUnique({ where: { id } });
  if (!generatedFile) {
    return NextResponse.json({ error: "Not found" }, { status: 404 });
  }

  const { session, filingPeriod } = await requireFilingPeriod(generatedFile.filingPeriodId);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });

  await prisma.generatedFile.delete({ where: { id } });

  // Every file from one generation attempt lives together in its own
  // timestamped directory (see generate/route.ts) — removing it frees the
  // disk space this history entry was taking up. Best-effort: the DB row is
  // already gone either way, so a filesystem hiccup here isn't fatal.
  try {
    await rm(path.dirname(generatedFile.rawTextPath), { recursive: true, force: true });
  } catch (err) {
    console.error("Failed to remove generated-file directory:", err);
  }

  return NextResponse.json({ ok: true });
}
