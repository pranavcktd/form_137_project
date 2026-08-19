import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { addBillingCycle } from "@/lib/subscriptions";
import { createPaymentLink, isRazorpayConfigured } from "@/lib/razorpay";
import { applicationTypeLabel } from "@/lib/applicationTypes";

/**
 * A firm's own ADMIN self-initiates paying for/renewing one of their products —
 * generates a Razorpay hosted payment link for its current price. Returns
 * `{available: false}` rather than an error when Razorpay isn't configured yet,
 * since that's an expected, non-broken state (self-service renewal simply isn't
 * live yet) rather than a failure the caller did something wrong to cause.
 */
export async function POST(
  request: Request,
  { params }: { params: Promise<{ application: string }> },
) {
  const session = await auth();
  if (!session?.user) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  if (session.user.role !== "ADMIN") {
    return NextResponse.json({ error: "Only your firm's admin can manage billing" }, { status: 403 });
  }

  const { application } = await params;
  const organizationId = session.user.organizationId;

  const [subscription, organization, configured] = await Promise.all([
    prisma.subscription.findUnique({
      where: { organizationId_application: { organizationId, application } },
    }),
    prisma.organization.findUnique({ where: { id: organizationId } }),
    isRazorpayConfigured(),
  ]);

  if (!subscription || !organization) {
    return NextResponse.json({ error: "Not found" }, { status: 404 });
  }
  if (!configured) {
    return NextResponse.json({ available: false });
  }

  const now = new Date();
  const periodStart = subscription.endDate && subscription.endDate > now ? subscription.endDate : now;
  const periodEnd = addBillingCycle(periodStart, subscription.billingCycle);

  const link = await createPaymentLink({
    amountRupees: Number(subscription.price),
    description: `${applicationTypeLabel(application)} subscription renewal — ${organization.name}`,
    customerName: organization.name,
    customerEmail: organization.contactEmail || session.user.email || "",
    referenceId: `sub_${subscription.id}_${now.getTime()}`,
  });

  await prisma.subscriptionPayment.create({
    data: {
      subscriptionId: subscription.id,
      amount: subscription.price,
      method: "RAZORPAY",
      status: "PENDING",
      periodStart,
      periodEnd,
      razorpayPaymentLinkId: link.id,
      razorpayPaymentLinkUrl: link.shortUrl,
    },
  });

  return NextResponse.json({ available: true, url: link.shortUrl });
}
