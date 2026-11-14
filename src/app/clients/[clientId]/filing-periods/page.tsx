import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { prisma } from "@/lib/prisma";
import { FilingPeriodsListClient } from "./client";

export default async function ClientFilingPeriodsPage({
  params,
}: {
  params: Promise<{ clientId: string }>;
}) {
  const { clientId } = await params;
  const client = await prisma.client.findUnique({ where: { id: clientId } });

  return (
    <AppShell clientNav={{ clientId, clientName: client?.departmentName, active: "filing-periods" }}>
      <PageHeader
        title={client?.departmentName ?? "Filing Periods"}
        subtitle={`AIN ${client?.ain ?? ""}`}
      />
      <div className="mt-6">
        <FilingPeriodsListClient clientId={clientId} />
      </div>
    </AppShell>
  );
}
