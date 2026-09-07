"use client";

import { useEffect, useState, type ReactNode } from "react";
import { createPortal } from "react-dom";

/**
 * Rendered via a portal to `document.body` rather than inline, so its own
 * <form> (e.g. the "+ Add new DDO" popup opened from inside a DDO transaction
 * form) never ends up nested inside a caller's <form> in the real DOM.
 * Nested <form> elements are invalid HTML — the browser's parser silently
 * drops the inner <form> tag on first (server-rendered) page load, which
 * both breaks the modal's submit handling and causes a hydration mismatch.
 */
export function Modal({
  title,
  onClose,
  children,
}: {
  title: ReactNode;
  onClose: () => void;
  children: ReactNode;
}) {
  // The portal target (document.body) only exists in the browser, so wait
  // until after mount to render it — keeps server and first-client-render
  // output identical (nothing), avoiding a hydration mismatch of its own.
  const [mounted, setMounted] = useState(false);
  useEffect(() => setMounted(true), []);
  if (!mounted) return null;

  return createPortal(
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4">
      <div className="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-xl bg-white p-6 shadow-xl">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-sm font-semibold text-slate-900">{title}</h3>
          <button
            type="button"
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600"
            aria-label="Close"
          >
            &times;
          </button>
        </div>
        {children}
      </div>
    </div>,
    document.body,
  );
}
