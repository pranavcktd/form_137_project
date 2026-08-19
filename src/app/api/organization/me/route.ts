import { NextResponse } from "next/server";
import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";
import { getEntitledApplications } from "@/lib/subscriptions";

export async function GET() {
  const session = await auth();
  if (!session?.user) return NextResponse.json({ error: "Unauthorized" }, { status: 401 });

  const organization = await prisma.organization.findUnique({
    where: { id: session.user.organizationId },
    select: { id: true, name: true },
  });
  if (!organization) return NextResponse.json({ error: "Not found" }, { status: 404 });

  const enabledApplications = await getEntitledApplications(organization.id);
  return NextResponse.json({ ...organization, enabledApplications });
}
