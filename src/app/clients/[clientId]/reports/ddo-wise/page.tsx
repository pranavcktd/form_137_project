import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { prisma } from "@/lib/prisma";
import { DdoWiseReportClient } from "./client";

export default async function DdoWiseReportPage({
  params,
}: {
  params: Promise<{ clientId: string }>;
}) {
  const { clientId } = await params;
  const client = await prisma.client.findUnique({ where: { id: clientId } });

  return (
    <AppShell clientNav={{ clientId, clientName: client?.departmentName, active: "reports" }}>
      <PageHeader
        title="DDO-wise Entry Details"
        actions={
          <a
            href={`/api/clients/${clientId}/reports/ddo-wise?format=xlsx`}
            className="text-sm text-indigo-600 hover:underline"
          >
            Export to Excel
          </a>
        }
      />
      <div className="mt-6">
        <DdoWiseReportClient clientId={clientId} />
      </div>
    </AppShell>
  );
}
