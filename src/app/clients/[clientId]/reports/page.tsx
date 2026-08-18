import Link from "next/link";
import { AppShell } from "@/components/app-shell";
import { Card, PageHeader } from "@/components/ui";
import { prisma } from "@/lib/prisma";

export default async function ReportsMenuPage({
  params,
}: {
  params: Promise<{ clientId: string }>;
}) {
  const { clientId } = await params;
  const client = await prisma.client.findUnique({ where: { id: clientId } });

  const reports = [
    {
      href: `/clients/${clientId}/reports/ddo-wise`,
      title: "DDO-wise Entry Details",
      description: "Every transaction filed for each DDO, across all filing periods.",
    },
    {
      href: `/clients/${clientId}/reports/fy-wise`,
      title: "FY-wise DDO Transactions",
      description: "All DDO transactions for a selected financial year, month by month.",
    },
    {
      href: `/clients/${clientId}/reports/history`,
      title: "Filing Compliance History",
      description: "Every filing period for this client with its status and receipt details.",
    },
  ];

  return (
    <AppShell>
      <PageHeader
        title="Reports"
        subtitle={client?.departmentName}
        actions={
          <Link
            href={`/clients/${clientId}/filing-periods`}
            className="text-sm text-indigo-600 hover:underline"
          >
            &larr; Filing periods
          </Link>
        }
      />
      <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {reports.map((report) => (
          <Link key={report.href} href={report.href}>
            <Card className="h-full p-5 transition-all hover:-translate-y-0.5 hover:border-indigo-200 hover:shadow-md">
              <h2 className="font-semibold text-slate-900">{report.title}</h2>
              <p className="mt-2 text-sm text-slate-500">{report.description}</p>
            </Card>
          </Link>
        ))}
      </div>
    </AppShell>
  );
}
