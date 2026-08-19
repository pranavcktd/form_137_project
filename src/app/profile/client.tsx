"use client";

import { useEffect, useState } from "react";
import { signOut } from "next-auth/react";
import { Alert, Button, Card, FieldLabel, LoadingState, inputClass } from "@/components/ui";

type Profile = {
  id: string;
  name: string;
  email: string;
  role: string;
  lastLoginAt: string | null;
  organization: { name: string };
};

export function ProfileClient() {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [loading, setLoading] = useState(true);
  const [name, setName] = useState("");
  const [savingProfile, setSavingProfile] = useState(false);
  const [profileMessage, setProfileMessage] = useState<string | null>(null);
  const [profileErrors, setProfileErrors] = useState<string[]>([]);

  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [changingPassword, setChangingPassword] = useState(false);
  const [passwordErrors, setPasswordErrors] = useState<string[]>([]);

  useEffect(() => {
    fetch("/api/profile")
      .then((res) => res.json())
      .then((data) => {
        setProfile(data);
        setName(data.name);
        setLoading(false);
      });
  }, []);

  const handleSaveProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setSavingProfile(true);
    setProfileMessage(null);
    setProfileErrors([]);

    const res = await fetch("/api/profile", {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name }),
    });
    setSavingProfile(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors
        ? Object.values(body.error.fieldErrors).flat()
        : [];
      setProfileErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    setProfileMessage("Profile updated.");
    setProfile((prev) => (prev ? { ...prev, name } : prev));
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setChangingPassword(true);
    setPasswordErrors([]);

    const res = await fetch("/api/profile/change-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ currentPassword, newPassword, confirmPassword }),
    });

    if (!res.ok) {
      setChangingPassword(false);
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors
        ? Object.values(body.error.fieldErrors).flat()
        : [];
      setPasswordErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    await signOut({ redirectTo: "/login" });
  };

  if (loading || !profile) return <LoadingState />;

  return (
    <div className="space-y-6">
      <Card className="p-6">
        <h3 className="mb-4 text-sm font-semibold text-slate-900">Account</h3>
        <form onSubmit={handleSaveProfile} className="space-y-4">
          {profileErrors.length > 0 && (
            <Alert>
              <ul className="list-inside list-disc">
                {profileErrors.map((err, i) => (
                  <li key={i}>{err}</li>
                ))}
              </ul>
            </Alert>
          )}
          {profileMessage && <Alert tone="green">{profileMessage}</Alert>}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div className="space-y-1">
              <FieldLabel>Name</FieldLabel>
              <input
                className={inputClass}
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </div>
            <div className="space-y-1">
              <FieldLabel>Email (can&apos;t be changed)</FieldLabel>
              <input className={inputClass} value={profile.email} disabled readOnly />
            </div>
            <div className="space-y-1">
              <FieldLabel>Firm</FieldLabel>
              <input className={inputClass} value={profile.organization.name} disabled readOnly />
            </div>
            <div className="space-y-1">
              <FieldLabel>Last login</FieldLabel>
              <input
                className={inputClass}
                disabled
                readOnly
                value={profile.lastLoginAt ? new Date(profile.lastLoginAt).toLocaleString() : "This is your first login"}
              />
            </div>
          </div>
          <Button type="submit" disabled={savingProfile}>
            {savingProfile ? "Saving..." : "Save changes"}
          </Button>
        </form>
      </Card>

      <Card className="p-6">
        <h3 className="mb-4 text-sm font-semibold text-slate-900">Change Password</h3>
        <form onSubmit={handleChangePassword} className="space-y-4">
          {passwordErrors.length > 0 && (
            <Alert>
              <ul className="list-inside list-disc">
                {passwordErrors.map((err, i) => (
                  <li key={i}>{err}</li>
                ))}
              </ul>
            </Alert>
          )}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
            <div className="space-y-1">
              <FieldLabel>Current password</FieldLabel>
              <input
                type="password"
                className={inputClass}
                required
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
              />
            </div>
            <div className="space-y-1">
              <FieldLabel>New password</FieldLabel>
              <input
                type="password"
                className={inputClass}
                required
                minLength={8}
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
              />
            </div>
            <div className="space-y-1">
              <FieldLabel>Confirm new password</FieldLabel>
              <input
                type="password"
                className={inputClass}
                required
                minLength={8}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
              />
            </div>
          </div>
          <Button type="submit" disabled={changingPassword}>
            {changingPassword ? "Changing..." : "Change password"}
          </Button>
          <p className="text-xs text-slate-500">
            You&apos;ll be signed out after changing your password so you can sign back in with it.
          </p>
        </form>
      </Card>

      {profile.role === "SUPER_ADMIN" && <ModuleVisibilitySettingsCard />}
      {profile.role === "SUPER_ADMIN" && <PlatformSettingsCard />}
      {profile.role === "SUPER_ADMIN" && <RazorpaySettingsCard />}
      {profile.role === "ADMIN" && <FirmSmtpSettingsCard />}
    </div>
  );
}

