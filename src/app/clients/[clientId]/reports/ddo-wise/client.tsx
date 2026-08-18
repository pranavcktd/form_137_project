"use client";

import { useEffect, useState } from "react";
import { Badge, Card, EmptyState, LoadingState, Pagination, inputClass } from "@/components/ui";
import { usePagination } from "@/lib/usePagination";
import { formTypeLabel } from "@/lib/formTypeLabels";

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
  const [search, setSearch] = useState("");

  useEffect(() => {
    fetch(`/api/clients/${clientId}/reports/ddo-wise`)
      .then((res) => res.json())
      .then(setRows);
  }, [clientId]);

  const searchTerm = search.trim().toLowerCase();
  const filteredRows = (rows ?? []).filter((ddo) =>
    searchTerm ? [ddo.tan, ddo.name].some((v) => (v ?? "").toLowerCase().includes(searchTerm)) : true,
  );
  const rowsPage = usePagination(filteredRows, 10, search);

  if (rows === null) return <LoadingState />;

  return (
    <div className="space-y-4">
      {rows.length > 0 && (
        <Card className="p-4">
          <input
            className={inputClass}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search DDO by name or TAN..."
          />
        </Card>
      )}

      {rows.length === 0 && (
        <Card>
          <EmptyState>No DDOs in the master list yet.</EmptyState>
        </Card>
      )}
      {rows.length > 0 && filteredRows.length === 0 && (
        <Card>
          <EmptyState>No DDOs match &ldquo;{search}&rdquo;.</EmptyState>
        </Card>
      )}
      {rowsPage.pageItems.map((ddo) => (
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
              <Badge tone={Math.round((ddo.totalDeducted - ddo.totalRemitted) * 100) === 0 ? "slate" : "amber"}>
                Difference: {(ddo.totalDeducted - ddo.totalRemitted).toFixed(2)}
              </Badge>
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
                  <th className="pr-4">Remitted</th>
                  <th>Difference</th>
                </tr>
              </thead>
              <tbody>
                {ddo.transactions.map((t, i) => {
                  const difference = Math.round((t.taxDeducted - t.totalRemitted) * 100) / 100;
                  return (
                    <tr key={i} className="border-t border-slate-100">
                      <td className="py-1 pr-4">
                        {t.financialYear}-{String((t.financialYear + 1) % 100).padStart(2, "0")}
                      </td>
                      <td className="py-1 pr-4">{MONTHS[t.month - 1]}</td>
                      <td className="py-1 pr-4">{t.statementType}</td>
                      <td className="py-1 pr-4">{formTypeLabel(t.formType)}</td>
                      <td className="py-1 pr-4">{t.taxDeducted.toFixed(2)}</td>
                      <td className="py-1 pr-4">{t.totalRemitted.toFixed(2)}</td>
                      <td className={difference !== 0 ? "py-1 font-medium text-amber-700" : "py-1"}>
                        {difference.toFixed(2)}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}
        </Card>
      ))}

      {filteredRows.length > 0 && (
        <Card>
          <Pagination
            page={rowsPage.page}
            totalPages={rowsPage.totalPages}
            onPageChange={rowsPage.setPage}
            totalItems={rowsPage.totalItems}
            pageSize={rowsPage.pageSize}
          />
        </Card>
      )}
    </div>
  );
}
