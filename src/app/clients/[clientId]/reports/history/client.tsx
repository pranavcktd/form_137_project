"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Badge, Card, EmptyState, LoadingState } from "@/components/ui";

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

  useEffect(() => {
    fetch(`/api/clients/${clientId}/reports/history`)
      .then((res) => res.json())
      .then(setRows);
  }, [clientId]);

  if (rows === null) return <LoadingState />;

  return (
    <Card className="divide-y divide-slate-100">
      {rows.length === 0 && <EmptyState>No filing periods yet.</EmptyState>}
      {rows.map((p) => (
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
    </Card>
  );
}
