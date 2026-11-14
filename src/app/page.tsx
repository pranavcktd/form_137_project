import Link from "next/link";
import { redirect } from "next/navigation";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { AppShell } from "@/components/app-shell";
import { Badge, Card } from "@/components/ui";
import { FvuVersionBanner } from "@/components/fvu-version-banner";
import { applicationTypeLabel } from "@/lib/applicationTypes";
import { isSubscriptionActive } from "@/lib/subscriptions";
import { SubscriptionRenewPanel, type ProductSubscriptionInfo } from "@/components/subscription-renew";

const ICON_PROPS = { viewBox: "0 0 20 20", fill: "none", className: "h-5 w-5" } as const;

function ClientsIcon() {
  return (
    <svg {...ICON_PROPS} aria-hidden="true">
      <circle cx="7" cy="6.5" r="2.3" stroke="currentColor" strokeWidth="1.4" />
      <path d="M2.8 16c.4-2.6 2.1-4 4.2-4s3.8 1.4 4.2 4" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" />
      <circle cx="14" cy="7" r="1.8" stroke="currentColor" strokeWidth="1.4" />
      <path d="M12.3 16c.3-1.9 1.4-3.2 3-3.5" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" />
    </svg>
  );
}

function ComplianceIcon() {
  return (
    <svg {...ICON_PROPS} aria-hidden="true">
      <path d="M4 16V9M10 16V4M16 16v-6" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
      <path d="M3 16.5h14" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
    </svg>
  );
}

function TeamIcon() {
  return (
    <svg {...ICON_PROPS} aria-hidden="true">
      <circle cx="6.5" cy="6" r="2.3" stroke="currentColor" strokeWidth="1.4" />
      <circle cx="13.5" cy="6" r="2.3" stroke="currentColor" strokeWidth="1.4" />
      <path
        d="M2.5 16c.4-2.6 2-4 4-4s3.6 1.4 4 4M9.5 16c.4-2.6 2-4 4-4s3.6 1.4 4 4"
        stroke="currentColor"
        strokeWidth="1.4"
        strokeLinecap="round"
      />
    </svg>
  );
}

