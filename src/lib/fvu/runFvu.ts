import { spawn } from "child_process";
import path from "path";
import { parseErrorHtml, type FvuFieldError } from "./parseErrorHtml";

const PROJECT_ROOT = process.cwd();

const VENDOR_JARS = [
  "barbecue-1.5.jar",
  "bcprov-jdk15to18-1.80.jar",
  "j2ee.jar",
  "log4j-api-2.12.4.jar",
  "log4j-core-2.12.4.jar",
  "pd4ml.jar",
  "ss_css2.jar",
].map((jar) => path.join(PROJECT_ROOT, "vendor", "fvu", jar));

const CLASSPATH = [
  path.join(PROJECT_ROOT, "fvu-wrapper", "resources"),
  path.join(PROJECT_ROOT, "fvu-wrapper", "out"),
  ...VENDOR_JARS,
].join(path.delimiter);

export interface FvuRunResult {
  success: boolean;
  stage: "usage" | "input" | "exception" | "hash" | "validation" | null;
  message: string | null;
  fvuFilePath: string | null;
  statisticFilePath: string | null;
  receiptFilePath: string | null;
  errHtmlPath: string | null;
  errors: FvuFieldError[];
}

/**
 * Runs the vendor's FVU validation/hashing logic headlessly against a
 * generated statement text file, via the HeadlessRunner wrapper (see
 * /fvu-wrapper). On failure, the FVU's own err.html is parsed into
 * structured field errors.
 */
export async function runFvu(
  inputTxtPath: string,
  outputDir: string,
  baseName: string,
): Promise<FvuRunResult> {
  const javaBin = process.env.FVU_JRE_PATH || "java";

  const stdout = await new Promise<string>((resolve, reject) => {
    const child = spawn(
      javaBin,
      [
        "-cp",
        CLASSPATH,
        "com.form137efiling.fvu.HeadlessRunner",
        inputTxtPath,
        outputDir,
        baseName,
      ],
      { cwd: PROJECT_ROOT },
    );

    let out = "";
    child.stdout.on("data", (chunk) => (out += chunk));
    child.stderr.on("data", () => {
      /* the vendor code logs verbosely to stderr; only stdout's JSON line matters */
    });

    child.on("error", reject);
    child.on("close", () => resolve(out));
  });

  const jsonLine = stdout
    .split(/\r?\n/)
    .reverse()
    .find((line) => line.trim().startsWith("{"));

  if (!jsonLine) {
    return {
      success: false,
      stage: "exception",
      message: "HeadlessRunner produced no output.",
      fvuFilePath: null,
      statisticFilePath: null,
      receiptFilePath: null,
      errHtmlPath: null,
      errors: [],
    };
  }

  const parsed = JSON.parse(jsonLine) as Omit<FvuRunResult, "errors">;
  const errors = parsed.errHtmlPath ? await parseErrorHtml(parsed.errHtmlPath) : [];

  return { ...parsed, errors };
}
