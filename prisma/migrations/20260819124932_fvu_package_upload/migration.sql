-- CreateEnum
CREATE TYPE "FvuPackageAnalysisStatus" AS ENUM ('ANALYZING', 'DONE', 'FAILED');

-- CreateTable
CREATE TABLE "FvuPackageUpload" (
    "id" TEXT NOT NULL,
    "uploadedByName" TEXT NOT NULL,
    "uploadedByEmail" TEXT NOT NULL,
    "originalFilename" TEXT NOT NULL,
    "storagePath" TEXT NOT NULL,
    "status" "FvuPackageAnalysisStatus" NOT NULL DEFAULT 'ANALYZING',
    "errorMessage" TEXT,
    "analysisResult" JSONB,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "FvuPackageUpload_pkey" PRIMARY KEY ("id")
);
