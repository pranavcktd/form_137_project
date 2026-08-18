import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { TeamClient } from "./client";

export default function TeamPage() {
  return (
    <AppShell>
      <PageHeader
        title="Team"
        subtitle="Manage the logins for your own firm — view, edit, disable, reset passwords, or remove."
      />
      <div className="mt-6">
        <TeamClient />
      </div>
    </AppShell>
  );
}
