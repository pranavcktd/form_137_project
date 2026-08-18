import { NextResponse } from "next/server";
import { mkdir, writeFile, readFile } from "fs/promises";
import path from "path";
import { ZipArchive } from "archiver";
import { prisma } from "@/lib/prisma";
import { requireFilingPeriod } from "@/lib/authz";
import { generateStatementText } from "@/lib/generator";
import { clientToInput, filingPeriodToInput, ddoRecordToInput } from "@/lib/generator/fromPrisma";
import { validateStatement } from "@/lib/validation/validateStatement";
import { runFvu } from "@/lib/fvu/runFvu";
import { isFutureFilingPeriod } from "@/lib/financialYear";

export async function POST(
  _request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { session, filingPeriod } = await requireFilingPeriod(id);
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (!filingPeriod) return NextResponse.json({ error: "Not found" }, { status: 404 });
  if (filingPeriod.status === "LOCKED") {
    return NextResponse.json(
      { error: "This filing period is already locked. Click Edit Return to make changes." },
      { status: 403 },
    );
  }
  if (isFutureFilingPeriod(filingPeriod.financialYear, filingPeriod.month)) {
    return NextResponse.json(
      { error: "This filing period is in the future — a return can't be filed for a month that hasn't happened yet." },
      { status: 403 },
    );
  }

  const [client, ddoRecordRows] = await Promise.all([
    prisma.client.findUniqueOrThrow({ where: { id: filingPeriod.clientId } }),
    prisma.ddoRecord.findMany({ where: { filingPeriodId: id }, orderBy: { serialNo: "asc" } }),
  ]);

  const orgInput = clientToInput(client);
  const periodInput = filingPeriodToInput(filingPeriod);
  const ddoInputs = ddoRecordRows.map(ddoRecordToInput);

  const validation = validateStatement(orgInput, periodInput, ddoInputs);
  if (!validation.isValid) {
    return NextResponse.json({ stage: "validation", validation }, { status: 400 });
  }

  // Durable, never-cleaned storage — every generation attempt (pass or
  // fail) keeps its own timestamped directory for the audit trail.
  const workDir = path.join(
    process.env.FVU_WORKDIR || "./storage/generated",
    filingPeriod.clientId,
    id,
    String(Date.now()),
  );
  await mkdir(workDir, { recursive: true });

  const statementText = generateStatementText(orgInput, periodInput, ddoInputs);
  const inputPath = path.join(workDir, "statement.txt");
  await writeFile(inputPath, statementText, "latin1");

  const fvuResult = await runFvu(inputPath, workDir, "statement");

  if (!fvuResult.success) {
    await prisma.generatedFile.create({
      data: {
        filingPeriodId: id,
        rawTextPath: inputPath,
        errHtmlPath: fvuResult.errHtmlPath,
        fvuVersion: "1.9",
        status: "FVU_FAILED",
      },
    });
    return NextResponse.json(
      { stage: fvuResult.stage, message: fvuResult.message, errors: fvuResult.errors },
      { status: 422 },
    );
  }

  await prisma.generatedFile.create({
    data: {
      filingPeriodId: id,
      rawTextPath: inputPath,
      fvuFilePath: fvuResult.fvuFilePath,
      statisticFilePath: fvuResult.statisticFilePath,
      receiptPath: fvuResult.receiptFilePath,
      fvuVersion: "1.9",
      status: "FVU_PASSED",
    },
  });

  // A successful generate locks the period — its filed data must stay
  // exactly what was actually submitted until the preparer explicitly
  // unlocks it via "Edit Return".
  await prisma.filingPeriod.update({
    where: { id },
    data: { status: "LOCKED", lockedAt: new Date() },
  });

  const filesToZip: Array<{ path: string; name: string }> = [
    { path: inputPath, name: "statement.txt" },
  ];
  if (fvuResult.fvuFilePath) filesToZip.push({ path: fvuResult.fvuFilePath, name: "statement.fvu" });
  if (fvuResult.statisticFilePath) {
    filesToZip.push({ path: fvuResult.statisticFilePath, name: "statistics-report.pdf" });
  }
  if (fvuResult.receiptFilePath) {
    filesToZip.push({ path: fvuResult.receiptFilePath, name: "receipt.html" });
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
      "Content-Disposition": `attachment; filename="form137-${filingPeriod.financialYear}-${String(filingPeriod.month).padStart(2, "0")}.zip"`,
    },
  });
}
