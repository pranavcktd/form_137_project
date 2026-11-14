import { AppShell } from "@/components/app-shell";
import { FvuVersionBanner } from "@/components/fvu-version-banner";
import { prisma } from "@/lib/prisma";
import { FilingPeriodDetailClient } from "./client";

export default async function FilingPeriodDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const filingPeriod = await prisma.filingPeriod.findUnique({
    where: { id },
    select: { clientId: true, client: { select: { departmentName: true } } },
  });

  return (
    <AppShell
      clientNav={
        filingPeriod
          ? { clientId: filingPeriod.clientId, clientName: filingPeriod.client.departmentName, active: "filing-periods" }
          : undefined
      }
    >
      <FilingPeriodDetailClient filingPeriodId={id} fvuBanner={<FvuVersionBanner />} />
    </AppShell>
  );
}
