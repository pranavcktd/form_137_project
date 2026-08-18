import { getCachedFvuVersionStatus } from "@/lib/alerts/checkFvuVersion";
import { Alert, Badge } from "@/components/ui";

export async function FvuVersionBanner() {
  const status = await getCachedFvuVersionStatus();

  if (status.isNewer && status.detectedVersion) {
    return (
      <Alert tone="amber">
        A newer FVU version (<strong>{status.detectedVersion}</strong>) has been published by
        Protean — this app currently supports version{" "}
        <strong>{status.supportedVersion}</strong>. Contact your platform admin before filing
        with the new format.
      </Alert>
    );
  }

  return (
    <div className="flex items-center gap-2 text-sm text-slate-500">
      <span>FVU Version:</span>
      <Badge tone="green">{status.supportedVersion} (up to date)</Badge>
    </div>
  );
}
