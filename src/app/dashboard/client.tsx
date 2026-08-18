"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Badge, Card, EmptyState, FieldLabel, LoadingState, inputClass } from "@/components/ui";
import { listFinancialYears, currentFinancialYear } from "@/lib/financialYear";

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

type ComplianceRow = {
  clientId: string;
  departmentName: string;
  ain: string;
  filingPeriodId: string | null;
  status: "NOT_STARTED" | "DRAFT" | "LOCKED";
  ddoCount: number;
  discrepancyCount: number;
};

function statusBadge(status: ComplianceRow["status"]) {
  if (status === "LOCKED") return <Badge tone="green">Locked / Filed</Badge>;
  if (status === "DRAFT") return <Badge tone="amber">Draft</Badge>;
  return <Badge tone="red">Not started</Badge>;
}

export function ComplianceDashboardClient() {
  const [financialYear, setFinancialYear] = useState(currentFinancialYear());
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [rows, setRows] = useState<ComplianceRow[] | null>(null);

  useEffect(() => {
    let cancelled = false;
    fetch(`/api/dashboard/compliance?financialYear=${financialYear}&month=${month}`)
      .then((res) => res.json())
      .then((data) => {
        if (!cancelled) setRows(data);
      });
    return () => {
      cancelled = true;
    };
  }, [financialYear, month]);

  const loading = rows === null;

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
            {listFinancialYears().map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>
        <div className="space-y-1">
          <FieldLabel>Month</FieldLabel>
          <select
            className={inputClass}
            value={month}
            onChange={(e) => setMonth(Number(e.target.value))}
          >
            {MONTHS.map((m, i) => (
              <option key={m} value={i + 1}>
                {m}
              </option>
            ))}
          </select>
        </div>
      </Card>

      {loading ? (
        <LoadingState />
      ) : (
        <Card className="mt-6 divide-y divide-slate-100">
          {rows.length === 0 && <EmptyState>No clients yet.</EmptyState>}
          {rows.map((row) => (
            <div key={row.clientId} className="flex items-center justify-between p-4">
              <div>
                <p className="font-medium text-slate-900">{row.departmentName}</p>
                <p className="text-sm text-slate-500">
                  AIN {row.ain}
                  {row.status !== "NOT_STARTED" && ` · ${row.ddoCount} DDO record(s)`}
                </p>
              </div>
              <div className="flex items-center gap-2">
                {row.discrepancyCount > 0 && (
                  <Badge tone="amber">{row.discrepancyCount} discrepanc{row.discrepancyCount === 1 ? "y" : "ies"}</Badge>
                )}
                {statusBadge(row.status)}
                {row.filingPeriodId ? (
                  <Link
                    href={`/filing-periods/${row.filingPeriodId}`}
                    className="text-sm text-indigo-600 hover:underline"
                  >
                    View
                  </Link>
                ) : (
                  <Link
                    href={`/clients/${row.clientId}/filing-periods`}
                    className="text-sm text-indigo-600 hover:underline"
                  >
                    Start
                  </Link>
                )}
              </div>
            </div>
          ))}
        </Card>
      )}
    </div>
  );
}
