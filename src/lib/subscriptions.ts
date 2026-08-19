import type { BillingCycle } from "@prisma/client";

/** Advances a date by one billing period — used to compute a subscription's next
 *  renewal/expiry date from its start date (or from "now" when recording a payment). */
export function addBillingCycle(date: Date, cycle: BillingCycle): Date {
  const next = new Date(date);
  if (cycle === "YEARLY") next.setFullYear(next.getFullYear() + 1);
  else next.setMonth(next.getMonth() + 1);
  return next;
}

/** A product is only actually usable while its subscription is ACTIVE and hasn't
 *  passed its end date — a PENDING_PAYMENT or CANCELLED subscription (or an ACTIVE
 *  one whose window has lapsed without renewal) doesn't count as entitled. */
export function isSubscriptionActive(subscription: { status: string; endDate: Date | null }): boolean {
  if (subscription.status !== "ACTIVE") return false;
  if (!subscription.endDate) return true;
  return subscription.endDate.getTime() >= Date.now();
}

/** The application keys (FORM137/TDS/GST) a firm can currently actually use — derived
 *  from its Subscription rows rather than a stored flag, so it can never drift out of
 *  sync with billing state. */
export async function getEntitledApplications(organizationId: string): Promise<string[]> {
  const { prisma } = await import("@/lib/prisma");
  const subscriptions = await prisma.subscription.findMany({ where: { organizationId } });
  return subscriptions.filter(isSubscriptionActive).map((s) => s.application);
}

export interface SubscriptionEntryInput {
  application: string;
  price: number;
  billingCycle: BillingCycle;
  startDate: Date | null;
  endDate: Date | null;
}

/**
 * Applies the super admin's per-product subscription edits (one entry per selected
 * application, each with its own price/billing cycle/start-end date) onto a firm's
 * Subscription rows: upserts each selected application as ACTIVE, and cancels
 * (never deletes, to keep payment history) any existing subscription for an
 * application that's no longer in the list.
 */
export async function syncFirmSubscriptions(
  organizationId: string,
  entries: SubscriptionEntryInput[],
): Promise<void> {
  const { prisma } = await import("@/lib/prisma");
  const existing = await prisma.subscription.findMany({ where: { organizationId } });
  const selectedApplications = entries.map((e) => e.application);

  await prisma.$transaction([
    ...entries.map((entry) =>
      prisma.subscription.upsert({
        where: { organizationId_application: { organizationId, application: entry.application } },
        create: {
          organizationId,
          application: entry.application,
          price: entry.price,
          billingCycle: entry.billingCycle,
          startDate: entry.startDate,
          endDate: entry.endDate,
          status: "ACTIVE",
        },
        update: {
          price: entry.price,
          billingCycle: entry.billingCycle,
          startDate: entry.startDate,
          endDate: entry.endDate,
          status: "ACTIVE",
        },
      }),
    ),
    ...existing
      .filter((s) => !selectedApplications.includes(s.application) && s.status !== "CANCELLED")
      .map((s) => prisma.subscription.update({ where: { id: s.id }, data: { status: "CANCELLED" } })),
  ]);
}

/** The platform's default price for each product (from Platform Settings > Product
 *  Pricing), used to prefill a new subscription's price when onboarding/adding a
 *  product — falls back to 0.00 for a product with no default set yet. */
export async function getDefaultPrices(): Promise<Record<string, { price: number; billingCycle: BillingCycle }>> {
  const { prisma } = await import("@/lib/prisma");
  const prices = await prisma.productPrice.findMany();
  return Object.fromEntries(
    prices.map((p) => [p.application, { price: Number(p.price), billingCycle: p.billingCycle }]),
  );
}
