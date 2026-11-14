import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { prisma } from "@/lib/prisma";
import { FyWiseReportClient } from "./client";

export default async function FyWiseReportPage({
  params,
}: {
  params: Promise<{ clientId: string }>;
}) {
  const { clientId } = await params;
  const client = await prisma.client.findUnique({ where: { id: clientId } });

  return (
    <AppShell clientNav={{ clientId, clientName: client?.departmentName, active: "reports" }}>
      <PageHeader title="FY-wise DDO Transactions" />
      <div className="mt-6">
        <FyWiseReportClient clientId={clientId} />
      </div>
    </AppShell>
  );
}
