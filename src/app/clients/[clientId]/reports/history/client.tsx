"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Badge, Card, EmptyState, LoadingState, Pagination, inputClass } from "@/components/ui";
import { usePagination } from "@/lib/usePagination";

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

type Row = {
  id: string;
  financialYear: number;
  month: number;
  statementType: string;
  status: "DRAFT" | "LOCKED";
  ddoCount: number;
  receiptNumber: string | null;
  receiptDate: string | null;
};

export function HistoryReportClient({ clientId }: { clientId: string }) {
  const [rows, setRows] = useState<Row[] | null>(null);
  const [search, setSearch] = useState("");

  useEffect(() => {
    fetch(`/api/clients/${clientId}/reports/history`)
      .then((res) => res.json())
      .then(setRows);
  }, [clientId]);

  const searchTerm = search.trim().toLowerCase();
  const filteredRows = (rows ?? []).filter((p) =>
    searchTerm
      ? [
          MONTHS[p.month - 1],
          String(p.financialYear),
          p.statementType,
          p.status,
          p.receiptNumber,
        ].some((v) => (v ?? "").toLowerCase().includes(searchTerm))
      : true,
  );
  const rowsPage = usePagination(filteredRows, undefined, search);

  if (rows === null) return <LoadingState />;

  return (
    <div>
      {rows.length > 0 && (
        <div className="mb-4">
          <input
            className={inputClass}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by month, FY, statement type, status, receipt no..."
          />
        </div>
      )}

      <Card className="divide-y divide-slate-100">
        {rows.length === 0 && <EmptyState>No filing periods yet.</EmptyState>}
        {rows.length > 0 && filteredRows.length === 0 && (
          <EmptyState>No filing periods match &ldquo;{search}&rdquo;.</EmptyState>
        )}
        {rowsPage.pageItems.map((p) => (
          <div key={p.id} className="flex items-center justify-between p-4 hover:bg-slate-50">
            <div>
              <p className="font-medium text-slate-900">
                FY {p.financialYear}-{String((p.financialYear + 1) % 100).padStart(2, "0")} &middot;{" "}
                {MONTHS[p.month - 1]}
              </p>
              <p className="text-sm text-slate-500">
                {p.statementType} &middot; {p.ddoCount} DDO record(s)
                {p.receiptNumber && ` · Receipt: ${p.receiptNumber}`}
                {p.receiptDate && ` (${new Date(p.receiptDate).toLocaleDateString()})`}
              </p>
            </div>
            <div className="flex items-center gap-2">
              <Badge tone={p.status === "LOCKED" ? "green" : "amber"}>
                {p.status === "LOCKED" ? "Locked" : "Draft"}
              </Badge>
              <Link href={`/filing-periods/${p.id}`} className="text-sm text-indigo-600 hover:underline">
                View
              </Link>
            </div>
          </div>
        ))}
        <Pagination
          page={rowsPage.page}
          totalPages={rowsPage.totalPages}
          onPageChange={rowsPage.setPage}
          totalItems={rowsPage.totalItems}
          pageSize={rowsPage.pageSize}
        />
      </Card>
    </div>
  );
}
