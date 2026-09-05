import { z } from "zod";

const ddoMasterBackupSchema = z.object({
  tan: z.string(),
  name: z.string(),
  address1: z.string().nullable(),
  address2: z.string().nullable(),
  address3: z.string().nullable(),
  address4: z.string().nullable(),
  city: z.string().nullable(),
  state: z.string().nullable(),
  pin: z.string().nullable(),
  ddoRegNo: z.string().nullable(),
  ddoCode: z.string().nullable(),
  email: z.string().nullable(),
});

const ddoRecordBackupSchema = ddoMasterBackupSchema.omit({ name: true }).extend({
  name: z.string(),
  serialNo: z.number(),
  taxDeducted: z.string(),
  formType: z.string().nullable(),
  totalRemitted: z.string(),
  natureOfDeduction: z.string().nullable(),
  mode: z.enum(["ADD", "UPDATE", "DELETE", "NO_CHANGE"]),
});

const filingPeriodBackupSchema = z.object({
  financialYear: z.number(),
  month: z.number(),
  statementType: z.enum(["ORIGINAL", "CORRECTION_M", "CORRECTION_X"]),
  status: z.enum(["DRAFT", "LOCKED"]),
  receiptNumber: z.string().nullable(),
  receiptDate: z.string().nullable(),
  ddoRecords: z.array(ddoRecordBackupSchema),
});

const clientBackupSchema = z.object({
  ain: z.string(),
  tan: z.string().nullable(),
  ministryName: z.string().nullable(),
  subMinistryName: z.string().nullable(),
  departmentName: z.string(),
  govtCategory: z.enum(["CENTRAL", "STATE"]),
  countryCode: z.string().nullable(),
  enabledReturnTypes: z.array(z.string()),
  responsiblePersonName: z.string().nullable(),
  responsiblePersonFirstName: z.string().nullable(),
  responsiblePersonMiddleName: z.string().nullable(),
  responsiblePersonLastName: z.string().nullable(),
  responsiblePersonDesignation: z.string(),
  responsiblePersonAddress1: z.string(),
  responsiblePersonAddress2: z.string().nullable(),
  responsiblePersonAddress3: z.string().nullable(),
  responsiblePersonAddress4: z.string().nullable(),
  responsiblePersonCity: z.string(),
  responsiblePersonState: z.string(),
  responsiblePersonPin: z.string(),
  responsiblePersonStdCode: z.string().nullable(),
  responsiblePersonPhone: z.string().nullable(),
  responsiblePersonMobile: z.string().nullable(),
  responsiblePersonEmail: z.string(),
  ddoMasters: z.array(ddoMasterBackupSchema),
  filingPeriods: z.array(filingPeriodBackupSchema),
});

export const organizationBackupSchema = z.object({
  formatVersion: z.number(),
  exportedAt: z.string(),
  organizationName: z.string(),
  clients: z.array(clientBackupSchema),
});

export const restoreRequestSchema = z.object({
  strategy: z.enum(["skip_existing", "upsert", "replace"]).default("upsert"),
});
