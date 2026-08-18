import Link from "next/link";
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
    <AppShell>
      <PageHeader
        title={client?.departmentName ?? "Filing Periods"}
        subtitle={
          <>
            AIN {client?.ain} &middot;{" "}
            <Link href={`/clients/${clientId}`} className="text-indigo-600 hover:underline">
              Edit client profile
            </Link>
            {" "}&middot;{" "}
            <Link href={`/clients/${clientId}/ddo-master`} className="text-indigo-600 hover:underline">
              DDO Master
            </Link>
            {" "}&middot;{" "}
            <Link href={`/clients/${clientId}/reports`} className="text-indigo-600 hover:underline">
              Reports
            </Link>
          </>
        }
        actions={
          <Link href="/clients" className="text-sm text-indigo-600 hover:underline">
            &larr; All clients
          </Link>
        }
      />
      <div className="mt-6">
        <FilingPeriodsListClient clientId={clientId} />
      </div>
    </AppShell>
  );
}
