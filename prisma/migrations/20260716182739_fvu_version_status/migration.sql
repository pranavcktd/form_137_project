
-- CreateTable
CREATE TABLE "FvuVersionStatus" (
    "id" TEXT NOT NULL DEFAULT 'singleton',
    "supportedVersion" TEXT NOT NULL,
    "detectedVersion" TEXT,
    "isNewer" BOOLEAN NOT NULL DEFAULT false,
    "checkedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "FvuVersionStatus_pkey" PRIMARY KEY ("id")
);

