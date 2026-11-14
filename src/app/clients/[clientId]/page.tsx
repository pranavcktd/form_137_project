import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { prisma } from "@/lib/prisma";
import { ClientProfileEditor } from "./client";

export default async function ClientProfilePage({
  params,
}: {
  params: Promise<{ clientId: string }>;
}) {
  const { clientId } = await params;
  const client = await prisma.client.findUnique({ where: { id: clientId } });

  return (
    <AppShell clientNav={{ clientId, clientName: client?.departmentName, active: "profile" }}>
      <PageHeader
        title="Client Profile"
        subtitle="AIN, TAN, ministry/department, and responsible person details used on every filing."
      />
      <div className="mt-6">
        <ClientProfileEditor clientId={clientId} />
      </div>
    </AppShell>
  );
}
