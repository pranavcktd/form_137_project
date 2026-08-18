/**
 * AIN check-digit validation, per the real FVU's RecordValidation.checkID:
 * the 7th digit of a 7-digit AIN must equal (first 6 digits) mod 7. This
 * isn't documented in the file-format spec sheet at all — only discovered
 * by running a generated statement through the actual FVU jar.
 */
export function ainCheckDigit(firstSixDigits: string): number {
  return Number(firstSixDigits) % 7;
}

export function isValidAin(ain: string): boolean {
  if (!/^\d{7}$/.test(ain)) return false;
  if (ain.startsWith("0")) return false;
  return Number(ain[6]) === ainCheckDigit(ain.slice(0, 6));
}
