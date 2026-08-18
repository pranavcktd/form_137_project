import { readFile } from "fs/promises";

export interface FvuFieldError {
  lineNo: string;
  recordType: string;
  fieldName: string;
  fieldIndex: number | null;
  transactionDetailNo: string;
  errorCode: string;
  errorDescription: string;
}

/**
 * Parses the FVU's err.html — a fixed, simple 6-column HTML table (Line No,
 * Record Type, Field Name & No., Transaction Detail No, Error Code, Error
 * Description) produced by TBAFFileGenerator.createErrorFile — into
 * structured errors the UI can map back to specific fields.
 */
export async function parseErrorHtml(errHtmlPath: string): Promise<FvuFieldError[]> {
  let html: string;
  try {
    html = await readFile(errHtmlPath, "utf8");
  } catch {
    return [];
  }

  const rows = [...html.matchAll(/<TR>([\s\S]*?)<\/TR>/gi)];
  const errors: FvuFieldError[] = [];

  for (const row of rows) {
    const cells = [...row[1].matchAll(/<TD[^>]*>([\s\S]*?)<\/TD>/gi)].map((m) =>
      stripTags(m[1]).trim(),
    );
    if (cells.length < 6) continue;
    // The header row's cells contain <B> tags around plain labels; skip it
    // by checking for the literal header text rather than assuming order.
    if (cells[0].toLowerCase().includes("line no")) continue;

    const [lineNo, recordType, fieldNameAndNo, transactionDetailNo, errorCode, errorDescription] =
      cells;
    const fieldIndexMatch = fieldNameAndNo.match(/\((\d+)\)\s*$/);

    errors.push({
      lineNo,
      recordType,
      fieldName: fieldIndexMatch ? fieldNameAndNo.slice(0, fieldIndexMatch.index).trim() : fieldNameAndNo,
      fieldIndex: fieldIndexMatch ? Number(fieldIndexMatch[1]) : null,
      transactionDetailNo,
      errorCode,
      errorDescription,
    });
  }

  return errors;
}

function stripTags(value: string): string {
  return value.replace(/<[^>]*>/g, "");
}
