"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { DdoRecordForm } from "@/components/ddo-record-form";
import type { DdoRecordFormInput } from "@/lib/validation/ddoRecord";
import type { ImportedRow } from "@/lib/excel/parseDdoImport";
import type { FieldError } from "@/lib/validation/validateField";
import type { FvuFieldError } from "@/lib/fvu/parseErrorHtml";
import { checkDiscrepancies } from "@/lib/analytics/discrepancies";
import {
  Alert,
  Badge,
  Button,
  Card,
  EmptyState,
  FieldLabel,
  LoadingState,
  PageHeader,
  inputClass,
} from "@/components/ui";

type FilingPeriod = {
  id: string;
  clientId: string;
  financialYear: number;
  month: number;
  statementType: "ORIGINAL" | "CORRECTION_M" | "CORRECTION_X";
  status: "DRAFT" | "LOCKED";
  receiptNumber: string | null;
  receiptDate: string | null;
};

type Client = {
  departmentName: string;
  ain: string;
};

type DdoRecord = DdoRecordFormInput & { id: string; serialNo: number };

type GeneratedFile = {
  id: string;
  status: "PENDING" | "FVU_PASSED" | "FVU_FAILED";
  fvuFilePath: string | null;
  createdAt: string;
};

type GenerateState =
  | { status: "idle" }
  | { status: "loading" }
  | { status: "validationError"; fhErrors: FieldError[]; bhErrors: FieldError[]; tdErrors: FieldError[][] }
  | { status: "fvuError"; message: string | null; errors: FvuFieldError[] }
  | { status: "success" };

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
      ...Object.entries(fieldErrors ?? {}).flatMap(([field, msgs]) =>
        msgs.map((m) => `${field}: ${m}`),
      ),
    ];
    if (messages.length > 0) return messages.join("; ");
  }
  return "Could not import these rows.";
}

type DuplicateGroup = {
  tan: string;
  name: string;
  formType: string;
  rows: { rowNumber: number; taxDeducted: number; totalRemitted: number }[];
  mergedTaxDeducted: number;
  mergedTotalRemitted: number;
};

type ExistingDataStrategy = "replace" | "upsert" | "skip_existing";

function describeImportResult(result: {
  created?: number;
  updated?: number;
  skipped?: number;
  merged?: number;
  ignored?: number;
}): string {
  const parts: string[] = [];
  if (result.created) parts.push(`${result.created} new record(s) added`);
  if (result.updated) parts.push(`${result.updated} existing record(s) updated`);
  if (result.skipped) parts.push(`${result.skipped} already-existing record(s) left unchanged`);
  if (result.merged) parts.push(`${result.merged} duplicate row(s) summed into their DDO's total`);
  if (result.ignored) parts.push(`${result.ignored} duplicate row(s) ignored`);
  return parts.length > 0 ? parts.join(". ") + "." : "Nothing to import.";
}

