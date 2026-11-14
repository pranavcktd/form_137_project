import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { prisma } from "@/lib/prisma";
import { HistoryReportClient } from "./client";

export default async function HistoryReportPage({
  params,
}: {
  params: Promise<{ clientId: string }>;
}) {
  const { clientId } = await params;
  const client = await prisma.client.findUnique({ where: { id: clientId } });

  return (
    <AppShell clientNav={{ clientId, clientName: client?.departmentName, active: "reports" }}>
      <PageHeader
        title="Filing Compliance History"
        actions={
          <a
            href={`/api/clients/${clientId}/reports/history?format=xlsx`}
            className="text-sm text-indigo-600 hover:underline"
          >
            Export to Excel
          </a>
        }
      />
      <div className="mt-6">
        <HistoryReportClient clientId={clientId} />
      </div>
    </AppShell>
  );
}
