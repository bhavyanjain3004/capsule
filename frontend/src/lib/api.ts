/**
 * api.ts — Central API client for the Capsule frontend.
 *
 * All fetch calls to the backend should go through this module.
 * Business-logic methods will be added here as features are built.
 */

export const API_BASE =
  process.env.NEXT_PUBLIC_API_BASE ?? 'http://localhost:8080'
