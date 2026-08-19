import { auth } from "@/lib/auth";
import { NextResponse } from "next/server";

// /api/webhooks is called by external services (e.g. Razorpay) with no session cookie —
// it authenticates each request itself (signature verification), not via login.
const PUBLIC_PATHS = ["/login", "/forgot-password", "/api/webhooks"];

export default auth((req) => {
  const isLoggedIn = !!req.auth;
  const isPublicPath = PUBLIC_PATHS.some((path) =>
    req.nextUrl.pathname.startsWith(path),
  );
  const isAdminPath = req.nextUrl.pathname.startsWith("/admin");
  const isTeamPath = req.nextUrl.pathname.startsWith("/team");
  const isChangePasswordPath = req.nextUrl.pathname.startsWith("/change-password");
  const isApiPath = req.nextUrl.pathname.startsWith("/api/");

  if (!isLoggedIn && !isPublicPath) {
    const loginUrl = new URL("/login", req.nextUrl.origin);
    return NextResponse.redirect(loginUrl);
  }

  if (isLoggedIn && isPublicPath) {
    return NextResponse.redirect(new URL("/", req.nextUrl.origin));
  }

  // A temp/reset password must be changed before anything else is usable.
  if (isLoggedIn && req.auth?.user?.mustChangePassword && !isChangePasswordPath && !isApiPath) {
    return NextResponse.redirect(new URL("/change-password", req.nextUrl.origin));
  }

  if (isLoggedIn && isAdminPath && req.auth?.user?.role !== "SUPER_ADMIN") {
    return NextResponse.redirect(new URL("/", req.nextUrl.origin));
  }

  if (isLoggedIn && isTeamPath && req.auth?.user?.role !== "ADMIN") {
    return NextResponse.redirect(new URL("/", req.nextUrl.origin));
  }
});

export const config = {
  matcher: ["/((?!api/auth|_next/static|_next/image|favicon.ico).*)"],
};
