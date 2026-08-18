import { NextResponse } from "next/server";
import { readFile } from "fs/promises";
import { ZipArchive } from "archiver";
import { prisma } from "@/lib/prisma";
import { requireFilingPeriod } from "@/lib/authz";

export async function GET(
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

  const filesToZip: Array<{ path: string; name: string }> = [];
  if (generatedFile.fvuFilePath) {
    filesToZip.push({ path: generatedFile.fvuFilePath, name: "statement.fvu" });
  }
  if (generatedFile.receiptPath) {
    filesToZip.push({ path: generatedFile.receiptPath, name: "receipt.html" });
  }
  if (generatedFile.errHtmlPath) {
    filesToZip.push({ path: generatedFile.errHtmlPath, name: "err.html" });
  }

  if (filesToZip.length === 0) {
    return NextResponse.json({ error: "No files available for this attempt" }, { status: 404 });
  }

  const zipBuffer = await new Promise<Buffer>((resolve, reject) => {
    const archive = new ZipArchive();
    const chunks: Buffer[] = [];
    archive.on("data", (chunk: Buffer) => chunks.push(chunk));
    archive.on("error", reject);
    archive.on("end", () => resolve(Buffer.concat(chunks)));

    Promise.all(filesToZip.map((f) => readFile(f.path)))
      .then((buffers) => {
        buffers.forEach((buffer, i) => archive.append(buffer, { name: filesToZip[i].name }));
        archive.finalize();
      })
      .catch(reject);
  });

  return new NextResponse(new Uint8Array(zipBuffer), {
    status: 200,
    headers: {
      "Content-Type": "application/zip",
      "Content-Disposition": `attachment; filename="form137-${generatedFile.id}.zip"`,
    },
  });
}
