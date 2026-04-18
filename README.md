# Capsule — Seal a Memory in Time

Capsule is a whimsical, secure, and time-locked digital memory platform. It allows users to create digital "time capsules" containing messages, files, and drawings that only unlock at a specific moment in the future.

## Features
- **Time-Locked Secrets**: Messages are encrypted with AES-256 and only revealed when the countdown reaches zero.
- **Hybrid Storage**: Local filesystem support for development and Cloudflare R2 for production.
- **AI Reflections**: Claude 3.5 Sonnet generates poetic summaries of your unlocked memories.
- **Whimsical UI**: A cinematic, paper-textured interface built with Next.js, Framer Motion, and Fabric.js.
- **Asynchronous Orchestration**: Managed via SQS for reliable, scheduled unlocking.

## Tech Stack
- **Backend**: Java 21, Spring Boot 3, PostgreSQL, AWS SQS, Cloudflare R2.
- **Frontend**: Next.js 16, TypeScript, Tailwind CSS, Framer Motion, Fabric.js.
- **AI**: Anthropic Claude API.

## Setup

### Backend
1. Configure `application.properties`:
   - `spring.datasource.url`: Your PostgreSQL URL.
   - `capsule.master-key`: A 32-character string for secret wrapping.
   - `aws.access-key` / `aws.secret-key`: For R2/SQS.
   - `ai.api-key`: Your Anthropic API key.
2. Run with Maven: `./mvnw spring-boot:run`

### Frontend
1. Install dependencies: `npm install`
2. Run development server: `npm run dev`
3. Access at `http://localhost:3000`

## Security
Capsule uses a **Dual Secret Strategy** where the URL token and the encryption key are decoupled. Content is encrypted at the application layer before storage, and keys are wrapped with a hardware-secured (or environment-secured) master key.
