"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import {
  Alert,
  Badge,
  Button,
  Card,
  EmptyState,
  FieldLabel,
  LoadingState,
  inputClass,
} from "@/components/ui";
import { listFinancialYears, currentFinancialYear } from "@/lib/financialYear";

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

type StatementType = "ORIGINAL" | "CORRECTION_M" | "CORRECTION_X";

type FilingPeriod = {
  id: string;
  financialYear: number;
  month: number;
  statementType: StatementType;
  status: "DRAFT" | "LOCKED";
  receiptNumber: string | null;
  receiptDate: string | null;
  _count: { ddoRecords: number; generatedFiles: number };
};

type FormValues = {
  financialYear: number;
  month: number;
  statementType: StatementType;
};

function defaultFormValues(): FormValues {
  return {
    financialYear: currentFinancialYear(),
    month: new Date().getMonth() + 1,
    statementType: "ORIGINAL",
  };
}

function PeriodFormFields({
  values,
  onChange,
}: {
  values: FormValues;
  onChange: (values: FormValues) => void;
}) {
  const fyOptions = listFinancialYears();
  return (
    <>
      <div className="space-y-1">
        <FieldLabel>Financial Year</FieldLabel>
        <select
          className={inputClass}
          value={values.financialYear}
          onChange={(e) => onChange({ ...values, financialYear: Number(e.target.value) })}
        >
          {fyOptions.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      </div>
      <div className="space-y-1">
        <FieldLabel>Month</FieldLabel>
        <select
          className={inputClass}
          value={values.month}
          onChange={(e) => onChange({ ...values, month: Number(e.target.value) })}
        >
          {MONTHS.map((m, i) => (
            <option key={m} value={i + 1}>
              {m}
            </option>
          ))}
        </select>
      </div>
      <div className="space-y-1">
        <FieldLabel>Statement Type</FieldLabel>
        <select
          className={inputClass}
          value={values.statementType}
          onChange={(e) => onChange({ ...values, statementType: e.target.value as StatementType })}
        >
          <option value="ORIGINAL">Original</option>
          <option value="CORRECTION_M">Correction (M)</option>
          <option value="CORRECTION_X">Correction (X)</option>
        </select>
      </div>
    </>
  );
}

export function FilingPeriodsListClient({ clientId }: { clientId: string }) {
  const [periods, setPeriods] = useState<FilingPeriod[]>([]);
  const [loading, setLoading] = useState(true);
  const [createValues, setCreateValues] = useState<FormValues>(defaultFormValues());
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editValues, setEditValues] = useState<FormValues>(defaultFormValues());
  const [error, setError] = useState<string | null>(null);

  const load = () => {
    fetch(`/api/filing-periods?clientId=${clientId}`)
      .then((res) => res.json())
      .then((data) => {
        setPeriods(data);
        setLoading(false);
      });
  };

  useEffect(load, [clientId]);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    const res = await fetch("/api/filing-periods", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ clientId, ...createValues }),
    });

    if (!res.ok) {
      const body = await res.json();
      setError(
        body.error?.formErrors?.[0] ??
          body.error?.fieldErrors?.month?.[0] ??
          "Could not create the filing period.",
      );
      return;
    }

    load();
  };

  const startEdit = (p: FilingPeriod) => {
    setEditingId(p.id);
    setEditValues({ financialYear: p.financialYear, month: p.month, statementType: p.statementType });
    setError(null);
  };

  const handleSaveEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingId) return;
    setError(null);

    const res = await fetch(`/api/filing-periods/${editingId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(editValues),
    });

    if (!res.ok) {
      const body = await res.json();
      setError(
        body.error?.formErrors?.[0] ??
          body.error?.fieldErrors?.month?.[0] ??
          body.error ??
          "Could not update the filing period.",
      );
      return;
    }

    setEditingId(null);
    load();
  };

  const handleDelete = async (p: FilingPeriod) => {
    if (p.status === "LOCKED") {
      window.alert('This return is locked. Open it and click "Edit Return" to unlock it before deleting.');
      return;
    }
    if (p.receiptNumber || p.receiptDate) {
      window.alert(
        "This return already has a Receipt/Acknowledgement Number and Date recorded. Open it and clear those fields first if you still want to delete it.",
      );
      return;
    }

    const confirmMessage =
      p._count.generatedFiles > 0
        ? "You already generated this return and it had a Receipt/Acknowledgement Number recorded at some point. Are you sure you still want to delete this filing period and all its DDO records?"
        : "Delete this filing period and all its DDO records? This can't be undone.";
    if (!window.confirm(confirmMessage)) return;

    const res = await fetch(`/api/filing-periods/${p.id}`, { method: "DELETE" });
    if (!res.ok) {
      const body = await res.json();
      window.alert(body.error?.formErrors?.[0] ?? "Could not delete this filing period.");
      return;
    }
    load();
  };

  if (loading) return <LoadingState />;

  return (
    <div>
      <Card className="p-5">
        <h3 className="mb-3 text-sm font-semibold text-slate-900">New Filing Period</h3>
        <form onSubmit={handleCreate} className="flex flex-wrap items-end gap-3">
          <PeriodFormFields values={createValues} onChange={setCreateValues} />
          <Button type="submit">+ Create</Button>
        </form>
      </Card>
      {error && (
        <div className="mt-2">
          <Alert>{error}</Alert>
        </div>
      )}

      <div className="mt-6 grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
        {periods.length === 0 && (
          <div className="sm:col-span-2 lg:col-span-3">
            <Card>
              <EmptyState>No filing periods yet. Create one above.</EmptyState>
            </Card>
          </div>
        )}
        {periods.map((p) =>
          editingId === p.id ? (
            <Card key={p.id} className="p-4 sm:col-span-2 lg:col-span-3">
              <form onSubmit={handleSaveEdit} className="flex flex-wrap items-end gap-3">
                <PeriodFormFields values={editValues} onChange={setEditValues} />
                <Button type="submit">Save</Button>
                <Button type="button" variant="secondary" onClick={() => setEditingId(null)}>
                  Cancel
                </Button>
              </form>
            </Card>
          ) : (
            <Card
              key={p.id}
              className="flex flex-col gap-3 p-4 transition-shadow hover:shadow-md"
            >
              <div className="flex items-start justify-between">
                <div>
                  <p className="font-medium text-slate-900">
                    FY {p.financialYear}-{String((p.financialYear + 1) % 100).padStart(2, "0")}
                  </p>
                  <p className="text-sm text-slate-500">{MONTHS[p.month - 1]}</p>
                </div>
                <Badge tone={p.status === "LOCKED" ? "green" : "amber"}>
                  {p.status === "LOCKED" ? "Locked" : "Draft"}
                </Badge>
              </div>
              <div className="flex items-center gap-2 text-xs text-slate-500">
                <Badge tone="indigo">{p.statementType}</Badge>
                <span>{p._count.ddoRecords} DDO record(s)</span>
              </div>
              <div className="mt-1 flex gap-2">
                <Link
                  href={`/filing-periods/${p.id}`}
                  className="flex-1 rounded-lg bg-slate-900 px-3 py-1.5 text-center text-sm font-medium text-white hover:bg-slate-800"
                >
                  Open
                </Link>
                {p.status !== "LOCKED" && (
                  <Button variant="secondary" onClick={() => startEdit(p)}>
                    Edit
                  </Button>
                )}
                <Button variant="danger" onClick={() => handleDelete(p)}>
                  Delete
                </Button>
              </div>
            </Card>
          ),
        )}
      </div>
    </div>
  );
}
