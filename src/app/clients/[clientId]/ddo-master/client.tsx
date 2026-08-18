"use client";

import { useEffect, useState } from "react";
import states from "@/schemas/24g-f137/v1_9/annexures/state.json";
import {
  Alert,
  Button,
  Card,
  EmptyState,
  FieldLabel,
  LoadingState,
  inputClass,
} from "@/components/ui";
import type { DdoMasterFormInput } from "@/lib/validation/ddoMaster";
import type { DdoMasterImportRow } from "@/lib/excel/parseDdoMasterImport";

type DdoMaster = DdoMasterFormInput & { id: string };

type DuplicateGroup = { tan: string; rows: { rowNumber: number; name: string }[] };

type ExistingDataStrategy = "upsert" | "skip_existing";

function ddoMasterDefaults(): DdoMasterFormInput {
  return {
    tan: "",
    name: "",
    address1: "",
    address2: "",
    address3: "",
    address4: "",
    city: "",
    state: "",
    pin: "",
    ddoRegNo: "",
    ddoCode: "",
    email: "",
  };
}

/** Import commit errors are either a plain message or a zod `flatten()` shape; render either as one readable string. */
function describeImportError(error: unknown): string {
  if (typeof error === "string") return error;
  if (error && typeof error === "object") {
    const { fieldErrors, formErrors } = error as {
      fieldErrors?: Record<string, string[]>;
      formErrors?: string[];
    };
    const messages = [
      ...(formErrors ?? []),
      ...Object.entries(fieldErrors ?? {}).flatMap(([field, msgs]) => msgs.map((m) => `${field}: ${m}`)),
    ];
    if (messages.length > 0) return messages.join("; ");
  }
  return "Could not import these rows.";
}

function describeImportResult(result: {
  created?: number;
  updated?: number;
  skipped?: number;
  ignored?: number;
}): string {
  const parts: string[] = [];
  if (result.created) parts.push(`${result.created} new DDO(s) added`);
  if (result.updated) parts.push(`${result.updated} existing DDO(s) updated`);
  if (result.skipped) parts.push(`${result.skipped} already-existing DDO(s) left unchanged`);
  if (result.ignored) parts.push(`${result.ignored} duplicate row(s) ignored`);
  return parts.length > 0 ? parts.join(". ") + "." : "Nothing to import.";
}

