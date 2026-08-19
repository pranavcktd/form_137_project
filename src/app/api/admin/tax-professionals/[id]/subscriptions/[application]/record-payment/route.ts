import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { recordPaymentSchema } from "@/lib/validation/organization";
import { addBillingCycle } from "@/lib/subscriptions";

async function requireSuperAdmin() {
  const session = await auth();
  if (!session?.user || session.user.role !== "SUPER_ADMIN") return null;
  return session;
}

/** Manually records a payment (bank transfer, UPI, cash, etc) against one of a firm's
 *  product subscriptions — marks it ACTIVE and extends its validity by one billing
 *  cycle from whichever is later: its current end date (renewing early doesn't lose
 *  the remaining paid time) or now (a lapsed subscription renews from today). */
export async function POST(
  request: Request,
  { params }: { params: Promise<{ id: string; application: string }> },
) {
  const session = await requireSuperAdmin();
  if (!session) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const { id, application } = await params;
  const body = await request.json();
  const parsed = recordPaymentSchema.safeParse(body);
  if (!parsed.success) {
    return NextResponse.json({ error: parsed.error.flatten() }, { status: 400 });
  }

  const subscription = await prisma.subscription.findUnique({
    where: { organizationId_application: { organizationId: id, application } },
  });
  if (!subscription) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const now = new Date();
  const periodStart = subscription.endDate && subscription.endDate > now ? subscription.endDate : now;
  const periodEnd = addBillingCycle(periodStart, subscription.billingCycle);

  await prisma.$transaction([
    prisma.subscriptionPayment.create({
      data: {
        subscriptionId: subscription.id,
        amount: parsed.data.amount,
        method: "MANUAL",
        status: "PAID",
        periodStart,
        periodEnd,
        paidAt: now,
        notes: parsed.data.notes || null,
      },
    }),
    prisma.subscription.update({
      where: { id: subscription.id },
      data: {
        status: "ACTIVE",
        startDate: subscription.startDate ?? periodStart,
        endDate: periodEnd,
      },
    }),
  ]);

  return NextResponse.json({ ok: true });
}
