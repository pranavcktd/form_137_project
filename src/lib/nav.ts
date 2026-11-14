import type { TranslationKey } from "@/lib/i18n/translations";

export type AppRole = "SUPER_ADMIN" | "ADMIN" | "PREPARER";

export interface NavItem {
  id: string;
  labelKey: TranslationKey;
  infoKey: TranslationKey;
  href: string;
  roles: AppRole[];
}

export interface NavGroup {
  id: string;
  labelKey: TranslationKey;
  items: NavItem[];
}

/**
 * Single source of truth for the app's global navigation. Previously there
 * was no persistent nav at all — every page hand-rolled its own breadcrumb
 * links and role checks, which is what made the app feel confusing to
 * navigate. Add new destinations here, not as ad-hoc links inside pages.
 */
export const NAV_GROUPS: NavGroup[] = [
  {
    id: "overview",
    labelKey: "nav.group.overview",
    items: [
      { id: "home", labelKey: "nav.home", infoKey: "nav.home.info", href: "/", roles: ["ADMIN", "PREPARER"] },
      { id: "firms", labelKey: "nav.firms", infoKey: "nav.firms.info", href: "/admin", roles: ["SUPER_ADMIN"] },
      {
        id: "compliance-dashboard",
        labelKey: "nav.complianceDashboard",
        infoKey: "nav.complianceDashboard.info",
        href: "/dashboard",
        roles: ["ADMIN", "PREPARER"],
      },
    ],
  },
  {
    id: "clients",
    labelKey: "nav.group.clients",
    items: [
      { id: "clients", labelKey: "nav.clients", infoKey: "nav.clients.info", href: "/clients", roles: ["ADMIN", "PREPARER"] },
    ],
  },
  {
    id: "firm",
    labelKey: "nav.group.firm",
    items: [
      { id: "team", labelKey: "nav.team", infoKey: "nav.team.info", href: "/team", roles: ["ADMIN"] },
      { id: "backup", labelKey: "nav.backup", infoKey: "nav.backup.info", href: "/backup", roles: ["ADMIN"] },
    ],
  },
  {
    id: "platform",
    labelKey: "nav.group.platform",
    items: [
      {
        id: "fvu-updates",
        labelKey: "nav.fvuUpdates",
        infoKey: "nav.fvuUpdates.info",
        href: "/admin/fvu-updates",
        roles: ["SUPER_ADMIN"],
      },
      {
        id: "onboard-firm",
        labelKey: "nav.onboardFirm",
        infoKey: "nav.onboardFirm.info",
        href: "/admin/tax-professionals/new",
        roles: ["SUPER_ADMIN"],
      },
    ],
  },
  {
    id: "account",
    labelKey: "nav.group.account",
    items: [
      {
        id: "profile",
        labelKey: "nav.profile",
        infoKey: "nav.profile.info",
        href: "/profile",
        roles: ["SUPER_ADMIN", "ADMIN", "PREPARER"],
      },
    ],
  },
];

/** Filters the nav model down to what a given role should actually see, dropping any now-empty groups. */
export function navForRole(role: string): NavGroup[] {
  return NAV_GROUPS.map((group) => ({
    ...group,
    items: group.items.filter((item) => item.roles.includes(role as AppRole)),
  })).filter((group) => group.items.length > 0);
}

export type ClientNavActiveKey = "profile" | "filing-periods" | "ddo-master" | "reports";

export interface ClientNavItem {
  id: ClientNavActiveKey;
  labelKey: TranslationKey;
  infoKey: TranslationKey;
  hrefSuffix: string;
}

/** The sub-nav shown while working inside one client's workspace (profile/filing/DDO master/reports). */
export const CLIENT_NAV_ITEMS: ClientNavItem[] = [
  { id: "profile", labelKey: "clientNav.profile", infoKey: "clientNav.profile.info", hrefSuffix: "" },
  {
    id: "filing-periods",
    labelKey: "clientNav.filingPeriods",
    infoKey: "clientNav.filingPeriods.info",
    hrefSuffix: "/filing-periods",
  },
  { id: "ddo-master", labelKey: "clientNav.ddoMaster", infoKey: "clientNav.ddoMaster.info", hrefSuffix: "/ddo-master" },
  { id: "reports", labelKey: "clientNav.reports", infoKey: "clientNav.reports.info", hrefSuffix: "/reports" },
];
