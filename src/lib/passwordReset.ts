import crypto from "crypto";

/** A self-service temporary password stays valid (alongside the existing password)
 *  for 24 hours before it expires unused. */
export const PENDING_PASSWORD_TTL_MS = 24 * 60 * 60 * 1000;

const UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ"; // no I/O — avoids look-alike confusion
const LOWER = "abcdefghijkmnpqrstuvwxyz"; // no l
const DIGITS = "23456789"; // no 0/1
const SYMBOLS = "!@#$%&*";
const ALL = UPPER + LOWER + DIGITS + SYMBOLS;

function randomChar(alphabet: string): string {
  return alphabet[crypto.randomInt(alphabet.length)];
}

/**
 * A cryptographically random one-time password for the self-service "forgot
 * password" flow — unlike an admin-triggered reset (which an authenticated
 * admin can see on-screen), this is emailed straight to an unauthenticated
 * requester, so it must actually be unguessable rather than a shared default.
 * Guarantees at least one of each character class, 12 characters total.
 */
export function generateTemporaryPassword(): string {
  const required = [randomChar(UPPER), randomChar(LOWER), randomChar(DIGITS), randomChar(SYMBOLS)];
  const rest = Array.from({ length: 8 }, () => randomChar(ALL));
  const chars = [...required, ...rest];

  // Fisher-Yates shuffle so the fixed-class characters aren't always in the same positions.
  for (let i = chars.length - 1; i > 0; i--) {
    const j = crypto.randomInt(i + 1);
    [chars[i], chars[j]] = [chars[j], chars[i]];
  }
  return chars.join("");
}
