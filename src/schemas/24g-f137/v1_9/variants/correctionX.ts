import type { StatementVariantConfig } from "../../types";

/**
 * Correction_X ("Cancellation") overlay — cancels an entire previously
 * accepted batch. Carries no DDO Transaction Detail records at all.
 * Source: "File format _Correction_X" sheet.
 *
 * A few fields in the source sheet show a contradiction between the
 * Mandatory column and the field's own remark text (e.g. field 29 is marked
 * "M" but its remark says "No value should be specified"). Where that
 * happens we follow the remark, since it's the more specific statement, and
 * call it out below rather than silently picking one.
 */
export const correctionX: StatementVariantConfig = {
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
        constant: "X",
        remarks: "Value should be 'X' for Correction_X (Cancellation).",
      },
      12: { mandatory: "NA", remarks: "No value should be specified." },
      13: { mandatory: "NA", remarks: "No value should be specified." },
      14: { mandatory: "NA", remarks: "No value should be specified." },
      15: { mandatory: "NA", remarks: "No value should be specified." },
      16: { mandatory: "NA", remarks: "No value should be specified." },
      17: { mandatory: "NA", remarks: "No value should be specified." },
      19: { mandatory: "NA", remarks: "No value should be specified." },
      28: { mandatory: "NA", remarks: "No value should be specified." },
      29: {
        mandatory: "NA",
        remarks:
          'Sheet marks this field "M" but its own remark says "No value should be specified" for Correction_X; treated as NA.',
      },
      30: {
        dataType: "CHAR",
        length: 15,
        mandatory: "M",
        remarks: "RRR/PRN of the regular return being cancelled.",
      },
      31: {
        remarks:
          "RRR/PRN of the previous correction if any, otherwise of the regular return, per the sheet's remark — though the Mandatory column itself reads NA.",
      },
      35: { mandatory: "NA", remarks: "No value should be specified." },
      36: { mandatory: "NA", remarks: "No value should be specified." },
      37: { mandatory: "NA", remarks: "No value should be specified." },
      38: { mandatory: "NA", remarks: "No value should be specified." },
      39: { mandatory: "NA", remarks: "No value should be specified." },
      40: { mandatory: "NA", remarks: "No value should be specified." },
      41: { mandatory: "NA", remarks: "No value should be specified." },
      42: { mandatory: "NA", remarks: "No value should be specified." },
      43: { mandatory: "NA", remarks: "No value should be specified." },
      44: { mandatory: "NA", remarks: "No value should be specified." },
      45: { mandatory: "NA", remarks: "No value should be specified." },
      51: { mandatory: "M", constant: "0", remarks: "Value should be 0." },
      52: { mandatory: "M", constant: "0.00", remarks: "Value should be 0." },
      53: { mandatory: "M", constant: "0.00", remarks: "Value should be 0." },
      54: { mandatory: "M", constant: "0", remarks: "Value should be 0." },
      55: { mandatory: "M", constant: "0.00", remarks: "Value should be 0." },
      56: { mandatory: "M", constant: "0.00", remarks: "Value should be 0." },
      57: { mandatory: "M", constant: "0", remarks: "Value should be 0." },
      58: { mandatory: "M", constant: "0.00", remarks: "Value should be 0." },
      59: { mandatory: "M", constant: "0.00", remarks: "Value should be 0." },
      60: { mandatory: "M", constant: "0", remarks: "Value should be 0." },
      61: { mandatory: "M", constant: "0.00", remarks: "Value should be 0." },
      62: { mandatory: "M", constant: "0.00", remarks: "Value should be 0." },
      64: { mandatory: "M", constant: "0", minValue: undefined, remarks: "Value should be 0." },
      65: { mandatory: "M", constant: "0.00", remarks: "Value should be 0." },
      66: { mandatory: "M", constant: "0", remarks: "Value should be 0." },
      67: { mandatory: "M", constant: "0", remarks: "Value should be 0." },
      68: { mandatory: "M", constant: "0", remarks: "Value should be 0." },
      71: {
        name: "Mobile Number",
        dataType: "INTEGER",
        length: 10,
        mandatory: "M",
        remarks: "10-digit numeric. Applicable from FY2026-27 onward.",
      },
    },
  },
  td: null,
};
