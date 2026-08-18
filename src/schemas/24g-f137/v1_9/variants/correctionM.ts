import type { StatementVariantConfig } from "../../types";

/**
 * Correction_M ("Modification") overlay — only fields that differ from the
 * Original variant are listed. Source: "File format _Correction_M" sheet.
 */
export const correctionM: StatementVariantConfig = {
  fh: {
    fields: {
      5: { constant: "C", remarks: 'Value should be "C" (Correction).' },
    },
  },
  bh: {
    fields: {
      4: {
        dataType: "CHAR",
        length: 1,
        mandatory: "M",
        constant: "M",
        remarks: "Value should be 'M' for Correction_M (Modification).",
      },
      24: {
        name: "Batch Updation Indicator",
        dataType: "INTEGER",
        length: 1,
        mandatory: "M",
        remarks:
          "1 = batch updation required, 0 = not required. Only provided for Correction_M; no value for Original or Correction_X.",
      },
      28: {
        minValue: 0,
        remarks: "Number of DDO records present in the batch. Count >= 0.",
      },
      30: {
        remarks:
          "Original PRN (Provisional Receipt Number) of the regular return being corrected should be provided.",
      },
      31: {
        remarks:
          "First correction: original PRN. Subsequent corrections: PRN of the last accepted correction.",
      },
      34: { dataType: "CHAR" },
      64: {
        minValue: 0,
        remarks:
          "Distinct TANs across TD records in the batch. Count >= 0 (0 if no TD records, or all DDO TANs are invalid).",
      },
      71: {
        name: "Mobile Number",
        dataType: "INTEGER",
        length: 10,
        mandatory: "M",
        remarks: "10-digit numeric. Applicable from FY2026-27 onward.",
      },
    },
  },
  td: {
    fields: {
      4: {
        name: "Revision Mode",
        dataType: "CHAR",
        length: 1,
        mandatory: "M",
        annexureRef: "revisionMode",
        pattern: "^[NDU]$",
        remarks:
          "N = addition of new DDO record, D = deletion, U = update of an existing DDO record.",
      },
      5: {
        remarks:
          "Strictly increasing. For mode D the serial no. must already exist; for mode N it must not already exist.",
      },
      6: {
        name: "LAST DDO TAN",
        dataType: "CHAR",
        length: 10,
        mandatory: "O",
        pattern: "^[A-Z0-9]{4}[0-9]{5}[A-Z]$",
        remarks: "Mandatory when Revision Mode = U; must be NULL otherwise.",
      },
      25: {
        name: "LAST Total TDS/TCS remitted to Government account (AG/Pr CCA)",
        dataType: "DECIMAL",
        length: 15,
        mandatory: "O",
        remarks: "Mandatory when Revision Mode = U; NULL otherwise.",
      },
      26: {
        name: "LAST DDO registration no.",
        dataType: "CHAR",
        length: 10,
        mandatory: "O",
        remarks: "Mandatory when Revision Mode = U; NULL otherwise.",
      },
      27: {
        name: "LAST DDO code",
        dataType: "CHAR",
        length: 20,
        mandatory: "O",
        remarks: "Mandatory when Revision Mode = U; NULL otherwise.",
      },
      28: {
        name: "Last TDS/TCS deducted Amount",
        dataType: "DECIMAL",
        length: [15, 2],
        mandatory: "O",
        remarks: "Mandatory when Revision Mode = U; NULL otherwise.",
      },
      29: {
        name: "Last NAT of DED",
        dataType: "CHAR",
        length: 4,
        mandatory: "O",
        annexureRef: "natureOfPayment",
        remarks: "Mandatory when Revision Mode = U; NULL otherwise.",
      },
    },
  },
};
