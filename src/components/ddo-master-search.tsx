"use client";

import { useEffect, useRef, useState } from "react";
import { inputClass } from "@/components/ui";

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

  return (
    <div className="relative">
      <input
        className={inputClass}
        placeholder="Search DDO by TAN or name..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        onFocus={() => results.length > 0 && setOpen(true)}
        onBlur={() => setTimeout(() => setOpen(false), 150)}
      />
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
          No matching DDO. Add it in the DDO Master first.
        </div>
      )}
    </div>
  );
}
