"use client";

import { useLanguage } from "@/lib/i18n/language-context";

/** EN / HI pill switch for the nav and shell chrome (page content isn't translated yet). */
export function LanguageToggle() {
  const { language, setLanguage, t } = useLanguage();

  return (
    <div
      role="group"
      aria-label={t("shell.language")}
      className="flex items-center rounded-full border border-slate-300 bg-white p-0.5 text-xs font-medium"
    >
      {(["en", "hi"] as const).map((lang) => (
        <button
          key={lang}
          type="button"
          onClick={() => setLanguage(lang)}
          aria-pressed={language === lang}
          className={`rounded-full px-2 py-1 transition-colors ${
            language === lang ? "bg-indigo-600 text-white" : "text-slate-500 hover:text-slate-700"
          }`}
        >
          {lang === "en" ? "EN" : "हि"}
        </button>
      ))}
    </div>
  );
}
