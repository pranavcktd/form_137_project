import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { addBillingCycle } from "@/lib/subscriptions";
import { createPaymentLink, isRazorpayConfigured } from "@/lib/razorpay";
import { applicationTypeLabel } from "@/lib/applicationTypes";
import { sendRenewalRequestAlert } from "@/lib/alerts/notify";

/**
 * A firm's own ADMIN self-initiates paying for/renewing one of their products.
 * With Razorpay configured, generates a hosted payment link. Without one, there's
 * no way to actually collect money here — instead this records a renewal
 * *request* (a PENDING manual SubscriptionPayment, the same row shape the admin's
 * Record Payment tool confirms) and emails the platform, so the super admin can
 * collect payment offline and confirm it — extending from the current expiry
 * either way, so paying in advance never loses time already paid for.
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
      include: { payments: { where: { status: "PENDING" }, take: 1 } },
    }),
    prisma.organization.findUnique({ where: { id: organizationId } }),
    isRazorpayConfigured(),
  ]);

  if (!subscription || !organization) {
    return NextResponse.json({ error: "Not found" }, { status: 404 });
  }

  const now = new Date();
  const periodStart = subscription.endDate && subscription.endDate > now ? subscription.endDate : now;
  const periodEnd = addBillingCycle(periodStart, subscription.billingCycle);

  if (!configured) {
    // Already requested and still awaiting confirmation — don't spam another one.
    if (!subscription.payments.length) {
      await prisma.subscriptionPayment.create({
        data: {
          subscriptionId: subscription.id,
          amount: subscription.price,
          method: "MANUAL",
          status: "PENDING",
          periodStart,
          periodEnd,
          notes: "Renewal requested by firm via self-service — awaiting payment collection & confirmation.",
        },
      });

      try {
        await sendRenewalRequestAlert(
          organization.name,
          applicationTypeLabel(application),
          Number(subscription.price),
          subscription.billingCycle,
        );
      } catch (err) {
        console.error("[renew] Failed to send renewal request alert:", err);
      }
    }

    return NextResponse.json({ available: false, requested: true });
  }

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