export function DdoMasterListClient({ clientId }: { clientId: string }) {
  const [ddoMasters, setDdoMasters] = useState<DdoMaster[]>([]);
  const [loading, setLoading] = useState(true);
  const [formMode, setFormMode] = useState<"none" | "new" | string>("none");
  const [values, setValues] = useState<DdoMasterFormInput>(ddoMasterDefaults());
  const [errors, setErrors] = useState<string[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [search, setSearch] = useState("");
  const [viewingId, setViewingId] = useState<string | null>(null);

  const [importRows, setImportRows] = useState<DdoMasterImportRow[] | null>(null);
  const [importDuplicates, setImportDuplicates] = useState<DuplicateGroup[]>([]);
  const [existingCount, setExistingCount] = useState(0);
  const [existingDataStrategy, setExistingDataStrategy] = useState<ExistingDataStrategy>("skip_existing");
  const [uploadingFile, setUploadingFile] = useState(false);
  const [importing, setImporting] = useState(false);
  const [importError, setImportError] = useState<string | null>(null);
  const [importSuccess, setImportSuccess] = useState<string | null>(null);

  const load = () => {
    fetch(`/api/clients/${clientId}/ddo-master`)
      .then((res) => res.json())
      .then((data) => {
        setDdoMasters(data);
        setLoading(false);
      });
  };

  useEffect(load, [clientId]);

  const startNew = () => {
    setValues(ddoMasterDefaults());
    setFormMode("new");
    setViewingId(null);
    setErrors([]);
  };

  const startEdit = (ddo: DdoMaster) => {
    setValues(ddo);
    setFormMode(ddo.id);
    setViewingId(null);
    setErrors([]);
  };

  const startView = (ddo: DdoMaster) => {
    setViewingId(ddo.id);
    setFormMode("none");
  };

  const searchTerm = search.trim().toLowerCase();
  const filteredMasters = searchTerm
    ? ddoMasters.filter((ddo) =>
        [
          ddo.tan,
          ddo.name,
          ddo.address1,
          ddo.address2,
          ddo.address3,
          ddo.address4,
          ddo.city,
          ddo.state,
          ddo.pin,
          ddo.email,
          ddo.ddoRegNo,
          ddo.ddoCode,
        ].some((field) => (field ?? "").toLowerCase().includes(searchTerm)),
      )
    : ddoMasters;

  const set = <K extends keyof DdoMasterFormInput>(key: K, value: DdoMasterFormInput[K]) =>
    setValues((prev) => ({ ...prev, [key]: value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setErrors([]);

    const isEdit = formMode !== "new";
    const url = isEdit
      ? `/api/clients/${clientId}/ddo-master/${formMode}`
      : `/api/clients/${clientId}/ddo-master`;

    const res = await fetch(url, {
      method: isEdit ? "PATCH" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(values),
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

    setFormMode("none");
    load();
  };

  const handleDelete = async (ddo: DdoMaster) => {
    if (!window.confirm(`Delete ${ddo.tan} — ${ddo.name}? This can't be undone.`)) return;
    await fetch(`/api/clients/${clientId}/ddo-master/${ddo.id}`, { method: "DELETE" });
    if (viewingId === ddo.id) setViewingId(null);
    load();
  };

  const resetImportPreview = () => {
    setImportRows(null);
    setImportDuplicates([]);
    setExistingCount(0);
    setExistingDataStrategy("skip_existing");
  };

  const handleImportFile = async (file: File) => {
    setImportError(null);
    setImportSuccess(null);
    resetImportPreview();
    setUploadingFile(true);
    try {
      const formData = new FormData();
      formData.append("file", file);
      const res = await fetch(`/api/clients/${clientId}/ddo-master/import/preview`, {
        method: "POST",
        body: formData,
      });
      if (!res.ok) {
        const message = await res
          .json()
          .then((body) => body.error ?? "Could not read that file.")
          .catch(() => "Could not read that file. Make sure it's a valid .xlsx export of the template.");
        throw new Error(typeof message === "string" ? message : "Could not read that file.");
      }
      const body = await res.json();
      setImportRows(body.rows);
      setImportDuplicates(body.duplicates ?? []);
      setExistingCount(body.existingCount ?? 0);
    } catch (err) {
      setImportError(err instanceof Error ? err.message : "Import failed. Please try again.");
    } finally {
      setUploadingFile(false);
    }
  };

  const commitImport = async (duplicateStrategy: "keep_last" | "skip") => {
    if (!importRows) return;
    setImporting(true);
    setImportError(null);
    try {
      const validRows = importRows.filter((r) => r.errors.length === 0).map((r) => r.data);
      const res = await fetch(`/api/clients/${clientId}/ddo-master/import/commit`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ rows: validRows, duplicateStrategy, existingDataStrategy }),
      });
      if (!res.ok) {
        const error = await res
          .json()
          .then((b) => b.error)
          .catch(() => null);
        throw new Error(describeImportError(error));
      }
      const result = await res.json();
      setImportSuccess(describeImportResult(result));
      resetImportPreview();
      load();
    } catch (err) {
      setImportError(err instanceof Error ? err.message : "Import failed. Please try again.");
    } finally {
      setImporting(false);
    }
  };

  if (loading) return <LoadingState />;

  return (
    <div>
      <div className="mb-4 flex flex-wrap items-center justify-end gap-2">
        <a
          href={`/api/clients/${clientId}/ddo-master/template`}
          className="inline-flex items-center rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-50"
        >
          Download Template
        </a>
        <a
          href={`/api/clients/${clientId}/ddo-master/export`}
          className="inline-flex items-center rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-50"
        >
          Export
        </a>
        <label
          className={`inline-flex items-center rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm text-slate-700 ${uploadingFile ? "opacity-60" : "cursor-pointer hover:bg-slate-50"}`}
        >
          {uploadingFile ? "Reading file..." : "Import from Excel"}
          <input
            type="file"
            accept=".xlsx"
            className="hidden"
            disabled={uploadingFile}
            onChange={(e) => {
              const file = e.target.files?.[0];
              if (file) handleImportFile(file);
              e.target.value = "";
            }}
          />
        </label>
        {formMode === "none" && <Button onClick={startNew}>+ New DDO</Button>}
      </div>

      {importError && (
        <div className="mb-4">
          <Alert tone="red">{importError}</Alert>
        </div>
      )}

      {importSuccess && (
        <div className="mb-4">
          <Alert tone="green">{importSuccess}</Alert>
        </div>
      )}

      {importRows && (
        <Card className="mb-6 p-4">
          <h3 className="font-medium text-slate-900">
            Import Preview &mdash; {importRows.length} row(s),{" "}
            {importRows.filter((r) => r.errors.length === 0).length} valid
          </h3>
          <div className="mt-2 max-h-64 overflow-y-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="text-slate-500">
                  <th className="pr-4">Row</th>
                  <th className="pr-4">TAN</th>
                  <th className="pr-4">Name</th>
                  <th>Errors</th>
                </tr>
              </thead>
              <tbody>
                {importRows.map((row) => (
                  <tr key={row.rowNumber} className="border-t border-slate-100">
                    <td className="py-1 pr-4">{row.rowNumber}</td>
                    <td className="py-1 pr-4">{row.data.tan}</td>
                    <td className="py-1 pr-4">{row.data.name}</td>
                    <td className="py-1 text-red-600">{row.errors.join("; ")}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {importDuplicates.length > 0 && (
            <div className="mt-4 rounded-lg border border-amber-200 bg-amber-50 p-3">
              <p className="text-sm font-medium text-amber-800">
                {importDuplicates.length} TAN(s) appear more than once in this file
              </p>
              <ul className="mt-2 space-y-1 text-sm text-amber-900">
                {importDuplicates.map((dup) => (
                  <li key={dup.tan}>
                    {dup.tan}: rows {dup.rows.map((r) => `${r.rowNumber} (${r.name})`).join(", ")}
                  </li>
                ))}
              </ul>
              <p className="mt-2 text-xs text-amber-700">
                A DDO can only have one master record per TAN. Choose below whether to keep the
                last occurrence of each, or ignore (skip) them entirely so you can fix the sheet
                and re-import.
              </p>
            </div>
          )}

          {existingCount > 0 && (
            <div className="mt-4 rounded-lg border border-slate-200 bg-slate-50 p-3">
              <p className="text-sm font-medium text-slate-800">
                {existingCount} of these TAN(s) already have a saved DDO Master. How should this
                import be applied?
              </p>
              <div className="mt-2 space-y-2 text-sm text-slate-700">
                <label className="flex items-start gap-2">
                  <input
                    type="radio"
                    className="mt-1"
                    checked={existingDataStrategy === "skip_existing"}
                    onChange={() => setExistingDataStrategy("skip_existing")}
                  />
                  <span>Add new DDOs only &mdash; leave any TAN that already exists untouched</span>
                </label>
                <label className="flex items-start gap-2">
                  <input
                    type="radio"
                    className="mt-1"
                    checked={existingDataStrategy === "upsert"}
                    onChange={() => setExistingDataStrategy("upsert")}
                  />
                  <span>Update existing DDOs with the new details, and add any new ones</span>
                </label>
              </div>
            </div>
          )}

          <div className="mt-3 flex flex-wrap gap-2">
            <Button onClick={() => commitImport("keep_last")} disabled={importing}>
              {importing
                ? "Importing..."
                : importDuplicates.length > 0
                  ? "Import (keep last occurrence of duplicates)"
                  : `Import ${importRows.filter((r) => r.errors.length === 0).length} valid row(s)`}
            </Button>
            {importDuplicates.length > 0 && (
              <Button variant="secondary" onClick={() => commitImport("skip")} disabled={importing}>
                Import (ignore duplicate rows)
              </Button>
            )}
            <Button variant="secondary" onClick={resetImportPreview} disabled={importing}>
              Retry &mdash; choose a different file
            </Button>
          </div>
        </Card>
      )}

      {formMode !== "none" && (
        <Card className="mb-6 p-6">
          <h3 className="mb-4 text-sm font-semibold text-slate-900">
            {formMode === "new" ? "New DDO" : "Edit DDO"}
          </h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            {errors.length > 0 && (
              <Alert>
                <ul className="list-inside list-disc">
                  {errors.map((err, i) => (
                    <li key={i}>{err}</li>
                  ))}
                </ul>
              </Alert>
            )}
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div className="space-y-1">
                <FieldLabel>TAN</FieldLabel>
                <input
                  className={inputClass}
                  required
                  maxLength={10}
                  value={values.tan}
                  onChange={(e) => set("tan", e.target.value.toUpperCase())}
                  placeholder="MUMD12345A"
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Name of the DDO</FieldLabel>
                <input
                  className={inputClass}
                  required
                  value={values.name}
                  onChange={(e) => set("name", e.target.value)}
                  placeholder="e.g. Asstt. Labour Commissioner"
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Address Line 1</FieldLabel>
                <input
                  className={inputClass}
                  value={values.address1}
                  onChange={(e) => set("address1", e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Address Line 2</FieldLabel>
                <input
                  className={inputClass}
                  value={values.address2}
                  onChange={(e) => set("address2", e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>City</FieldLabel>
                <input
                  className={inputClass}
                  value={values.city}
                  onChange={(e) => set("city", e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>State</FieldLabel>
                <select
                  className={inputClass}
                  value={values.state}
                  onChange={(e) => set("state", e.target.value)}
                >
                  <option value="">Select state</option>
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
                  maxLength={6}
                  value={values.pin}
                  onChange={(e) => set("pin", e.target.value.replace(/\D/g, ""))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Email</FieldLabel>
                <input
                  type="email"
                  className={inputClass}
                  value={values.email}
                  onChange={(e) => set("email", e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>DDO Registration No. (optional)</FieldLabel>
                <input
                  className={inputClass}
                  value={values.ddoRegNo}
                  onChange={(e) => set("ddoRegNo", e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>DDO Code (optional)</FieldLabel>
                <input
                  className={inputClass}
                  value={values.ddoCode}
                  onChange={(e) => set("ddoCode", e.target.value)}
                />
              </div>
            </div>
            <div className="flex gap-2">
              <Button type="submit" disabled={submitting}>
                {submitting ? "Saving..." : "Save DDO"}
              </Button>
              <Button type="button" variant="secondary" onClick={() => setFormMode("none")}>
                Cancel
              </Button>
            </div>
          </form>
        </Card>
      )}

      {viewingId &&
        (() => {
          const ddo = ddoMasters.find((d) => d.id === viewingId);
          if (!ddo) return null;
          const stateName = states.find((s) => s.code === ddo.state)?.name ?? ddo.state;
          return (
            <Card className="mb-6 p-6">
              <div className="mb-4 flex items-center justify-between">
                <h3 className="text-sm font-semibold text-slate-900">DDO Details</h3>
                <Button variant="secondary" onClick={() => setViewingId(null)}>
                  Close
                </Button>
              </div>
              <dl className="grid grid-cols-1 gap-4 text-sm sm:grid-cols-2">
                {[
                  ["TAN", ddo.tan],
                  ["Name of the DDO", ddo.name],
                  ["Address Line 1", ddo.address1],
                  ["Address Line 2", ddo.address2],
                  ["City", ddo.city],
                  ["State", stateName],
                  ["PIN Code", ddo.pin],
                  ["Email", ddo.email],
                  ["DDO Registration No.", ddo.ddoRegNo],
                  ["DDO Code", ddo.ddoCode],
                ].map(([label, value]) => (
                  <div key={label}>
                    <dt className="text-xs font-medium uppercase tracking-wide text-slate-400">
                      {label}
                    </dt>
                    <dd className="mt-0.5 text-slate-900">{value || "—"}</dd>
                  </div>
                ))}
              </dl>
              <div className="mt-4 flex gap-2">
                <Button onClick={() => startEdit(ddo)}>Edit</Button>
                <Button variant="danger" onClick={() => handleDelete(ddo)}>
                  Delete
                </Button>
              </div>
            </Card>
          );
        })()}

      {ddoMasters.length > 0 && (
        <div className="mb-4">
          <input
            className={inputClass}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by TAN, name, address, city, PIN, email, DDO code..."
          />
        </div>
      )}

      <Card className="overflow-x-auto">
        {ddoMasters.length === 0 && (
          <EmptyState>
            No DDOs yet. Add one above — you&apos;ll pick from this list when entering
            transactions.
          </EmptyState>
        )}
        {ddoMasters.length > 0 && filteredMasters.length === 0 && (
          <EmptyState>No DDOs match &ldquo;{search}&rdquo;.</EmptyState>
        )}
        {filteredMasters.length > 0 && (
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b border-slate-200 text-slate-500">
                <th className="px-4 py-2 font-medium">TAN</th>
                <th className="px-4 py-2 font-medium">Name</th>
                <th className="px-4 py-2 font-medium">City</th>
                <th className="px-4 py-2 font-medium">Email</th>
                <th className="px-4 py-2 font-medium">DDO Code</th>
                <th className="px-4 py-2 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredMasters.map((ddo) => (
                <tr key={ddo.id}>
                  <td className="px-4 py-2 font-medium text-slate-900">{ddo.tan}</td>
                  <td className="px-4 py-2 text-slate-700">{ddo.name}</td>
                  <td className="px-4 py-2 text-slate-500">{ddo.city || "—"}</td>
                  <td className="px-4 py-2 text-slate-500">{ddo.email || "—"}</td>
                  <td className="px-4 py-2 text-slate-500">{ddo.ddoCode || "—"}</td>
                  <td className="px-4 py-2">
                    <div className="flex gap-2">
                      <Button variant="secondary" onClick={() => startView(ddo)}>
                        View
                      </Button>
                      <Button variant="secondary" onClick={() => startEdit(ddo)}>
                        Edit
                      </Button>
                      <Button variant="danger" onClick={() => handleDelete(ddo)}>
                        Delete
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  );
}