function BackupIcon() {
  return (
    <svg {...ICON_PROPS} aria-hidden="true">
      <path
        d="M5.5 13.5a3.3 3.3 0 0 1-.5-6.56A4.2 4.2 0 0 1 13 6.1a3.5 3.5 0 0 1-.4 7.4"
        stroke="currentColor"
        strokeWidth="1.4"
        strokeLinejoin="round"
      />
      <path d="M10 9v6.5M8 13.5l2 2 2-2" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

function ItIcon() {
  return (
    <svg {...ICON_PROPS} aria-hidden="true">
      <path d="M5 3h7l3 3v11H5V3Z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round" />
      <path d="M7.5 9h5M7.5 11.5h5M7.5 14h3" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" />
    </svg>
  );
}

function TdsIcon() {
  return (
    <svg {...ICON_PROPS} aria-hidden="true">
      <circle cx="6" cy="6" r="2" stroke="currentColor" strokeWidth="1.4" />
      <circle cx="14" cy="14" r="2" stroke="currentColor" strokeWidth="1.4" />
      <path d="M14.5 5.5 5.5 14.5" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" />
    </svg>
  );
}

function GstIcon() {
  return (
    <svg {...ICON_PROPS} aria-hidden="true">
      <path d="M5 3h8l2 2v12H5V3Z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round" />
      <path d="M7 7.5h6M7 10h6M7 12.5h3.5" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" />
    </svg>
  );
}

const PRODUCTS = [
  {
    application: "FORM137",
    name: "Nex IT",
    tagline: "Income Tax filings",
    accent: { icon: ItIcon, chip: "bg-indigo-50 text-indigo-600", bar: "bg-indigo-500" },
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
    accent: { icon: TdsIcon, chip: "bg-sky-50 text-sky-600", bar: "bg-sky-500" },
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
    accent: { icon: GstIcon, chip: "bg-amber-50 text-amber-600", bar: "bg-amber-500" },
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
  { href: "/clients", label: "Clients", description: "Manage your clients and their profiles", icon: ClientsIcon },
  { href: "/dashboard", label: "Compliance Dashboard", description: "Filing status across all clients", icon: ComplianceIcon },
] as const;

const ADMIN_QUICK_LINKS = [
  {
    href: "/team",
    label: "Team",
    description: "Manage your firm's own users — view, edit, disable, reset passwords",
    icon: TeamIcon,
  },
  {
    href: "/backup",
    label: "Backup & Restore",
    description: "Download a full backup of your firm's data, or restore from one",
    icon: BackupIcon,
  },
] as const;

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
    prisma.subscription.findMany({
      where: { organizationId: session.user.organizationId },
      include: { payments: { where: { status: "PENDING" }, take: 1 } },
    }),
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
      {
        price: Number(s.price),
        billingCycle: s.billingCycle,
        endDate: s.endDate?.toISOString() ?? null,
        status: s.status,
        hasPendingRequest: s.payments.length > 0,
      },
    ]),
  );
  const canRenew = session.user.role === "ADMIN";
  const quickLinks = session.user.role === "ADMIN" ? [...QUICK_LINKS, ...ADMIN_QUICK_LINKS] : QUICK_LINKS;

  return (
    <AppShell firmName={organization?.name}>
      {/* Hero */}
      <div className="overflow-hidden rounded-2xl bg-linear-to-br from-indigo-600 via-indigo-600 to-indigo-800 px-6 py-8 text-white shadow-lg shadow-indigo-200/60 sm:px-8 sm:py-10">
        <p className="text-sm font-medium text-indigo-200">Welcome back</p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight sm:text-3xl">
          {organization?.name ?? "there"}
        </h1>
        <p className="mt-2 max-w-md text-sm text-indigo-100">
          Choose a service below to get started, or jump straight to your clients.
        </p>
      </div>

      <div className="mt-4 flex justify-end">
        <FvuVersionBanner />
      </div>

      {organization && (
        <Card className="mt-4 overflow-hidden">
          <div
            className={`h-1 w-full ${organization.status === "ACTIVE" ? "bg-emerald-500" : "bg-red-500"}`}
          />
          <div className="flex flex-wrap items-center justify-between gap-6 p-5">
            <div className="flex flex-wrap items-center gap-x-8 gap-y-3">
              <div>
                <p className="text-xs font-medium uppercase tracking-wide text-slate-400">Status</p>
                <div className="mt-1">
                  <Badge tone={organization.status === "ACTIVE" ? "green" : "red"}>
                    {organization.status === "ACTIVE" ? "Active" : "Disabled"}
                  </Badge>
                </div>
              </div>
              <div>
                <p className="text-xs font-medium uppercase tracking-wide text-slate-400">Subscribed to</p>
                <p className="mt-1 text-sm font-medium text-slate-800">
                  {entitledApplications.length
                    ? entitledApplications.map(applicationTypeLabel).join(", ")
                    : "No active subscriptions"}
                </p>
              </div>
              {nearestRenewal && (
                <div>
                  <p className="text-xs font-medium uppercase tracking-wide text-slate-400">Next renewal</p>
                  <p className="mt-1 text-sm font-medium text-slate-800">{nearestRenewal.toLocaleDateString()}</p>
                </div>
              )}
              <div>
                <p className="text-xs font-medium uppercase tracking-wide text-slate-400">Clients</p>
                <p className="mt-1 text-sm font-medium text-slate-800">{clientCount}</p>
              </div>
            </div>
            <Link
              href="/profile"
              className="shrink-0 rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition-colors hover:border-indigo-200 hover:bg-indigo-50 hover:text-indigo-700"
            >
              My Profile &rarr;
            </Link>
          </div>
        </Card>
      )}

      <div className="mt-6 grid grid-cols-1 gap-3 sm:grid-cols-2">
        {quickLinks.map((link) => (
          <Link key={link.href} href={link.href}>
            <Card className="flex items-center gap-4 p-4 transition-all hover:-translate-y-0.5 hover:border-indigo-200 hover:shadow-md">
              <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-indigo-50 text-indigo-600">
                <link.icon />
              </span>
              <div className="min-w-0 flex-1">
                <p className="font-medium text-slate-900">{link.label}</p>
                <p className="text-sm text-slate-500">{link.description}</p>
              </div>
              {link.href === "/clients" && (
                <Badge tone="indigo">
                  {clientCount} client{clientCount === 1 ? "" : "s"}
                </Badge>
              )}
            </Card>
          </Link>
        ))}
      </div>

      {PRODUCTS.map((product) => {
        const entitled = entitledApplications.includes(product.application);
        if (!entitled && hideUnsubscribedModules) return null;
        const Icon = product.accent.icon;

        return (
          <div key={product.application} className="mt-10">
            <div className="flex items-center gap-2.5">
              <span className={`flex h-7 w-7 items-center justify-center rounded-lg ${product.accent.chip}`}>
                <Icon />
              </span>
              <h2 className="text-sm font-semibold text-slate-900">{product.name}</h2>
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
                    <Card className="h-full overflow-hidden transition-all hover:-translate-y-0.5 hover:shadow-md">
                      <div className={`h-1 w-full ${product.accent.bar}`} />
                      <div className="p-5">
                        <div className="flex items-start justify-between">
                          <h3 className="font-semibold text-slate-900">{service.title}</h3>
                          <Badge tone="green">Active</Badge>
                        </div>
                        <p className="mt-1 text-sm font-medium text-indigo-600">{service.subtitle}</p>
                        <p className="mt-2 text-sm text-slate-500">{service.description}</p>
                      </div>
                    </Card>
                  </Link>
                ) : (
                  <Card key={service.key} className="h-full overflow-hidden opacity-70">
                    <div className="h-1 w-full bg-slate-200" />
                    <div className="p-5">
                      <div className="flex items-start justify-between">
                        <h3 className="font-semibold text-slate-900">{service.title}</h3>
                        <Badge>{!entitled ? "Locked" : "Not yet available"}</Badge>
                      </div>
                      <p className="mt-1 text-sm font-medium text-slate-500">{service.subtitle}</p>
                      <p className="mt-2 text-sm text-slate-500">{service.description}</p>
                    </div>
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
