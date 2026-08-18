import Link from "next/link";
import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { ComplianceDashboardClient } from "./client";

export default function DashboardPage() {
  return (
    <AppShell>
      <PageHeader
        title="Compliance Dashboard"
        subtitle="Filing status across all your clients for a given period."
        actions={
          <Link href="/clients" className="text-sm text-indigo-600 hover:underline">
            &larr; Clients
          </Link>
        }
      />
      <div className="mt-6">
        <ComplianceDashboardClient />
      </div>
    </AppShell>
  );
}
