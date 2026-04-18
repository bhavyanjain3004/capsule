# Architectural Decisions — Capsule

## 1. Security & Encryption
- **Dual Secret Strategy**: We use a `token` (UUID) for URL routing and a separate `encryptionSecret` (UUID) for content encryption. The `encryptionSecret` is never exposed in the URL or logs.
- **Secret Wrapping**: The `encryptionSecret` is wrapped with a `CAPSULE_MASTER_KEY` (AES/GCM) before being stored in the database. This ensures that even a full database leak does not expose capsule content without the master key.
- **Deterministic Key Derivation**: We use SHA-256 to derive a 256-bit AES key from the `encryptionSecret`. This ensures the same secret always unlocks the same content without storing the key itself.

## 2. Storage Strategy
- **Hybrid Storage**: The system supports both `local` and `r2` modes. 
  - `local`: Files are stored in the `./uploads` directory and served via `LocalFileController`. This allows for zero-cost development and testing.
  - `r2`: Files are stored in Cloudflare R2 (S3-compatible) with pre-signed URLs. This provides professional-grade scalability for production.

## 3. Orchestration & Background Processing
- **SQS-First Polling**: Unlocks are managed via SQS delay queues.
- **DevUnlockScheduler**: A background task that identifies capsules entering the 15-minute SQS window and enqueues them. This bridges the gap between long-term database storage and short-term SQS delay.

## 4. AI Integration
- **Poetic Reflections**: We use Claude 3.5 Sonnet to generate nostalgic reflections for unlocked capsules. This is triggered asynchronously to ensure no delay in the user experience.

## 5. Frontend Aesthetics
- **Whimsical Design**: The UI uses a blend of `Playfair Display` (headers) and `Lora` (body) to create a "digital stationary" feel.
- **Texture Overlay**: A global grain texture is applied to all pages to remove the "clinical" feel of modern web apps.
