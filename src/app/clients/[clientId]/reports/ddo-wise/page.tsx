import Link from "next/link";
import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { DdoWiseReportClient } from "./client";

export default async function DdoWiseReportPage({
  params,
}: {
  params: Promise<{ clientId: string }>;
}) {
  const { clientId } = await params;

  return (
    <AppShell>
      <PageHeader
        title="DDO-wise Entry Details"
        actions={
          <div className="flex items-center gap-3">
            <a
              href={`/api/clients/${clientId}/reports/ddo-wise?format=xlsx`}
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
        <DdoWiseReportClient clientId={clientId} />
      </div>
    </AppShell>
  );
}
