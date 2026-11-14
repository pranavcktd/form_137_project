"use client";

import Link from "next/link";
import { CLIENT_NAV_ITEMS, type ClientNavActiveKey } from "@/lib/nav";
import { useLanguage } from "@/lib/i18n/language-context";
import { InfoButton } from "@/components/info-button";

/** Tab bar shown while working inside one client's workspace, so Profile/Filing Periods/DDO Master/Reports are always one click away instead of buried in ad-hoc breadcrumb links. */
export function ClientWorkspaceNav({
  clientId,
  clientName,
  active,
}: {
  clientId: string;
  clientName?: string;
  active: ClientNavActiveKey;
}) {
  const { t } = useLanguage();

  return (
    <div className="border-b border-slate-200 bg-slate-50">
      <div className="mx-auto flex max-w-6xl flex-wrap items-center gap-x-1 gap-y-1 px-4 py-2">
        {clientName && (
          <span className="mr-3 truncate text-sm font-medium text-slate-500">{clientName}</span>
        )}
        {CLIENT_NAV_ITEMS.map((item) => {
          const isActive = item.id === active;
          return (
            <div key={item.id} className="flex items-center gap-0.5">
              <Link
                href={`/clients/${clientId}${item.hrefSuffix}`}
                className={`rounded-md px-2.5 py-1 text-sm font-medium transition-colors ${
                  isActive ? "bg-white text-indigo-700 shadow-sm" : "text-slate-600 hover:bg-white/60 hover:text-slate-900"
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
  );
}
