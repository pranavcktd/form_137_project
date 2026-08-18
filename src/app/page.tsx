import Link from "next/link";
import { redirect } from "next/navigation";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { AppShell } from "@/components/app-shell";
import { Badge, Card, PageHeader } from "@/components/ui";
import { FvuVersionBanner } from "@/components/fvu-version-banner";

const SERVICES = [
  {
    key: "form137",
    title: "Form 137 / 24G",
    subtitle: "Book Adjustment Monthly Statement",
    description:
      "Prepare, validate, and generate Form 137 (Form 24G) e-filing returns for your clients.",
    href: "/clients",
    active: true,
  },
  {
    key: "form138",
    title: "Form 138 (24Q)",
    subtitle: "Salary TDS Return",
    description: "Quarterly TDS return for salary payments.",
    href: null,
    active: false,
  },
  {
    key: "form140",
    title: "Form 140 (26Q)",
    subtitle: "Non-Salary TDS Return",
    description: "Quarterly TDS return for payments other than salary.",
    href: null,
    active: false,
  },
  {
    key: "form144",
    title: "Form 144 (27Q)",
    subtitle: "Payments to Non-Residents",
    description: "Quarterly TDS return for payments to non-residents.",
    href: null,
    active: false,
  },
  {
    key: "form143",
    title: "Form 143 (27EQ)",
    subtitle: "TCS Return",
    description: "Quarterly Tax Collected at Source return.",
    href: null,
    active: false,
  },
] as const;

const QUICK_LINKS = [
  { href: "/clients", label: "Clients", description: "Manage your clients and their profiles" },
  { href: "/dashboard", label: "Compliance Dashboard", description: "Filing status across all clients" },
] as const;

export default async function Home() {
  const session = await auth();
  if (!session?.user) redirect("/login");

  if (session.user.role === "SUPER_ADMIN") {
    redirect("/admin");
  }

  const [organization, clientCount] = await Promise.all([
    prisma.organization.findUnique({ where: { id: session.user.organizationId } }),
    prisma.client.count({ where: { organizationId: session.user.organizationId } }),
  ]);

  return (
    <AppShell firmName={organization?.name}>
      <PageHeader
        title={`Welcome back${organization?.name ? `, ${organization.name}` : ""}`}
        subtitle="Choose a service to get started."
        actions={<FvuVersionBanner />}
      />

      <div className="mt-6 grid grid-cols-1 gap-3 sm:grid-cols-2">
        {QUICK_LINKS.map((link) => (
          <Link key={link.href} href={link.href}>
            <Card className="flex items-center justify-between p-4 transition-colors hover:border-indigo-200 hover:bg-indigo-50/40">
              <div>
                <p className="font-medium text-slate-900">{link.label}</p>
                <p className="text-sm text-slate-500">{link.description}</p>
              </div>
              {link.href === "/clients" && (
                <Badge tone="indigo">{clientCount} client{clientCount === 1 ? "" : "s"}</Badge>
              )}
            </Card>
          </Link>
        ))}
      </div>

      <h2 className="mt-8 text-sm font-semibold uppercase tracking-wide text-slate-500">
        Services
      </h2>
      <div className="mt-3 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {SERVICES.map((service) =>
          service.active && service.href ? (
            <Link key={service.key} href={service.href}>
              <Card className="h-full p-5 transition-all hover:-translate-y-0.5 hover:border-indigo-200 hover:shadow-md">
                <div className="flex items-start justify-between">
                  <h2 className="font-semibold text-slate-900">
                    {service.title}
                  </h2>
                  <Badge tone="green">Active</Badge>
                </div>
                <p className="mt-1 text-sm font-medium text-indigo-600">
                  {service.subtitle}
                </p>
                <p className="mt-2 text-sm text-slate-500">
                  {service.description}
                </p>
              </Card>
            </Link>
          ) : (
            <Card key={service.key} className="h-full p-5 opacity-60">
              <div className="flex items-start justify-between">
                <h2 className="font-semibold text-slate-900">
                  {service.title}
                </h2>
                <Badge>Coming soon</Badge>
              </div>
              <p className="mt-1 text-sm font-medium text-slate-500">
                {service.subtitle}
              </p>
              <p className="mt-2 text-sm text-slate-500">
                {service.description}
              </p>
            </Card>
          ),
        )}
      </div>
    </AppShell>
  );
}
