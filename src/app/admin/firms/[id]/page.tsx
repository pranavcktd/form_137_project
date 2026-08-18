import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import Link from "next/link";
import { FirmDetailClient } from "./client";

export default async function FirmDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;

  return (
    <AppShell firmName="Platform Admin">
      <PageHeader
        title="Firm Details"
        subtitle="Manage this firm's profile, subscription, and user logins."
        actions={
          <Link href="/admin" className="text-sm text-indigo-600 hover:underline">
            &larr; All firms
          </Link>
        }
      />
      <div className="mt-6">
        <FirmDetailClient firmId={id} />
      </div>
    </AppShell>
  );
}
