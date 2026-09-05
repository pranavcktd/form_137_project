import { prisma } from "@/lib/prisma";
import type { OrganizationBackup } from "./exportOrganization";

/** Same three choices as the existing DDO Excel importer, applied at filing-period
 *  granularity: "skip_existing" never touches a period (or its records) that's
 *  already there; "upsert" updates it and adds/updates records; "replace" wipes
 *  that period's existing records first, then reloads it fresh from the backup.
 *  None of these ever delete/touch a client, period, or record that simply isn't
 *  mentioned in the backup — restoring never removes data the backup doesn't know about. */
export type RestoreStrategy = "skip_existing" | "upsert" | "replace";

export interface RestoreSummary {
  clientsCreated: number;
  clientsUpdated: number;
  /** The backup's AIN already belongs to a different firm — that client (and
   *  everything under it) was left untouched entirely. */
  clientsSkippedConflict: number;
  filingPeriodsCreated: number;
  filingPeriodsUpdated: number;
  filingPeriodsSkipped: number;
  ddoRecordsCreated: number;
  ddoRecordsUpdated: number;
  ddoRecordsSkipped: number;
}

function emptySummary(): RestoreSummary {
  return {
    clientsCreated: 0,
    clientsUpdated: 0,
    clientsSkippedConflict: 0,
    filingPeriodsCreated: 0,
    filingPeriodsUpdated: 0,
    filingPeriodsSkipped: 0,
    ddoRecordsCreated: 0,
    ddoRecordsUpdated: 0,
    ddoRecordsSkipped: 0,
  };
}

