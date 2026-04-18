# DECISIONS.md

Architectural and product decisions for **Capsule**.
This file is the living record of every meaningful choice made during development.

---

## 001 — Stack Selection

| Layer | Choice | Rationale |
|-------|--------|-----------|
| Backend language | Java 21 (Virtual Threads) | Structured concurrency + Project Loom for high I/O throughput without reactive complexity |
| Backend framework | Spring Boot 3.2.x | Mature ecosystem; first-class Virtual Thread support via `spring.threads.virtual.enabled=true` |
| Database | PostgreSQL 16 | JSONB for flexible capsule metadata; strong consistency guarantees |
| Cache / rate-limit | Redis 7 | Token TTL, rate-limit counters, session state |
| Object storage | Cloudflare R2 | S3-compatible, zero egress fees — critical for media-heavy capsules |
| Message queue | AWS SQS (ElasticMQ local) | Decouple unlock scheduler from email delivery; ElasticMQ mirrors SQS API for dev |
| Frontend | Next.js 14 App Router | RSC for fast initial loads; `[token]` dynamic route for capsule viewer |
| Styling | Tailwind CSS v3 | Utility-first; pairs well with Framer Motion animations |
| Canvas | Fabric.js 5 | Rich sticker / drawing layer for the capsule page decorator |

---

## 002 — No-Auth Philosophy

Capsules are identified by a cryptographically random `shareToken` (UUID v4 + HMAC-SHA256 suffix).
No accounts are required. Recipients are named at creation time; the backend verifies them against the stored recipient list before serving content.

---

## 003 — Encryption Strategy

- Each capsule's media is encrypted at rest with AES-256-GCM.
- A per-capsule data-encryption key (DEK) is wrapped with a master key loaded from `CAPSULE_MASTER_KEY`.
- This gives us envelope encryption without a full KMS dependency for the MVP.

---

## 004 — Unlock Mechanism

1. A Spring `@Scheduled` job polls unlockable capsules every minute.
2. On unlock, a message is published to `capsule-unlock-queue`.
3. An SQS consumer sends personalized emails to each named recipient.
4. The capsule's `status` column flips from `SEALED` → `OPEN`.

---

## 005 — Local Development

`docker-compose up` starts Postgres, Redis, and ElasticMQ.
Run Spring Boot with `-Dspring.profiles.active=dev` to pick up `application-dev.properties`.
Set `storage.mode=local` to skip R2 and write uploads to `./uploads/`.

---

*Add new decisions below with incrementing IDs.*
