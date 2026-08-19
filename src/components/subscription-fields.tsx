"use client";

import { Card, FieldLabel, inputClass } from "@/components/ui";
import { APPLICATION_TYPES } from "@/lib/applicationTypes";

export type ProductSubscriptionState = {
  selected: boolean;
  price: string;
  billingCycle: "MONTHLY" | "YEARLY";
  startDate: string;
  endDate: string;
};

export type SubscriptionFormState = Record<string, ProductSubscriptionState>;

/** One entry per product, FORM137 pre-selected — the sensible default for a brand
 *  new firm, since it's the only product actually built today. */
export function initialSubscriptionState(): SubscriptionFormState {
  return Object.fromEntries(
    APPLICATION_TYPES.map((app) => [
      app.key,
      { selected: app.key === "FORM137", price: "", billingCycle: "MONTHLY" as const, startDate: "", endDate: "" },
    ]),
  );
}

/** Populates the per-product editor's state from a firm's actual Subscription rows
 *  (e.g. loaded from the edit-firm API) — a cancelled or missing subscription for a
 *  product starts out unchecked with blank fields, ready to be filled in if re-added. */
export function subscriptionsToFormState(
  subscriptions: {
    application: string;
    price: string | number;
    billingCycle: "MONTHLY" | "YEARLY";
    startDate: string | null;
    endDate: string | null;
    status: string;
  }[],
): SubscriptionFormState {
  const byApp = new Map(subscriptions.map((s) => [s.application, s]));
  return Object.fromEntries(
    APPLICATION_TYPES.map((app) => {
      const sub = byApp.get(app.key);
      return [
        app.key,
        {
          selected: Boolean(sub) && sub!.status !== "CANCELLED",
          price: sub ? String(sub.price) : "",
          billingCycle: sub?.billingCycle ?? "MONTHLY",
          startDate: sub?.startDate ? sub.startDate.slice(0, 10) : "",
          endDate: sub?.endDate ? sub.endDate.slice(0, 10) : "",
        },
      ];
    }),
  );
}

export function subscriptionStateToPayload(state: SubscriptionFormState) {
  return Object.entries(state)
    .filter(([, v]) => v.selected)
    .map(([application, v]) => ({
      application,
      price: v.price || "0",
      billingCycle: v.billingCycle,
      startDate: v.startDate,
      endDate: v.endDate,
    }));
}

/** Per-product subscription editor: one card per product with a subscribe checkbox,
 *  price, billing cycle, and its own start/end date — used by both the firm
 *  onboarding form and the edit-firm page so the two stay visually consistent. */
export function SubscriptionProductFields({
  state,
  onChange,
}: {
  state: SubscriptionFormState;
  onChange: (application: string, patch: Partial<ProductSubscriptionState>) => void;
}) {
  return (
    <div className="space-y-3">
      {APPLICATION_TYPES.map((app) => {
        const entry = state[app.key];
        return (
          <Card key={app.key} className={`p-4 ${entry.selected ? "border-indigo-200" : ""}`}>
            <label className="flex items-start gap-2 text-sm text-slate-700">
              <input
                type="checkbox"
                className="mt-1"
                checked={entry.selected}
                onChange={(e) => onChange(app.key, { selected: e.target.checked })}
              />
              <span>
                <span className="font-medium">{app.label}</span>
                <span className="block text-xs text-slate-500">{app.description}</span>
              </span>
            </label>

            {entry.selected && (
              <div className="mt-3 grid grid-cols-2 gap-3 sm:grid-cols-4">
                <div className="space-y-1">
                  <FieldLabel>Price (INR)</FieldLabel>
                  <input
                    type="number"
                    min={0}
                    step="0.01"
                    className={inputClass}
                    value={entry.price}
                    onChange={(e) => onChange(app.key, { price: e.target.value })}
                    placeholder="0.00"
                  />
                </div>
                <div className="space-y-1">
                  <FieldLabel>Billing Cycle</FieldLabel>
                  <select
                    className={inputClass}
                    value={entry.billingCycle}
                    onChange={(e) => onChange(app.key, { billingCycle: e.target.value as "MONTHLY" | "YEARLY" })}
                  >
                    <option value="MONTHLY">Monthly</option>
                    <option value="YEARLY">Yearly</option>
                  </select>
                </div>
                <div className="space-y-1">
                  <FieldLabel>Start Date</FieldLabel>
                  <input
                    type="date"
                    className={inputClass}
                    value={entry.startDate}
                    onChange={(e) => onChange(app.key, { startDate: e.target.value })}
                  />
                </div>
                <div className="space-y-1">
                  <FieldLabel>End Date</FieldLabel>
                  <input
                    type="date"
                    className={inputClass}
                    value={entry.endDate}
                    onChange={(e) => onChange(app.key, { endDate: e.target.value })}
                  />
                </div>
              </div>
            )}
          </Card>
        );
      })}
    </div>
  );
}
