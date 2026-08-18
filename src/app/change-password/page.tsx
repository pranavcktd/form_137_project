"use client";

import { useState } from "react";
import { signOut } from "next-auth/react";
import { Alert, BrandFooter, Button, ContactInfo, FieldLabel, NexLogo, inputClass } from "@/components/ui";

export default function ChangePasswordPage() {
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errors, setErrors] = useState<string[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [done, setDone] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setErrors([]);

    const res = await fetch("/api/profile/change-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ currentPassword, newPassword, confirmPassword }),
    });

    if (!res.ok) {
      setSubmitting(false);
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors
        ? Object.values(body.error.fieldErrors).flat()
        : [];
      const formErrors = body.error?.formErrors ?? [];
      setErrors([...formErrors, ...fieldErrors] as string[]);
      return;
    }

    setDone(true);
    await signOut({ redirectTo: "/login" });
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-b from-slate-50 to-slate-100 px-4">
      <div className="w-full max-w-sm">
        <div className="mb-8 flex flex-col items-center gap-1.5">
          <NexLogo />
        </div>

        <form
          onSubmit={handleSubmit}
          className="space-y-4 rounded-2xl border border-slate-200 bg-white p-8 shadow-xl shadow-slate-200/50"
        >
          <div>
            <h1 className="text-xl font-semibold text-slate-900">Set a new password</h1>
            <p className="mt-1 text-sm text-slate-500">
              Your password was reset. Enter it below along with a new password before
              continuing.
            </p>
          </div>

          {errors.length > 0 && (
            <Alert>
              <ul className="list-inside list-disc">
                {errors.map((err, i) => (
                  <li key={i}>{err}</li>
                ))}
              </ul>
            </Alert>
          )}
          {done && <Alert tone="green">Password changed. Signing you out to sign in again...</Alert>}

          <div className="space-y-1">
            <FieldLabel>Current (temporary) password</FieldLabel>
            <input
              type="password"
              required
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              className={inputClass}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>New password</FieldLabel>
            <input
              type="password"
              required
              minLength={8}
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              className={inputClass}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>Confirm new password</FieldLabel>
            <input
              type="password"
              required
              minLength={8}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              className={inputClass}
            />
          </div>

          <Button type="submit" disabled={submitting || done} className="w-full">
            {submitting ? "Saving..." : "Change password"}
          </Button>
        </form>
        <ContactInfo className="mt-6" />
        <BrandFooter className="mt-3" />
      </div>
    </div>
  );
}
