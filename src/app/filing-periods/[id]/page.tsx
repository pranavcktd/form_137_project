import { AppShell } from "@/components/app-shell";
import { FilingPeriodDetailClient } from "./client";

export default async function FilingPeriodDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  return (
    <AppShell>
      <FilingPeriodDetailClient filingPeriodId={id} />
    </AppShell>
  );
}
