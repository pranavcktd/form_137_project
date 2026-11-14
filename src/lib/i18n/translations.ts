export type Language = "en" | "hi";

/**
 * Nav/shell translation strings only — this is NOT a full app translation.
 * Individual page content (forms, tables, reports) stays English for now;
 * see the language toggle's rollout plan before adding more scope here.
 */
export const translations = {
  "nav.group.overview": { en: "Overview", hi: "ओवरव्यू" },
  "nav.group.clients": { en: "Clients", hi: "क्लाइंट" },
  "nav.group.firm": { en: "Firm", hi: "फर्म" },
  "nav.group.platform": { en: "Platform Admin", hi: "प्लेटफ़ॉर्म एडमिन" },
  "nav.group.account": { en: "Account", hi: "खाता" },

  "nav.home": { en: "Home", hi: "होम" },
  "nav.home.info": {
    en: "Your dashboard — subscribed apps, renewal status, and quick links to your work.",
    hi: "आपका डैशबोर्ड — सब्सक्राइब किए गए ऐप्स, रिन्यूअल स्थिति, और आपके काम के शॉर्टकट।",
  },
  "nav.firms": { en: "Firms", hi: "फर्म्स" },
  "nav.firms.info": {
    en: "All tax-professional firms onboarded on the platform. Open one to view or edit it.",
    hi: "प्लेटफ़ॉर्म पर जोड़ी गई सभी टैक्स-प्रोफेशनल फर्में। देखने या बदलने के लिए किसी एक को खोलें।",
  },
  "nav.complianceDashboard": { en: "Compliance Dashboard", hi: "कम्प्लायंस डैशबोर्ड" },
  "nav.complianceDashboard.info": {
    en: "Filing status across every one of your clients, at a glance.",
    hi: "आपके सभी क्लाइंट्स की फाइलिंग स्थिति एक ही जगह पर।",
  },
  "nav.clients": { en: "Clients", hi: "क्लाइंट" },
  "nav.clients.info": {
    en: "Manage your client government offices — profile, DDO Master, filing periods, and reports for each one.",
    hi: "अपने क्लाइंट सरकारी कार्यालयों को प्रबंधित करें — प्रत्येक की प्रोफ़ाइल, DDO मास्टर, फाइलिंग पीरियड और रिपोर्ट्स।",
  },
  "nav.team": { en: "Team", hi: "टीम" },
  "nav.team.info": {
    en: "Your firm's own staff accounts — add new users, disable them, or reset their passwords.",
    hi: "आपकी फर्म के स्टाफ खाते — नए यूज़र जोड़ें, निष्क्रिय करें, या पासवर्ड रीसेट करें।",
  },
  "nav.backup": { en: "Backup & Restore", hi: "बैकअप और रीस्टोर" },
  "nav.backup.info": {
    en: "Download a full backup of your firm's data (as a .zip), or restore from a previous backup.",
    hi: "अपनी फर्म के डेटा का पूरा बैकअप (.zip के रूप में) डाउनलोड करें, या पुराने बैकअप से रीस्टोर करें।",
  },
  "nav.fvuUpdates": { en: "FVU Updates", hi: "FVU अपडेट्स" },
  "nav.fvuUpdates.info": {
    en: "Upload a new FVU/validation package from Protean and see what changed vs. the current one.",
    hi: "Protean से नया FVU/वैलिडेशन पैकेज अपलोड करें और देखें कि मौजूदा पैकेज से क्या बदला है।",
  },
  "nav.onboardFirm": { en: "Onboard a Firm", hi: "नई फर्म जोड़ें" },
  "nav.onboardFirm.info": {
    en: "Set up a new tax-professional firm on the platform, with its own admin login.",
    hi: "प्लेटफ़ॉर्म पर एक नई टैक्स-प्रोफेशनल फर्म जोड़ें, उसके अपने एडमिन लॉगिन के साथ।",
  },
  "nav.profile": { en: "Profile", hi: "प्रोफ़ाइल" },
  "nav.profile.info": {
    en: "Your own account details, password, and (depending on your role) firm or platform settings.",
    hi: "आपके खाते का विवरण, पासवर्ड, और (आपकी भूमिका के अनुसार) फर्म या प्लेटफ़ॉर्म सेटिंग्स।",
  },

  "clientNav.profile": { en: "Profile", hi: "प्रोफ़ाइल" },
  "clientNav.profile.info": {
    en: "This client's AIN, ministry/department, and responsible person details — used on every filing.",
    hi: "इस क्लाइंट का AIN, मंत्रालय/विभाग, और जिम्मेदार व्यक्ति का विवरण — हर फाइलिंग में उपयोग होता है।",
  },
  "clientNav.filingPeriods": { en: "Filing Periods", hi: "फाइलिंग पीरियड" },
  "clientNav.filingPeriods.info": {
    en: "Create and manage this client's monthly/period returns — add DDO transactions and generate FVU files.",
    hi: "इस क्लाइंट के मासिक/अवधि रिटर्न बनाएं और प्रबंधित करें — DDO ट्रांजैक्शन जोड़ें और FVU फाइलें बनाएं।",
  },
  "clientNav.ddoMaster": { en: "DDO Master", hi: "DDO मास्टर" },
  "clientNav.ddoMaster.info": {
    en: "The reusable list of this client's DDOs (Drawing & Disbursing Officers), picked when entering transactions.",
    hi: "इस क्लाइंट के DDO (Drawing & Disbursing Officer) की पुन: उपयोग होने वाली सूची, ट्रांजैक्शन दर्ज करते समय चुनी जाती है।",
  },
  "clientNav.reports": { en: "Reports", hi: "रिपोर्ट्स" },
  "clientNav.reports.info": {
    en: "DDO-wise, FY-wise, and filing-history reports for this client.",
    hi: "इस क्लाइंट के लिए DDO-वाइज़, वित्तीय वर्ष-वाइज़, और फाइलिंग-हिस्ट्री रिपोर्ट्स।",
  },

  "shell.signOut": { en: "Sign out", hi: "साइन आउट" },
  "shell.lastLogin": { en: "Last login", hi: "पिछला लॉगिन" },
  "shell.firstLogin": { en: "first login", hi: "पहला लॉगिन" },
  "shell.language": { en: "Language", hi: "भाषा" },
} satisfies Record<string, Record<Language, string>>;

export type TranslationKey = keyof typeof translations;
