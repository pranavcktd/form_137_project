import Link from "next/link";
import type { ReactNode } from "react";
import { auth, signOut } from "@/lib/auth";

export async function AppShell({
  children,
  firmName,
}: {
  children: ReactNode;
  /** Override the firm name shown in the header (e.g. a client's own name on client-scoped pages). */
  firmName?: string;
}) {
  const session = await auth();

  return (
    <div className="min-h-screen bg-slate-50">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
          <Link href="/" className="flex items-center gap-2">
            <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-indigo-600 text-sm font-bold text-white">
              F1
            </span>
            <span className="font-semibold text-slate-900">Form137 Suite</span>
          </Link>

          <div className="flex items-center gap-4">
            {firmName && (
              <span className="hidden text-sm text-slate-500 sm:inline">
                {firmName}
              </span>
            )}
            {session?.user?.email && (
              <span className="hidden text-sm text-slate-500 md:inline">
                {session.user.email}
              </span>
            )}
            <form
              action={async () => {
                "use server";
                await signOut({ redirectTo: "/login" });
              }}
            >
              <button className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-50">
                Sign out
              </button>
            </form>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-4 py-10">{children}</main>
    </div>
  );
}
