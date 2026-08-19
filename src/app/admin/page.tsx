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
            <LinkButton href="/admin/fvu-updates" variant="secondary">
              FVU/RPU Updates
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
