import { AppShell } from "@/components/app-shell";
import { Card, LinkButton, PageHeader, Badge } from "@/components/ui";
import { prisma } from "@/lib/prisma";
import { FvuVersionBanner } from "@/components/fvu-version-banner";

export default async function AdminPage() {
  const firms = await prisma.organization.findMany({
    where: { users: { some: { role: { in: ["ADMIN", "PREPARER"] } } } },
    orderBy: { createdAt: "desc" },
    include: {
      _count: { select: { clients: true, users: true } },
      users: { where: { role: "ADMIN" }, take: 1, select: { email: true, name: true } },
    },
  });

  return (
    <AppShell firmName="Platform Admin">
      <PageHeader
        title="Tax Professional Firms"
        subtitle="Onboard and manage the firms using Form137 Suite."
        actions={
          <div className="flex items-center gap-4">
            <FvuVersionBanner />
            <LinkButton href="/admin/tax-professionals/new">
              + Onboard a firm
            </LinkButton>
          </div>
        }
      />

      <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
        <Card className="p-5">
          <p className="text-sm text-slate-500">Firms</p>
          <p className="mt-1 text-2xl font-semibold text-slate-900">{firms.length}</p>
        </Card>
        <Card className="p-5">
          <p className="text-sm text-slate-500">Total Clients</p>
          <p className="mt-1 text-2xl font-semibold text-slate-900">
            {firms.reduce((sum, f) => sum + f._count.clients, 0)}
          </p>
        </Card>
        <Card className="p-5">
          <p className="text-sm text-slate-500">Total Users</p>
          <p className="mt-1 text-2xl font-semibold text-slate-900">
            {firms.reduce((sum, f) => sum + f._count.users, 0)}
          </p>
        </Card>
      </div>

      <Card className="mt-6 divide-y divide-slate-100">
        {firms.length === 0 && (
          <div className="p-10 text-center text-sm text-slate-500">
            No Tax Professional firms yet. Onboard the first one to get started.
          </div>
        )}
        {firms.map((firm) => (
          <div key={firm.id} className="flex items-center justify-between p-4 hover:bg-slate-50">
            <div>
              <p className="font-medium text-slate-900">{firm.name}</p>
              <p className="text-sm text-slate-500">
                {firm.users[0]?.name} &middot; {firm.users[0]?.email}
              </p>
            </div>
            <div className="flex items-center gap-2">
              <Badge tone="indigo">{firm._count.clients} client(s)</Badge>
              <Badge>{firm._count.users} user(s)</Badge>
            </div>
          </div>
        ))}
      </Card>
    </AppShell>
  );
}