function ModuleVisibilitySettingsCard() {
  const [hideUnsubscribedModules, setHideUnsubscribedModules] = useState<boolean | null>(null);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    fetch("/api/admin/settings")
      .then((res) => res.json())
      .then((data) => setHideUnsubscribedModules(Boolean(data.hideUnsubscribedModules)));
  }, []);

  const handleChange = async (value: boolean) => {
    setSaving(true);
    setMessage(null);
    const res = await fetch("/api/admin/settings", {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ hideUnsubscribedModules: value }),
    });
    setSaving(false);
    if (!res.ok) {
      setMessage("Could not save this setting.");
      return;
    }
    setHideUnsubscribedModules(value);
    setMessage("Saved.");
  };

  if (hideUnsubscribedModules === null) return null;

  return (
    <Card className="p-6">
      <h3 className="mb-1 text-sm font-semibold text-slate-900">Module Visibility</h3>
      <p className="mb-4 text-sm text-slate-500">
        Controls what firms see on their dashboard for products they haven&apos;t subscribed to
        (e.g. Nex TDS, Nex GST) — applies platform-wide, and takes effect immediately.
      </p>
      {message && (
        <div className="mb-4">
          <Alert tone="green">{message}</Alert>
        </div>
      )}
      <div className="space-y-2 text-sm text-slate-700">
        <label className="flex items-start gap-2">
          <input
            type="radio"
            className="mt-1"
            checked={!hideUnsubscribedModules}
            disabled={saving}
            onChange={() => handleChange(false)}
          />
          <span>Show unsubscribed products locked/grayed out (current default)</span>
        </label>
        <label className="flex items-start gap-2">
          <input
            type="radio"
            className="mt-1"
            checked={hideUnsubscribedModules}
            disabled={saving}
            onChange={() => handleChange(true)}
          />
          <span>Hide unsubscribed products entirely</span>
        </label>
      </div>
    </Card>
  );
}

type PlatformSettings = {
  smtpHost: string;
  smtpPort: string | number;
  smtpUser: string;
  hasSmtpPassword: boolean;
  alertEmailTo: string;
  slackWebhookUrl: string;
};

function PlatformSettingsCard() {
  const [settings, setSettings] = useState<PlatformSettings | null>(null);
  const [smtpHost, setSmtpHost] = useState("");
  const [smtpPort, setSmtpPort] = useState("");
  const [smtpUser, setSmtpUser] = useState("");
  const [smtpPassword, setSmtpPassword] = useState("");
  const [alertEmailTo, setAlertEmailTo] = useState("");
  const [slackWebhookUrl, setSlackWebhookUrl] = useState("");
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [errors, setErrors] = useState<string[]>([]);

  useEffect(() => {
    fetch("/api/admin/settings")
      .then((res) => res.json())
      .then((data: PlatformSettings) => {
        setSettings(data);
        setSmtpHost(data.smtpHost);
        setSmtpPort(String(data.smtpPort ?? ""));
        setSmtpUser(data.smtpUser);
        setAlertEmailTo(data.alertEmailTo);
        setSlackWebhookUrl(data.slackWebhookUrl);
      });
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setMessage(null);
    setErrors([]);

    const res = await fetch("/api/admin/settings", {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ smtpHost, smtpPort, smtpUser, smtpPassword, alertEmailTo, slackWebhookUrl }),
    });
    setSaving(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    setSmtpPassword("");
    setMessage("Notification settings saved.");
    setSettings((prev) => (prev ? { ...prev, hasSmtpPassword: prev.hasSmtpPassword || smtpPassword.length > 0 } : prev));
  };

  if (!settings) return null;

  return (
    <Card className="p-6">
      <h3 className="mb-1 text-sm font-semibold text-slate-900">Platform Notification Settings</h3>
      <p className="mb-4 text-sm text-slate-500">
        Used to email/Slack you when a newer FVU version is published, and to email password
        resets to users. Configured here instead of a server file, so credentials never leave
        this app.
      </p>
      <form onSubmit={handleSave} className="space-y-4">
        {errors.length > 0 && (
          <Alert>
            <ul className="list-inside list-disc">
              {errors.map((err, i) => (
                <li key={i}>{err}</li>
              ))}
            </ul>
          </Alert>
        )}
        {message && <Alert tone="green">{message}</Alert>}

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div className="space-y-1">
            <FieldLabel>SMTP Host</FieldLabel>
            <input
              className={inputClass}
              placeholder="smtp.gmail.com"
              value={smtpHost}
              onChange={(e) => setSmtpHost(e.target.value)}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>SMTP Port</FieldLabel>
            <input
              className={inputClass}
              placeholder="587"
              value={smtpPort}
              onChange={(e) => setSmtpPort(e.target.value.replace(/\D/g, ""))}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>SMTP Username</FieldLabel>
            <input
              className={inputClass}
              placeholder="you@gmail.com"
              value={smtpUser}
              onChange={(e) => setSmtpUser(e.target.value)}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>
              SMTP Password / App Password{" "}
              {settings.hasSmtpPassword && <span className="text-xs text-emerald-600">(saved — leave blank to keep it)</span>}
            </FieldLabel>
            <input
              type="password"
              className={inputClass}
              placeholder={settings.hasSmtpPassword ? "••••••••••••••••" : ""}
              value={smtpPassword}
              onChange={(e) => setSmtpPassword(e.target.value)}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>Alert Recipient Email</FieldLabel>
            <input
              type="email"
              className={inputClass}
              placeholder="admin@gmail.com"
              value={alertEmailTo}
              onChange={(e) => setAlertEmailTo(e.target.value)}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>Slack Webhook URL (optional)</FieldLabel>
            <input
              className={inputClass}
              placeholder="https://hooks.slack.com/services/..."
              value={slackWebhookUrl}
              onChange={(e) => setSlackWebhookUrl(e.target.value)}
            />
          </div>
        </div>

        <Button type="submit" disabled={saving}>
          {saving ? "Saving..." : "Save Notification Settings"}
        </Button>
      </form>
    </Card>
  );
}

