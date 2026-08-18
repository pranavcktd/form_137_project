FROM node:20-bookworm AS base
RUN apt-get update && apt-get install -y --no-install-recommends default-jdk-headless \
    && rm -rf /var/lib/apt/lists/*

FROM base AS deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

FROM base AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run prisma:generate
RUN npm run build
# The vendor jar ships most of its own classes as source only (no .class),
# so the wrapper's build step compiles the vendor source tree together with
# HeadlessRunner.java rather than depending on the jar's (incomplete)
# precompiled classes. See fvu-wrapper/src/com/tin/etbaf/form24G for
# provenance notes.
RUN javac -implicit:none \
    -cp "vendor/fvu/barbecue-1.5.jar:vendor/fvu/bcprov-jdk15to18-1.80.jar:vendor/fvu/j2ee.jar:vendor/fvu/log4j-api-2.12.4.jar:vendor/fvu/log4j-core-2.12.4.jar:vendor/fvu/pd4ml.jar:vendor/fvu/ss_css2.jar" \
    -d fvu-wrapper/out \
    $(find fvu-wrapper/src -name "*.java")

FROM base AS runner
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json
COPY --from=builder /app/prisma ./prisma
COPY --from=builder /app/vendor ./vendor
COPY --from=builder /app/fvu-wrapper/out ./fvu-wrapper/out
COPY --from=builder /app/fvu-wrapper/resources ./fvu-wrapper/resources

EXPOSE 3000
CMD ["npm", "run", "start"]
