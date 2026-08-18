import crypto from "crypto";

/** Self-service reset links expire after 30 minutes. */
export const RESET_TOKEN_TTL_MS = 30 * 60 * 1000;

/**
 * The raw token goes in the emailed link; only its hash is ever persisted
 * (same principle as a password hash) so a database compromise alone
 * can't be used to reset anyone's account.
 */
export function generateResetToken(): { rawToken: string; tokenHash: string } {
  const rawToken = crypto.randomBytes(32).toString("hex");
  return { rawToken, tokenHash: hashResetToken(rawToken) };
}

export function hashResetToken(rawToken: string): string {
  return crypto.createHash("sha256").update(rawToken).digest("hex");
}
