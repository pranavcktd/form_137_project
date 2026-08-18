-- CreateEnum
CREATE TYPE "GovtCategory" AS ENUM ('CENTRAL', 'STATE');

-- CreateEnum
CREATE TYPE "UserRole" AS ENUM ('SUPER_ADMIN', 'ADMIN', 'PREPARER');

-- CreateEnum
CREATE TYPE "StatementType" AS ENUM ('ORIGINAL', 'CORRECTION_M', 'CORRECTION_X');

-- CreateEnum
CREATE TYPE "DdoMode" AS ENUM ('ADD', 'UPDATE', 'DELETE', 'NO_CHANGE');

-- CreateEnum
CREATE TYPE "RecordStatus" AS ENUM ('STAGING', 'VALIDATED', 'FINAL');

-- CreateEnum
CREATE TYPE "GeneratedFileStatus" AS ENUM ('PENDING', 'FVU_PASSED', 'FVU_FAILED');

-- CreateTable
CREATE TABLE "Organization" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "contactEmail" TEXT,
    "contactPhone" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "Organization_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "User" (
    "id" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    "passwordHash" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "role" "UserRole" NOT NULL DEFAULT 'PREPARER',
    "organizationId" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "User_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Client" (
    "id" TEXT NOT NULL,
    "organizationId" TEXT NOT NULL,
    "ain" TEXT NOT NULL,
    "tan" TEXT,
    "ministryName" TEXT,
    "departmentName" TEXT NOT NULL,
    "govtCategory" "GovtCategory" NOT NULL,
    "countryCode" TEXT,
    "responsiblePersonName" TEXT,
    "responsiblePersonFirstName" TEXT,
    "responsiblePersonMiddleName" TEXT,
    "responsiblePersonLastName" TEXT,
    "responsiblePersonDesignation" TEXT NOT NULL,
    "responsiblePersonAddress1" TEXT NOT NULL,
    "responsiblePersonAddress2" TEXT,
    "responsiblePersonAddress3" TEXT,
    "responsiblePersonAddress4" TEXT,
    "responsiblePersonCity" TEXT NOT NULL,
    "responsiblePersonState" TEXT NOT NULL,
    "responsiblePersonPin" TEXT NOT NULL,
    "responsiblePersonStdCode" TEXT,
    "responsiblePersonPhone" TEXT,
    "responsiblePersonMobile" TEXT,
    "responsiblePersonEmail" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "Client_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "FilingPeriod" (
    "id" TEXT NOT NULL,
    "clientId" TEXT NOT NULL,
    "financialYear" INTEGER NOT NULL,
    "month" INTEGER NOT NULL,
    "statementType" "StatementType" NOT NULL DEFAULT 'ORIGINAL',
    "schemaVersion" TEXT NOT NULL DEFAULT 'v1_9',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "FilingPeriod_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "DdoRecord" (
    "id" TEXT NOT NULL,
    "filingPeriodId" TEXT NOT NULL,
    "serialNo" INTEGER NOT NULL,
    "tan" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "address1" TEXT,
    "address2" TEXT,
    "address3" TEXT,
    "address4" TEXT,
    "city" TEXT,
    "state" TEXT,
    "pin" TEXT,
    "ddoRegNo" TEXT,
    "ddoCode" TEXT,
    "email" TEXT,
    "taxDeducted" DECIMAL(15,2) NOT NULL,
    "formType" TEXT,
    "totalRemitted" DECIMAL(15,2) NOT NULL,
    "natureOfDeduction" TEXT,
    "mode" "DdoMode" NOT NULL DEFAULT 'ADD',
    "status" "RecordStatus" NOT NULL DEFAULT 'STAGING',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "DdoRecord_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "GeneratedFile" (
    "id" TEXT NOT NULL,
    "filingPeriodId" TEXT NOT NULL,
    "rawTextPath" TEXT NOT NULL,
    "fvuFilePath" TEXT,
    "errHtmlPath" TEXT,
    "receiptPath" TEXT,
    "fvuVersion" TEXT NOT NULL,
    "status" "GeneratedFileStatus" NOT NULL DEFAULT 'PENDING',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "GeneratedFile_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "User_email_key" ON "User"("email");

-- CreateIndex
CREATE UNIQUE INDEX "Client_ain_key" ON "Client"("ain");

-- CreateIndex
CREATE INDEX "Client_organizationId_idx" ON "Client"("organizationId");

-- CreateIndex
CREATE UNIQUE INDEX "FilingPeriod_clientId_financialYear_month_statementType_key" ON "FilingPeriod"("clientId", "financialYear", "month", "statementType");

-- CreateIndex
CREATE INDEX "DdoRecord_filingPeriodId_idx" ON "DdoRecord"("filingPeriodId");

-- AddForeignKey
ALTER TABLE "User" ADD CONSTRAINT "User_organizationId_fkey" FOREIGN KEY ("organizationId") REFERENCES "Organization"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Client" ADD CONSTRAINT "Client_organizationId_fkey" FOREIGN KEY ("organizationId") REFERENCES "Organization"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FilingPeriod" ADD CONSTRAINT "FilingPeriod_clientId_fkey" FOREIGN KEY ("clientId") REFERENCES "Client"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "DdoRecord" ADD CONSTRAINT "DdoRecord_filingPeriodId_fkey" FOREIGN KEY ("filingPeriodId") REFERENCES "FilingPeriod"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "GeneratedFile" ADD CONSTRAINT "GeneratedFile_filingPeriodId_fkey" FOREIGN KEY ("filingPeriodId") REFERENCES "FilingPeriod"("id") ON DELETE CASCADE ON UPDATE CASCADE;
