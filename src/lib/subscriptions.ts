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
