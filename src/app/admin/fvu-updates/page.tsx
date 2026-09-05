import Link from "next/link";
import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { FvuUpdatesClient } from "./client";

export default function FvuUpdatesPage() {
  return (
    <AppShell firmName="Platform Admin">
      <PageHeader
        title="FVU Updates"
        subtitle="Upload a new FVU package (Form 137/24G) from Protean to see how it differs from what's currently built in. RPU (the separate TDS return tool) isn't supported here yet — Nex doesn't file TDS returns yet either."
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
