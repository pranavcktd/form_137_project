import { spawn } from "child_process";
import path from "path";
import { mkdir, readdir, readFile } from "fs/promises";
import AdmZip from "adm-zip";

const PROJECT_ROOT = process.cwd();
const CFR_JAR = path.join(PROJECT_ROOT, "vendor", "cfr", "cfr-0.152.jar");
const BASELINE_JAR = path.join(PROJECT_ROOT, "vendor", "fvu", "24GFVU.jar");

export interface FvuPackageAnalysis {
  baselineJar: string;
  uploadedJarPath: string;
  classCounts: { baseline: number; uploaded: number };
  classesAdded: string[];
  classesRemoved: string[];
  /** Field labels are how the FVU tags each error to a specific field, e.g.
   *  "Responsible Person First Name(76)" — these strings survive Protean's
   *  obfuscation intact even though class/method names don't, so diffing them
   *  directly says which fields are new/removed without needing to actually
   *  read the (unreadably renamed) validation logic itself. */
  fieldLabels: { added: string[]; removed: string[]; unchangedCount: number };
  errorCodes: { added: { code: string; sampleLine: string }[]; removed: string[] };
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
  fieldLabels: Set<string>;
  errorCodes: Map<string, string>;
}

const FIELD_LABEL_PATTERN = /"([^"\\]{2,80}\([0-9]{1,3}\))"/g;
const ERROR_CODE_PATTERN = /\b[A-Za-z0-9]+\/[A-Za-z0-9]+-FV-\d{3,5}\b/g;

async function extractSignals(decompiledDir: string): Promise<ExtractedSignals> {
  const files = await listJavaFiles(decompiledDir);
  const fieldLabels = new Set<string>();
  const errorCodes = new Map<string, string>();

  for (const file of files) {
    const content = await readFile(file, "utf8");
    for (const m of content.matchAll(FIELD_LABEL_PATTERN)) {
      fieldLabels.add(m[1]);
    }
    for (const m of content.matchAll(ERROR_CODE_PATTERN)) {
      if (errorCodes.has(m[0])) continue;
      const lineStart = content.lastIndexOf("\n", m.index) + 1;
      const lineEndIdx = content.indexOf("\n", m.index);
      const lineEnd = lineEndIdx === -1 ? content.length : lineEndIdx;
      errorCodes.set(m[0], content.slice(lineStart, lineEnd).trim().slice(0, 200));
    }
  }

  return {
    classCount: files.length,
    classNames: files
      .map((f) => path.relative(decompiledDir, f).replace(/\.java$/, "").split(path.sep).join("."))
      .sort(),
    fieldLabels,
    errorCodes,
  };
}

function diffSets(baseline: Set<string> | string[], updated: Set<string> | string[]) {
  const baseSet = baseline instanceof Set ? baseline : new Set(baseline);
  const updSet = updated instanceof Set ? updated : new Set(updated);
  return {
    added: [...updSet].filter((x) => !baseSet.has(x)).sort(),
    removed: [...baseSet].filter((x) => !updSet.has(x)).sort(),
  };
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

  const fieldDiff = diffSets(baseline.fieldLabels, uploaded.fieldLabels);
  const codeDiff = diffSets(new Set(baseline.errorCodes.keys()), new Set(uploaded.errorCodes.keys()));
  const classDiff = diffSets(baseline.classNames, uploaded.classNames);

  return {
    baselineJar: path.relative(PROJECT_ROOT, BASELINE_JAR),
    uploadedJarPath: path.relative(PROJECT_ROOT, uploadedJarPath),
    classCounts: { baseline: baseline.classCount, uploaded: uploaded.classCount },
    classesAdded: classDiff.added,
    classesRemoved: classDiff.removed,
    fieldLabels: {
      added: fieldDiff.added,
      removed: fieldDiff.removed,
      unchangedCount: uploaded.fieldLabels.size - fieldDiff.added.length,
    },
    errorCodes: {
      added: codeDiff.added.map((code) => ({ code, sampleLine: uploaded.errorCodes.get(code) ?? "" })),
      removed: codeDiff.removed,
    },
    decompiledOutputDir: path.relative(PROJECT_ROOT, uploadedOutDir),
  };
}
