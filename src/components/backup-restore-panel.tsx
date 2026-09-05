"use client";

import { useState } from "react";
import { Alert, Button, Card, FieldLabel, inputClass } from "@/components/ui";

type RestoreStrategy = "skip_existing" | "upsert" | "replace";

type RestoreSummary = {
  clientsCreated: number;
  clientsUpdated: number;
  clientsSkippedConflict: number;
  filingPeriodsCreated: number;
  filingPeriodsUpdated: number;
  filingPeriodsSkipped: number;
  ddoRecordsCreated: number;
  ddoRecordsUpdated: number;
  ddoRecordsSkipped: number;
};

const STRATEGY_LABELS: Record<RestoreStrategy, string> = {
  skip_existing: "Add only what's missing — never touch a client/period that already exists",
  upsert: "Update existing + add new — fills in and refreshes matching records",
  replace: "Replace — wipes an existing period's records first, then reloads it from the backup",
};

/** Shared by the firm's own Backup page and the super admin's per-firm view —
 *  only the backup/restore URLs differ between "my own firm" and "this firm". */
export function BackupRestorePanel({
  backupUrl,
  restoreUrl,
  backupLabel = "Download Backup",
}: {
  backupUrl: string;
  restoreUrl: string;
  backupLabel?: string;
}) {
  const [file, setFile] = useState<File | null>(null);
  const [strategy, setStrategy] = useState<RestoreStrategy>("upsert");
  const [restoring, setRestoring] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [summary, setSummary] = useState<RestoreSummary | null>(null);

  const handleRestore = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return;

    if (
      strategy === "replace" &&
      !window.confirm(
        "Replace will wipe the existing DDO records for any filing period also present in this backup, then reload them fresh. This can't be undone. Continue?",
      )
    ) {
      return;
    }

    setRestoring(true);
    setError(null);
    setSummary(null);

    const formData = new FormData();
    formData.append("file", file);
    formData.append("strategy", strategy);
    const res = await fetch(restoreUrl, { method: "POST", body: formData });
    setRestoring(false);

    if (!res.ok) {
      // A body-size-limit rejection or other server-level failure returns an
      // empty/non-JSON body rather than our own {error: {...}} shape.
      const body = await res.json().catch(() => null);
      const fieldErrors = body?.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setError(
        [...(body?.error?.formErrors ?? []), ...fieldErrors].join(" ") ||
          `Restore failed (HTTP ${res.status}). The file may be too large or the server hit an unexpected error.`,
      );
      return;
    }

    setSummary(await res.json());
    setFile(null);
  };

  return (
    <div className="space-y-6">
      <Card className="p-6">
        <h3 className="mb-1 text-sm font-semibold text-slate-900">Backup</h3>
        <p className="mb-4 text-sm text-slate-500">
          Downloads a .zip with a database.json (every client, DDO, and filing period — receipt
          numbers and dates included) plus one Excel file per filed month, organized by client and
          financial year.
        </p>
        <a href={backupUrl}>
          <Button type="button">{backupLabel}</Button>
        </a>
      </Card>

      <Card className="p-6">
        <h3 className="mb-1 text-sm font-semibold text-slate-900">Restore</h3>
        <p className="mb-4 text-sm text-slate-500">
          Upload a backup .zip (or its database.json). Restoring only ever adds or changes what
          the backup actually contains — it never deletes a client, period, or record that simply
          isn&apos;t in the backup.
        </p>
        <form onSubmit={handleRestore} className="space-y-4">
          {error && <Alert>{error}</Alert>}
          {summary && (
            <Alert tone="green">
              Clients: {summary.clientsCreated} added, {summary.clientsUpdated} updated
              {summary.clientsSkippedConflict > 0 &&
                `, ${summary.clientsSkippedConflict} skipped (AIN belongs to another firm)`}
              . Filing periods: {summary.filingPeriodsCreated} added, {summary.filingPeriodsUpdated} updated,{" "}
              {summary.filingPeriodsSkipped} skipped. DDO records: {summary.ddoRecordsCreated} added,{" "}
              {summary.ddoRecordsUpdated} updated, {summary.ddoRecordsSkipped} skipped.
            </Alert>
          )}

          <div className="space-y-1">
            <FieldLabel>Backup file (.zip or database.json)</FieldLabel>
            <input
              type="file"
              accept=".zip,.json"
              onChange={(e) => setFile(e.target.files?.[0] ?? null)}
              className="text-sm text-slate-700"
            />
          </div>

          <div className="space-y-1">
            <FieldLabel>If a client/period/record already exists</FieldLabel>
            <select
              className={inputClass}
              value={strategy}
              onChange={(e) => setStrategy(e.target.value as RestoreStrategy)}
            >
              {Object.entries(STRATEGY_LABELS).map(([value, label]) => (
                <option key={value} value={value}>
                  {label}
                </option>
              ))}
            </select>
          </div>

          <Button type="submit" disabled={!file || restoring}>
            {restoring ? "Restoring..." : "Restore"}
          </Button>
        </form>
      </Card>
    </div>
  );
}
