-- AlterTable
ALTER TABLE "User" ADD COLUMN     "pendingPasswordExpiresAt" TIMESTAMP(3),
ADD COLUMN     "pendingPasswordHash" TEXT;
