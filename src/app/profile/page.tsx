import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { ProfileClient } from "./client";

export default function ProfilePage() {
  return (
    <AppShell>
      <PageHeader title="My Profile" subtitle="View and update your account details." />
      <div className="mt-6">
        <ProfileClient />
      </div>
    </AppShell>
  );
}
