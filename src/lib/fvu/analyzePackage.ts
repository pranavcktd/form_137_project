import { spawn } from "child_process";
import path from "path";
import { mkdir, readdir, readFile } from "fs/promises";
import AdmZip from "adm-zip";

const PROJECT_ROOT = process.cwd();
const CFR_JAR = path.join(PROJECT_ROOT, "vendor", "cfr", "cfr-0.152.jar");
const BASELINE_JAR = path.join(PROJECT_ROOT, "vendor", "fvu", "24GFVU.jar");

export type DiffStatus = "added" | "removed" | "changed" | "unchanged";

export interface FieldLabelDiffRow {
  /** The number the FVU itself puts in parentheses after the field name, e.g.
   *  the "76" in "Responsible Person First Name(76)" — stable regardless of
   *  wording, and what src/lib/fvu/errorFieldMap.ts keys off of. BH/TD/FH each
   *  number their own fields from 1, so the same index can legitimately mean
   *  a different field in each — `oldLabels`/`newLabels` can hold more than
   *  one label when that index's text differs across record types. */
  index: number;
  oldLabels: string[];
  newLabels: string[];
  status: DiffStatus;
}

export interface ErrorCodeDiffRow {
  code: string;
  oldMessage: string | null;
  newMessage: string | null;
  status: DiffStatus;
}

export interface FvuPackageAnalysis {
  baselineJar: string;
  uploadedJarPath: string;
  classCounts: { baseline: number; uploaded: number };
  classesAdded: string[];
  classesRemoved: string[];
  /** One row per field index seen in either version, old label(s) vs new —
   *  this is what survives Protean's obfuscation intact, so it's the most
   *  reliable signal of what actually changed without reading renamed logic. */
  fieldLabelDiff: FieldLabelDiffRow[];
  errorCodeDiff: ErrorCodeDiffRow[];
  /** Full decompiled source of the uploaded jar, kept on disk for whoever does
   *  the actual integration work to read directly — this report is a starting
   *  point, not a substitute for that. */
  decompiledOutputDir: string;
}

async function findJars(dir: string): Promise<string[]> {
  const entries = await readdir(dir, { withFileTypes: true, recursive: true });
  return entries
    .filter((e) => e.isFile() && e.name.toLowerCase().endsWith(".jar"))
    .map((e) => path.join((e as unknown as { parentPath: string }).parentPath ?? dir, e.name));
}

async function listJavaFiles(dir: string): Promise<string[]> {
  const entries = await readdir(dir, { withFileTypes: true, recursive: true });
  return entries
    .filter((e) => e.isFile() && e.name.toLowerCase().endsWith(".java"))
    .map((e) => path.join((e as unknown as { parentPath: string }).parentPath ?? dir, e.name));
}

/** The uploaded file is either the jar itself, or (Protean's usual distribution
 *  shape) a zip containing it alongside other resources — extract and pick the
 *  jar that looks like the real tool rather than a small vendor/support jar. */
async function resolveUploadedJar(uploadedFilePath: string, workDir: string): Promise<string> {
  if (uploadedFilePath.toLowerCase().endsWith(".jar")) return uploadedFilePath;

  const extractDir = path.join(workDir, "extracted");
  await mkdir(extractDir, { recursive: true });
  new AdmZip(uploadedFilePath).extractAllTo(extractDir, true);

  const jars = await findJars(extractDir);
  if (jars.length === 0) {
    throw new Error("No .jar file found inside the uploaded package.");
  }
  return jars.find((j) => /fvu/i.test(path.basename(j))) ?? jars[0];
}

function runCfr(jarPath: string, outDir: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const child = spawn("java", ["-jar", CFR_JAR, jarPath, "--outputdir", outDir, "--silent"], {
      windowsHide: true,
    });

    // The real FVU jar (~19 classes) decompiles in a few seconds. A wrong or
    // unusually large upload could otherwise hang the request indefinitely —
    // fail clearly instead of leaving the admin staring at a spinner.
    const timeout = setTimeout(() => {
      child.kill();
      reject(new Error("Decompiling this jar took too long (over 2 minutes) — is this really the FVU package?"));
    }, 120_000);

    child.on("error", (err) => {
      clearTimeout(timeout);
      reject(err);
    });
    // CFR can exit non-zero on a handful of unparseable classes while still
    // decompiling everything else usefully — only a spawn failure is fatal.
    child.on("close", () => {
      clearTimeout(timeout);
      resolve();
    });
  });
}

interface ExtractedSignals {
  classCount: number;
  classNames: string[];
  /** Field index -> the distinct label text(s) found at that index. */
  fieldLabelsByIndex: Map<number, Set<string>>;
  /** Error code -> one sample line of surrounding context. */
  errorMessageByCode: Map<string, string>;
}

// Captures the label text and its index separately (previously one combined
// string) so rows can be grouped by index for an old-vs-new table.
const FIELD_LABEL_PATTERN = /"([^"\\]{2,80})\((\d{1,3})\)"/g;
const ERROR_CODE_PATTERN = /\b[A-Za-z0-9]+\/[A-Za-z0-9]+-FV-\d{3,5}\b/g;

