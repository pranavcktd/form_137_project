import { AppShell } from "@/components/app-shell";
import { PageHeader, LinkButton } from "@/components/ui";
import { FvuVersionBanner } from "@/components/fvu-version-banner";
import { AdminFirmsListClient } from "./client";

export default function AdminPage() {
  return (
    <AppShell firmName="Platform Admin">
      <PageHeader
        title="Tax Professional Firms"
        subtitle="Onboard and manage the firms using Nex."
        actions={
          <div className="flex items-center gap-4">
            <FvuVersionBanner />
            <a
              href="/api/admin/backup"
              className="inline-flex items-center justify-center rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition-colors hover:bg-slate-50"
            >
              Download Platform Backup
            </a>
            <LinkButton href="/admin/fvu-updates" variant="secondary">
              FVU Updates
            </LinkButton>
            <LinkButton href="/admin/tax-professionals/new">
              + Onboard a firm
            </LinkButton>
          </div>
        }
      />
      <div className="mt-6">
        <AdminFirmsListClient />
      </div>
    </AppShell>
  );
}
