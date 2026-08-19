"use client";

import { useEffect, useState } from "react";
import { Badge, Card, EmptyState, FieldLabel, LoadingState, Pagination, inputClass } from "@/components/ui";
import { listFinancialYears, monthsInQuarter, QUARTERS } from "@/lib/financialYear";
import { usePagination } from "@/lib/usePagination";
import { formTypeLabel } from "@/lib/formTypeLabels";

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

type Row = {
  month: number;
  statementType: string;
  periodStatus: "DRAFT" | "LOCKED";
  tan: string;
  name: string;
  formType: string | null;
  taxDeducted: number;
  totalRemitted: number;
};

export function FyWiseReportClient({ clientId }: { clientId: string }) {
  const fyOptions = listFinancialYears();
  const [financialYear, setFinancialYear] = useState(fyOptions[0]?.value);
  const [result, setResult] = useState<{ financialYear: number; rows: Row[] } | null>(null);
  const [search, setSearch] = useState("");
  const [quarter, setQuarter] = useState(0);

  useEffect(() => {
    if (!financialYear) return;
    let cancelled = false;
    fetch(`/api/clients/${clientId}/reports/fy-wise?financialYear=${financialYear}`)
      .then((res) => res.json())
      .then((data) => {
        if (!cancelled) setResult({ financialYear, rows: data.rows });
      });
    return () => {
      cancelled = true;
    };
  }, [clientId, financialYear]);

  const rows = result?.financialYear === financialYear ? result.rows : null;

  const quarterMonths = monthsInQuarter(quarter);
  const quarterRows = rows ? (quarterMonths.length ? rows.filter((r) => quarterMonths.includes(r.month)) : rows) : null;

  const totalDeducted = quarterRows?.reduce((sum, r) => sum + r.taxDeducted, 0) ?? 0;
  const totalRemitted = quarterRows?.reduce((sum, r) => sum + r.totalRemitted, 0) ?? 0;

  const searchTerm = search.trim().toLowerCase();
  const filteredRows = quarterRows
    ? searchTerm
      ? quarterRows.filter((r) => [r.tan, r.name].some((v) => (v ?? "").toLowerCase().includes(searchTerm)))
      : quarterRows
    : [];
  const rowsPage = usePagination(filteredRows, undefined, `${financialYear}::${quarter}::${search}`);

  return (
    <div>
      <Card className="flex flex-wrap items-end gap-3 p-5">
        <div className="space-y-1">
          <FieldLabel>Financial Year</FieldLabel>
          <select
            className={inputClass}
            value={financialYear}
            onChange={(e) => setFinancialYear(Number(e.target.value))}
          >
            {fyOptions.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>
        <div className="space-y-1">
          <FieldLabel>Quarter</FieldLabel>
          <select
            className={inputClass}
            value={quarter}
            onChange={(e) => setQuarter(Number(e.target.value))}
          >
            {QUARTERS.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>
        <div className="space-y-1">
          <FieldLabel>Search DDO (name or TAN)</FieldLabel>
          <input
            className={inputClass}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Type a DDO name or TAN..."
          />
        </div>
        {quarterRows && quarterRows.length > 0 && (
          <div className="ml-auto flex items-center gap-3">
            <Badge tone="indigo">Total Deducted: {totalDeducted.toFixed(2)}</Badge>
            <Badge tone="green">Total Remitted: {totalRemitted.toFixed(2)}</Badge>
            <Badge tone={Math.round((totalDeducted - totalRemitted) * 100) === 0 ? "slate" : "amber"}>
              Difference: {(totalDeducted - totalRemitted).toFixed(2)}
            </Badge>
          </div>
        )}
        <a
          href={`/api/clients/${clientId}/reports/fy-wise?financialYear=${financialYear}&quarter=${quarter}&format=xlsx`}
          className="text-sm text-indigo-600 hover:underline"
        >
          Export to Excel
        </a>
      </Card>

      <Card className="mt-4 p-4">
        {rows === null ? (
          <LoadingState />
        ) : rows.length === 0 ? (
          <EmptyState>No transactions filed for this financial year.</EmptyState>
        ) : quarterRows && quarterRows.length === 0 ? (
          <EmptyState>No transactions filed for {QUARTERS.find((q) => q.value === quarter)?.label}.</EmptyState>
        ) : filteredRows.length === 0 ? (
          <EmptyState>No transactions match &ldquo;{search}&rdquo;.</EmptyState>
        ) : (
          <>
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="text-slate-500">
                  <th className="pr-4 py-1">Month</th>
                  <th className="pr-4">Type</th>
                  <th className="pr-4">Status</th>
                  <th className="pr-4">TAN</th>
                  <th className="pr-4">DDO Name</th>
                  <th className="pr-4">Form</th>
                  <th className="pr-4">Deducted</th>
                  <th className="pr-4">Remitted</th>
                  <th>Difference</th>
                </tr>
              </thead>
              <tbody>
                {rowsPage.pageItems.map((r, i) => {
                  const difference = Math.round((r.taxDeducted - r.totalRemitted) * 100) / 100;
                  return (
                    <tr key={i} className="border-t border-slate-100">
                      <td className="py-1 pr-4">{MONTHS[r.month - 1]}</td>
                      <td className="py-1 pr-4">{r.statementType}</td>
                      <td className="py-1 pr-4">
                        <Badge tone={r.periodStatus === "LOCKED" ? "green" : "amber"}>
                          {r.periodStatus === "LOCKED" ? "Locked" : "Draft"}
                        </Badge>
                      </td>
                      <td className="py-1 pr-4">{r.tan}</td>
                      <td className="py-1 pr-4">{r.name}</td>
                      <td className="py-1 pr-4">{formTypeLabel(r.formType)}</td>
                      <td className="py-1 pr-4">{r.taxDeducted.toFixed(2)}</td>
                      <td className="py-1 pr-4">{r.totalRemitted.toFixed(2)}</td>
                      <td className={`py-1 ${difference !== 0 ? "font-medium text-amber-700" : ""}`}>
                        {difference.toFixed(2)}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
            <Pagination
              page={rowsPage.page}
              totalPages={rowsPage.totalPages}
              onPageChange={rowsPage.setPage}
              totalItems={rowsPage.totalItems}
              pageSize={rowsPage.pageSize}
            />
          </>
        )}
      </Card>
    </div>
  );
}
