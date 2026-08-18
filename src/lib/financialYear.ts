/** Indian FY runs April→March. Month 1-3 (Jan-Mar) belong to financialYear+1; month 4-12 belong to financialYear itself. */
export function calendarYearForFilingMonth(financialYear: number, month: number): number {
  return month <= 3 ? financialYear + 1 : financialYear;
}

export function currentFinancialYear(now: Date = new Date()): number {
  return now.getMonth() >= 3 ? now.getFullYear() : now.getFullYear() - 1;
}

/**
 * Mirrors the real FVU's own rule (error F137/F24G-FV-2058: "Month should be
 * less than or equal to current month") — a filing period can't be for a
 * calendar month that hasn't happened yet. Confirmed empirically: generating
 * a FY2026-27 statement for month 9 (Sept) while "today" was July 2026 was
 * rejected by the real FVU; month 5 (May) was accepted.
 */
export function isFutureFilingPeriod(
  financialYear: number,
  month: number,
  now: Date = new Date(),
): boolean {
  const calendarYear = calendarYearForFilingMonth(financialYear, month);
  const currentYear = now.getFullYear();
  const currentMonth = now.getMonth() + 1;

  if (calendarYear !== currentYear) return calendarYear > currentYear;
  return month > currentMonth;
}

export interface FinancialYearOption {
  value: number;
  label: string;
}

/**
 * FY options from 2005-06 (the earliest FY the schema/FVU accepts) up to the
 * current FY, newest first, with the current one called out.
 */
export function listFinancialYears(now: Date = new Date()): FinancialYearOption[] {
  const current = currentFinancialYear(now);
  const options: FinancialYearOption[] = [];
  for (let y = current; y >= 2005; y--) {
    const label = `FY ${y}-${String((y + 1) % 100).padStart(2, "0")}`;
    options.push({ value: y, label: y === current ? `${label} (Current)` : label });
  }
  return options;
}
