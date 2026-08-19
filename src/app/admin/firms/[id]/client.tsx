"use client";

import { useEffect, useState } from "react";
import { Alert, Badge, Button, Card, FieldLabel, LoadingState, Pagination, inputClass } from "@/components/ui";
import { applicationTypeLabel } from "@/lib/applicationTypes";
import {
  initialSubscriptionState,
  SubscriptionProductFields,
  subscriptionsToFormState,
  subscriptionStateToPayload,
  type SubscriptionFormState,
} from "@/components/subscription-fields";
import { usePagination } from "@/lib/usePagination";

type FirmUser = {
  id: string;
  name: string;
  email: string;
  role: "ADMIN" | "PREPARER";
  disabled: boolean;
  lastLoginAt: string | null;
  createdAt: string;
};

type SubscriptionPayment = {
  id: string;
  amount: string;
  method: "MANUAL" | "RAZORPAY";
  status: "PENDING" | "PAID" | "FAILED" | "CANCELLED";
  periodStart: string;
  periodEnd: string;
  paidAt: string | null;
  notes: string | null;
  createdAt: string;
};

type Subscription = {
  id: string;
  application: string;
  price: string;
  billingCycle: "MONTHLY" | "YEARLY";
  startDate: string | null;
  endDate: string | null;
  status: "PENDING_PAYMENT" | "ACTIVE" | "CANCELLED";
  payments: SubscriptionPayment[];
};

type Firm = {
  id: string;
  name: string;
  contactEmail: string | null;
  contactPhone: string | null;
  status: "ACTIVE" | "DISABLED";
  subscriptions: Subscription[];
  _count: { clients: number };
  users: FirmUser[];
};

function subscriptionStatus(sub: Subscription): { label: string; tone: "green" | "amber" | "red" | "slate" } {
  if (sub.status === "CANCELLED") return { label: "Cancelled", tone: "slate" };
  if (sub.status === "PENDING_PAYMENT") return { label: "Pending payment", tone: "amber" };
  if (sub.endDate && new Date(sub.endDate) < new Date()) return { label: "Expired", tone: "red" };
  return { label: "Active", tone: "green" };
}

