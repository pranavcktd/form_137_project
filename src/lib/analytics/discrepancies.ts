export interface DiscrepancyInput {
  tan: string;
  name: string;
  formType: string | null;
  taxDeducted: number;
  totalRemitted: number;
}

export interface Discrepancy {
  tan: string;
  name: string;
  formType: string | null;
  taxDeducted: number;
  totalRemitted: number;
  difference: number;
  message: string;
}

/**
 * Advisory (non-blocking) check: tax deducted and tax remitted don't have to
 * match exactly — remittance can legitimately lag deduction across months —
 * but a mismatch is worth a preparer's second look before generating, since
 * it's the single most common data-entry mistake in this kind of form.
 */
export function checkDiscrepancies(records: DiscrepancyInput[]): Discrepancy[] {
  return records
    .filter((r) => Math.round((r.taxDeducted - r.totalRemitted) * 100) !== 0)
    .map((r) => {
      const difference = Math.round((r.taxDeducted - r.totalRemitted) * 100) / 100;
      return {
        tan: r.tan,
        name: r.name,
        formType: r.formType,
        taxDeducted: r.taxDeducted,
        totalRemitted: r.totalRemitted,
        difference,
        message:
          difference > 0
            ? `Remitted is ₹${difference.toFixed(2)} less than deducted — confirm this is a partial/pending remittance.`
            : `Remitted is ₹${Math.abs(difference).toFixed(2)} more than deducted — check for a data-entry error.`,
      };
    });
}
