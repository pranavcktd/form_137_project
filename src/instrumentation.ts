export async function register() {
  if (process.env.NEXT_RUNTIME !== "nodejs") return;

  const cron = await import("node-cron");
  const { checkFvuVersion, persistFvuVersionStatus } = await import("@/lib/alerts/checkFvuVersion");
  const { sendVersionAlert } = await import("@/lib/alerts/notify");

  // Runs once daily at 06:00 server time, checking Protean's Form 137 page
  // for a newer FVU version than this app currently supports.
  cron.schedule("0 6 * * *", async () => {
    try {
      const result = await checkFvuVersion();
      await persistFvuVersionStatus(result);
      if (result.isNewer) {
        await sendVersionAlert(result);
      }
    } catch (err) {
      console.error("[fvu-version-check] Scheduled check failed:", err);
    }
  });
}
