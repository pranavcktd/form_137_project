import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  experimental: {
    // Default is 10MB, too small for admin uploads like a full Protean FVU
    // package or a firm's backup .zip — every request passes through
    // src/proxy.ts, so this cap applies platform-wide, not just to one route.
    proxyClientMaxBodySize: "100mb",
  },
  // Next's dev server only trusts requests whose Origin is localhost by
  // default (a safety feature, not a bug) — without this, every dev-only
  // request from another device on the LAN (including the HMR websocket)
  // gets silently blocked, which looked like "login just reloads the page"
  // since the credentials request never even reached the server. Wildcarded
  // to the whole office subnet since DHCP can hand out a different address
  // than 192.168.1.33 later.
  allowedDevOrigins: ["192.168.1.*"],
};

export default nextConfig;
