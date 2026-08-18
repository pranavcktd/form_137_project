"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Alert, Button, Card, EmptyState, FieldLabel, LoadingState, inputClass } from "@/components/ui";
import { isValidAin } from "@/lib/validation/ain";
import states from "@/schemas/24g-f137/v1_9/annexures/state.json";

type Client = {
  id: string;
  ain: string;
  departmentName: string;
  govtCategory: "CENTRAL" | "STATE";
  _count: { filingPeriods: number };
};

export function ClientsListClient() {
  const [clients, setClients] = useState<Client[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [ain, setAin] = useState("");
  const [departmentName, setDepartmentName] = useState("");
  const [govtCategory, setGovtCategory] = useState<"CENTRAL" | "STATE">("CENTRAL");
  const [responsiblePersonDesignation, setResponsiblePersonDesignation] = useState("");
  const [responsiblePersonAddress1, setResponsiblePersonAddress1] = useState("");
  const [responsiblePersonCity, setResponsiblePersonCity] = useState("");
  const [responsiblePersonState, setResponsiblePersonState] = useState("19");
  const [responsiblePersonPin, setResponsiblePersonPin] = useState("");
  const [responsiblePersonEmail, setResponsiblePersonEmail] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const load = () => {
    fetch("/api/clients")
      .then((res) => res.json())
      .then((data) => {
        setClients(data);
        setLoading(false);
      });
  };

  useEffect(load, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!isValidAin(ain)) {
      setError("Invalid AIN: the 7th digit must be (first 6 digits mod 7).");
      return;
    }

    setSubmitting(true);
    const res = await fetch("/api/clients", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        ain,
        departmentName,
        govtCategory,
        responsiblePersonDesignation,
        responsiblePersonAddress1,
        responsiblePersonCity,
        responsiblePersonState,
        responsiblePersonPin,
        responsiblePersonEmail,
      }),
    });
    setSubmitting(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors
        ? Object.values(body.error.fieldErrors).flat()
        : [];
      const formErrors = body.error?.formErrors ?? [];
      setError(([...formErrors, ...fieldErrors] as string[])[0] ?? "Could not create the client.");
      return;
    }

    setShowForm(false);
    setAin("");
    setDepartmentName("");
    setResponsiblePersonDesignation("");
    setResponsiblePersonAddress1("");
    setResponsiblePersonCity("");
    setResponsiblePersonPin("");
    setResponsiblePersonEmail("");
    load();
  };

  if (loading) return <LoadingState />;

  return (
    <div>
      <div className="mb-4 flex justify-end">
        <Button onClick={() => setShowForm((v) => !v)}>
          {showForm ? "Cancel" : "+ New Client"}
        </Button>
      </div>

      {showForm && (
        <Card className="mb-6 p-6">
          <h3 className="mb-4 text-sm font-semibold text-slate-900">
            New Client — quick setup
          </h3>
          <p className="mb-4 text-sm text-slate-500">
            Enter the essentials now; you can fill in the rest of the profile
            (TAN, responsible person contact details, etc.) any time before
            generating a return.
          </p>
          <form onSubmit={handleCreate} className="space-y-4">
            {error && <Alert>{error}</Alert>}
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div className="space-y-1">
                <FieldLabel>AIN</FieldLabel>
                <input
                  className={inputClass}
                  required
                  maxLength={7}
                  value={ain}
                  onChange={(e) => setAin(e.target.value.replace(/\D/g, ""))}
                  placeholder="7 digit AIN"
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Department Name</FieldLabel>
                <input
                  className={inputClass}
                  required
                  value={departmentName}
                  onChange={(e) => setDepartmentName(e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Government Category</FieldLabel>
                <select
                  className={inputClass}
                  value={govtCategory}
                  onChange={(e) => setGovtCategory(e.target.value as "CENTRAL" | "STATE")}
                >
                  <option value="CENTRAL">Central</option>
                  <option value="STATE">State</option>
                </select>
              </div>
              <div className="space-y-1">
                <FieldLabel>Responsible Person Designation</FieldLabel>
                <input
                  className={inputClass}
                  required
                  value={responsiblePersonDesignation}
                  onChange={(e) => setResponsiblePersonDesignation(e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Address Line 1</FieldLabel>
                <input
                  className={inputClass}
                  required
                  value={responsiblePersonAddress1}
                  onChange={(e) => setResponsiblePersonAddress1(e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>City</FieldLabel>
                <input
                  className={inputClass}
                  required
                  value={responsiblePersonCity}
                  onChange={(e) => setResponsiblePersonCity(e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>State</FieldLabel>
                <select
                  className={inputClass}
                  required
                  value={responsiblePersonState}
                  onChange={(e) => setResponsiblePersonState(e.target.value)}
                >
                  {states.map((s) => (
                    <option key={s.code} value={s.code}>
                      {s.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="space-y-1">
                <FieldLabel>PIN Code</FieldLabel>
                <input
                  className={inputClass}
                  required
                  maxLength={6}
                  value={responsiblePersonPin}
                  onChange={(e) => setResponsiblePersonPin(e.target.value.replace(/\D/g, ""))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Email</FieldLabel>
                <input
                  type="email"
                  className={inputClass}
                  required
                  value={responsiblePersonEmail}
                  onChange={(e) => setResponsiblePersonEmail(e.target.value)}
                />
              </div>
            </div>
            <Button type="submit" disabled={submitting}>
              {submitting ? "Creating..." : "Create Client"}
            </Button>
          </form>
        </Card>
      )}

      <Card className="divide-y divide-slate-100">
        {clients.length === 0 && (
          <EmptyState>No clients yet. Create your first one above.</EmptyState>
        )}
        {clients.map((client) => (
          <div key={client.id} className="flex items-center justify-between p-4 hover:bg-slate-50">
            <Link href={`/clients/${client.id}/filing-periods`} className="flex-1">
              <p className="font-medium text-slate-900">{client.departmentName}</p>
              <p className="text-sm text-slate-500">
                AIN {client.ain} &middot; {client.govtCategory === "CENTRAL" ? "Central" : "State"} Govt.
                &middot; {client._count.filingPeriods} filing period(s)
              </p>
            </Link>
            <Link
              href={`/clients/${client.id}`}
              className="text-sm text-indigo-600 hover:underline"
            >
              Edit profile
            </Link>
          </div>
        ))}
      </Card>
    </div>
  );
}
