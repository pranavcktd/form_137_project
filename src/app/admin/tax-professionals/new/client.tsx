"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Alert, Button, Card, FieldLabel, inputClass } from "@/components/ui";
import {
  initialSubscriptionState,
  SubscriptionProductFields,
  subscriptionStateToPayload,
  type SubscriptionFormState,
} from "@/components/subscription-fields";

export function NewTaxProfessionalForm() {
  const router = useRouter();
  const [firmName, setFirmName] = useState("");
  const [contactEmail, setContactEmail] = useState("");
  const [contactPhone, setContactPhone] = useState("");
  const [adminName, setAdminName] = useState("");
  const [adminEmail, setAdminEmail] = useState("");
  const [adminPassword, setAdminPassword] = useState("");
  const [subscriptions, setSubscriptions] = useState<SubscriptionFormState>(initialSubscriptionState());
  const [errors, setErrors] = useState<string[]>([]);
  const [submitting, setSubmitting] = useState(false);

  const updateSubscription = (application: string, patch: Partial<SubscriptionFormState[string]>) =>
    setSubscriptions((prev) => ({ ...prev, [application]: { ...prev[application], ...patch } }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setErrors([]);

    const res = await fetch("/api/admin/tax-professionals", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        firmName,
        contactEmail,
        contactPhone,
        adminName,
        adminEmail,
        adminPassword,
        subscriptions: subscriptionStateToPayload(subscriptions),
      }),
    });

    setSubmitting(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors
        ? Object.values(body.error.fieldErrors).flat()
        : [];
      const formErrors = body.error?.formErrors ?? [];
      setErrors([...formErrors, ...fieldErrors] as string[]);
      return;
    }

    router.push("/admin");
    router.refresh();
  };

  return (
    <Card className="mt-6 max-w-2xl p-6">
      <form onSubmit={handleSubmit} className="space-y-5">
        {errors.length > 0 && (
          <Alert>
            <ul className="list-inside list-disc">
              {errors.map((err, i) => (
                <li key={i}>{err}</li>
              ))}
            </ul>
          </Alert>
        )}

        <div className="space-y-1">
          <FieldLabel>Firm Name</FieldLabel>
          <input
            className={inputClass}
            required
            value={firmName}
            onChange={(e) => setFirmName(e.target.value)}
            placeholder="e.g. Sharma & Associates"
          />
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div className="space-y-1">
            <FieldLabel>Contact Email (optional)</FieldLabel>
            <input
              type="email"
              className={inputClass}
              value={contactEmail}
              onChange={(e) => setContactEmail(e.target.value)}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>Contact Phone (optional)</FieldLabel>
            <input
              className={inputClass}
              value={contactPhone}
              onChange={(e) => setContactPhone(e.target.value)}
            />
          </div>
        </div>

        <hr className="border-slate-100" />

        <h3 className="text-sm font-semibold text-slate-900">
          Firm Admin Login
        </h3>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div className="space-y-1 sm:col-span-2">
            <FieldLabel>Admin Name</FieldLabel>
            <input
              className={inputClass}
              required
              value={adminName}
              onChange={(e) => setAdminName(e.target.value)}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>Admin Email</FieldLabel>
            <input
              type="email"
              className={inputClass}
              required
              value={adminEmail}
              onChange={(e) => setAdminEmail(e.target.value)}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>Temporary Password</FieldLabel>
            <input
              type="password"
              className={inputClass}
              required
              minLength={8}
              value={adminPassword}
              onChange={(e) => setAdminPassword(e.target.value)}
            />
          </div>
        </div>

        <hr className="border-slate-100" />

        <h3 className="text-sm font-semibold text-slate-900">Subscription</h3>
        <p className="text-xs text-slate-500">
          Pick the products this firm is subscribed to, with a price, billing cycle, and
          validity window for each — they can differ per product.
        </p>
        <SubscriptionProductFields state={subscriptions} onChange={updateSubscription} />

        <Button type="submit" disabled={submitting}>
          {submitting ? "Creating..." : "Create Firm"}
        </Button>
      </form>
    </Card>
  );
}