export function FilingPeriodDetailClient({ filingPeriodId }: { filingPeriodId: string }) {
  const [loading, setLoading] = useState(true);
  const [filingPeriod, setFilingPeriod] = useState<FilingPeriod | null>(null);
  const [client, setClient] = useState<Client | null>(null);
  const [ddoRecords, setDdoRecords] = useState<DdoRecord[]>([]);
  const [formMode, setFormMode] = useState<"none" | "new" | string>("none");
  const [importRows, setImportRows] = useState<ImportedRow[] | null>(null);
  const [importDuplicates, setImportDuplicates] = useState<DuplicateGroup[]>([]);
  const [existingRecordCount, setExistingRecordCount] = useState(0);
  const [existingDataStrategy, setExistingDataStrategy] = useState<ExistingDataStrategy>("skip_existing");
  const [importing, setImporting] = useState(false);
  const [uploadingFile, setUploadingFile] = useState(false);
  const [importError, setImportError] = useState<string | null>(null);
  const [importSuccess, setImportSuccess] = useState<string | null>(null);
  const [generateState, setGenerateState] = useState<GenerateState>({ status: "idle" });
  const [history, setHistory] = useState<GeneratedFile[]>([]);
  const [receiptNumber, setReceiptNumber] = useState("");
  const [receiptDate, setReceiptDate] = useState("");
  const [savingReceipt, setSavingReceipt] = useState(false);

  const load = () => {
    fetch(`/api/filing-periods/${filingPeriodId}`)
      .then((res) => res.json())
      .then((data) => {
        setFilingPeriod(data.filingPeriod);
        setClient(data.client);
        setDdoRecords(data.ddoRecords);
        setReceiptNumber(data.filingPeriod.receiptNumber ?? "");
        setReceiptDate(data.filingPeriod.receiptDate ? data.filingPeriod.receiptDate.slice(0, 10) : "");
        setLoading(false);
      });
  };

  const loadHistory = () => {
    fetch(`/api/filing-periods/${filingPeriodId}/generated-files`)
      .then((res) => res.json())
      .then(setHistory);
  };

  useEffect(load, [filingPeriodId]);
  useEffect(loadHistory, [filingPeriodId]);

  if (loading || !filingPeriod) {
    return <LoadingState />;
  }

  const locked = filingPeriod.status === "LOCKED";

  const createRecord = async (values: DdoRecordFormInput) => {
    const res = await fetch(`/api/filing-periods/${filingPeriodId}/ddo-records`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(values),
    });
    if (!res.ok) {
      const body = await res.json();
      throw new Error(body.error?.formErrors?.[0] ?? "Could not create the DDO record.");
    }
    setFormMode("none");
    load();
  };

  const updateRecord = async (id: string, values: DdoRecordFormInput) => {
    const res = await fetch(`/api/filing-periods/${filingPeriodId}/ddo-records/${id}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(values),
    });
    if (!res.ok) {
      const body = await res.json();
      throw new Error(body.error?.formErrors?.[0] ?? "Could not update the DDO record.");
    }
    setFormMode("none");
    load();
  };

  const deleteRecord = async (id: string) => {
    await fetch(`/api/filing-periods/${filingPeriodId}/ddo-records/${id}`, { method: "DELETE" });
    load();
  };

  const resetImportPreview = () => {
    setImportRows(null);
    setImportDuplicates([]);
    setExistingRecordCount(0);
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
      const res = await fetch(`/api/filing-periods/${filingPeriodId}/ddo-records/import/preview`, {
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
      setExistingRecordCount(body.existingRecordCount ?? 0);
    } catch (err) {
      setImportError(err instanceof Error ? err.message : "Import failed. Please try again.");
    } finally {
      setUploadingFile(false);
    }
  };

  const commitImport = async (duplicateStrategy: "merge" | "skip") => {
    if (!importRows) return;
    setImporting(true);
    setImportError(null);
    try {
      const validRows = importRows.filter((r) => r.errors.length === 0).map((r) => r.data);
      const res = await fetch(`/api/filing-periods/${filingPeriodId}/ddo-records/import/commit`, {
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

  const handleGenerate = async () => {
    setGenerateState({ status: "loading" });
    const res = await fetch(`/api/filing-periods/${filingPeriodId}/generate`, { method: "POST" });

    if (res.status === 400) {
      const body = await res.json();
      setGenerateState({ status: "validationError", ...body.validation });
      return;
    }
    if (res.status === 422) {
      const body = await res.json();
      setGenerateState({ status: "fvuError", message: body.message, errors: body.errors ?? [] });
      loadHistory();
      return;
    }
    if (!res.ok) {
      setGenerateState({ status: "fvuError", message: "Unexpected error while generating.", errors: [] });
      return;
    }

    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `form137-${filingPeriod.financialYear}-${String(filingPeriod.month).padStart(2, "0")}.zip`;
    a.click();
    URL.revokeObjectURL(url);
    setGenerateState({ status: "success" });
    loadHistory();
    load();
  };

  const handleUnlock = async () => {
    await fetch(`/api/filing-periods/${filingPeriodId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ action: "unlock" }),
    });
    setGenerateState({ status: "idle" });
    load();
  };

  const handleSaveReceipt = async (e: React.FormEvent) => {
    e.preventDefault();
    setSavingReceipt(true);
    await fetch(`/api/filing-periods/${filingPeriodId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ receiptNumber, receiptDate }),
    });
    setSavingReceipt(false);
    load();
  };

  const discrepancies = checkDiscrepancies(
    ddoRecords.map((r) => ({
      tan: r.tan,
      name: r.name,
      formType: r.formType || null,
      taxDeducted: Number(r.taxDeducted),
      totalRemitted: Number(r.totalRemitted),
    })),
  );

  return (
    <div>
      <PageHeader
        title={client?.departmentName}
        subtitle={
          <>
            AIN {client?.ain} &middot; FY {filingPeriod.financialYear}-
            {String((filingPeriod.financialYear + 1) % 100).padStart(2, "0")} &middot; Month{" "}
            {filingPeriod.month} &middot; {filingPeriod.statementType}
          </>
        }
        actions={
          <div className="flex items-center gap-3">
            {locked && <Badge tone="green">Locked</Badge>}
            <Link
              href={`/clients/${filingPeriod.clientId}/filing-periods`}
              className="text-sm text-indigo-600 hover:underline"
            >
              &larr; All filing periods
            </Link>
          </div>
        }
      />

      {locked && (
        <div className="mt-4">
          <Alert tone="amber">
            This return is locked. Its data can&apos;t be changed unless you click{" "}
            <strong>Edit Return</strong> below.
          </Alert>
        </div>
      )}

      {discrepancies.length > 0 && (
        <div className="mt-4">
          <Alert tone="amber">
            <p className="font-medium">
              {discrepancies.length} DDO record(s) have deducted/remitted amounts that don&apos;t match:
            </p>
            {discrepancies.map((d, i) => (
              <p key={i}>
                {d.tan} &mdash; {d.name} ({d.formType}): {d.message}
              </p>
            ))}
          </Alert>
        </div>
      )}

      <div className="mt-8 flex items-center justify-between">
        <h2 className="text-lg font-medium text-slate-900">DDO Records</h2>
        <div className="flex gap-2">
          <a
            href={`/api/filing-periods/${filingPeriodId}/summary-report`}
            className="inline-flex items-center rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-50"
          >
            Download Summary Report
          </a>
          {!locked && (
            <>
              <a
                href={`/api/filing-periods/${filingPeriodId}/ddo-records/template`}
                className="inline-flex items-center rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-50"
              >
                Download Excel Template
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
              <Button onClick={() => setFormMode("new")}>Add DDO</Button>
            </>
          )}
        </div>
      </div>

      {importError && (
        <div className="mt-4">
          <Alert tone="red">{importError}</Alert>
        </div>
      )}

      {importSuccess && (
        <div className="mt-4">
          <Alert tone="green">{importSuccess}</Alert>
        </div>
      )}

      {importRows && (
        <Card className="mt-4 p-4">
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
                    <td className="py-1 text-red-600">
                      {row.errors.map((e) => e.message).join("; ")}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {importDuplicates.length > 0 && (
            <div className="mt-4 rounded-lg border border-amber-200 bg-amber-50 p-3">
              <p className="text-sm font-medium text-amber-800">
                {importDuplicates.length} DDO(s) appear more than once under the same Form Type
              </p>
              <ul className="mt-2 space-y-1 text-sm text-amber-900">
                {importDuplicates.map((dup) => (
                  <li key={`${dup.tan}::${dup.formType}`}>
                    {dup.tan} &mdash; {dup.name} ({dup.formType}): rows{" "}
                    {dup.rows.map((r) => r.rowNumber).join(", ")} &mdash; amounts{" "}
                    {dup.rows.map((r) => r.taxDeducted).join(" + ")} = {dup.mergedTaxDeducted}
                  </li>
                ))}
              </ul>
              <p className="mt-2 text-xs text-amber-700">
                Choose below whether to sum these into one record per DDO, or ignore (skip) them
                entirely so you can fix the sheet and re-import.
              </p>
            </div>
          )}

          {existingRecordCount > 0 && (
            <div className="mt-4 rounded-lg border border-slate-200 bg-slate-50 p-3">
              <p className="text-sm font-medium text-slate-800">
                This filing period already has {existingRecordCount} DDO record(s) saved. How
                should this import be applied?
              </p>
              <div className="mt-2 space-y-2 text-sm text-slate-700">
                <label className="flex items-start gap-2">
                  <input
                    type="radio"
                    className="mt-1"
                    checked={existingDataStrategy === "skip_existing"}
                    onChange={() => setExistingDataStrategy("skip_existing")}
                  />
                  <span>Add new DDOs only &mdash; leave any DDO/Form Type that already exists untouched</span>
                </label>
                <label className="flex items-start gap-2">
                  <input
                    type="radio"
                    className="mt-1"
                    checked={existingDataStrategy === "upsert"}
                    onChange={() => setExistingDataStrategy("upsert")}
                  />
                  <span>Update existing DDOs with the new amounts, and add any new ones</span>
                </label>
                <label className="flex items-start gap-2">
                  <input
                    type="radio"
                    className="mt-1"
                    checked={existingDataStrategy === "replace"}
                    onChange={() => setExistingDataStrategy("replace")}
                  />
                  <span>Delete all {existingRecordCount} existing record(s) first, then import this file fresh</span>
                </label>
              </div>
            </div>
          )}

          <div className="mt-3 flex flex-wrap gap-2">
            <Button onClick={() => commitImport("merge")} disabled={importing}>
              {importing
                ? "Importing..."
                : importDuplicates.length > 0
                  ? `Import (sum duplicate amounts)`
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

      {formMode === "new" && !locked && (
        <div className="mt-4">
          <DdoRecordForm
            clientId={filingPeriod.clientId}
            financialYear={filingPeriod.financialYear}
            statementType={filingPeriod.statementType}
            onSubmit={createRecord}
            onCancel={() => setFormMode("none")}
          />
        </div>
      )}

      <Card className="mt-4 divide-y divide-slate-100">
        {ddoRecords.length === 0 && <EmptyState>No DDO records yet.</EmptyState>}
        {ddoRecords.map((record) =>
          formMode === record.id && !locked ? (
            <div key={record.id} className="p-4">
              <DdoRecordForm
                clientId={filingPeriod.clientId}
                financialYear={filingPeriod.financialYear}
                statementType={filingPeriod.statementType}
                initialValues={record}
                onSubmit={(values) => updateRecord(record.id, values)}
                onCancel={() => setFormMode("none")}
              />
            </div>
          ) : (
            <div key={record.id} className="flex items-center justify-between p-4">
              <div>
                <p className="font-medium text-slate-900">
                  {record.serialNo}. {record.tan} &mdash; {record.name}
                </p>
                <p className="text-sm text-slate-500">
                  {record.formType} &middot; Tax deducted: {record.taxDeducted} &middot; Remitted:{" "}
                  {record.totalRemitted}
                </p>
              </div>
              {!locked && (
                <div className="flex gap-2">
                  <Button variant="secondary" onClick={() => setFormMode(record.id)}>
                    Edit
                  </Button>
                  <Button variant="danger" onClick={() => deleteRecord(record.id)}>
                    Delete
                  </Button>
                </div>
              )}
            </div>
          ),
        )}
      </Card>

      {!locked && (
        <Card className="mt-8 p-5">
          <Button onClick={handleGenerate} disabled={generateState.status === "loading"}>
            {generateState.status === "loading" ? "Generating..." : "Generate & Validate"}
          </Button>

          {generateState.status === "validationError" && (
            <div className="mt-3">
              <Alert>
                <p className="font-medium">Fix these before generating:</p>
                {[...generateState.fhErrors, ...generateState.bhErrors, ...generateState.tdErrors.flat()].map(
                  (err, i) => (
                    <p key={i}>
                      {err.fieldName}: {err.message}
                    </p>
                  ),
                )}
              </Alert>
            </div>
          )}

          {generateState.status === "fvuError" && (
            <div className="mt-3">
              <Alert>
                <p className="font-medium">{generateState.message ?? "FVU validation failed."}</p>
                {generateState.errors.map((err, i) => (
                  <p key={i}>
                    Line {err.lineNo} ({err.recordType}) &mdash; {err.fieldName}: {err.errorDescription}
                  </p>
                ))}
              </Alert>
            </div>
          )}
        </Card>
      )}

      {/* Receipt/acknowledgement details are a record of what was actually
          uploaded to the government portal — always editable, independent
          of whether the DDO data is currently locked. */}
      <Card className="mt-8 p-5">
        <div className="flex items-center justify-between">
          <h3 className="text-sm font-semibold text-slate-900">Upload Receipt</h3>
          {locked && (
            <Button variant="secondary" onClick={handleUnlock}>
              Edit Return
            </Button>
          )}
        </div>
        <form onSubmit={handleSaveReceipt} className="mt-4 flex flex-wrap items-end gap-3">
          <div className="space-y-1">
            <FieldLabel>Receipt / Acknowledgement Number</FieldLabel>
            <input
              className={`${inputClass} w-56`}
              value={receiptNumber}
              onChange={(e) => setReceiptNumber(e.target.value)}
              placeholder="e.g. PRN or acknowledgement no."
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>Receipt Date</FieldLabel>
            <input
              type="date"
              className={inputClass}
              value={receiptDate}
              onChange={(e) => setReceiptDate(e.target.value)}
            />
          </div>
          <Button type="submit" disabled={savingReceipt}>
            {savingReceipt ? "Saving..." : "Save Receipt"}
          </Button>
        </form>
      </Card>

      <div className="mt-8">
        <h2 className="text-lg font-medium text-slate-900">Generation History</h2>
        <Card className="mt-3 divide-y divide-slate-100">
          {history.length === 0 && (
            <EmptyState>No generation attempts yet.</EmptyState>
          )}
          {history.map((item) => (
            <div key={item.id} className="flex items-center justify-between p-4">
              <div>
                <p className="text-sm font-medium text-slate-900">
                  {new Date(item.createdAt).toLocaleString()}
                </p>
                <Badge tone={item.status === "FVU_PASSED" ? "green" : "red"}>
                  {item.status === "FVU_PASSED" ? "Passed" : "Failed"}
                </Badge>
              </div>
              {item.fvuFilePath && (
                <a
                  href={`/api/generated-files/${item.id}/download`}
                  className="text-sm text-indigo-600 hover:underline"
                >
                  Download
                </a>
              )}
            </div>
          ))}
        </Card>
      </div>
    </div>
  );
}
