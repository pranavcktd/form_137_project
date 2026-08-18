import NextAuth from "next-auth";
import Credentials from "next-auth/providers/credentials";
import bcrypt from "bcryptjs";
import { prisma } from "@/lib/prisma";

export const { handlers, auth, signIn, signOut } = NextAuth({
  session: { strategy: "jwt" },
  pages: { signIn: "/login" },
  providers: [
    Credentials({
      credentials: {
        email: {},
        password: {},
      },
      authorize: async (credentials) => {
        const email = credentials?.email as string | undefined;
        const password = credentials?.password as string | undefined;
        if (!email || !password) return null;

        const user = await prisma.user.findUnique({
          where: { email },
          include: { organization: true },
        });
        if (!user) return null;

        const isValid = await bcrypt.compare(password, user.passwordHash);
        if (!isValid) return null;

        // Disabled user or disabled firm: reject same as a bad password —
        // an internal error code here would leak account existence/status.
        if (user.disabled || user.organization.status === "DISABLED") return null;

        // Captured before the overwrite below, so the session carries the
        // login *before this one* — what a "Last login: ..." banner means.
        const previousLoginAt = user.lastLoginAt;
        await prisma.user.update({ where: { id: user.id }, data: { lastLoginAt: new Date() } });

        return {
          id: user.id,
          email: user.email,
          name: user.name,
          role: user.role,
          organizationId: user.organizationId,
          mustChangePassword: user.mustChangePassword,
          previousLoginAt: previousLoginAt ? previousLoginAt.toISOString() : null,
        };
      },
    }),
  ],
  callbacks: {
    jwt: ({ token, user }) => {
      if (user) {
        token.role = user.role;
        token.organizationId = user.organizationId;
        token.mustChangePassword = user.mustChangePassword;
        token.previousLoginAt = user.previousLoginAt;
      }
      return token;
    },
    session: ({ session, token }) => {
      if (session.user) {
        session.user.id = token.sub as string;
        session.user.role = token.role as string;
        session.user.organizationId = token.organizationId as string;
        session.user.mustChangePassword = token.mustChangePassword as boolean;
        session.user.previousLoginAt = token.previousLoginAt as string | null;
      }
      return session;
    },
  },
});
