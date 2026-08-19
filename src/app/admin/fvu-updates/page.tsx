import Link from "next/link";
import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { FvuUpdatesClient } from "./client";

export default function FvuUpdatesPage() {
  return (
    <AppShell firmName="Platform Admin">
      <PageHeader
        title="FVU/RPU Updates"
        subtitle="Upload a new FVU/RPU package from Protean to see how it differs from what's currently built in."
        actions={
          <Link href="/admin" className="text-sm text-indigo-600 hover:underline">
            &larr; All firms
          </Link>
        }
      />
      <div className="mt-6">
        <FvuUpdatesClient />
      </div>
    </AppShell>
  );
}
