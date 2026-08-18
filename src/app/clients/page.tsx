import Link from "next/link";
import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { ClientsListClient } from "./client";

export default function ClientsPage() {
  return (
    <AppShell>
      <PageHeader
        title="Clients"
        subtitle="The Accounts Offices / DTOs / CDDOs you file Form 137 returns for."
        actions={
          <Link href="/dashboard" className="text-sm text-indigo-600 hover:underline">
            Compliance Dashboard &rarr;
          </Link>
        }
      />
      <div className="mt-6">
        <ClientsListClient />
      </div>
    </AppShell>
  );
}
