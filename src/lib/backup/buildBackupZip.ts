import AdmZip from "adm-zip";
import { prisma } from "@/lib/prisma";
import { buildOrganizationBackup, type OrganizationBackup } from "./exportOrganization";
import { buildFilingPeriodExcel, monthLabel } from "@/lib/excel/buildFilingPeriodExcel";

function sanitizeForPath(value: string): string {
  return value.replace(/[\\/:*?"<>|]/g, "-").trim() || "unnamed";
}

function fyLabel(financialYear: number): string {
  return `FY ${financialYear}-${String((financialYear + 1) % 100).padStart(2, "0")}`;
}

async function addBackupToZip(zip: AdmZip, backup: OrganizationBackup, prefix: string): Promise<void> {
  zip.addFile(`${prefix}database.json`, Buffer.from(JSON.stringify(backup, null, 2)));

  for (const client of backup.clients) {
    const clientFolder = `${prefix}excel/${sanitizeForPath(client.departmentName)} (AIN ${client.ain})`;
    for (const fp of client.filingPeriods) {
      const excelBuffer = await buildFilingPeriodExcel(fp.financialYear, fp.month, fp.ddoRecords);
      const fileName = `${String(fp.month).padStart(2, "0")} - ${monthLabel(fp.month)} - ${fp.statementType}.xlsx`;
      zip.addFile(`${clientFolder}/${sanitizeForPath(fyLabel(fp.financialYear))}/${fileName}`, excelBuffer);
    }
  }
}

/**
 * A firm's backup as a zip: database.json (the full structured export
 * restoreOrganization reads back) plus one Excel file per filing period,
 * organized by client / financial year — "module return month wise", one
 * sheet per month per client, since Form 137 is the only module with actual
 * filing periods today.
 */
export async function buildBackupZip(organizationId: string): Promise<Buffer> {
  const backup = await buildOrganizationBackup(organizationId);
  const zip = new AdmZip();
  await addBackupToZip(zip, backup, "");
  return zip.toBuffer();
}

/**
 * Every firm's backup in one zip, each under its own top-level folder —
 * disaster-recovery/archival only, no matching whole-platform restore:
 * restoring always targets one specific, already-existing firm (see
 * restoreOrganization), so this is a download-only export.
 */
export async function buildPlatformBackupZip(): Promise<Buffer> {
  const organizations = await prisma.organization.findMany({ select: { id: true, name: true } });
  const zip = new AdmZip();
  const usedFolderNames = new Set<string>();

  for (const org of organizations) {
    let folder = sanitizeForPath(org.name);
    // Two firms can share a display name — keep each folder distinct.
    if (usedFolderNames.has(folder)) folder = `${folder} (${org.id})`;
    usedFolderNames.add(folder);

    const backup = await buildOrganizationBackup(org.id);
    await addBackupToZip(zip, backup, `${folder}/`);
  }

  return zip.toBuffer();
}
