"use client";

import { useState, type ReactNode } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { BrandFooter, ContactInfo, NexLogo } from "@/components/ui";
import { ClientWorkspaceNav } from "@/components/client-workspace-nav";
import { LanguageToggle } from "@/components/language-toggle";
import { InfoButton } from "@/components/info-button";
import { useLanguage } from "@/lib/i18n/language-context";
import type { NavGroup, ClientNavActiveKey } from "@/lib/nav";

function isActive(pathname: string, href: string) {
  if (href === "/") return pathname === "/";
  return pathname === href || pathname.startsWith(href + "/");
}

/** The vertical list of modules shown in the sidebar (desktop) or drawer (mobile) — same content, just two different containers. */
function NavList({ groups, onNavigate }: { groups: NavGroup[]; onNavigate: () => void }) {
  const pathname = usePathname();
  const { t } = useLanguage();

  return (
    <nav className="space-y-5">
      {groups.map((group) => (
        <div key={group.id}>
          <p className="px-2 text-[10px] font-semibold uppercase tracking-wide text-slate-400">
            {t(group.labelKey)}
          </p>
          <div className="mt-1 space-y-0.5">
            {group.items.map((item) => {
              const active = isActive(pathname, item.href);
              return (
                <div key={item.id} className="flex items-center gap-1">
                  <Link
                    href={item.href}
                    onClick={onNavigate}
                    className={`flex-1 rounded-md px-2 py-1.5 text-sm font-medium transition-colors ${
                      active
                        ? "bg-emerald-100 text-emerald-800"
                        : "text-slate-600 hover:bg-emerald-50 hover:text-emerald-800"
                    }`}
                  >
                    {t(item.labelKey)}
                  </Link>
                  <InfoButton text={t(item.infoKey)} label={`About ${t(item.labelKey)}`} />
                </div>
              );
            })}
          </div>
        </div>
      ))}
    </nav>
  );
}

const MenuIcon = () => (
  <svg viewBox="0 0 20 20" fill="none" className="h-4 w-4" aria-hidden="true">
    <path d="M3 5h14M3 10h14M3 15h14" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" />
  </svg>
);

export function ShellChrome({
  children,
  navGroups,
  firmName,
  clientNav,
  userEmail,
  previousLoginAt,
  isLoggedIn,
  signOutAction,
}: {
  children: ReactNode;
  navGroups: NavGroup[];
  firmName?: string;
  clientNav?: { clientId: string; clientName?: string; active: ClientNavActiveKey };
  userEmail: string | null;
  previousLoginAt: string | null;
  isLoggedIn: boolean;
  signOutAction: () => Promise<void>;
}) {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { t } = useLanguage();
  const hasNav = navGroups.length > 0;

  const sidebarInner = (
    <>
      <Link href="/" className="mb-4 flex items-center px-2 transition-opacity hover:opacity-90">
        <NexLogo compact showCompanyName={false} />
      </Link>
      {hasNav && <NavList groups={navGroups} onNavigate={() => setSidebarOpen(false)} />}
    </>
  );

  return (
    <div className="flex min-h-screen bg-slate-50">
      {hasNav && (
        <aside className="sticky top-0 hidden h-screen w-60 shrink-0 overflow-y-auto border-r border-emerald-100 bg-emerald-50/50 px-3 py-5 md:block">
          {sidebarInner}
        </aside>
      )}

      {sidebarOpen && (
        <div className="fixed inset-0 z-50 md:hidden">
          <div className="absolute inset-0 bg-slate-900/40" onClick={() => setSidebarOpen(false)} />
          <div className="relative h-full w-64 overflow-y-auto bg-emerald-50/50 px-3 py-5 shadow-xl">{sidebarInner}</div>
        </div>
      )}

      <div className="flex min-h-screen flex-1 flex-col">
        <header className="border-b border-slate-200 bg-white/95 shadow-sm shadow-slate-100 backdrop-blur">
          <div className="mx-auto flex max-w-6xl items-center justify-between gap-3 px-4 py-3">
            <div className="flex items-center gap-3">
              {hasNav && (
                <button
                  type="button"
                  onClick={() => setSidebarOpen(true)}
                  aria-label="Open menu"
                  className="rounded-md border border-slate-300 p-1.5 text-slate-600 md:hidden"
                >
                  <MenuIcon />
                </button>
              )}
              <Link href="/" className="transition-opacity hover:opacity-90 md:hidden">
                <NexLogo compact />
              </Link>
            </div>

            <div className="flex items-center gap-4">
              {firmName && (
                <span className="hidden text-sm font-medium text-slate-600 sm:inline">{firmName}</span>
              )}
              {isLoggedIn && (
                <span className="hidden text-right text-xs leading-snug text-slate-400 lg:inline">
                  {userEmail}
                  <br />
                  {t("shell.lastLogin")}:{" "}
                  {previousLoginAt
                    ? // Pin an explicit locale — the bare default depends on the
                      // runtime's configured locale, which can differ between
                      // server (Node/ICU) and browser, causing a hydration
                      // mismatch (seen as e.g. "11/14/2026" vs "14/11/2026").
                      new Date(previousLoginAt).toLocaleString("en-IN")
                    : t("shell.firstLogin")}
                </span>
              )}
              <LanguageToggle />
              {isLoggedIn && (
                <Link
                  href="/profile"
                  className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition-colors hover:border-indigo-200 hover:bg-indigo-50 hover:text-indigo-700"
                >
                  {t("nav.profile")}
                </Link>
              )}
              <form action={signOutAction}>
                <button className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition-colors hover:border-red-200 hover:bg-red-50 hover:text-red-700">
                  {t("shell.signOut")}
                </button>
              </form>
            </div>
          </div>
        </header>

        {clientNav && (
          <ClientWorkspaceNav clientId={clientNav.clientId} clientName={clientNav.clientName} active={clientNav.active} />
        )}

        <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-10">{children}</main>

        <footer className="border-t border-slate-200 bg-white">
          <div className="mx-auto max-w-6xl px-4 py-6 text-center">
            <ContactInfo />
            <div className="mx-auto mt-4 h-px w-16 bg-slate-200" />
            <BrandFooter className="mt-4" />
          </div>
        </footer>
      </div>
    </div>
  );
}
