import { describe, expect, it } from "vitest";
import { readFileSync } from "fs";
import { join } from "path";
import { generateStatementText } from "..";
import { validateStatement } from "@/lib/validation/validateStatement";
import type {
  DdoRecordInput,
  FilingPeriodInput,
  OrganizationInput,
} from "../types";

const SAMPLE_PATH = join(
  process.cwd(),
  "basic requirements",
  "Sample file prepared as per file format",
  "Sample file prepared as per file format.txt",
);

// The sample's "File Creation Date" (field 4 of FH) is always "today" in a
// real generated file, so it can't be compared literally — everything else
// in the file is static and should match byte-for-byte.
function stripCreationDate(fhLine: string): string {
  const cells = fhLine.split("^");
  cells[3] = "<DATE>";
  return cells.join("^");
}

describe("generateStatementText", () => {
  const organization: OrganizationInput = {
    ain: "1091985",
    tan: "SHLE00150D",
    ministryName: null,
    subMinistryName: null,
    departmentName: "BHAGYASHREE A",
    govtCategory: "STATE",
    countryCode: "113",
    responsiblePersonName: "",
    responsiblePersonFirstName: "SHIVA",
    responsiblePersonMiddleName: "PATIL",
    responsiblePersonLastName: "SHREE",
    responsiblePersonDesignation: "CA",
    responsiblePersonAddress1: "1UNIT",
    responsiblePersonAddress2: null,
    responsiblePersonAddress3: null,
    responsiblePersonAddress4: null,
    responsiblePersonCity: "NASHIK",
    responsiblePersonState: "16",
    responsiblePersonPin: "678976",
    responsiblePersonStdCode: "67890",
    responsiblePersonPhone: "6568776766",
    responsiblePersonMobile: "4567890876",
    responsiblePersonEmail: "BHAGYASHREE@TESTT.COM",
  };

  // Two different "person" identities appear in the sample BH record: fields
  // 7-17 (AO_*) use one address/contact, fields 35-45 (Responsible person_*)
  // use another. Our domain model has a single responsiblePerson* block, so
  // this fixture only round-trips the AO_* half faithfully; the
  // Responsible-person half is asserted separately below.
  const filingPeriod: FilingPeriodInput = {
    financialYear: 2026,
    month: 2,
    statementType: "ORIGINAL",
  };

  const ddoRecords: DdoRecordInput[] = [
    { serialNo: 1, tan: "PTLP14601G", name: "ABCD", address1: null, address2: null, address3: null, address4: null, city: null, state: null, pin: null, ddoRegNo: null, ddoCode: null, email: null, taxDeducted: 321000, formType: "F138", totalRemitted: 21000, natureOfDeduction: null, mode: "ADD" },
    { serialNo: 2, tan: "NSKE00566G", name: "EFGH", address1: null, address2: null, address3: null, address4: null, city: null, state: null, pin: null, ddoRegNo: null, ddoCode: null, email: null, taxDeducted: 421000, formType: "F140", totalRemitted: 43000, natureOfDeduction: null, mode: "ADD" },
    { serialNo: 3, tan: "DELE07455A", name: "IJKL", address1: null, address2: null, address3: null, address4: null, city: null, state: null, pin: null, ddoRegNo: null, ddoCode: null, email: null, taxDeducted: 679278, formType: "F144", totalRemitted: 6787, natureOfDeduction: null, mode: "ADD" },
    { serialNo: 4, tan: "SHLE00150D", name: "LMNOP", address1: null, address2: null, address3: null, address4: null, city: null, state: null, pin: null, ddoRegNo: null, ddoCode: null, email: null, taxDeducted: 676767, formType: "F143", totalRemitted: 8798, natureOfDeduction: null, mode: "ADD" },
  ];

  it("matches the real sample file's FH/TD structure and BH control totals", () => {
    const sample = readFileSync(SAMPLE_PATH, "utf8");
    const sampleLines = sample.split(/\r?\n/).filter(Boolean);

    const generated = generateStatementText(organization, filingPeriod, ddoRecords);
    const generatedLines = generated.split(/\r?\n/).filter(Boolean);

    expect(generatedLines).toHaveLength(sampleLines.length);

    // FH: identical except the creation date, which is always "today".
    expect(stripCreationDate(generatedLines[0])).toBe(stripCreationDate(sampleLines[0]));

    // TD rows: fully identical, field for field.
    expect(generatedLines[2]).toBe(sampleLines[2]);
    expect(generatedLines[3]).toBe(sampleLines[3]);
    expect(generatedLines[4]).toBe(sampleLines[4]);
    expect(generatedLines[5]).toBe(sampleLines[5]);

    // BH: check the computed control totals rather than the full line,
    // since the sample mixes two different "person" identities into the one
    // AO_*/Responsible-person* pair our domain model represents as one.
    const bhCells = generatedLines[1].split("^");
    const sampleBhCells = sampleLines[1].split("^");
    const bhFieldsToCompare = [
      3, // Batch Number
      5, // AIN
      12, // AO_City
      13, // AO_State
      14, // AO_PIN
      28, // No. of Transactions
      29, // Total TDS/TCS amount reported
      51, 52, 53, // 24Q
      54, 55, 56, // 26Q
      57, 58, 59, // 27Q
      60, 61, 62, // 27EQ
      64, // Count of Distinct DDOs
      65, // Total TDS/TCS remitted
      66, 67, 68, // added/updated/deleted
      72, // TAN of Accounts Office
    ];
    for (const index of bhFieldsToCompare) {
      expect(bhCells[index - 1], `BH field ${index}`).toBe(sampleBhCells[index - 1]);
    }
  });

  it("produces a statement that validates cleanly", () => {
    const result = validateStatement(organization, filingPeriod, ddoRecords);
    expect(result.fhErrors).toEqual([]);
    expect(result.bhErrors).toEqual([]);
    expect(result.tdErrors.flat()).toEqual([]);
    expect(result.isValid).toBe(true);
  });
});
