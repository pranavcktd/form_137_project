import { NextResponse } from "next/server";
import { writeFile, mkdir } from "fs/promises";
import path from "path";
import type { Prisma } from "@prisma/client";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { analyzeFvuPackage } from "@/lib/fvu/analyzePackage";

async function requireSuperAdmin() {
  const session = await auth();
  if (!session?.user || session.user.role !== "SUPER_ADMIN") return null;
  return session;
}

export async function GET() {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const uploads = await prisma.fvuPackageUpload.findMany({
    orderBy: { createdAt: "desc" },
    select: {
      id: true,
      originalFilename: true,
      uploadedByName: true,
      status: true,
      errorMessage: true,
      createdAt: true,
    },
  });
  return NextResponse.json(uploads);
}

/**
 * Accepts the FVU/RPU package as downloaded from Protean (a .zip or .jar),
 * stores it, and decompiles + diffs it against the currently-vendored jar —
 * synchronously, since a full decompile+scan of the real FVU jar takes only
 * a few seconds. Never executes the uploaded file, only reads its bytecode.
 */
export async function POST(request: Request) {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const formData = await request.formData();
  const file = formData.get("file");
  if (!(file instanceof File)) {
    return NextResponse.json({ error: { formErrors: ["No file uploaded."] } }, { status: 400 });
  }
  if (!/\.(zip|jar)$/i.test(file.name)) {
    return NextResponse.json(
      { error: { formErrors: ["Upload the .zip or .jar file as downloaded from Protean."] } },
      { status: 400 },
    );
  }

  const upload = await prisma.fvuPackageUpload.create({
    data: {
      uploadedByName: session.user.name ?? session.user.email ?? "Unknown",
      uploadedByEmail: session.user.email ?? "",
      originalFilename: file.name,
      storagePath: "",
      status: "ANALYZING",
    },
  });

  const workDir = path.join(process.cwd(), "storage", "fvu-uploads", upload.id);
  await mkdir(workDir, { recursive: true });
  const storagePath = path.join(workDir, file.name);
  await writeFile(storagePath, Buffer.from(await file.arrayBuffer()));

  try {
    const analysisResult = await analyzeFvuPackage(storagePath, workDir);
    const updated = await prisma.fvuPackageUpload.update({
      where: { id: upload.id },
      data: { storagePath, status: "DONE", analysisResult: analysisResult as unknown as Prisma.InputJsonValue },
    });
    return NextResponse.json(updated, { status: 201 });
  } catch (err) {
    const errorMessage = err instanceof Error ? err.message : "Analysis failed.";
    const updated = await prisma.fvuPackageUpload.update({
      where: { id: upload.id },
      data: { storagePath, status: "FAILED", errorMessage },
    });
    return NextResponse.json(updated, { status: 201 });
  }
}
