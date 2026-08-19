"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Badge, Button, Card, EmptyState, LoadingState, Pagination, inputClass } from "@/components/ui";
import { applicationTypeLabel } from "@/lib/applicationTypes";
import { subscriptionsToFormState, subscriptionStateToPayload } from "@/components/subscription-fields";
import { usePagination } from "@/lib/usePagination";

type Subscription = {
  application: string;
  price: string;
  billingCycle: "MONTHLY" | "YEARLY";
  startDate: string | null;
  endDate: string | null;
  status: "PENDING_PAYMENT" | "ACTIVE" | "CANCELLED";
};

type Firm = {
  id: string;
  name: string;
  status: "ACTIVE" | "DISABLED";
  contactEmail: string | null;
  contactPhone: string | null;
  subscriptions: Subscription[];
  _count: { clients: number; users: number };
  users: { email: string; name: string }[];
};

function isActive(sub: Subscription): boolean {
  return sub.status === "ACTIVE" && (!sub.endDate || new Date(sub.endDate) >= new Date());
}

export function AdminFirmsListClient() {
  const [firms, setFirms] = useState<Firm[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [busyId, setBusyId] = useState<string | null>(null);

  const load = () => {
    fetch("/api/admin/tax-professionals")
      .then((res) => res.json())
      .then((data) => {
        setFirms(data);
        setLoading(false);
      });
  };

  useEffect(load, []);

  const toggleStatus = async (firm: Firm) => {
    setBusyId(firm.id);
    await fetch(`/api/admin/tax-professionals/${firm.id}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        firmName: firm.name,
        contactEmail: firm.contactEmail ?? "",
        contactPhone: firm.contactPhone ?? "",
        status: firm.status === "ACTIVE" ? "DISABLED" : "ACTIVE",
        subscriptions: subscriptionStateToPayload(subscriptionsToFormState(firm.subscriptions)),
      }),
    });
    setBusyId(null);
    load();
  };

  const handleDelete = async (firm: Firm) => {
    if (
      !window.confirm(
        `Delete "${firm.name}"? This permanently deletes all ${firm._count.clients} client(s), their filing periods, DDO records, and generated files. This can't be undone.`,
      )
    ) {
      return;
    }
    setBusyId(firm.id);
    await fetch(`/api/admin/tax-professionals/${firm.id}`, { method: "DELETE" });
    setBusyId(null);
    load();
  };

  const searchTerm = search.trim().toLowerCase();
  const filtered = searchTerm
    ? firms.filter((f) =>
        [f.name, f.contactEmail, f.users[0]?.name, f.users[0]?.email].some((v) =>
          (v ?? "").toLowerCase().includes(searchTerm),
        ),
      )
    : firms;
  const firmsPage = usePagination(filtered, undefined, search);

  if (loading) return <LoadingState />;

  return (
    <div>
      <div className="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
        <Card className="p-5">
          <p className="text-sm text-slate-500">Firms</p>
          <p className="mt-1 text-2xl font-semibold text-slate-900">{firms.length}</p>
        </Card>
        <Card className="p-5">
          <p className="text-sm text-slate-500">Total Clients</p>
          <p className="mt-1 text-2xl font-semibold text-slate-900">
            {firms.reduce((sum, f) => sum + f._count.clients, 0)}
          </p>
        </Card>
        <Card className="p-5">
          <p className="text-sm text-slate-500">Total Users</p>
          <p className="mt-1 text-2xl font-semibold text-slate-900">
            {firms.reduce((sum, f) => sum + f._count.users, 0)}
          </p>
        </Card>
      </div>

      {firms.length > 0 && (
        <div className="mb-4">
          <input
            className={inputClass}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search firms by name, contact, or admin..."
          />
        </div>
      )}

      <Card className="overflow-x-auto">
        {firms.length === 0 && (
          <EmptyState>
            No Tax Professional firms yet. Onboard the first one to get started.
          </EmptyState>
        )}
        {firms.length > 0 && filtered.length === 0 && (
          <EmptyState>No firms match &ldquo;{search}&rdquo;.</EmptyState>
        )}
        {filtered.length > 0 && (
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b border-slate-200 text-slate-500">
                <th className="px-4 py-2 font-medium">Firm</th>
                <th className="px-4 py-2 font-medium">Admin</th>
                <th className="px-4 py-2 font-medium">Products</th>
                <th className="px-4 py-2 font-medium">Next Renewal</th>
                <th className="px-4 py-2 font-medium">Status</th>
                <th className="px-4 py-2 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {firmsPage.pageItems.map((firm) => (
                <tr key={firm.id}>
                  <td className="px-4 py-2">
                    <p className="font-medium text-slate-900">{firm.name}</p>
                    <p className="text-xs text-slate-500">
                      {firm._count.clients} client(s) &middot; {firm._count.users} user(s)
                    </p>
                  </td>
                  <td className="px-4 py-2 text-slate-500">
                    {firm.users[0]?.name}
                    <br />
                    {firm.users[0]?.email}
                  </td>
                  <td className="px-4 py-2">
                    <div className="flex flex-wrap gap-1">
                      {firm.subscriptions.filter((s) => s.status !== "CANCELLED").length === 0 && (
                        <span className="text-slate-400">—</span>
                      )}
                      {firm.subscriptions
                        .filter((s) => s.status !== "CANCELLED")
                        .map((s) => (
                          <Badge key={s.application} tone={isActive(s) ? "green" : "amber"}>
                            {applicationTypeLabel(s.application)}
                          </Badge>
                        ))}
                    </div>
                  </td>
                  <td className="px-4 py-2 text-slate-500">
                    {(() => {
                      const dates = firm.subscriptions
                        .filter(isActive)
                        .map((s) => s.endDate)
                        .filter((d): d is string => d !== null);
                      if (dates.length === 0) return "—";
                      const nearest = new Date(Math.min(...dates.map((d) => new Date(d).getTime())));
                      return nearest.toLocaleDateString();
                    })()}
                  </td>
                  <td className="px-4 py-2">
                    <Badge tone={firm.status === "ACTIVE" ? "green" : "red"}>
                      {firm.status === "ACTIVE" ? "Active" : "Disabled"}
                    </Badge>
                  </td>
                  <td className="px-4 py-2">
                    <div className="flex flex-wrap gap-2">
                      <Link
                        href={`/admin/firms/${firm.id}`}
                        className="rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-50"
                      >
                        View / Edit
                      </Link>
                      <Button
                        variant="secondary"
                        disabled={busyId === firm.id}
                        onClick={() => toggleStatus(firm)}
                      >
                        {firm.status === "ACTIVE" ? "Disable" : "Enable"}
                      </Button>
                      <Button
                        variant="danger"
                        disabled={busyId === firm.id}
                        onClick={() => handleDelete(firm)}
                      >
                        Delete
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        <Pagination
          page={firmsPage.page}
          totalPages={firmsPage.totalPages}
          onPageChange={firmsPage.setPage}
          totalItems={firmsPage.totalItems}
          pageSize={firmsPage.pageSize}
        />
      </Card>
    </div>
  );
}
