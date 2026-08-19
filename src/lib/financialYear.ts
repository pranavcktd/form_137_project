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

export interface QuarterOption {
  value: 0 | 1 | 2 | 3 | 4;
  label: string;
  /** Calendar month numbers (1=Jan..12=Dec) belonging to this quarter; empty for "all quarters". */
  months: number[];
}

/** Q1 = Apr-Jun, Q2 = Jul-Sep, Q3 = Oct-Dec, Q4 = Jan-Mar, matching the Apr-Mar Indian FY. */
export const QUARTERS: QuarterOption[] = [
  { value: 0, label: "All quarters", months: [] },
  { value: 1, label: "Q1 (Apr-Jun)", months: [4, 5, 6] },
  { value: 2, label: "Q2 (Jul-Sep)", months: [7, 8, 9] },
  { value: 3, label: "Q3 (Oct-Dec)", months: [10, 11, 12] },
  { value: 4, label: "Q4 (Jan-Mar)", months: [1, 2, 3] },
];

export function monthsInQuarter(quarter: number): number[] {
  return QUARTERS.find((q) => q.value === quarter)?.months ?? [];
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
