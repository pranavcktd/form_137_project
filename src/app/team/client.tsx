"use client";

import { useEffect, useState } from "react";
import { Alert, Badge, Button, Card, FieldLabel, LoadingState, Pagination, inputClass } from "@/components/ui";
import { usePagination } from "@/lib/usePagination";

type TeamUser = {
  id: string;
  name: string;
  email: string;
  role: "ADMIN" | "PREPARER";
  disabled: boolean;
  lastLoginAt: string | null;
};

export function TeamClient() {
  const [users, setUsers] = useState<TeamUser[] | null>(null);

  const [editingUserId, setEditingUserId] = useState<string | null>(null);
  const [userForm, setUserForm] = useState({ name: "", email: "", role: "PREPARER" as "ADMIN" | "PREPARER", disabled: false });
  const [userErrors, setUserErrors] = useState<string[]>([]);
  const [busyUserId, setBusyUserId] = useState<string | null>(null);
  const [resetMessage, setResetMessage] = useState<string | null>(null);

  const [showAddUser, setShowAddUser] = useState(false);
  const [newUser, setNewUser] = useState({ name: "", email: "", password: "", role: "PREPARER" as "ADMIN" | "PREPARER" });
  const [addUserErrors, setAddUserErrors] = useState<string[]>([]);
  const [addingUser, setAddingUser] = useState(false);

  const load = () => {
    fetch("/api/team")
      .then((res) => res.json())
      .then(setUsers);
  };

  useEffect(load, []);

  const startEditUser = (user: TeamUser) => {
    setEditingUserId(user.id);
    setUserForm({ name: user.name, email: user.email, role: user.role, disabled: user.disabled });
    setUserErrors([]);
    setResetMessage(null);
  };

  const handleSaveUser = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingUserId) return;
    setBusyUserId(editingUserId);
    setUserErrors([]);

    const res = await fetch(`/api/team/${editingUserId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(userForm),
    });
    setBusyUserId(null);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setUserErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    setEditingUserId(null);
    load();
  };

  const toggleUserDisabled = async (user: TeamUser) => {
    setBusyUserId(user.id);
    const res = await fetch(`/api/team/${user.id}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name: user.name, email: user.email, role: user.role, disabled: !user.disabled }),
    });
    setBusyUserId(null);
    if (!res.ok) {
      const body = await res.json();
      setResetMessage(body.error?.formErrors?.[0] ?? "Could not update that user.");
      return;
    }
    load();
  };

  const handleDeleteUser = async (user: TeamUser) => {
    if (!window.confirm(`Delete the login for ${user.name} (${user.email})? This can't be undone.`)) return;
    setBusyUserId(user.id);
    const res = await fetch(`/api/team/${user.id}`, { method: "DELETE" });
    setBusyUserId(null);
    if (!res.ok) {
      const body = await res.json();
      setResetMessage(body.error?.formErrors?.[0] ?? "Could not delete that user.");
      return;
    }
    load();
  };

  const handleResetPassword = async (user: TeamUser) => {
    if (!window.confirm(`Reset ${user.name}'s password to the default? They'll be required to change it at next login.`)) {
      return;
    }
    setBusyUserId(user.id);
    setResetMessage(null);
    const res = await fetch(`/api/team/${user.id}/reset-password`, { method: "POST" });
    const body = await res.json();
    setBusyUserId(null);
    setResetMessage(
      body.emailed
        ? `Password reset — an email was sent to ${user.email}.`
        : `Password reset — no email is configured, so relay this to them manually: ${user.email} / ${body.temporaryPassword}`,
    );
  };

  const handleAddUser = async (e: React.FormEvent) => {
    e.preventDefault();
    setAddingUser(true);
    setAddUserErrors([]);

    const res = await fetch("/api/team", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newUser),
    });
    setAddingUser(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setAddUserErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    setShowAddUser(false);
    setNewUser({ name: "", email: "", password: "", role: "PREPARER" });
    load();
  };

  const usersPage = usePagination(users ?? []);

  if (!users) return <LoadingState />;

  return (
    <Card className="p-6">
      <div className="mb-4 flex items-center justify-between">
        <h3 className="text-sm font-semibold text-slate-900">{users.length} user(s)</h3>
        <Button variant="secondary" onClick={() => setShowAddUser((v) => !v)}>
          {showAddUser ? "Cancel" : "+ Add User"}
        </Button>
      </div>

      {resetMessage && (
        <div className="mb-4">
          <Alert tone="green">{resetMessage}</Alert>
        </div>
      )}

      {showAddUser && (
        <form onSubmit={handleAddUser} className="mb-6 space-y-4 rounded-lg border border-slate-200 p-4">
          {addUserErrors.length > 0 && (
            <Alert>
              <ul className="list-inside list-disc">
                {addUserErrors.map((err, i) => (
                  <li key={i}>{err}</li>
                ))}
              </ul>
            </Alert>
          )}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div className="space-y-1">
              <FieldLabel>Name</FieldLabel>
              <input
                className={inputClass}
                required
                value={newUser.name}
                onChange={(e) => setNewUser((p) => ({ ...p, name: e.target.value }))}
              />
            </div>
            <div className="space-y-1">
              <FieldLabel>Email</FieldLabel>
              <input
                type="email"
                className={inputClass}
                required
                value={newUser.email}
                onChange={(e) => setNewUser((p) => ({ ...p, email: e.target.value }))}
              />
            </div>
            <div className="space-y-1">
              <FieldLabel>Temporary Password</FieldLabel>
              <input
                type="password"
                className={inputClass}
                required
                minLength={8}
                value={newUser.password}
                onChange={(e) => setNewUser((p) => ({ ...p, password: e.target.value }))}
              />
            </div>
            <div className="space-y-1">
              <FieldLabel>Role</FieldLabel>
              <select
                className={inputClass}
                value={newUser.role}
                onChange={(e) => setNewUser((p) => ({ ...p, role: e.target.value as "ADMIN" | "PREPARER" }))}
              >
                <option value="ADMIN">Admin</option>
                <option value="PREPARER">Preparer</option>
              </select>
            </div>
          </div>
          <Button type="submit" disabled={addingUser}>
            {addingUser ? "Adding..." : "Add User"}
          </Button>
        </form>
      )}

      <div className="divide-y divide-slate-100">
        {usersPage.pageItems.map((user) => (
          <div key={user.id} className="py-3">
            {editingUserId === user.id ? (
              <form onSubmit={handleSaveUser} className="space-y-3 rounded-lg border border-slate-200 p-3">
                {userErrors.length > 0 && (
                  <Alert>
                    <ul className="list-inside list-disc">
                      {userErrors.map((err, i) => (
                        <li key={i}>{err}</li>
                      ))}
                    </ul>
                  </Alert>
                )}
                <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
                  <input
                    className={inputClass}
                    required
                    placeholder="Name"
                    value={userForm.name}
                    onChange={(e) => setUserForm((p) => ({ ...p, name: e.target.value }))}
                  />
                  <input
                    type="email"
                    className={inputClass}
                    required
                    placeholder="Email"
                    value={userForm.email}
                    onChange={(e) => setUserForm((p) => ({ ...p, email: e.target.value }))}
                  />
                  <select
                    className={inputClass}
                    value={userForm.role}
                    onChange={(e) => setUserForm((p) => ({ ...p, role: e.target.value as "ADMIN" | "PREPARER" }))}
                  >
                    <option value="ADMIN">Admin</option>
                    <option value="PREPARER">Preparer</option>
                  </select>
                </div>
                <div className="flex gap-2">
                  <Button type="submit" disabled={busyUserId === user.id}>
                    Save
                  </Button>
                  <Button type="button" variant="secondary" onClick={() => setEditingUserId(null)}>
                    Cancel
                  </Button>
                </div>
              </form>
            ) : (
              <div className="flex flex-wrap items-center justify-between gap-2">
                <div>
                  <p className="font-medium text-slate-900">
                    {user.name} <Badge tone="slate">{user.role}</Badge>{" "}
                    {user.disabled && <Badge tone="red">Disabled</Badge>}
                  </p>
                  <p className="text-sm text-slate-500">{user.email}</p>
                  <p className="text-xs text-slate-400">
                    Last login: {user.lastLoginAt ? new Date(user.lastLoginAt).toLocaleString() : "never"}
                  </p>
                </div>
                <div className="flex flex-wrap gap-2">
                  <Button variant="secondary" onClick={() => startEditUser(user)}>
                    Edit
                  </Button>
                  <Button
                    variant="secondary"
                    disabled={busyUserId === user.id}
                    onClick={() => toggleUserDisabled(user)}
                  >
                    {user.disabled ? "Enable" : "Disable"}
                  </Button>
                  <Button
                    variant="secondary"
                    disabled={busyUserId === user.id}
                    onClick={() => handleResetPassword(user)}
                  >
                    Reset Password
                  </Button>
                  <Button variant="danger" disabled={busyUserId === user.id} onClick={() => handleDeleteUser(user)}>
                    Delete
                  </Button>
                </div>
              </div>
            )}
          </div>
        ))}
      </div>
      <Pagination
        page={usersPage.page}
        totalPages={usersPage.totalPages}
        onPageChange={usersPage.setPage}
        totalItems={usersPage.totalItems}
        pageSize={usersPage.pageSize}
      />
    </Card>
  );
}
