"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import {
  ClientProfileFields,
  clientProfileDefaults,
  type ClientProfileValues,
} from "@/components/client-profile-fields";
import { Alert, Button, Card, LoadingState } from "@/components/ui";

export function ClientProfileEditor({ clientId }: { clientId: string }) {
  const [profile, setProfile] = useState<ClientProfileValues>(clientProfileDefaults());
  const [availableReturnTypes, setAvailableReturnTypes] = useState<string[]>(["FORM137"]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<string[]>([]);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    fetch("/api/organization/me")
      .then((res) => res.json())
      .then((org) => setAvailableReturnTypes(org.enabledApplications ?? ["FORM137"]));
  }, []);

  useEffect(() => {
    fetch(`/api/clients/${clientId}`)
      .then((res) => res.json())
      .then((client) => {
        if (client) {
          setProfile({
            enabledReturnTypes: client.enabledReturnTypes ?? ["FORM137"],
            ain: client.ain ?? "",
            tan: client.tan ?? "",
            ministryName: client.ministryName ?? "",
            subMinistryName: client.subMinistryName ?? "",
            departmentName: client.departmentName ?? "",
            govtCategory: client.govtCategory ?? "CENTRAL",
            countryCode: client.countryCode ?? "",
            responsiblePersonName: client.responsiblePersonName ?? "",
            responsiblePersonFirstName: client.responsiblePersonFirstName ?? "",
            responsiblePersonMiddleName: client.responsiblePersonMiddleName ?? "",
            responsiblePersonLastName: client.responsiblePersonLastName ?? "",
            responsiblePersonDesignation: client.responsiblePersonDesignation ?? "",
            responsiblePersonAddress1: client.responsiblePersonAddress1 ?? "",
            responsiblePersonAddress2: client.responsiblePersonAddress2 ?? "",
            responsiblePersonAddress3: client.responsiblePersonAddress3 ?? "",
            responsiblePersonAddress4: client.responsiblePersonAddress4 ?? "",
            responsiblePersonCity: client.responsiblePersonCity ?? "",
            responsiblePersonState: client.responsiblePersonState ?? "",
            responsiblePersonPin: client.responsiblePersonPin ?? "",
            responsiblePersonStdCode: client.responsiblePersonStdCode ?? "",
            responsiblePersonPhone: client.responsiblePersonPhone ?? "",
            responsiblePersonMobile: client.responsiblePersonMobile ?? "",
            responsiblePersonEmail: client.responsiblePersonEmail ?? "",
          });
        }
        setLoading(false);
      });
  }, [clientId]);

  // Arriving from a "fix this field" link on an FVU error (e.g. from a filing
  // period's Generate & Validate failure) — scroll to and focus that exact
  // field once the profile has actually loaded and rendered it.
  useEffect(() => {
    if (loading) return;
    const focusKey = new URLSearchParams(window.location.search).get("focus");
    if (!focusKey) return;
    const el = document.getElementById(focusKey);
    el?.scrollIntoView({ behavior: "smooth", block: "center" });
    el?.focus();
  }, [loading]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setErrors([]);
    setSaved(false);

    const res = await fetch(`/api/clients/${clientId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(profile),
    });

    setSaving(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors
        ? Object.values(body.error.fieldErrors).flat()
        : [];
      const formErrors = body.error?.formErrors ?? [];
      setErrors([...formErrors, ...fieldErrors] as string[]);
      return;
    }

    setSaved(true);
  };

  if (loading) return <LoadingState />;

  return (
    <Card className="p-6">
      <div className="mb-4 flex items-center justify-between">
        <Link
          href={`/clients/${clientId}/filing-periods`}
          className="text-sm text-indigo-600 hover:underline"
        >
          &larr; Back to filing periods
        </Link>
      </div>
      <form onSubmit={handleSubmit} className="space-y-6">
        {errors.length > 0 && (
          <Alert>
            <ul className="list-inside list-disc">
              {errors.map((err, i) => (
                <li key={i}>{err}</li>
              ))}
            </ul>
          </Alert>
        )}
        {saved && <Alert tone="green">Profile saved.</Alert>}

        <ClientProfileFields
          values={profile}
          onChange={setProfile}
          availableReturnTypes={availableReturnTypes}
        />

        <Button type="submit" disabled={saving}>
          {saving ? "Saving..." : "Save Profile"}
        </Button>
      </form>
    </Card>
  );
}
