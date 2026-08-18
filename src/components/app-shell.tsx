import Link from "next/link";
import type { ReactNode } from "react";
import { auth, signOut } from "@/lib/auth";
import { BrandFooter, ContactInfo, NexLogo } from "@/components/ui";

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
    <div className="flex min-h-screen flex-col bg-slate-50">
      <header className="border-b border-slate-200 bg-white/95 shadow-sm shadow-slate-100 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
          <Link href="/" className="transition-opacity hover:opacity-90">
            <NexLogo compact />
          </Link>

          <div className="flex items-center gap-4">
            {firmName && (
              <span className="hidden text-sm font-medium text-slate-600 sm:inline">
                {firmName}
              </span>
            )}
            {session?.user && (
              <span className="hidden text-right text-xs leading-snug text-slate-400 lg:inline">
                {session.user.email}
                <br />
                Last login:{" "}
                {session.user.previousLoginAt
                  ? new Date(session.user.previousLoginAt).toLocaleString()
                  : "first login"}
              </span>
            )}
            {session?.user && (
              <Link
                href="/profile"
                className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition-colors hover:border-indigo-200 hover:bg-indigo-50 hover:text-indigo-700"
              >
                Profile
              </Link>
            )}
            <form
              action={async () => {
                "use server";
                await signOut({ redirectTo: "/login" });
              }}
            >
              <button className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition-colors hover:border-red-200 hover:bg-red-50 hover:text-red-700">
                Sign out
              </button>
            </form>
          </div>
        </div>
      </header>

      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-10">{children}</main>

      <footer className="border-t border-slate-200 bg-white">
        <div className="mx-auto max-w-6xl px-4 py-6 text-center">
          <ContactInfo />
          <div className="mx-auto mt-4 h-px w-16 bg-slate-200" />
          <BrandFooter className="mt-4" />
        </div>
      </footer>
    </div>
  );
}
