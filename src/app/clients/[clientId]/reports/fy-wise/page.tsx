import Link from "next/link";
import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { FyWiseReportClient } from "./client";

export default async function FyWiseReportPage({
  params,
}: {
  params: Promise<{ clientId: string }>;
}) {
  const { clientId } = await params;

  return (
    <AppShell>
      <PageHeader
        title="FY-wise DDO Transactions"
        actions={
          <Link href={`/clients/${clientId}/reports`} className="text-sm text-indigo-600 hover:underline">
            &larr; Reports
          </Link>
        }
      />
      <div className="mt-6">
        <FyWiseReportClient clientId={clientId} />
      </div>
    </AppShell>
  );
}
