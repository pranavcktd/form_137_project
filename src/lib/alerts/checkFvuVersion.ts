export const SUPPORTED_FVU_VERSION = "1.9";

export const FVU_VERSION_CHECK_URL =
  process.env.FVU_VERSION_CHECK_URL ||
  "https://tinpan.proteantech.in/services/form-no_137/form-no_137-index.html";

// Primary signal: the actual FVU download link's filename (e.g.
// "FORM24G_FVU_1.9.zip") — more stable than the prose announcement banner,
// which is free-text and could get rephrased without the version changing.
const DOWNLOAD_LINK_PATTERN = /FORM24G_FVU_([\d.]+)\.zip/i;

// Fallback: the human-readable "Latest FVU Version X.Y ... released" banner.
const BANNER_PATTERN = /Latest FVU Version\s+([\d.]+)\s+for Form 24G\s*\/\s*Form (?:No\.?\s*)?137/i;

export interface FvuVersionCheckResult {
  supportedVersion: string;
  detectedVersion: string | null;
  isNewer: boolean;
  sourceUrl: string;
}

function compareVersions(a: string, b: string): number {
  const partsA = a.split(".").map(Number);
  const partsB = b.split(".").map(Number);
  const len = Math.max(partsA.length, partsB.length);
  for (let i = 0; i < len; i++) {
    const diff = (partsA[i] ?? 0) - (partsB[i] ?? 0);
    if (diff !== 0) return diff;
  }
  return 0;
}

export function extractFvuVersion(html: string): string | null {
  const downloadMatch = html.match(DOWNLOAD_LINK_PATTERN);
  if (downloadMatch) return downloadMatch[1];

  const bannerMatch = html.match(BANNER_PATTERN);
  if (bannerMatch) return bannerMatch[1];

  return null;
}

export async function checkFvuVersion(): Promise<FvuVersionCheckResult> {
  const response = await fetch(FVU_VERSION_CHECK_URL, {
    headers: { "User-Agent": "Mozilla/5.0 (compatible; Form137EFilingBot/1.0)" },
  });
  if (!response.ok) {
    throw new Error(`Failed to fetch ${FVU_VERSION_CHECK_URL}: HTTP ${response.status}`);
  }
  const html = await response.text();
  const detectedVersion = extractFvuVersion(html);

  return {
    supportedVersion: SUPPORTED_FVU_VERSION,
    detectedVersion,
    isNewer: detectedVersion !== null && compareVersions(detectedVersion, SUPPORTED_FVU_VERSION) > 0,
    sourceUrl: FVU_VERSION_CHECK_URL,
  };
}

/** Persists a check result so pages can display it without re-scraping on every load. */
export async function persistFvuVersionStatus(result: FvuVersionCheckResult): Promise<void> {
  const { prisma } = await import("@/lib/prisma");
  await prisma.fvuVersionStatus.upsert({
    where: { id: "singleton" },
    create: {
      id: "singleton",
      supportedVersion: result.supportedVersion,
      detectedVersion: result.detectedVersion,
      isNewer: result.isNewer,
    },
    update: {
      supportedVersion: result.supportedVersion,
      detectedVersion: result.detectedVersion,
      isNewer: result.isNewer,
      checkedAt: new Date(),
    },
  });
}

const CACHE_MAX_AGE_MS = 6 * 60 * 60 * 1000; // 6 hours

/**
 * Reads the last persisted check, refreshing it inline if it's missing or
 * stale. Safe to call from a page render — the live scrape only happens at
 * most once per CACHE_MAX_AGE_MS across all requests.
 */
export async function getCachedFvuVersionStatus(): Promise<FvuVersionCheckResult> {
  const { prisma } = await import("@/lib/prisma");
  const cached = await prisma.fvuVersionStatus.findUnique({ where: { id: "singleton" } });

  const isStale = !cached || Date.now() - cached.checkedAt.getTime() > CACHE_MAX_AGE_MS;
  if (!isStale && cached) {
    return {
      supportedVersion: cached.supportedVersion,
      detectedVersion: cached.detectedVersion,
      isNewer: cached.isNewer,
      sourceUrl: FVU_VERSION_CHECK_URL,
    };
  }

  try {
    const fresh = await checkFvuVersion();
    await persistFvuVersionStatus(fresh);
    return fresh;
  } catch {
    // Scrape failed (network blip, site down) — fall back to whatever we
    // last knew, or a "not yet known" placeholder if this is the very first check.
    return (
      cached && {
        supportedVersion: cached.supportedVersion,
        detectedVersion: cached.detectedVersion,
        isNewer: cached.isNewer,
        sourceUrl: FVU_VERSION_CHECK_URL,
      }
    ) || { supportedVersion: SUPPORTED_FVU_VERSION, detectedVersion: null, isNewer: false, sourceUrl: FVU_VERSION_CHECK_URL };
  }
}
