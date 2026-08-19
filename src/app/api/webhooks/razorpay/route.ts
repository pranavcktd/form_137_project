import { NextResponse } from "next/server";
import { prisma } from "@/lib/prisma";
import { verifyWebhookSignature } from "@/lib/razorpay";

/**
 * Razorpay calls this when a payment link's status changes. Only `payment_link.paid`
 * is handled — that's the one event that means a firm's self-service renewal actually
 * went through, so the matching SubscriptionPayment (created when the link was
 * generated) can be marked PAID and the subscription's validity extended to match.
 * The raw body must be read (not `request.json()`) so the signature can be checked
 * against exactly what Razorpay signed, before trusting anything in the payload.
 */
export async function POST(request: Request) {
  const rawBody = await request.text();
  const signature = request.headers.get("x-razorpay-signature");
  if (!signature || !(await verifyWebhookSignature(rawBody, signature))) {
    return NextResponse.json({ error: "Invalid signature" }, { status: 400 });
  }

  const event = JSON.parse(rawBody);
  if (event.event !== "payment_link.paid") {
    return NextResponse.json({ ok: true });
  }

  const linkId: string | undefined = event.payload?.payment_link?.entity?.id;
  const paymentId: string | undefined = event.payload?.payment?.entity?.id;
  if (!linkId) return NextResponse.json({ ok: true });

  const payment = await prisma.subscriptionPayment.findFirst({
    where: { razorpayPaymentLinkId: linkId, status: "PENDING" },
    include: { subscription: true },
  });
  if (!payment) return NextResponse.json({ ok: true });

  await prisma.$transaction([
    prisma.subscriptionPayment.update({
      where: { id: payment.id },
      data: { status: "PAID", paidAt: new Date(), razorpayPaymentId: paymentId || null },
    }),
    prisma.subscription.update({
      where: { id: payment.subscriptionId },
      data: {
        status: "ACTIVE",
        startDate: payment.subscription.startDate ?? payment.periodStart,
        endDate: payment.periodEnd,
      },
    }),
  ]);

  return NextResponse.json({ ok: true });
}