async function extractSignals(decompiledDir: string): Promise<ExtractedSignals> {
  const files = await listJavaFiles(decompiledDir);
  const fieldLabelsByIndex = new Map<number, Set<string>>();
  const errorMessageByCode = new Map<string, string>();

  for (const file of files) {
    const content = await readFile(file, "utf8");
    for (const m of content.matchAll(FIELD_LABEL_PATTERN)) {
      const index = Number(m[2]);
      const label = m[1];
      const existing = fieldLabelsByIndex.get(index);
      if (existing) existing.add(label);
      else fieldLabelsByIndex.set(index, new Set([label]));
    }
    for (const m of content.matchAll(ERROR_CODE_PATTERN)) {
      if (errorMessageByCode.has(m[0])) continue;
      const lineStart = content.lastIndexOf("\n", m.index) + 1;
      const lineEndIdx = content.indexOf("\n", m.index);
      const lineEnd = lineEndIdx === -1 ? content.length : lineEndIdx;
      errorMessageByCode.set(m[0], content.slice(lineStart, lineEnd).trim().slice(0, 200));
    }
  }

  return {
    classCount: files.length,
    classNames: files
      .map((f) => path.relative(decompiledDir, f).replace(/\.java$/, "").split(path.sep).join("."))
      .sort(),
    fieldLabelsByIndex,
    errorMessageByCode,
  };
}

function diffLists(baseline: string[], updated: string[]) {
  const baseSet = new Set(baseline);
  const updSet = new Set(updated);
  return {
    added: updated.filter((x) => !baseSet.has(x)).sort(),
    removed: baseline.filter((x) => !updSet.has(x)).sort(),
  };
}

function diffFieldLabels(
  baseline: Map<number, Set<string>>,
  updated: Map<number, Set<string>>,
): FieldLabelDiffRow[] {
  const allIndices = new Set([...baseline.keys(), ...updated.keys()]);
  const rows: FieldLabelDiffRow[] = [];

  for (const index of allIndices) {
    const oldLabels = [...(baseline.get(index) ?? [])].sort();
    const newLabels = [...(updated.get(index) ?? [])].sort();

    let status: DiffStatus;
    if (oldLabels.length === 0) status = "added";
    else if (newLabels.length === 0) status = "removed";
    else if (oldLabels.length === newLabels.length && oldLabels.every((l, i) => l === newLabels[i])) {
      status = "unchanged";
    } else {
      status = "changed";
    }

    rows.push({ index, oldLabels, newLabels, status });
  }

  return rows.sort((a, b) => a.index - b.index);
}

function diffErrorCodes(baseline: Map<string, string>, updated: Map<string, string>): ErrorCodeDiffRow[] {
  const allCodes = new Set([...baseline.keys(), ...updated.keys()]);
  const rows: ErrorCodeDiffRow[] = [];

  for (const code of allCodes) {
    const oldMessage = baseline.get(code) ?? null;
    const newMessage = updated.get(code) ?? null;

    let status: DiffStatus;
    if (oldMessage === null) status = "added";
    else if (newMessage === null) status = "removed";
    else if (oldMessage === newMessage) status = "unchanged";
    else status = "changed";

    rows.push({ code, oldMessage, newMessage, status });
  }

  return rows.sort((a, b) => a.code.localeCompare(b.code));
}

/**
 * Decompiles both the currently-vendored FVU jar and a newly uploaded one
 * (via CFR — never executes either jar, only reads its bytecode) and diffs
 * the field labels / error codes / class list that survive Protean's
 * obfuscation. This is a structural signal for a human to start from, not an
 * automatic determination of what changed — obfuscated method bodies still
 * need to be read to know what a changed class actually does differently.
 */
export async function analyzeFvuPackage(uploadedFilePath: string, workDir: string): Promise<FvuPackageAnalysis> {
  await mkdir(workDir, { recursive: true });

  const uploadedJarPath = await resolveUploadedJar(uploadedFilePath, workDir);

  const baselineOutDir = path.join(workDir, "decompiled-baseline");
  const uploadedOutDir = path.join(workDir, "decompiled-uploaded");
  await Promise.all([runCfr(BASELINE_JAR, baselineOutDir), runCfr(uploadedJarPath, uploadedOutDir)]);

  const [baseline, uploaded] = await Promise.all([
    extractSignals(baselineOutDir),
    extractSignals(uploadedOutDir),
  ]);

  if (uploaded.classCount === 0) {
    throw new Error("CFR couldn't decompile any classes from the uploaded jar — is it a valid Java archive?");
  }

  const classDiff = diffLists(baseline.classNames, uploaded.classNames);

  return {
    baselineJar: path.relative(PROJECT_ROOT, BASELINE_JAR),
    uploadedJarPath: path.relative(PROJECT_ROOT, uploadedJarPath),
    classCounts: { baseline: baseline.classCount, uploaded: uploaded.classCount },
    classesAdded: classDiff.added,
    classesRemoved: classDiff.removed,
    fieldLabelDiff: diffFieldLabels(baseline.fieldLabelsByIndex, uploaded.fieldLabelsByIndex),
    errorCodeDiff: diffErrorCodes(baseline.errorMessageByCode, uploaded.errorMessageByCode),
    decompiledOutputDir: path.relative(PROJECT_ROOT, uploadedOutDir),
  };
}
