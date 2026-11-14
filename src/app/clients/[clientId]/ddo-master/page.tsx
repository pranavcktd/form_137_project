import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { prisma } from "@/lib/prisma";
import { DdoMasterListClient } from "./client";

export default async function DdoMasterPage({
  params,
}: {
  params: Promise<{ clientId: string }>;
}) {
  const { clientId } = await params;
  const client = await prisma.client.findUnique({ where: { id: clientId } });

  return (
    <AppShell clientNav={{ clientId, clientName: client?.departmentName, active: "ddo-master" }}>
      <PageHeader
        title="DDO Master"
        subtitle={`${client?.departmentName ?? ""} — reusable DDO details, selected when entering monthly transactions.`}
      />
      <div className="mt-6">
        <DdoMasterListClient clientId={clientId} />
      </div>
    </AppShell>
  );
}
