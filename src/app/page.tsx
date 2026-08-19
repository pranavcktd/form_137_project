import Link from "next/link";
import { redirect } from "next/navigation";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { AppShell } from "@/components/app-shell";
import { Badge, Card, PageHeader } from "@/components/ui";
import { FvuVersionBanner } from "@/components/fvu-version-banner";
import { applicationTypeLabel } from "@/lib/applicationTypes";
import { isSubscriptionActive } from "@/lib/subscriptions";
import { SubscriptionRenewPanel, type ProductSubscriptionInfo } from "@/components/subscription-renew";

const PRODUCTS = [
  {
    application: "FORM137",
    name: "Nex IT",
    tagline: "Income Tax filings",
    services: [
      {
        key: "form137",
        title: "Form 137 / 24G",
        subtitle: "Book Adjustment Monthly Statement",
        description:
          "Prepare, validate, and generate Form 137 (Form 24G) e-filing returns for your clients.",
        href: "/clients",
        built: true,
      },
    ],
  },
  {
    application: "TDS",
    name: "Nex TDS",
    tagline: "Quarterly TDS/TCS returns",
    services: [
      {
        key: "form138",
        title: "Form 138 (24Q)",
        subtitle: "Salary TDS Return",
        description: "Quarterly TDS return for salary payments.",
        href: null,
        built: false,
      },
      {
        key: "form140",
        title: "Form 140 (26Q)",
        subtitle: "Non-Salary TDS Return",
        description: "Quarterly TDS return for payments other than salary.",
        href: null,
        built: false,
      },
      {
        key: "form144",
        title: "Form 144 (27Q)",
        subtitle: "Payments to Non-Residents",
        description: "Quarterly TDS return for payments to non-residents.",
        href: null,
        built: false,
      },
      {
        key: "form143",
        title: "Form 143 (27EQ)",
        subtitle: "TCS Return",
        description: "Quarterly Tax Collected at Source return.",
        href: null,
        built: false,
      },
    ],
  },
  {
    application: "GST",
    name: "Nex GST",
    tagline: "GST return filing",
    services: [
      {
        key: "gst",
        title: "GST Returns",
        subtitle: "GSTR-1 / GSTR-3B / GSTR-9",
        description: "GST return preparation and filing.",
        href: null,
        built: false,
      },
    ],
  },
] as const;

const QUICK_LINKS = [
  { href: "/clients", label: "Clients", description: "Manage your clients and their profiles" },
  { href: "/dashboard", label: "Compliance Dashboard", description: "Filing status across all clients" },
] as const;

const ADMIN_QUICK_LINK = {
  href: "/team",
  label: "Team",
  description: "Manage your firm's own users — view, edit, disable, reset passwords",
} as const;

export default async function Home() {
  const session = await auth();
  if (!session?.user) redirect("/login");

  if (session.user.role === "SUPER_ADMIN") {
    redirect("/admin");
  }

  const [organization, clientCount, platformSettings, subscriptions] = await Promise.all([
    prisma.organization.findUnique({ where: { id: session.user.organizationId } }),
    prisma.client.count({ where: { organizationId: session.user.organizationId } }),
    prisma.platformSettings.findUnique({ where: { id: "singleton" } }),
    prisma.subscription.findMany({ where: { organizationId: session.user.organizationId } }),
  ]);
  const hideUnsubscribedModules = platformSettings?.hideUnsubscribedModules ?? false;
  const activeSubscriptions = subscriptions.filter(isSubscriptionActive);
  const entitledApplications = activeSubscriptions.map((s) => s.application);
  const activeEndDates = activeSubscriptions.map((s) => s.endDate).filter((d): d is Date => d !== null);
  const nearestRenewal = activeEndDates.length
    ? new Date(Math.min(...activeEndDates.map((d) => d.getTime())))
    : null;
  const subscriptionByApp = new Map<string, ProductSubscriptionInfo>(
    subscriptions.map((s) => [
      s.application,
      { price: Number(s.price), billingCycle: s.billingCycle, endDate: s.endDate?.toISOString() ?? null, status: s.status },
    ]),
  );
  const canRenew = session.user.role === "ADMIN";

  return (
    <AppShell firmName={organization?.name}>
      <PageHeader
        title={`Welcome back${organization?.name ? `, ${organization.name}` : ""}`}
        subtitle="Choose a service to get started."
        actions={<FvuVersionBanner />}
      />

      {organization && (
        <Card className="mt-6 p-5">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div>
              <p className="text-sm font-semibold text-slate-900">{organization.name}</p>
              <p className="mt-1 text-sm text-slate-500">
                Subscribed to:{" "}
                {entitledApplications.length
                  ? entitledApplications.map(applicationTypeLabel).join(", ")
                  : "No active subscriptions"}
              </p>
              {nearestRenewal && (
                <p className="mt-0.5 text-xs text-slate-400">
                  Next renewal due {nearestRenewal.toLocaleDateString()}
                </p>
              )}
            </div>
            <div className="flex items-center gap-2">
              <Badge tone={organization.status === "ACTIVE" ? "green" : "red"}>
                {organization.status === "ACTIVE" ? "Active" : "Disabled"}
              </Badge>
              <Link href="/profile" className="text-sm text-indigo-600 hover:underline">
                My Profile &rarr;
              </Link>
            </div>
          </div>
        </Card>
      )}

      <div className="mt-6 grid grid-cols-1 gap-3 sm:grid-cols-2">
        {(session.user.role === "ADMIN" ? [...QUICK_LINKS, ADMIN_QUICK_LINK] : QUICK_LINKS).map((link) => (
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

      {PRODUCTS.map((product) => {
        const entitled = entitledApplications.includes(product.application);
        if (!entitled && hideUnsubscribedModules) return null;

        return (
          <div key={product.application} className="mt-8">
            <div className="flex items-baseline gap-2">
              <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
                {product.name}
              </h2>
              <span className="text-xs text-slate-400">{product.tagline}</span>
              {!entitled && !subscriptionByApp.get(product.application) && (
                <Badge tone="slate">Not in your subscription</Badge>
              )}
            </div>
            <SubscriptionRenewPanel
              application={product.application}
              subscription={subscriptionByApp.get(product.application) ?? null}
              canRenew={canRenew}
            />
            <div className="mt-3 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {product.services.map((service) => {
                const active = service.built && entitled;

                return active && service.href ? (
                  <Link key={service.key} href={service.href}>
                    <Card className="h-full p-5 transition-all hover:-translate-y-0.5 hover:border-indigo-200 hover:shadow-md">
                      <div className="flex items-start justify-between">
                        <h3 className="font-semibold text-slate-900">
                          {service.title}
                        </h3>
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
                      <h3 className="font-semibold text-slate-900">
                        {service.title}
                      </h3>
                      <Badge>{!entitled ? "Locked" : "Not yet available"}</Badge>
                    </div>
                    <p className="mt-1 text-sm font-medium text-slate-500">
                      {service.subtitle}
                    </p>
                    <p className="mt-2 text-sm text-slate-500">
                      {service.description}
                    </p>
                  </Card>
                );
              })}
            </div>
          </div>
        );
      })}
    </AppShell>
  );
}
