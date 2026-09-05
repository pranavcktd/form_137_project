import { prisma } from "@/lib/prisma";

export const BACKUP_FORMAT_VERSION = 1;

/**
 * A firm's full filing data as one nested, self-contained JSON tree — no raw
 * database IDs, only the natural keys each level is actually unique on (AIN,
 * DDO TAN, financial year/month/statement type, TAN+Form Type). That's what
 * lets restoreOrganization match "this same record" again later even though
 * every id will be freshly generated, and lets a backup be restored into any
 * organization, not just the one it came from.
 *
 * Deliberately excludes GeneratedFile (FVU run history/binary artifacts) —
 * those are regenerable from the DDO records + receipt info below, and
 * serializing FVU output files isn't what "the data" means for this backup.
 */
export interface OrganizationBackup {
  formatVersion: number;
  exportedAt: string;
  organizationName: string;
  clients: ClientBackup[];
}

export interface ClientBackup {
  ain: string;
  tan: string | null;
  ministryName: string | null;
  subMinistryName: string | null;
  departmentName: string;
  govtCategory: "CENTRAL" | "STATE";
  countryCode: string | null;
  enabledReturnTypes: string[];
  responsiblePersonName: string | null;
  responsiblePersonFirstName: string | null;
  responsiblePersonMiddleName: string | null;
  responsiblePersonLastName: string | null;
  responsiblePersonDesignation: string;
  responsiblePersonAddress1: string;
  responsiblePersonAddress2: string | null;
  responsiblePersonAddress3: string | null;
  responsiblePersonAddress4: string | null;
  responsiblePersonCity: string;
  responsiblePersonState: string;
  responsiblePersonPin: string;
  responsiblePersonStdCode: string | null;
  responsiblePersonPhone: string | null;
  responsiblePersonMobile: string | null;
  responsiblePersonEmail: string;
  ddoMasters: DdoMasterBackup[];
  filingPeriods: FilingPeriodBackup[];
}

export interface DdoMasterBackup {
  tan: string;
  name: string;
  address1: string | null;
  address2: string | null;
  address3: string | null;
  address4: string | null;
  city: string | null;
  state: string | null;
  pin: string | null;
  ddoRegNo: string | null;
  ddoCode: string | null;
  email: string | null;
}

export interface FilingPeriodBackup {
  financialYear: number;
  month: number;
  statementType: "ORIGINAL" | "CORRECTION_M" | "CORRECTION_X";
  status: "DRAFT" | "LOCKED";
  receiptNumber: string | null;
  receiptDate: string | null;
  ddoRecords: DdoRecordBackup[];
}

export interface DdoRecordBackup {
  serialNo: number;
  tan: string;
  name: string;
  address1: string | null;
  address2: string | null;
  address3: string | null;
  address4: string | null;
  city: string | null;
  state: string | null;
  pin: string | null;
  ddoRegNo: string | null;
  ddoCode: string | null;
  email: string | null;
  taxDeducted: string;
  formType: string | null;
  totalRemitted: string;
  natureOfDeduction: string | null;
  mode: "ADD" | "UPDATE" | "DELETE" | "NO_CHANGE";
}

export async function buildOrganizationBackup(organizationId: string): Promise<OrganizationBackup> {
  const organization = await prisma.organization.findUniqueOrThrow({ where: { id: organizationId } });

  const clients = await prisma.client.findMany({
    where: { organizationId },
    include: {
      ddoMasters: true,
      filingPeriods: {
        include: { ddoRecords: { orderBy: { serialNo: "asc" } } },
      },
    },
  });

  return {
    formatVersion: BACKUP_FORMAT_VERSION,
    exportedAt: new Date().toISOString(),
    organizationName: organization.name,
    clients: clients.map((client) => ({
      ain: client.ain,
      tan: client.tan,
      ministryName: client.ministryName,
      subMinistryName: client.subMinistryName,
      departmentName: client.departmentName,
      govtCategory: client.govtCategory,
      countryCode: client.countryCode,
      enabledReturnTypes: client.enabledReturnTypes,
      responsiblePersonName: client.responsiblePersonName,
      responsiblePersonFirstName: client.responsiblePersonFirstName,
      responsiblePersonMiddleName: client.responsiblePersonMiddleName,
      responsiblePersonLastName: client.responsiblePersonLastName,
      responsiblePersonDesignation: client.responsiblePersonDesignation,
      responsiblePersonAddress1: client.responsiblePersonAddress1,
      responsiblePersonAddress2: client.responsiblePersonAddress2,
      responsiblePersonAddress3: client.responsiblePersonAddress3,
      responsiblePersonAddress4: client.responsiblePersonAddress4,
      responsiblePersonCity: client.responsiblePersonCity,
      responsiblePersonState: client.responsiblePersonState,
      responsiblePersonPin: client.responsiblePersonPin,
      responsiblePersonStdCode: client.responsiblePersonStdCode,
      responsiblePersonPhone: client.responsiblePersonPhone,
      responsiblePersonMobile: client.responsiblePersonMobile,
      responsiblePersonEmail: client.responsiblePersonEmail,
      ddoMasters: client.ddoMasters.map((m) => ({
        tan: m.tan,
        name: m.name,
        address1: m.address1,
        address2: m.address2,
        address3: m.address3,
        address4: m.address4,
        city: m.city,
        state: m.state,
        pin: m.pin,
        ddoRegNo: m.ddoRegNo,
        ddoCode: m.ddoCode,
        email: m.email,
      })),
      filingPeriods: client.filingPeriods.map((fp) => ({
        financialYear: fp.financialYear,
        month: fp.month,
        statementType: fp.statementType,
        status: fp.status,
        receiptNumber: fp.receiptNumber,
        receiptDate: fp.receiptDate ? fp.receiptDate.toISOString() : null,
        ddoRecords: fp.ddoRecords.map((r) => ({
          serialNo: r.serialNo,
          tan: r.tan,
          name: r.name,
          address1: r.address1,
          address2: r.address2,
          address3: r.address3,
          address4: r.address4,
          city: r.city,
          state: r.state,
          pin: r.pin,
          ddoRegNo: r.ddoRegNo,
          ddoCode: r.ddoCode,
          email: r.email,
          taxDeducted: r.taxDeducted.toString(),
          formType: r.formType,
          totalRemitted: r.totalRemitted.toString(),
          natureOfDeduction: r.natureOfDeduction,
          mode: r.mode,
        })),
      })),
    })),
  };
}
