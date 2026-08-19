"use client";

import { useState } from "react";
import { Button } from "@/components/ui";

export type ProductSubscriptionInfo = {
  price: number;
  billingCycle: "MONTHLY" | "YEARLY";
  endDate: string | null;
  status: "PENDING_PAYMENT" | "ACTIVE" | "CANCELLED";
  /** A renewal request is already awaiting the platform's confirmation — see the
   *  renew API route for how it's created (self-service, no gateway configured). */
  hasPendingRequest: boolean;
};

/**
 * Shows a subscribed product's expiry (or "pending payment") on the firm's own
 * dashboard, with a self-service Renew button for admins — hits the firm-side
 * renew endpoint, which generates a Razorpay payment link when one's configured,
 * or otherwise records a renewal request for the platform to confirm once paid
 * offline (extending from the current expiry either way, so paying ahead of time
 * never loses already-paid validity).
 */
export function SubscriptionRenewPanel({
  application,
  subscription,
  canRenew,
}: {
  application: string;
  subscription: ProductSubscriptionInfo | null;
  canRenew: boolean;
}) {
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [requested, setRequested] = useState(false);

  if (!subscription || subscription.status === "CANCELLED") return null;

  const isExpired =
    subscription.status === "PENDING_PAYMENT" ||
    (subscription.endDate !== null && new Date(subscription.endDate) < new Date());
  const pendingRequest = requested || subscription.hasPendingRequest;

  const handleRenew = async () => {
    setBusy(true);
    setMessage(null);
    const res = await fetch(`/api/organization/subscriptions/${application}/renew`, { method: "POST" });
    const body = await res.json();
    setBusy(false);

    if (!res.ok) {
      setMessage("Couldn't start renewal — please try again.");
      return;
    }
    if (body.available) {
      window.open(body.url, "_blank", "noopener,noreferrer");
      return;
    }
    if (body.requested) {
      setRequested(true);
      return;
    }
    setMessage("Online renewal isn't set up yet — contact us to renew this product.");
  };

  return (
    <div className="mt-2 flex flex-wrap items-center gap-2 text-xs">
      {subscription.status === "PENDING_PAYMENT" ? (
        <span className="text-amber-600">Payment pending</span>
      ) : subscription.endDate ? (
        <span className={isExpired ? "font-medium text-red-600" : "text-slate-500"}>
          {isExpired ? "Expired" : "Valid through"} {new Date(subscription.endDate).toLocaleDateString()}
        </span>
      ) : (
        <span className="text-slate-500">No expiry set</span>
      )}
      {pendingRequest ? (
        <span className="text-amber-600">Renewal requested — awaiting confirmation</span>
      ) : (
        canRenew && (
          <Button variant="secondary" className="px-2 py-1 text-xs" onClick={handleRenew} disabled={busy}>
            {busy ? "..." : "Renew"}
          </Button>
        )
      )}
      {message && <span className="text-amber-600">{message}</span>}
    </div>
  );
}
