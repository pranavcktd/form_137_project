import Link from "next/link";
import type { ReactNode } from "react";
import { auth, signOut } from "@/lib/auth";
import { BrandFooter, NexLogo } from "@/components/ui";

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
          <Link href="/">
            <NexLogo compact />
          </Link>

          <div className="flex items-center gap-4">
            {firmName && (
              <span className="hidden text-sm text-slate-500 sm:inline">
                {firmName}
              </span>
            )}
            {session?.user && (
              <span className="hidden text-right text-xs text-slate-400 lg:inline">
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
                className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-50"
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
              <button className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-50">
                Sign out
              </button>
            </form>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-4 py-10">{children}</main>

      <footer className="border-t border-slate-200 bg-white py-4">
        <BrandFooter />
      </footer>
    </div>
  );
}
