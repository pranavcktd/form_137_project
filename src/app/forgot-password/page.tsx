"use client";

import { useState } from "react";
import Link from "next/link";
import { Alert, BrandFooter, Button, ContactInfo, FieldLabel, NexLogo, inputClass } from "@/components/ui";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [done, setDone] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);

    await fetch("/api/auth/forgot-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email }),
    });

    setSubmitting(false);
    setDone(true);
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
              Enter your account email and we&apos;ll send you a link to reset it.
            </p>
          </div>

          {done ? (
            <Alert tone="green">
              If that email is registered, a password reset link has been sent. It expires in 30
              minutes.
            </Alert>
          ) : (
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
                {submitting ? "Sending..." : "Send reset link"}
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
