import { auth } from "@/lib/auth";
import { prisma } from "@/lib/prisma";

/**
 * Loads a session-scoped Client, returning null if the caller isn't
 * authenticated or the client doesn't belong to their firm (Organization)
 * (rather than leaking existence via a 404 vs 403 distinction).
 */
export async function requireClient(clientId: string) {
  const session = await auth();
  if (!session?.user) return { session: null, client: null };

  const client = await prisma.client.findFirst({
    where: { id: clientId, organizationId: session.user.organizationId },
  });

  return { session, client };
}

/**
 * Loads a session-scoped FilingPeriod, returning null if the caller isn't
 * authenticated or the filing period's client doesn't belong to their firm.
 */
export async function requireFilingPeriod(filingPeriodId: string) {
  const session = await auth();
  if (!session?.user) return { session: null, filingPeriod: null };

  const filingPeriod = await prisma.filingPeriod.findFirst({
    where: {
      id: filingPeriodId,
      client: { organizationId: session.user.organizationId },
    },
  });

  return { session, filingPeriod };
}