export async function restoreOrganizationBackup(
  organizationId: string,
  backup: OrganizationBackup,
  strategy: RestoreStrategy,
): Promise<RestoreSummary> {
  const summary = emptySummary();

  await prisma.$transaction(
    async (tx) => {
      for (const clientBackup of backup.clients) {
        const existingClient = await tx.client.findUnique({ where: { ain: clientBackup.ain } });

        if (existingClient && existingClient.organizationId !== organizationId) {
          summary.clientsSkippedConflict++;
          continue;
        }

        const clientData = {
          tan: clientBackup.tan,
          ministryName: clientBackup.ministryName,
          subMinistryName: clientBackup.subMinistryName,
          departmentName: clientBackup.departmentName,
          govtCategory: clientBackup.govtCategory,
          countryCode: clientBackup.countryCode,
          enabledReturnTypes: clientBackup.enabledReturnTypes,
          responsiblePersonName: clientBackup.responsiblePersonName,
          responsiblePersonFirstName: clientBackup.responsiblePersonFirstName,
          responsiblePersonMiddleName: clientBackup.responsiblePersonMiddleName,
          responsiblePersonLastName: clientBackup.responsiblePersonLastName,
          responsiblePersonDesignation: clientBackup.responsiblePersonDesignation,
          responsiblePersonAddress1: clientBackup.responsiblePersonAddress1,
          responsiblePersonAddress2: clientBackup.responsiblePersonAddress2,
          responsiblePersonAddress3: clientBackup.responsiblePersonAddress3,
          responsiblePersonAddress4: clientBackup.responsiblePersonAddress4,
          responsiblePersonCity: clientBackup.responsiblePersonCity,
          responsiblePersonState: clientBackup.responsiblePersonState,
          responsiblePersonPin: clientBackup.responsiblePersonPin,
          responsiblePersonStdCode: clientBackup.responsiblePersonStdCode,
          responsiblePersonPhone: clientBackup.responsiblePersonPhone,
          responsiblePersonMobile: clientBackup.responsiblePersonMobile,
          responsiblePersonEmail: clientBackup.responsiblePersonEmail,
        };

        let client;
        if (!existingClient) {
          client = await tx.client.create({ data: { ...clientData, organizationId, ain: clientBackup.ain } });
          summary.clientsCreated++;
        } else {
          // Contact/profile fields are low-risk to keep in sync regardless of the
          // chosen strategy — that choice is about filing data, not contact info.
          client = await tx.client.update({ where: { id: existingClient.id }, data: clientData });
          summary.clientsUpdated++;
        }

        const masterIdByTan = new Map<string, string>();
        for (const masterBackup of clientBackup.ddoMasters) {
          const existingMaster = await tx.ddoMaster.findUnique({
            where: { clientId_tan: { clientId: client.id, tan: masterBackup.tan } },
          });
          const master =
            existingMaster ?? (await tx.ddoMaster.create({ data: { ...masterBackup, clientId: client.id } }));
          masterIdByTan.set(masterBackup.tan, master.id);
        }

        for (const fpBackup of clientBackup.filingPeriods) {
          const existingFp = await tx.filingPeriod.findUnique({
            where: {
              clientId_financialYear_month_statementType: {
                clientId: client.id,
                financialYear: fpBackup.financialYear,
                month: fpBackup.month,
                statementType: fpBackup.statementType,
              },
            },
          });

          if (existingFp && strategy === "skip_existing") {
            summary.filingPeriodsSkipped++;
            continue;
          }

          const fpData = {
            status: fpBackup.status,
            receiptNumber: fpBackup.receiptNumber,
            receiptDate: fpBackup.receiptDate ? new Date(fpBackup.receiptDate) : null,
          };

          let filingPeriod;
          if (existingFp) {
            if (strategy === "replace") {
              await tx.ddoRecord.deleteMany({ where: { filingPeriodId: existingFp.id } });
            }
            filingPeriod = await tx.filingPeriod.update({ where: { id: existingFp.id }, data: fpData });
            summary.filingPeriodsUpdated++;
          } else {
            filingPeriod = await tx.filingPeriod.create({
              data: {
                clientId: client.id,
                financialYear: fpBackup.financialYear,
                month: fpBackup.month,
                statementType: fpBackup.statementType,
                ...fpData,
              },
            });
            summary.filingPeriodsCreated++;
          }

          const startingMax =
            existingFp && strategy !== "replace"
              ? ((await tx.ddoRecord.aggregate({ where: { filingPeriodId: filingPeriod.id }, _max: { serialNo: true } }))
                  ._max.serialNo ?? 0)
              : 0;
          let nextSerial = startingMax + 1;

          for (const recordBackup of fpBackup.ddoRecords) {
            let ddoMasterId = masterIdByTan.get(recordBackup.tan);
            if (!ddoMasterId) {
              // Not among this client's backed-up ddoMasters (shouldn't normally
              // happen) — fall back to the record's own snapshot to seed one,
              // same as the Excel importer does for a DDO it hasn't seen before.
              const existingMaster = await tx.ddoMaster.findUnique({
                where: { clientId_tan: { clientId: client.id, tan: recordBackup.tan } },
              });
              const master =
                existingMaster ??
                (await tx.ddoMaster.create({
                  data: {
                    clientId: client.id,
                    tan: recordBackup.tan,
                    name: recordBackup.name,
                    address1: recordBackup.address1,
                    address2: recordBackup.address2,
                    address3: recordBackup.address3,
                    address4: recordBackup.address4,
                    city: recordBackup.city,
                    state: recordBackup.state,
                    pin: recordBackup.pin,
                    ddoRegNo: recordBackup.ddoRegNo,
                    ddoCode: recordBackup.ddoCode,
                    email: recordBackup.email,
                  },
                }));
              ddoMasterId = master.id;
              masterIdByTan.set(recordBackup.tan, master.id);
            }

            const formType = recordBackup.formType ?? "";
            const existingRecord =
              strategy === "replace"
                ? null
                : await tx.ddoRecord.findUnique({
                    where: {
                      filingPeriodId_ddoMasterId_formType: { filingPeriodId: filingPeriod.id, ddoMasterId, formType },
                    },
                  });

            const recordData = {
              tan: recordBackup.tan,
              name: recordBackup.name,
              address1: recordBackup.address1,
              address2: recordBackup.address2,
              address3: recordBackup.address3,
              address4: recordBackup.address4,
              city: recordBackup.city,
              state: recordBackup.state,
              pin: recordBackup.pin,
              ddoRegNo: recordBackup.ddoRegNo,
              ddoCode: recordBackup.ddoCode,
              email: recordBackup.email,
              taxDeducted: recordBackup.taxDeducted,
              formType,
              totalRemitted: recordBackup.totalRemitted,
              natureOfDeduction: recordBackup.natureOfDeduction,
              mode: recordBackup.mode,
            };

            if (existingRecord) {
              if (strategy === "skip_existing") {
                summary.ddoRecordsSkipped++;
                continue;
              }
              await tx.ddoRecord.update({ where: { id: existingRecord.id }, data: recordData });
              summary.ddoRecordsUpdated++;
            } else {
              await tx.ddoRecord.create({
                data: { ...recordData, ddoMasterId, filingPeriodId: filingPeriod.id, serialNo: nextSerial++ },
              });
              summary.ddoRecordsCreated++;
            }
          }
        }
      }
    },
    { timeout: 120_000 },
  );

  return summary;
}
