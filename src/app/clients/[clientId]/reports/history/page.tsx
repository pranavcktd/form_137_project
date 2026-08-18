import Link from "next/link";
import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { HistoryReportClient } from "./client";

export default async function HistoryReportPage({
  params,
}: {
  params: Promise<{ clientId: string }>;
}) {
  const { clientId } = await params;

  return (
    <AppShell>
      <PageHeader
        title="Filing Compliance History"
        actions={
          <div className="flex items-center gap-3">
            <a
              href={`/api/clients/${clientId}/reports/history?format=xlsx`}
              className="text-sm text-indigo-600 hover:underline"
            >
              Export to Excel
            </a>
            <Link href={`/clients/${clientId}/reports`} className="text-sm text-indigo-600 hover:underline">
              &larr; Reports
            </Link>
          </div>
        }
      />
      <div className="mt-6">
        <HistoryReportClient clientId={clientId} />
      </div>
    </AppShell>
  );
}
