/**
 * TAN check-digit validation, per the real FVU's RecordValidation.checkTan.
 * None of this is documented in the file-format spec sheet — only found by
 * running a generated statement through the actual FVU jar and reading its
 * decompiled-equivalent source:
 *
 *  - First 3 characters must be one of the Income Tax Department's
 *    Regional Computer Centre (RCC) jurisdiction codes.
 *  - 4th character: a letter (A-Z) or digit.
 *  - Next 5 characters: digits.
 *  - Last character: a check letter — (digits 5-9 as a number) mod 7 maps
 *    to one of two acceptable letters (0->A/H, 1->B/I, ... 6->G/N).
 */
const VALID_TAN_RCC = [
  "AGR", "AHM", "ALD", "AMR", "BBN", "BLR", "BPL", "BRD", "CAL", "CHE",
  "CHN", "CMB", "DEL", "HYD", "JBP", "JDH", "JLD", "JPR", "KLP", "KNP",
  "LKN", "MRI", "MRT", "MUM", "NGP", "NSK", "PNE", "PTL", "PTN", "RCH",
  "RKT", "RTK", "SHL", "SRT", "TVD", "VPN",
];

const CHECK_LETTER_PAIRS = ["AH", "BI", "CJ", "DK", "EL", "FM", "GN"];

export function isValidTan(tan: string): boolean {
  if (tan.length !== 10) return false;

  const rcc = tan.slice(0, 3);
  if (!VALID_TAN_RCC.includes(rcc)) return false;

  const fourth = tan[3];
  const isLetter = /^[A-Z]$/.test(fourth);
  const isDigit = /^[0-9]$/.test(fourth);
  if (!isLetter && !isDigit) return false;

  const midDigits = tan.slice(4, 9);
  if (!/^\d{5}$/.test(midDigits)) return false;

  const checkLetter = tan[9];
  const checkBit = Number(midDigits) % 7;
  return CHECK_LETTER_PAIRS[checkBit].includes(checkLetter);
}
