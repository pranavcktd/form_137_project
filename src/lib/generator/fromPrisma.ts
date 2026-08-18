import type { Client, DdoRecord, FilingPeriod } from "@prisma/client";
import type { DdoRecordInput, FilingPeriodInput, OrganizationInput } from "./types";

export function clientToInput(client: Client): OrganizationInput {
  return {
    ain: client.ain,
    tan: client.tan,
    ministryName: client.ministryName,
    subMinistryName: client.subMinistryName,
    departmentName: client.departmentName,
    govtCategory: client.govtCategory,
    countryCode: client.countryCode,
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
  };
}

export function filingPeriodToInput(period: FilingPeriod): FilingPeriodInput {
  return {
    financialYear: period.financialYear,
    month: period.month,
    statementType: period.statementType,
  };
}

export function ddoRecordToInput(record: DdoRecord): DdoRecordInput {
  return {
    serialNo: record.serialNo,
    tan: record.tan,
    name: record.name,
    address1: record.address1,
    address2: record.address2,
    address3: record.address3,
    address4: record.address4,
    city: record.city,
    state: record.state,
    pin: record.pin,
    ddoRegNo: record.ddoRegNo,
    ddoCode: record.ddoCode,
    email: record.email,
    taxDeducted: Number(record.taxDeducted),
    formType: record.formType,
    totalRemitted: Number(record.totalRemitted),
    natureOfDeduction: record.natureOfDeduction,
    mode: record.mode,
  };
}
