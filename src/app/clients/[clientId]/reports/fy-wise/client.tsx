"use client";

import { useEffect, useState } from "react";
import { Badge, Card, EmptyState, FieldLabel, LoadingState, inputClass } from "@/components/ui";
import { listFinancialYears } from "@/lib/financialYear";

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

  const totalDeducted = rows?.reduce((sum, r) => sum + r.taxDeducted, 0) ?? 0;
  const totalRemitted = rows?.reduce((sum, r) => sum + r.totalRemitted, 0) ?? 0;

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
        {rows && rows.length > 0 && (
          <div className="ml-auto flex items-center gap-3">
            <Badge tone="indigo">Total Deducted: {totalDeducted.toFixed(2)}</Badge>
            <Badge tone="green">Total Remitted: {totalRemitted.toFixed(2)}</Badge>
          </div>
        )}
        <a
          href={`/api/clients/${clientId}/reports/fy-wise?financialYear=${financialYear}&format=xlsx`}
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
        ) : (
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
                <th>Remitted</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r, i) => (
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
                  <td className="py-1 pr-4">{r.formType}</td>
                  <td className="py-1 pr-4">{r.taxDeducted.toFixed(2)}</td>
                  <td className="py-1">{r.totalRemitted.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  );
}
