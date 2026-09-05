import AdmZip from "adm-zip";
import { organizationBackupSchema } from "@/lib/validation/backup";
import type { OrganizationBackup } from "./exportOrganization";

/** Accepts either the whole backup .zip (as produced by buildBackupZip) or a
 *  bare database.json extracted from one — restore isn't picky about which. */
export function parseUploadedBackup(filename: string, buffer: Buffer): OrganizationBackup {
  let jsonText: string;

  if (filename.toLowerCase().endsWith(".zip")) {
    const zip = new AdmZip(buffer);
    const entry = zip.getEntries().find((e) => e.entryName.toLowerCase() === "database.json");
    if (!entry) {
      throw new Error("This zip doesn't contain a database.json — is it really a Nex backup?");
    }
    jsonText = entry.getData().toString("utf8");
  } else {
    jsonText = buffer.toString("utf8");
  }

  let parsed: unknown;
  try {
    parsed = JSON.parse(jsonText);
  } catch {
    throw new Error("Couldn't read that file as JSON — is it really a Nex backup?");
  }

  const result = organizationBackupSchema.safeParse(parsed);
  if (!result.success) {
    throw new Error("That file doesn't match the expected backup format.");
  }

  return result.data;
}
