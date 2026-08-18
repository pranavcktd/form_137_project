/**
 * The Nex platform's sellable products. A firm's (Organization) subscription
 * enables a subset of these; a Client can only be set up for return types
 * its firm is actually subscribed to.
 *
 * `key` is the stable internal identifier persisted in the database
 * (Organization.enabledApplications, Client.enabledReturnTypes) — it stays
 * as "FORM137" even though the product is branded "Nex IT" to end users,
 * since Form 137/24G is an Income Tax filing. Only `label`/`description`
 * are meant to change as branding evolves.
 */
export const APPLICATION_TYPES = [
  {
    key: "FORM137",
    label: "Nex IT",
    description: "Income Tax filings — currently Form 137 / 24G (Book Adjustment Monthly Statement).",
  },
  {
    key: "TDS",
    label: "Nex TDS",
    description: "TDS/TCS returns (24Q / 26Q / 27Q / 27EQ) for deductors.",
  },
  {
    key: "GST",
    label: "Nex GST",
    description: "GST return filing.",
  },
] as const;

export type ApplicationTypeKey = (typeof APPLICATION_TYPES)[number]["key"];

export function applicationTypeLabel(key: string): string {
  return APPLICATION_TYPES.find((a) => a.key === key)?.label ?? key;
}
