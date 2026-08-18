"use client";

import { Suspense, useState } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { Alert, BrandFooter, Button, ContactInfo, FieldLabel, NexLogo, inputClass } from "@/components/ui";

function ResetPasswordForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get("token") ?? "";

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errors, setErrors] = useState<string[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [done, setDone] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setErrors([]);

    const res = await fetch("/api/auth/reset-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ token, newPassword, confirmPassword }),
    });

    if (!res.ok) {
      setSubmitting(false);
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      const formErrors = body.error?.formErrors ?? [];
      setErrors([...formErrors, ...fieldErrors] as string[]);
      return;
    }

    setSubmitting(false);
    setDone(true);
    setTimeout(() => router.push("/login"), 2000);
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="space-y-4 rounded-2xl border border-slate-200 bg-white p-8 shadow-xl shadow-slate-200/50"
    >
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Set a new password</h1>
        <p className="mt-1 text-sm text-slate-500">Choose a new password for your account.</p>
      </div>

      {!token && <Alert>This reset link is missing its token. Request a new one below.</Alert>}
      {errors.length > 0 && (
        <Alert>
          <ul className="list-inside list-disc">
            {errors.map((err, i) => (
              <li key={i}>{err}</li>
            ))}
          </ul>
        </Alert>
      )}
      {done && <Alert tone="green">Password reset. Redirecting you to login...</Alert>}

      {!done && (
        <>
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

          <Button type="submit" disabled={submitting || !token} className="w-full">
            {submitting ? "Saving..." : "Reset password"}
          </Button>
        </>
      )}

      <p className="text-center text-sm">
        <Link href="/forgot-password" className="font-medium text-indigo-600 hover:text-indigo-700">
          Request a new link
        </Link>
      </p>
    </form>
  );
}

export default function ResetPasswordPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-b from-slate-50 to-slate-100 px-4">
      <div className="w-full max-w-sm">
        <div className="mb-8 flex flex-col items-center gap-1.5">
          <NexLogo />
        </div>

        <Suspense fallback={null}>
          <ResetPasswordForm />
        </Suspense>

        <ContactInfo className="mt-6" />
        <BrandFooter className="mt-3" />
      </div>
    </div>
  );
}
