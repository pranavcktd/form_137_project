import type { ReactNode } from "react";
import { auth, signOut } from "@/lib/auth";
import { ShellChrome } from "@/components/shell-chrome";
import { navForRole, type ClientNavActiveKey } from "@/lib/nav";

export async function AppShell({
  children,
  firmName,
  clientNav,
}: {
  children: ReactNode;
  /** Override the firm name shown in the header (e.g. a client's own name on client-scoped pages). */
  firmName?: string;
  /** Pass this on any page scoped to one client to show the Profile/Filing Periods/DDO Master/Reports tab bar. */
  clientNav?: { clientId: string; clientName?: string; active: ClientNavActiveKey };
}) {
  const session = await auth();
  const navGroups = session?.user ? navForRole(session.user.role) : [];

  async function signOutAction() {
    "use server";
    await signOut({ redirectTo: "/login" });
  }

  return (
    <ShellChrome
      navGroups={navGroups}
      firmName={firmName}
      clientNav={clientNav}
      userEmail={session?.user?.email ?? null}
      previousLoginAt={session?.user?.previousLoginAt ?? null}
      isLoggedIn={!!session?.user}
      signOutAction={signOutAction}
    >
      {children}
    </ShellChrome>
  );
}
