-- CreateEnum
CREATE TYPE "OrganizationStatus" AS ENUM ('ACTIVE', 'DISABLED');

-- AlterTable
ALTER TABLE "Client" ADD COLUMN     "enabledReturnTypes" TEXT[] DEFAULT ARRAY['FORM137']::TEXT[];

-- AlterTable
ALTER TABLE "GeneratedFile" ADD COLUMN     "statisticFilePath" TEXT;

-- AlterTable
ALTER TABLE "Organization" ADD COLUMN     "enabledApplications" TEXT[] DEFAULT ARRAY['FORM137']::TEXT[],
ADD COLUMN     "status" "OrganizationStatus" NOT NULL DEFAULT 'ACTIVE',
ADD COLUMN     "subscriptionEndDate" TIMESTAMP(3),
ADD COLUMN     "subscriptionStartDate" TIMESTAMP(3);

-- AlterTable
ALTER TABLE "User" ADD COLUMN     "disabled" BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN     "lastLoginAt" TIMESTAMP(3),
ADD COLUMN     "mustChangePassword" BOOLEAN NOT NULL DEFAULT false;
