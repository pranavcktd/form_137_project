"use client";

import { useEffect, useState } from "react";
import { Alert, Badge, Button, Card, EmptyState, LoadingState } from "@/components/ui";

type UploadSummary = {
  id: string;
  originalFilename: string;
  uploadedByName: string;
  status: "ANALYZING" | "DONE" | "FAILED";
  errorMessage: string | null;
  createdAt: string;
};

type FvuPackageAnalysis = {
  baselineJar: string;
  uploadedJarPath: string;
  classCounts: { baseline: number; uploaded: number };
  classesAdded: string[];
  classesRemoved: string[];
  fieldLabels: { added: string[]; removed: string[]; unchangedCount: number };
  errorCodes: { added: { code: string; sampleLine: string }[]; removed: string[] };
  decompiledOutputDir: string;
};

type UploadDetail = UploadSummary & { analysisResult: FvuPackageAnalysis | null; storagePath: string };

export function FvuUpdatesClient() {
  const [uploads, setUploads] = useState<UploadSummary[] | null>(null);
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState<string | null>(null);
  const [expandedId, setExpandedId] = useState<string | null>(null);
  const [detail, setDetail] = useState<UploadDetail | null>(null);
  const [loadingDetail, setLoadingDetail] = useState(false);
  const [busyId, setBusyId] = useState<string | null>(null);

  const load = () => {
    fetch("/api/admin/fvu-updates")
      .then((res) => res.json())
      .then(setUploads);
  };

  useEffect(load, []);

  const handleUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return;
    setUploading(true);
    setUploadError(null);

    const formData = new FormData();
    formData.append("file", file);
    const res = await fetch("/api/admin/fvu-updates", { method: "POST", body: formData });
    setUploading(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setUploadError([...(body.error?.formErrors ?? []), ...fieldErrors].join(" ") || "Upload failed.");
      return;
    }

    const created = await res.json();
    setFile(null);
    load();
    setExpandedId(created.id);
    setDetail(created);
  };

  const toggleExpand = async (upload: UploadSummary) => {
    if (expandedId === upload.id) {
      setExpandedId(null);
      setDetail(null);
      return;
    }
    setExpandedId(upload.id);
    setDetail(null);
    setLoadingDetail(true);
    const res = await fetch(`/api/admin/fvu-updates/${upload.id}`);
    setDetail(await res.json());
    setLoadingDetail(false);
  };

  const handleDelete = async (upload: UploadSummary) => {
    if (!window.confirm(`Delete the analysis for "${upload.originalFilename}"? This can't be undone.`)) return;
    setBusyId(upload.id);
    await fetch(`/api/admin/fvu-updates/${upload.id}`, { method: "DELETE" });
    setBusyId(null);
    if (expandedId === upload.id) {
      setExpandedId(null);
      setDetail(null);
    }
    load();
  };

  if (uploads === null) return <LoadingState />;

  return (
    <div className="space-y-6">
      <Card className="p-6">
        <h3 className="mb-1 text-sm font-semibold text-slate-900">Upload a New FVU/RPU Package</h3>
        <p className="mb-4 text-sm text-slate-500">
          Upload the .zip or .jar exactly as downloaded from Protean. The system decompiles it (never
          runs it) and compares it against the currently built-in version — new/removed field labels,
          new/removed error codes, and which internal classes changed. This is a starting point for
          someone to review and implement from, not an automatic update to how returns are validated.
        </p>
        <form onSubmit={handleUpload} className="flex flex-wrap items-center gap-3">
          {uploadError && (
            <div className="w-full">
              <Alert>{uploadError}</Alert>
            </div>
          )}
          <input
            type="file"
            accept=".zip,.jar"
            onChange={(e) => setFile(e.target.files?.[0] ?? null)}
            className="text-sm text-slate-700"
          />
          <Button type="submit" disabled={!file || uploading}>
            {uploading ? "Analyzing... (can take up to a couple of minutes)" : "Upload & Analyze"}
          </Button>
        </form>
      </Card>

      <Card className="overflow-x-auto">
        {uploads.length === 0 && <EmptyState>No FVU/RPU packages uploaded yet.</EmptyState>}
        {uploads.length > 0 && (
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b border-slate-200 text-slate-500">
                <th className="px-4 py-2 font-medium">File</th>
                <th className="px-4 py-2 font-medium">Uploaded By</th>
                <th className="px-4 py-2 font-medium">When</th>
                <th className="px-4 py-2 font-medium">Status</th>
                <th className="px-4 py-2 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {uploads.map((upload) => (
                <>
                  <tr key={upload.id}>
                    <td className="px-4 py-2 font-medium text-slate-900">{upload.originalFilename}</td>
                    <td className="px-4 py-2 text-slate-500">{upload.uploadedByName}</td>
                    <td className="px-4 py-2 text-slate-500">{new Date(upload.createdAt).toLocaleString()}</td>
                    <td className="px-4 py-2">
                      <Badge
                        tone={upload.status === "DONE" ? "green" : upload.status === "FAILED" ? "red" : "amber"}
                      >
                        {upload.status === "DONE" ? "Analyzed" : upload.status === "FAILED" ? "Failed" : "Analyzing"}
                      </Badge>
                    </td>
                    <td className="px-4 py-2">
                      <div className="flex flex-wrap gap-2">
                        <Button variant="secondary" onClick={() => toggleExpand(upload)}>
                          {expandedId === upload.id ? "Hide Report" : "View Report"}
                        </Button>
                        <Button variant="danger" disabled={busyId === upload.id} onClick={() => handleDelete(upload)}>
                          Delete
                        </Button>
                      </div>
                    </td>
                  </tr>
                  {expandedId === upload.id && (
                    <tr>
                      <td colSpan={5} className="bg-slate-50 px-4 py-4">
                        {loadingDetail || !detail ? (
                          <LoadingState />
                        ) : detail.status === "FAILED" ? (
                          <Alert>{detail.errorMessage ?? "Analysis failed."}</Alert>
                        ) : detail.analysisResult ? (
                          <AnalysisReport analysis={detail.analysisResult} />
                        ) : (
                          <p className="text-sm text-slate-500">No report available.</p>
                        )}
                      </td>
                    </tr>
                  )}
                </>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  );
}

function StringListSection({ title, tone, items }: { title: string; tone: "green" | "red"; items: string[] }) {
  if (items.length === 0) return null;
  return (
    <div>
      <p className="text-sm font-medium text-slate-800">
        {title} <Badge tone={tone}>{items.length}</Badge>
      </p>
      <ul className="mt-1 max-h-48 overflow-y-auto rounded-lg border border-slate-200 bg-white p-2 text-xs text-slate-700">
        {items.map((item) => (
          <li key={item} className="py-0.5 font-mono">
            {item}
          </li>
        ))}
      </ul>
    </div>
  );
}

function AnalysisReport({ analysis }: { analysis: FvuPackageAnalysis }) {
  const nothingChanged =
    analysis.fieldLabels.added.length === 0 &&
    analysis.fieldLabels.removed.length === 0 &&
    analysis.errorCodes.added.length === 0 &&
    analysis.errorCodes.removed.length === 0 &&
    analysis.classesAdded.length === 0 &&
    analysis.classesRemoved.length === 0;

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap gap-4 text-sm text-slate-600">
        <span>
          Classes: {analysis.classCounts.baseline} &rarr; {analysis.classCounts.uploaded}
        </span>
        <span>Baseline: {analysis.baselineJar}</span>
        <span>Uploaded: {analysis.uploadedJarPath}</span>
      </div>

      {nothingChanged && (
        <Alert tone="green">
          No differences detected in field labels, error codes, or class list — this package may be
          identical (or very close) to what&apos;s already built in.
        </Alert>
      )}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <StringListSection title="New field labels" tone="green" items={analysis.fieldLabels.added} />
        <StringListSection title="Removed field labels" tone="red" items={analysis.fieldLabels.removed} />
        <StringListSection
          title="New error codes"
          tone="green"
          items={analysis.errorCodes.added.map((e) => `${e.code} — ${e.sampleLine}`)}
        />
        <StringListSection title="Removed error codes" tone="red" items={analysis.errorCodes.removed} />
        <StringListSection title="New internal classes" tone="green" items={analysis.classesAdded} />
        <StringListSection title="Removed internal classes" tone="red" items={analysis.classesRemoved} />
      </div>

      <p className="text-xs text-slate-500">
        Full decompiled source of the uploaded package is saved at{" "}
        <code className="rounded bg-slate-100 px-1 py-0.5">{analysis.decompiledOutputDir}</code> on the
        server for manual review — field/error-code text survives Protean&apos;s obfuscation, but the
        actual validation logic inside each class does not read cleanly and still needs a human to work
        through before anything here gets implemented.
      </p>
    </div>
  );
}
