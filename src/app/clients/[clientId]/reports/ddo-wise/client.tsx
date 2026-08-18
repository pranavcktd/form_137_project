"use client";

import { useEffect, useState } from "react";
import { Badge, Card, EmptyState, LoadingState } from "@/components/ui";

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

type Row = {
  ddoMasterId: string;
  tan: string;
  name: string;
  transactions: Array<{
    financialYear: number;
    month: number;
    statementType: string;
    formType: string | null;
    taxDeducted: number;
    totalRemitted: number;
  }>;
  totalDeducted: number;
  totalRemitted: number;
};

export function DdoWiseReportClient({ clientId }: { clientId: string }) {
  const [rows, setRows] = useState<Row[] | null>(null);

  useEffect(() => {
    fetch(`/api/clients/${clientId}/reports/ddo-wise`)
      .then((res) => res.json())
      .then(setRows);
  }, [clientId]);

  if (rows === null) return <LoadingState />;

  return (
    <div className="space-y-4">
      {rows.length === 0 && (
        <Card>
          <EmptyState>No DDOs in the master list yet.</EmptyState>
        </Card>
      )}
      {rows.map((ddo) => (
        <Card key={ddo.ddoMasterId} className="p-5">
          <div className="flex items-center justify-between">
            <div>
              <p className="font-medium text-slate-900">
                {ddo.tan} &mdash; {ddo.name}
              </p>
              <p className="text-sm text-slate-500">
                {ddo.transactions.length} transaction(s)
              </p>
            </div>
            <div className="flex gap-2">
              <Badge tone="indigo">Deducted: {ddo.totalDeducted.toFixed(2)}</Badge>
              <Badge tone="green">Remitted: {ddo.totalRemitted.toFixed(2)}</Badge>
            </div>
          </div>
          {ddo.transactions.length > 0 && (
            <table className="mt-3 w-full text-left text-sm">
              <thead>
                <tr className="text-slate-500">
                  <th className="pr-4">FY</th>
                  <th className="pr-4">Month</th>
                  <th className="pr-4">Type</th>
                  <th className="pr-4">Form</th>
                  <th className="pr-4">Deducted</th>
                  <th>Remitted</th>
                </tr>
              </thead>
              <tbody>
                {ddo.transactions.map((t, i) => (
                  <tr key={i} className="border-t border-slate-100">
                    <td className="py-1 pr-4">
                      {t.financialYear}-{String((t.financialYear + 1) % 100).padStart(2, "0")}
                    </td>
                    <td className="py-1 pr-4">{MONTHS[t.month - 1]}</td>
                    <td className="py-1 pr-4">{t.statementType}</td>
                    <td className="py-1 pr-4">{t.formType}</td>
                    <td className="py-1 pr-4">{t.taxDeducted.toFixed(2)}</td>
                    <td className="py-1">{t.totalRemitted.toFixed(2)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </Card>
      ))}
    </div>
  );
}
