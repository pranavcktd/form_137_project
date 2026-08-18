
-- CreateEnum
CREATE TYPE "FilingPeriodStatus" AS ENUM ('DRAFT', 'LOCKED');

-- AlterTable
ALTER TABLE "DdoRecord" ADD COLUMN     "ddoMasterId" TEXT;

-- AlterTable
ALTER TABLE "FilingPeriod" ADD COLUMN     "lockedAt" TIMESTAMP(3),
ADD COLUMN     "receiptDate" TIMESTAMP(3),
ADD COLUMN     "receiptNumber" TEXT,
ADD COLUMN     "status" "FilingPeriodStatus" NOT NULL DEFAULT 'DRAFT';

-- CreateTable
CREATE TABLE "DdoMaster" (
    "id" TEXT NOT NULL,
    "clientId" TEXT NOT NULL,
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
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "DdoMaster_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "DdoMaster_clientId_tan_key" ON "DdoMaster"("clientId", "tan");

-- CreateIndex
CREATE UNIQUE INDEX "DdoRecord_filingPeriodId_ddoMasterId_formType_key" ON "DdoRecord"("filingPeriodId", "ddoMasterId", "formType");

-- AddForeignKey
ALTER TABLE "DdoMaster" ADD CONSTRAINT "DdoMaster_clientId_fkey" FOREIGN KEY ("clientId") REFERENCES "Client"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "DdoRecord" ADD CONSTRAINT "DdoRecord_ddoMasterId_fkey" FOREIGN KEY ("ddoMasterId") REFERENCES "DdoMaster"("id") ON DELETE SET NULL ON UPDATE CASCADE;

