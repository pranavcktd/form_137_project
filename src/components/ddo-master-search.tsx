"use client";

import { useEffect, useRef, useState } from "react";
import states from "@/schemas/24g-f137/v1_9/annexures/state.json";
import { Alert, Button, FieldLabel, Modal, inputClass } from "@/components/ui";

export type DdoMasterSearchResult = {
  id: string;
  tan: string;
  name: string;
  address1: string | null;
  address2: string | null;
  address3: string | null;
  address4: string | null;
  city: string | null;
  state: string | null;
  pin: string | null;
  ddoRegNo: string | null;
  ddoCode: string | null;
  email: string | null;
};

function newDdoDefaults() {
  return { tan: "", name: "", address1: "", address2: "", city: "", state: "", pin: "", ddoRegNo: "", ddoCode: "", email: "" };
}

/** Debounced typeahead over a client's DDO Master, for picking a DDO before entering a transaction. */
export function DdoMasterSearch({
  clientId,
  onSelect,
}: {
  clientId: string;
  onSelect: (ddo: DdoMasterSearchResult) => void;
}) {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<DdoMasterSearchResult[]>([]);
  const [open, setOpen] = useState(false);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const [showAddModal, setShowAddModal] = useState(false);
  const [newDdo, setNewDdo] = useState(newDdoDefaults());
  const [addErrors, setAddErrors] = useState<string[]>([]);
  const [adding, setAdding] = useState(false);

  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      if (query.trim().length === 0) {
        setResults([]);
        return;
      }
      fetch(`/api/clients/${clientId}/ddo-master/search?q=${encodeURIComponent(query)}`)
        .then((res) => res.json())
        .then((data) => {
          setResults(data);
          setOpen(true);
        });
    }, 250);
    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current);
    };
  }, [query, clientId]);

  const openAddModal = () => {
    setNewDdo({ ...newDdoDefaults(), tan: query.trim().toUpperCase() });
    setAddErrors([]);
    setShowAddModal(true);
    setOpen(false);
  };

  const handleAddDdo = async (e: React.FormEvent) => {
    e.preventDefault();
    setAdding(true);
    setAddErrors([]);

    const res = await fetch(`/api/clients/${clientId}/ddo-master`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newDdo),
    });
    setAdding(false);

    if (!res.ok) {
      const body = await res.json();
      const fieldErrors = body.error?.fieldErrors ? Object.values(body.error.fieldErrors).flat() : [];
      setAddErrors([...(body.error?.formErrors ?? []), ...fieldErrors] as string[]);
      return;
    }

    const created: DdoMasterSearchResult = await res.json();
    setShowAddModal(false);
    setQuery("");
    setResults([]);
    onSelect(created);
  };

  return (
    <div className="relative">
      <div className="flex gap-2">
        <input
          className={inputClass}
          placeholder="Search DDO by TAN or name..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => results.length > 0 && setOpen(true)}
          onBlur={() => setTimeout(() => setOpen(false), 150)}
        />
        <Button type="button" variant="secondary" onClick={openAddModal}>
          + Add new DDO
        </Button>
      </div>
      {open && results.length > 0 && (
        <ul className="absolute z-10 mt-1 max-h-56 w-full overflow-y-auto rounded-lg border border-slate-200 bg-white shadow-lg">
          {results.map((ddo) => (
            <li key={ddo.id}>
              <button
                type="button"
                className="block w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                onMouseDown={(e) => {
                  e.preventDefault();
                  onSelect(ddo);
                  setQuery("");
                  setResults([]);
                  setOpen(false);
                }}
              >
                <span className="font-medium text-slate-900">{ddo.tan}</span>
                <span className="text-slate-500"> &mdash; {ddo.name}</span>
              </button>
            </li>
          ))}
        </ul>
      )}
      {open && results.length === 0 && query.trim().length > 0 && (
        <div className="absolute z-10 mt-1 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-500 shadow-lg">
          No matching DDO. Click &ldquo;+ Add new DDO&rdquo; to create it.
        </div>
      )}

      {showAddModal && (
        <Modal title="Add new DDO" onClose={() => setShowAddModal(false)}>
          <form onSubmit={handleAddDdo} className="space-y-4">
            {addErrors.length > 0 && (
              <Alert>
                <ul className="list-inside list-disc">
                  {addErrors.map((err, i) => (
                    <li key={i}>{err}</li>
                  ))}
                </ul>
              </Alert>
            )}
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
              <div className="space-y-1">
                <FieldLabel>TAN</FieldLabel>
                <input
                  className={inputClass}
                  required
                  maxLength={10}
                  value={newDdo.tan}
                  onChange={(e) => setNewDdo((p) => ({ ...p, tan: e.target.value.toUpperCase() }))}
                  placeholder="MUMD12345A"
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Name of the DDO</FieldLabel>
                <input
                  className={inputClass}
                  required
                  value={newDdo.name}
                  onChange={(e) => setNewDdo((p) => ({ ...p, name: e.target.value }))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Address Line 1</FieldLabel>
                <input
                  className={inputClass}
                  value={newDdo.address1}
                  onChange={(e) => setNewDdo((p) => ({ ...p, address1: e.target.value }))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Address Line 2</FieldLabel>
                <input
                  className={inputClass}
                  value={newDdo.address2}
                  onChange={(e) => setNewDdo((p) => ({ ...p, address2: e.target.value }))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>City</FieldLabel>
                <input
                  className={inputClass}
                  value={newDdo.city}
                  onChange={(e) => setNewDdo((p) => ({ ...p, city: e.target.value }))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>State</FieldLabel>
                <select
                  className={inputClass}
                  value={newDdo.state}
                  onChange={(e) => setNewDdo((p) => ({ ...p, state: e.target.value }))}
                >
                  <option value="">Select state</option>
                  {states.map((s) => (
                    <option key={s.code} value={s.code}>
                      {s.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="space-y-1">
                <FieldLabel>PIN Code</FieldLabel>
                <input
                  className={inputClass}
                  maxLength={6}
                  value={newDdo.pin}
                  onChange={(e) => setNewDdo((p) => ({ ...p, pin: e.target.value.replace(/\D/g, "") }))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>Email</FieldLabel>
                <input
                  type="email"
                  className={inputClass}
                  value={newDdo.email}
                  onChange={(e) => setNewDdo((p) => ({ ...p, email: e.target.value }))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>DDO Registration No. (optional)</FieldLabel>
                <input
                  className={inputClass}
                  value={newDdo.ddoRegNo}
                  onChange={(e) => setNewDdo((p) => ({ ...p, ddoRegNo: e.target.value }))}
                />
              </div>
              <div className="space-y-1">
                <FieldLabel>DDO Code (optional)</FieldLabel>
                <input
                  className={inputClass}
                  value={newDdo.ddoCode}
                  onChange={(e) => setNewDdo((p) => ({ ...p, ddoCode: e.target.value }))}
                />
              </div>
            </div>
            <div className="flex gap-2">
              <Button type="submit" disabled={adding}>
                {adding ? "Adding..." : "Add DDO & Select"}
              </Button>
              <Button type="button" variant="secondary" onClick={() => setShowAddModal(false)}>
                Cancel
              </Button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
