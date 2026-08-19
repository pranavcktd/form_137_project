"use client";

import { useState } from "react";
import Link from "next/link";
import { Alert, BrandFooter, Button, ContactInfo, FieldLabel, NexLogo, inputClass } from "@/components/ui";

type Result =
  | { status: "sent" }
  | { status: "not_found" }
  | { status: "send_failed" }
  | { status: "org_suspended"; contactName: string; contactEmail: string | null }
  | { status: "account_disabled"; contactName: string; contactEmail: string | null; contactPhone: string | null };

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState<Result | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    setResult(null);

    const res = await fetch("/api/auth/forgot-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email }),
    });

    setSubmitting(false);
    if (!res.ok) {
      setError("Something went wrong — please try again.");
      return;
    }
    setResult(await res.json());
  };

  const renderResult = () => {
    if (!result) return null;
    switch (result.status) {
      case "sent":
        return (
          <Alert tone="green">
            A temporary password has been emailed to you. Your existing password still works
            until you sign in with the new one — once you do, you&apos;ll be asked to set a new
            password.
          </Alert>
        );
      case "not_found":
        return (
          <Alert>
            Sorry, this email isn&apos;t registered. Contact your admin, or reach out to us below,
            to get access.
          </Alert>
        );
      case "send_failed":
        return <Alert>We couldn&apos;t send that email right now. Please try again shortly.</Alert>;
      case "org_suspended":
        return (
          <Alert>
            Your firm&apos;s subscription is inactive. Contact {result.contactName}
            {result.contactEmail ? ` (${result.contactEmail})` : ""} to reactivate it.
          </Alert>
        );
      case "account_disabled":
        return (
          <Alert>
            Your account has been disabled. Contact {result.contactName}
            {result.contactEmail ? ` (${result.contactEmail})` : ""}
            {result.contactPhone ? `, ${result.contactPhone}` : ""} to have it reactivated.
          </Alert>
        );
    }
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
            <h1 className="text-xl font-semibold text-slate-900">Forgot your password?</h1>
            <p className="mt-1 text-sm text-slate-500">
              Enter your account email and we&apos;ll email you a temporary password to sign in with.
            </p>
          </div>

          {error && <Alert>{error}</Alert>}
          {renderResult()}

          {result?.status !== "sent" && (
            <>
              <div className="space-y-1">
                <FieldLabel>Email</FieldLabel>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className={inputClass}
                />
              </div>

              <Button type="submit" disabled={submitting} className="w-full">
                {submitting ? "Sending..." : "Email me a temporary password"}
              </Button>
            </>
          )}

          <p className="text-center text-sm">
            <Link href="/login" className="font-medium text-indigo-600 hover:text-indigo-700">
              Back to login
            </Link>
          </p>
        </form>
        <ContactInfo className="mt-6" />
        <BrandFooter className="mt-3" />
      </div>
    </div>
  );
}