export function FirmDetailClient({ firmId }: { firmId: string }) {
  const [firm, setFirm] = useState<Firm | null>(null);
  const [loading, setLoading] = useState(true);

  const [firmName, setFirmName] = useState("");
  const [contactEmail, setContactEmail] = useState("");
  const [contactPhone, setContactPhone] = useState("");
  const [status, setStatus] = useState<"ACTIVE" | "DISABLED">("ACTIVE");
  const [savingFirm, setSavingFirm] = useState(false);
  const [firmErrors, setFirmErrors] = useState<string[]>([]);
  const [firmMessage, setFirmMessage] = useState<string | null>(null);

  const [subscriptions, setSubscriptions] = useState<SubscriptionFormState>(initialSubscriptionState());
  const [savingSubscriptions, setSavingSubscriptions] = useState(false);
  const [subscriptionErrors, setSubscriptionErrors] = useState<string[]>([]);
  const [subscriptionMessage, setSubscriptionMessage] = useState<string | null>(null);

  const [payingApp, setPayingApp] = useState<string | null>(null);
  const [paymentForm, setPaymentForm] = useState({ amount: "", notes: "" });
  const [recordingPayment, setRecordingPayment] = useState(false);
  const [paymentErrors, setPaymentErrors] = useState<string[]>([]);

  const [editingUserId, setEditingUserId] = useState<string | null>(null);
  const [userForm, setUserForm] = useState({ name: "", email: "", role: "PREPARER" as "ADMIN" | "PREPARER", disabled: false });
  const [userErrors, setUserErrors] = useState<string[]>([]);
  const [busyUserId, setBusyUserId] = useState<string | null>(null);
  const [resetMessage, setResetMessage] = useState<string | null>(null);

  const [showAddUser, setShowAddUser] = useState(false);
  const [newUser, setNewUser] = useState({ name: "", email: "", password: "", role: "PREPARER" as "ADMIN" | "PREPARER" });
  const [addUserErrors, setAddUserErrors] = useState<string[]>([]);
  const [addingUser, setAddingUser] = useState(false);

  const load = () => {
    fetch(`/api/admin/tax-professionals/${firmId}`)
      .then((res) => res.json())
      .then((data: Firm) => {
        setFirm(data);
        setFirmName(data.name);
        setContactEmail(data.contactEmail ?? "");
        setContactPhone(data.contactPhone ?? "");
        setStatus(data.status);
        setSubscriptions(subscriptionsToFormState(data.subscriptions));
        setLoading(false);
      });
  };

  useEffect(load, [firmId]);

  const updateSubscription = (application: string, patch: Partial<SubscriptionFormState[string]>) =>
    setSubscriptions((prev) => ({ ...prev, [application]: { ...prev[application], ...patch } }));

  const handleSaveFirm = async (e: React.FormEvent) => {
    e.preventDefault();
    setSavingFirm(true);
    setFirmErrors([]);
    setFirmMessage(null);

    const res = await fetch(`/api/admin/tax-professionals/${firmId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ firmName, contactEmail, contactPhone, status, subscriptions: subscriptionStateToPayload(firm ? subscriptionsToFormState(firm.subscriptions) : subscriptions) }),
    });
    setSavingFirm(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setFirmErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    setFirmMessage("Firm details saved.");
    load();
  };

  const handleSaveSubscriptions = async (e: React.FormEvent) => {
    e.preventDefault();
    setSavingSubscriptions(true);
    setSubscriptionErrors([]);
    setSubscriptionMessage(null);

    const res = await fetch(`/api/admin/tax-professionals/${firmId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        firmName,
        contactEmail,
        contactPhone,
        status,
        subscriptions: subscriptionStateToPayload(subscriptions),
      }),
    });
    setSavingSubscriptions(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setSubscriptionErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    setSubscriptionMessage("Subscriptions saved.");
    load();
  };

  const startRecordPayment = (application: string, price: string) => {
    setPayingApp(application);
    setPaymentForm({ amount: price, notes: "" });
    setPaymentErrors([]);
  };

  const handleRecordPayment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!payingApp) return;
    setRecordingPayment(true);
    setPaymentErrors([]);

    const res = await fetch(`/api/admin/tax-professionals/${firmId}/subscriptions/${payingApp}/record-payment`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(paymentForm),
    });
    setRecordingPayment(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setPaymentErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    setPayingApp(null);
    load();
  };

  const startEditUser = (user: FirmUser) => {
    setEditingUserId(user.id);
    setUserForm({ name: user.name, email: user.email, role: user.role, disabled: user.disabled });
    setUserErrors([]);
    setResetMessage(null);
  };

  const handleSaveUser = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingUserId) return;
    setBusyUserId(editingUserId);
    setUserErrors([]);

    const res = await fetch(`/api/admin/users/${editingUserId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(userForm),
    });
    setBusyUserId(null);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setUserErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    setEditingUserId(null);
    load();
  };

  const toggleUserDisabled = async (user: FirmUser) => {
    setBusyUserId(user.id);
    await fetch(`/api/admin/users/${user.id}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name: user.name, email: user.email, role: user.role, disabled: !user.disabled }),
    });
    setBusyUserId(null);
    load();
  };

  const handleDeleteUser = async (user: FirmUser) => {
    if (!window.confirm(`Delete the login for ${user.name} (${user.email})? This can't be undone.`)) return;
    setBusyUserId(user.id);
    await fetch(`/api/admin/users/${user.id}`, { method: "DELETE" });
    setBusyUserId(null);
    load();
  };

  const handleResetPassword = async (user: FirmUser) => {
    if (!window.confirm(`Reset ${user.name}'s password to the default? They'll be required to change it at next login.`)) {
      return;
    }
    setBusyUserId(user.id);
    setResetMessage(null);
    const res = await fetch(`/api/admin/users/${user.id}/reset-password`, { method: "POST" });
    const body = await res.json();
    setBusyUserId(null);
    setResetMessage(
      body.emailed
        ? `Password reset — an email was sent to ${user.email}.`
        : `Password reset — no email is configured, so relay this to them manually: ${user.email} / ${body.temporaryPassword}`,
    );
  };

  const handleAddUser = async (e: React.FormEvent) => {
    e.preventDefault();
    setAddingUser(true);
    setAddUserErrors([]);

    const res = await fetch(`/api/admin/tax-professionals/${firmId}/users`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newUser),
    });
    setAddingUser(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setAddUserErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    setShowAddUser(false);
    setNewUser({ name: "", email: "", password: "", role: "PREPARER" });
    load();
  };

  const usersPage = usePagination(firm?.users ?? []);

  if (loading || !firm) return <LoadingState />;

  return (
    <div className="space-y-6">
      <Card className="p-6">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-sm font-semibold text-slate-900">Firm Profile</h3>
          <Badge tone={firm.status === "ACTIVE" ? "green" : "red"}>
            {firm.status === "ACTIVE" ? "Active" : "Disabled"}
          </Badge>
        </div>
        <form onSubmit={handleSaveFirm} className="space-y-4">
          {firmErrors.length > 0 && (
            <Alert>
              <ul className="list-inside list-disc">
                {firmErrors.map((err, i) => (
                  <li key={i}>{err}</li>
                ))}
              </ul>
            </Alert>
          )}
          {firmMessage && <Alert tone="green">{firmMessage}</Alert>}

          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div className="space-y-1">
              <FieldLabel>Firm Name</FieldLabel>
              <input className={inputClass} required value={firmName} onChange={(e) => setFirmName(e.target.value)} />
            </div>
            <div className="space-y-1">
              <FieldLabel>Status</FieldLabel>
              <select
                className={inputClass}
                value={status}
                onChange={(e) => setStatus(e.target.value as "ACTIVE" | "DISABLED")}
              >
                <option value="ACTIVE">Active</option>
                <option value="DISABLED">Disabled (blocks all logins)</option>
              </select>
            </div>
            <div className="space-y-1">
              <FieldLabel>Contact Email</FieldLabel>
              <input
                type="email"
                className={inputClass}
                value={contactEmail}
                onChange={(e) => setContactEmail(e.target.value)}
              />
            </div>
            <div className="space-y-1">
              <FieldLabel>Contact Phone</FieldLabel>
              <input className={inputClass} value={contactPhone} onChange={(e) => setContactPhone(e.target.value)} />
            </div>
          </div>

          <Button type="submit" disabled={savingFirm}>
            {savingFirm ? "Saving..." : "Save Firm Details"}
          </Button>
        </form>
      </Card>

      <Card className="p-6">
        <h3 className="mb-1 text-sm font-semibold text-slate-900">Products & Subscriptions</h3>
        <p className="mb-4 text-sm text-slate-500">
          Each product has its own price, billing cycle, and validity window. Deselecting a
          product cancels it (its payment history is kept, and it can be re-added later).
        </p>
        <form onSubmit={handleSaveSubscriptions} className="space-y-4">
          {subscriptionErrors.length > 0 && (
            <Alert>
              <ul className="list-inside list-disc">
                {subscriptionErrors.map((err, i) => (
                  <li key={i}>{err}</li>
                ))}
              </ul>
            </Alert>
          )}
          {subscriptionMessage && <Alert tone="green">{subscriptionMessage}</Alert>}

          <SubscriptionProductFields state={subscriptions} onChange={updateSubscription} />

          <Button type="submit" disabled={savingSubscriptions}>
            {savingSubscriptions ? "Saving..." : "Save Subscriptions"}
          </Button>
        </form>

        {firm.subscriptions.filter((s) => s.status !== "CANCELLED").length > 0 && (
          <div className="mt-6 space-y-4 border-t border-slate-100 pt-4">
            <h4 className="text-sm font-semibold text-slate-900">Payments</h4>
            {firm.subscriptions
              .filter((s) => s.status !== "CANCELLED")
              .map((sub) => {
                const statusInfo = subscriptionStatus(sub);
                return (
                  <div key={sub.id} className="rounded-lg border border-slate-200 p-4">
                    <div className="flex flex-wrap items-center justify-between gap-2">
                      <div>
                        <p className="font-medium text-slate-900">
                          {applicationTypeLabel(sub.application)} <Badge tone={statusInfo.tone}>{statusInfo.label}</Badge>
                        </p>
                        <p className="text-sm text-slate-500">
                          &#8377;{Number(sub.price).toFixed(2)} / {sub.billingCycle === "MONTHLY" ? "month" : "year"}
                          {sub.endDate && ` · valid through ${new Date(sub.endDate).toLocaleDateString()}`}
                        </p>
                      </div>
                      <Button variant="secondary" onClick={() => startRecordPayment(sub.application, sub.price)}>
                        Record Payment
                      </Button>
                    </div>

                    {payingApp === sub.application && (
                      <form
                        onSubmit={handleRecordPayment}
                        className="mt-3 space-y-3 rounded-lg border border-slate-200 bg-slate-50 p-3"
                      >
                        {paymentErrors.length > 0 && (
                          <Alert>
                            <ul className="list-inside list-disc">
                              {paymentErrors.map((err, i) => (
                                <li key={i}>{err}</li>
                              ))}
                            </ul>
                          </Alert>
                        )}
                        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                          <div className="space-y-1">
                            <FieldLabel>Amount Received (INR)</FieldLabel>
                            <input
                              type="number"
                              min={0.01}
                              step="0.01"
                              required
                              className={inputClass}
                              value={paymentForm.amount}
                              onChange={(e) => setPaymentForm((p) => ({ ...p, amount: e.target.value }))}
                            />
                          </div>
                          <div className="space-y-1">
                            <FieldLabel>Notes (e.g. UPI/bank ref)</FieldLabel>
                            <input
                              className={inputClass}
                              value={paymentForm.notes}
                              onChange={(e) => setPaymentForm((p) => ({ ...p, notes: e.target.value }))}
                            />
                          </div>
                        </div>
                        <div className="flex gap-2">
                          <Button type="submit" disabled={recordingPayment}>
                            {recordingPayment ? "Saving..." : "Confirm Payment"}
                          </Button>
                          <Button type="button" variant="secondary" onClick={() => setPayingApp(null)}>
                            Cancel
                          </Button>
                        </div>
                      </form>
                    )}

                    {sub.payments.length > 0 && (
                      <ul className="mt-3 space-y-1 text-xs text-slate-500">
                        {sub.payments.slice(0, 5).map((p) => (
                          <li key={p.id}>
                            {new Date(p.createdAt).toLocaleDateString()} — &#8377;{Number(p.amount).toFixed(2)} via{" "}
                            {p.method === "MANUAL" ? "manual entry" : "Razorpay"}{" "}
                            <Badge tone={p.status === "PAID" ? "green" : p.status === "PENDING" ? "amber" : "red"}>
                              {p.status}
                            </Badge>
                            {p.notes && ` — ${p.notes}`}
                          </li>
                        ))}
                      </ul>
                    )}
                  </div>
                );
              })}
          </div>
        )}
      </Card>

      <Card className="p-6">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-sm font-semibold text-slate-900">
            Users ({firm.users.length}) &middot; {firm._count.clients} client(s)
          </h3>
          <Button variant="secondary" onClick={() => setShowAddUser((v) => !v)}>
            {showAddUser ? "Cancel" : "+ Add User"}
          </Button>
        </div>

        {resetMessage && (
          <div className="mb-4">
            <Alert tone="green">{resetMessage}</Alert>
          </div>
        )}

        {showAddUser && (
          <form onSubmit={handleAddUser} className="mb-6 space-y-4 rounded-lg border border-slate-200 p-4">
            {addUserErrors.length > 0 && (
              <Alert>
                <ul className="list-inside list-disc">
                  {addUserErrors.map((err, i) => (
                    <li key={i}>{err}</li>
                  ))}
                </ul>
              </Alert>
            )}
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div className="space-y-1">
                <FieldLabel>Name</FieldLabel>
                <input
                  className={inputClass}
                  required
                  value={newUser.name}
                  onChange={(e) => setNewUser((p) => ({ ...p, name: e.target.value }))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Email</FieldLabel>
                <input
                  type="email"
                  className={inputClass}
                  required
                  value={newUser.email}
                  onChange={(e) => setNewUser((p) => ({ ...p, email: e.target.value }))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Temporary Password</FieldLabel>
                <input
                  type="password"
                  className={inputClass}
                  required
                  minLength={8}
                  value={newUser.password}
                  onChange={(e) => setNewUser((p) => ({ ...p, password: e.target.value }))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Role</FieldLabel>
                <select
                  className={inputClass}
                  value={newUser.role}
                  onChange={(e) => setNewUser((p) => ({ ...p, role: e.target.value as "ADMIN" | "PREPARER" }))}
                >
                  <option value="ADMIN">Admin</option>
                  <option value="PREPARER">Preparer</option>
                </select>
              </div>
            </div>
            <Button type="submit" disabled={addingUser}>
              {addingUser ? "Adding..." : "Add User"}
            </Button>
          </form>
        )}

        <div className="divide-y divide-slate-100">
          {usersPage.pageItems.map((user) => (
            <div key={user.id} className="py-3">
              {editingUserId === user.id ? (
                <form onSubmit={handleSaveUser} className="space-y-3 rounded-lg border border-slate-200 p-3">
                  {userErrors.length > 0 && (
                    <Alert>
                      <ul className="list-inside list-disc">
                        {userErrors.map((err, i) => (
                          <li key={i}>{err}</li>
                        ))}
                      </ul>
                    </Alert>
                  )}
                  <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
                    <input
                      className={inputClass}
                      required
                      placeholder="Name"
                      value={userForm.name}
                      onChange={(e) => setUserForm((p) => ({ ...p, name: e.target.value }))}
                    />
                    <input
                      type="email"
                      className={inputClass}
                      required
                      placeholder="Email"
                      value={userForm.email}
                      onChange={(e) => setUserForm((p) => ({ ...p, email: e.target.value }))}
                    />
                    <select
                      className={inputClass}
                      value={userForm.role}
                      onChange={(e) => setUserForm((p) => ({ ...p, role: e.target.value as "ADMIN" | "PREPARER" }))}
                    >
                      <option value="ADMIN">Admin</option>
                      <option value="PREPARER">Preparer</option>
                    </select>
                  </div>
                  <div className="flex gap-2">
                    <Button type="submit" disabled={busyUserId === user.id}>
                      Save
                    </Button>
                    <Button type="button" variant="secondary" onClick={() => setEditingUserId(null)}>
                      Cancel
                    </Button>
                  </div>
                </form>
              ) : (
                <div className="flex flex-wrap items-center justify-between gap-2">
                  <div>
                    <p className="font-medium text-slate-900">
                      {user.name} <Badge tone="slate">{user.role}</Badge>{" "}
                      {user.disabled && <Badge tone="red">Disabled</Badge>}
                    </p>
                    <p className="text-sm text-slate-500">{user.email}</p>
                    <p className="text-xs text-slate-400">
                      Last login:{" "}
                      {user.lastLoginAt ? new Date(user.lastLoginAt).toLocaleString() : "never"}
                    </p>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    <Button variant="secondary" onClick={() => startEditUser(user)}>
                      Edit
                    </Button>
                    <Button
                      variant="secondary"
                      disabled={busyUserId === user.id}
                      onClick={() => toggleUserDisabled(user)}
                    >
                      {user.disabled ? "Enable" : "Disable"}
                    </Button>
                    <Button
                      variant="secondary"
                      disabled={busyUserId === user.id}
                      onClick={() => handleResetPassword(user)}
                    >
                      Reset Password
                    </Button>
                    <Button variant="danger" disabled={busyUserId === user.id} onClick={() => handleDeleteUser(user)}>
                      Delete
                    </Button>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
        <Pagination
          page={usersPage.page}
          totalPages={usersPage.totalPages}
          onPageChange={usersPage.setPage}
          totalItems={usersPage.totalItems}
          pageSize={usersPage.pageSize}
        />
      </Card>
    </div>
  );
}
