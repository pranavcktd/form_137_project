import "dotenv/config";
import { checkFvuVersion, persistFvuVersionStatus } from "@/lib/alerts/checkFvuVersion";
import { sendVersionAlert } from "@/lib/alerts/notify";

async function main() {
  const result = await checkFvuVersion();
  console.log(
    `[fvu-version-check] supported=${result.supportedVersion} detected=${result.detectedVersion ?? "unknown"} isNewer=${result.isNewer}`,
  );
  await persistFvuVersionStatus(result);

  if (result.isNewer) {
    console.log("[fvu-version-check] Newer version detected — sending alerts.");
    await sendVersionAlert(result);
  }
}

main().catch((err) => {
  console.error("[fvu-version-check] Failed:", err);
  process.exitCode = 1;
});
