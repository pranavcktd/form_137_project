import { redirect } from "next/navigation";
import { auth } from "@/lib/auth";
import { AppShell } from "@/components/app-shell";
import { PageHeader } from "@/components/ui";
import { BackupRestorePanel } from "@/components/backup-restore-panel";

export default async function BackupPage() {
  const session = await auth();
  if (!session?.user) redirect("/login");
  if (session.user.role !== "ADMIN") redirect("/");

  return (
    <AppShell>
      <PageHeader
        title="Backup & Restore"
        subtitle="Download a full backup of your firm's data, or restore from one."
      />
      <div className="mt-6">
        <BackupRestorePanel backupUrl="/api/organization/backup" restoreUrl="/api/organization/restore" />
      </div>
    </AppShell>
  );
}