type RazorpaySettings = {
  razorpayKeyId: string;
  hasRazorpayKeySecret: boolean;
  hasRazorpayWebhookSecret: boolean;
};

function RazorpaySettingsCard() {
  const [settings, setSettings] = useState<RazorpaySettings | null>(null);
  const [keyId, setKeyId] = useState("");
  const [keySecret, setKeySecret] = useState("");
  const [webhookSecret, setWebhookSecret] = useState("");
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [errors, setErrors] = useState<string[]>([]);

  useEffect(() => {
    fetch("/api/admin/settings")
      .then((res) => res.json())
      .then((data: RazorpaySettings) => {
        setSettings(data);
        setKeyId(data.razorpayKeyId);
      });
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setMessage(null);
    setErrors([]);

    const res = await fetch("/api/admin/settings", {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ razorpayKeyId: keyId, razorpayKeySecret: keySecret, razorpayWebhookSecret: webhookSecret }),
    });
    setSaving(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    setMessage("Razorpay settings saved.");
    setSettings((prev) =>
      prev
        ? {
            ...prev,
            razorpayKeyId: keyId,
            hasRazorpayKeySecret: prev.hasRazorpayKeySecret || keySecret.length > 0,
            hasRazorpayWebhookSecret: prev.hasRazorpayWebhookSecret || webhookSecret.length > 0,
          }
        : prev,
    );
    setKeySecret("");
    setWebhookSecret("");
  };

  if (!settings) return null;

  const configured = Boolean(settings.razorpayKeyId && settings.hasRazorpayKeySecret);

  return (
    <Card className="p-6">
      <div className="mb-1 flex items-center gap-2">
        <h3 className="text-sm font-semibold text-slate-900">Razorpay (Subscription Payments)</h3>
        <span
          className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
            configured ? "bg-emerald-50 text-emerald-700" : "bg-slate-100 text-slate-500"
          }`}
        >
          {configured ? "Configured" : "Not configured"}
        </span>
      </div>
      <p className="mb-4 text-sm text-slate-500">
        Used to generate payment links firms can pay from their own dashboard to renew a product
        subscription. Until this is configured, firms see a message to contact you instead of a
        working &ldquo;Renew&rdquo; button. Find your keys under Settings &rarr; API Keys in the
        Razorpay dashboard, and set the webhook URL there to{" "}
        <code className="rounded bg-slate-100 px-1 py-0.5 text-xs">/api/webhooks/razorpay</code>{" "}
        (event: <code className="rounded bg-slate-100 px-1 py-0.5 text-xs">payment_link.paid</code>).
      </p>
      <form onSubmit={handleSave} className="space-y-4">
        {errors.length > 0 && (
          <Alert>
            <ul className="list-inside list-disc">
              {errors.map((err, i) => (
                <li key={i}>{err}</li>
              ))}
            </ul>
          </Alert>
        )}
        {message && <Alert tone="green">{message}</Alert>}

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div className="space-y-1">
            <FieldLabel>Key ID</FieldLabel>
            <input
              className={inputClass}
              placeholder="rzp_live_..."
              value={keyId}
              onChange={(e) => setKeyId(e.target.value)}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>
              Key Secret{" "}
              {settings.hasRazorpayKeySecret && <span className="text-xs text-emerald-600">(saved — leave blank to keep it)</span>}
            </FieldLabel>
            <input
              type="password"
              className={inputClass}
              placeholder={settings.hasRazorpayKeySecret ? "••••••••••••••••" : ""}
              value={keySecret}
              onChange={(e) => setKeySecret(e.target.value)}
            />
          </div>
          <div className="space-y-1 sm:col-span-2">
            <FieldLabel>
              Webhook Secret{" "}
              {settings.hasRazorpayWebhookSecret && <span className="text-xs text-emerald-600">(saved — leave blank to keep it)</span>}
            </FieldLabel>
            <input
              type="password"
              className={inputClass}
              placeholder={settings.hasRazorpayWebhookSecret ? "••••••••••••••••" : ""}
              value={webhookSecret}
              onChange={(e) => setWebhookSecret(e.target.value)}
            />
          </div>
        </div>

        <Button type="submit" disabled={saving}>
          {saving ? "Saving..." : "Save Razorpay Settings"}
        </Button>
      </form>
    </Card>
  );
}

type FirmSmtpSettings = {
  smtpHost: string;
  smtpPort: string | number;
  smtpUser: string;
  hasSmtpPassword: boolean;
};

function FirmSmtpSettingsCard() {
  const [settings, setSettings] = useState<FirmSmtpSettings | null>(null);
  const [smtpHost, setSmtpHost] = useState("");
  const [smtpPort, setSmtpPort] = useState("");
  const [smtpUser, setSmtpUser] = useState("");
  const [smtpPassword, setSmtpPassword] = useState("");
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [errors, setErrors] = useState<string[]>([]);

  useEffect(() => {
    fetch("/api/organization/smtp-settings")
      .then((res) => res.json())
      .then((data: FirmSmtpSettings) => {
        setSettings(data);
        setSmtpHost(data.smtpHost);
        setSmtpPort(String(data.smtpPort ?? ""));
        setSmtpUser(data.smtpUser);
      });
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setMessage(null);
    setErrors([]);

    const res = await fetch("/api/organization/smtp-settings", {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ smtpHost, smtpPort, smtpUser, smtpPassword }),
    });
    setSaving(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    setSmtpPassword("");
    setMessage("Your firm's notification settings were saved.");
    setSettings((prev) => (prev ? { ...prev, hasSmtpPassword: prev.hasSmtpPassword || smtpPassword.length > 0 } : prev));
  };

  if (!settings) return null;

  return (
    <Card className="p-6">
      <h3 className="mb-1 text-sm font-semibold text-slate-900">Firm Notification Settings</h3>
      <p className="mb-4 text-sm text-slate-500">
        Your own SMTP, used when you reset a password for someone on your team, and for any mail
        your firm sends to your own clients. If you leave this blank, the platform's own email
        settings are used instead.
      </p>
      <form onSubmit={handleSave} className="space-y-4">
        {errors.length > 0 && (
          <Alert>
            <ul className="list-inside list-disc">
              {errors.map((err, i) => (
                <li key={i}>{err}</li>
              ))}
            </ul>
          </Alert>
        )}
        {message && <Alert tone="green">{message}</Alert>}

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div className="space-y-1">
            <FieldLabel>SMTP Host</FieldLabel>
            <input
              className={inputClass}
              placeholder="smtp.gmail.com"
              value={smtpHost}
              onChange={(e) => setSmtpHost(e.target.value)}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>SMTP Port</FieldLabel>
            <input
              className={inputClass}
              placeholder="587"
              value={smtpPort}
              onChange={(e) => setSmtpPort(e.target.value.replace(/\D/g, ""))}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>SMTP Username</FieldLabel>
            <input
              className={inputClass}
              placeholder="you@gmail.com"
              value={smtpUser}
              onChange={(e) => setSmtpUser(e.target.value)}
            />
          </div>
          <div className="space-y-1">
            <FieldLabel>
              SMTP Password / App Password{" "}
              {settings.hasSmtpPassword && <span className="text-xs text-emerald-600">(saved — leave blank to keep it)</span>}
            </FieldLabel>
            <input
              type="password"
              className={inputClass}
              placeholder={settings.hasSmtpPassword ? "••••••••••••••••" : ""}
              value={smtpPassword}
              onChange={(e) => setSmtpPassword(e.target.value)}
            />
          </div>
        </div>

        <Button type="submit" disabled={saving}>
          {saving ? "Saving..." : "Save Firm Notification Settings"}
        </Button>
      </form>
    </Card>
  );
}
