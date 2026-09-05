import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  experimental: {
    // Default is 10MB, too small for admin uploads like a full Protean FVU
    // package or a firm's backup .zip — every request passes through
    // src/proxy.ts, so this cap applies platform-wide, not just to one route.
    proxyClientMaxBodySize: "100mb",
  },
};

export default nextConfig;
