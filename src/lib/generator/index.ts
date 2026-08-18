import { resolveFields, fyScopeForFinancialYear } from "@/schemas/24g-f137/v1_9";
import { renderRecord, terminateLine } from "./render";
import { buildBhValues, buildFhValues, buildTdValues } from "./buildValues";
import type { DdoRecordInput, FilingPeriodInput, OrganizationInput } from "./types";

export * from "./types";

export function generateStatementText(
  organization: OrganizationInput,
  filingPeriod: FilingPeriodInput,
  ddoRecords: DdoRecordInput[],
): string {
  const fyScope = fyScopeForFinancialYear(filingPeriod.financialYear);
  const statementType = filingPeriod.statementType;

  const fhFields = resolveFields("FH", statementType, fyScope)!;
  const bhFields = resolveFields("BH", statementType, fyScope)!;
  const tdFields = resolveFields("TD", statementType, fyScope);

  let lineNumber = 1;
  const lines: string[] = [];

  lines.push(
    terminateLine(renderRecord(fhFields, lineNumber++, buildFhValues(organization))),
  );
  lines.push(
    terminateLine(
      renderRecord(
        bhFields,
        lineNumber++,
        buildBhValues(organization, filingPeriod, ddoRecords, tdFields),
      ),
    ),
  );

  if (tdFields) {
    for (const record of ddoRecords) {
      lines.push(terminateLine(renderRecord(tdFields, lineNumber++, buildTdValues(record))));
    }
  }

  return lines.join("");
}
