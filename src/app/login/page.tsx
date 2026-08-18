"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { signIn } from "next-auth/react";
import { Alert, BrandFooter, Button, ContactInfo, FieldLabel, NexLogo, inputClass } from "@/components/ui";

const PRODUCT_BADGES = [
  { key: "IT", label: "IT", tone: "bg-indigo-50 text-indigo-700 shadow-indigo-100/80", rotate: "-rotate-3" },
  { key: "TDS", label: "TDS", tone: "bg-sky-50 text-sky-700 shadow-sky-100/80", rotate: "rotate-2" },
  { key: "GST", label: "GST", tone: "bg-amber-50 text-amber-700 shadow-amber-100/80", rotate: "-rotate-2" },
] as const;

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    const result = await signIn("credentials", {
      email,
      password,
      redirect: false,
    });

    if (result?.error) {
      const statusRes = await fetch("/api/auth/login-status", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email }),
      });
      const status = await statusRes.json();
      setSubmitting(false);

      if (status.status === "org_suspended") {
        setError(
          `Your firm's subscription is inactive. Contact ${status.contactName}` +
            (status.contactEmail ? ` (${status.contactEmail})` : "") +
            " to reactivate it.",
        );
        return;
      }
      if (status.status === "account_disabled") {
        setError(
          `Your account has been disabled. Contact ${status.contactName}` +
            (status.contactEmail ? ` (${status.contactEmail})` : "") +
            (status.contactPhone ? `, ${status.contactPhone}` : "") +
            " to have it reactivated.",
        );
        return;
      }
      setError("Invalid email or password.");
      return;
    }
    setSubmitting(false);

    router.push("/");
    router.refresh();
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 py-12">
      <div className="grid w-full max-w-5xl grid-cols-1 items-center gap-12 lg:grid-cols-2">
        {/* Marketing panel — hidden on small screens */}
        <div className="hidden lg:block">
          <NexLogo />

          <div className="mt-12 flex justify-center gap-4">
            {PRODUCT_BADGES.map((p) => (
              <span
                key={p.key}
                className={`flex h-20 w-24 items-center justify-center rounded-2xl shadow-lg transition-transform hover:rotate-0 ${p.tone} ${p.rotate}`}
              >
                <span className="text-xl font-bold">{p.label}</span>
              </span>
            ))}
          </div>

          <h1 className="mt-8 text-3xl font-semibold tracking-tight text-slate-900">
            Simplify your tax compliance with Nex
          </h1>

          <ul className="mt-6 space-y-3">
            <li className="flex items-start gap-2 text-sm text-slate-600">
              <span className="mt-0.5 text-indigo-600">✓</span>
              File Form 137 / 24G returns with built-in FVU validation — Nex IT
            </li>
            <li className="flex items-start gap-2 text-sm text-slate-600">
              <span className="mt-0.5 text-indigo-600">✓</span>
              TDS returns (24Q / 26Q / 27Q / 27EQ) — Nex TDS
            </li>
            <li className="flex items-start gap-2 text-sm text-slate-600">
              <span className="mt-0.5 text-indigo-600">✓</span>
              GST return filing — Nex GST
            </li>
          </ul>

          <div className="mt-10">
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">
              Need help?
            </p>
            <ContactInfo align="start" className="mt-2" />
          </div>
        </div>

        {/* Login card */}
        <div className="mx-auto w-full max-w-sm">
          <div className="mb-6 flex flex-col items-center gap-1.5 lg:hidden">
            <NexLogo />
          </div>

          <form
            onSubmit={handleSubmit}
            className="space-y-4 rounded-2xl border border-slate-200 bg-white p-8 shadow-xl shadow-slate-200/50"
          >
            <div>
              <h1 className="text-xl font-semibold text-indigo-600">Welcome back!</h1>
              <p className="mt-1 text-sm text-slate-500">Login to access your account.</p>
            </div>

            {error && <Alert>{error}</Alert>}

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

            <div className="space-y-1">
              <FieldLabel>Password</FieldLabel>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className={`${inputClass} pr-16`}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((v) => !v)}
                  className="absolute inset-y-0 right-0 px-3 text-xs font-medium text-slate-400 hover:text-slate-600"
                >
                  {showPassword ? "Hide" : "Show"}
                </button>
              </div>
            </div>

            <Button type="submit" disabled={submitting} className="w-full">
              {submitting ? "Signing in..." : "Login"}
            </Button>
          </form>

          <p className="mt-6 text-center text-sm">
            <Link href="/forgot-password" className="font-medium text-indigo-600 hover:text-indigo-700">
              Forgot password?
            </Link>
          </p>
          <p className="mt-2 text-center text-xs text-slate-400">
            New to Nex? Contact your administrator.
          </p>
          <ContactInfo className="mt-3" />
          <BrandFooter className="mt-3" />
        </div>
      </div>
    </div>
  );
}
