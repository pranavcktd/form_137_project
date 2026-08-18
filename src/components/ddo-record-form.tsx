"use client";

import { useState } from "react";
import { ddoEditableFields } from "@/lib/excel/ddoFields";
import { FIELD_NAME_TO_PROPERTY } from "@/lib/excel/fieldNameMap";
import { annexureOptions } from "@/lib/excel/annexureOptions";
import type { AnnexureKey } from "@/lib/validation/annexureLookup";
import type { DdoRecordFormInput } from "@/lib/validation/ddoRecord";
import type { StatementType } from "@/schemas/24g-f137/v1_9";
import { DdoMasterSearch, type DdoMasterSearchResult } from "@/components/ddo-master-search";
import { Button } from "@/components/ui";

// Identity/contact fields sourced from the DDO Master — shown read-only
// once a DDO has been selected, since they're a snapshot, not free text.
const MASTER_FIELD_PROPERTIES = new Set<keyof DdoRecordFormInput>([
  "tan",
  "name",
  "address1",
  "address2",
  "address3",
  "address4",
  "city",
  "state",
  "pin",
  "ddoRegNo",
  "ddoCode",
  "email",
]);

export function ddoRecordDefaults(): DdoRecordFormInput & { ddoMasterId: string } {
  return {
    ddoMasterId: "",
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
    taxDeducted: 0,
    formType: "",
    totalRemitted: 0,
    natureOfDeduction: "",
    mode: "ADD",
  };
}

const inputClass =
  "w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-slate-500 focus:outline-none";
const readOnlyInputClass =
  "w-full rounded-md border border-slate-200 bg-slate-100 px-3 py-2 text-sm text-slate-600";

export function DdoRecordForm({
  clientId,
  financialYear,
  statementType,
  initialValues,
  onSubmit,
  onCancel,
}: {
  clientId: string;
  financialYear: number;
  statementType: StatementType;
  initialValues?: DdoRecordFormInput & { ddoMasterId?: string | null };
  onSubmit: (values: DdoRecordFormInput) => Promise<void>;
  onCancel: () => void;
}) {
  const [values, setValues] = useState<DdoRecordFormInput & { ddoMasterId: string }>({
    ...ddoRecordDefaults(),
    ...initialValues,
    ddoMasterId: initialValues?.ddoMasterId ?? "",
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fields = ddoEditableFields(financialYear, statementType);
  const ddoSelected = values.ddoMasterId.length > 0;

  const set = (property: keyof DdoRecordFormInput, value: string | number) =>
    setValues((prev) => ({ ...prev, [property]: value }));

  const handleSelectDdo = (ddo: DdoMasterSearchResult) => {
    setValues((prev) => ({
      ...prev,
      ddoMasterId: ddo.id,
      tan: ddo.tan,
      name: ddo.name,
      address1: ddo.address1 ?? "",
      address2: ddo.address2 ?? "",
      address3: ddo.address3 ?? "",
      address4: ddo.address4 ?? "",
      city: ddo.city ?? "",
      state: ddo.state ?? "",
      pin: ddo.pin ?? "",
      ddoRegNo: ddo.ddoRegNo ?? "",
      ddoCode: ddo.ddoCode ?? "",
      email: ddo.email ?? "",
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await onSubmit(values);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not save the record.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="space-y-4 rounded-lg border border-slate-200 bg-slate-50 p-4"
    >
      {error && (
        <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>
      )}

      {!ddoSelected && (
        <div className="space-y-1">
          <label className="text-sm font-medium text-slate-700">
            Select DDO <span className="text-red-500">*</span>
          </label>
          <DdoMasterSearch clientId={clientId} onSelect={handleSelectDdo} />
        </div>
      )}
      {ddoSelected && !initialValues && (
        <p className="text-xs text-emerald-700">
          Selected: {values.tan} &mdash; {values.name}
        </p>
      )}

      {ddoSelected && (
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
          {fields.map((field) => {
            const property = FIELD_NAME_TO_PROPERTY[field.name];
            if (!property) return null;

            const isMasterField = MASTER_FIELD_PROPERTIES.has(property);
            const isAmount = property === "taxDeducted" || property === "totalRemitted";
            const value = values[property];

            if (isMasterField) {
              return (
                <div key={field.name} className="space-y-1">
                  <label className="text-sm font-medium text-slate-700">{field.name}</label>
                  <input className={readOnlyInputClass} readOnly value={String(value ?? "")} />
                </div>
              );
            }

            return (
              <div key={field.name} className="space-y-1">
                <label className="text-sm font-medium text-slate-700">
                  {field.name}
                  {field.mandatory === "M" ? (
                    <span className="text-red-500"> * Required</span>
                  ) : (
                    <span className="text-slate-400"> (Optional)</span>
                  )}
                </label>
                {field.annexureRef ? (
                  <select
                    className={inputClass}
                    required={field.mandatory === "M"}
                    value={String(value)}
                    onChange={(e) => set(property, e.target.value)}
                  >
                    <option value="">Select</option>
                    {annexureOptions(field.annexureRef as AnnexureKey).map((opt) => (
                      <option key={opt.code} value={opt.code}>
                        {opt.label}
                      </option>
                    ))}
                  </select>
                ) : (
                  <input
                    className={inputClass}
                    required={field.mandatory === "M"}
                    type={isAmount ? "number" : "text"}
                    step={isAmount ? "0.01" : undefined}
                    maxLength={typeof field.length === "number" ? field.length : undefined}
                    value={value}
                    onChange={(e) =>
                      set(property, isAmount ? Number(e.target.value) : e.target.value)
                    }
                  />
                )}
                <p className="text-xs text-slate-500">{field.remarks}</p>
              </div>
            );
          })}
        </div>
      )}

      <div className="flex gap-2">
        <Button type="submit" disabled={submitting || !ddoSelected}>
          {submitting ? "Saving..." : "Save"}
        </Button>
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>
      </div>
    </form>
  );
}
