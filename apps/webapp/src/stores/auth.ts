// Uses raw fetch (not apiClient) to avoid a circular dependency:
//   apiClient → useAuthStore() → apiClient would create an import cycle.
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Role } from '@/types/user'


const API_BASE: string = import.meta.env.VITE_API_BASE || (() => { throw new Error('[authStore] VITE_API_BASE is not defined.') })()

const AUTH_STORAGE_KEY = 'legalnow-auth'

// Extend User for auth purposes — the existing User type lacks id/avatar_url.
export interface AuthUser {
  id: string
  email: string
  name: string
  role: Role | 'admin'
  avatar_url: string | null
  /** Mapped from avatar_url for backward compat with pre-2b views. */
  avatar?: string | null
  /** Lawyer room assignment — still used by Janus views (pre-2b). */
  roomId?: number
  /** Set after email confirmation. */
  emailConfirmedAt?: string | null
  /** Set after lawyer completes onboarding wizard. */
  onboardingCompletedAt?: string | null
  /** Set after SEP verification passes. */
  verifiedAt?: string | null
}

interface TokenSet {
  access_token: string
  refresh_token: string
  expires_in: number // seconds
  token_type: string
}

interface AuthApiResponse {
  user: AuthUser
  tokens: TokenSet
}

interface StoredAuth {
  user: AuthUser
  accessToken: string
  refreshToken: string
  accessTokenExpiresAt: number
}

async function authFetch(path: string, body: unknown, token?: string): Promise<Response> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    'ngrok-skip-browser-warning': 'true',
  }
  if (token) headers['Authorization'] = `Bearer ${token}`
  return fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers,
    body: JSON.stringify(body),
  })
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<AuthUser | null>(null)
  const accessToken = ref<string | null>(null)
  const refreshToken = ref<string | null>(null)
  const accessTokenExpiresAt = ref<number | null>(null)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!user.value && !!accessToken.value)

  // ─── Persistence helpers ──────────────────────────────────────────────────

  function persist(): void {
    if (!user.value || !accessToken.value || !refreshToken.value || !accessTokenExpiresAt.value) return
    const stored: StoredAuth = {
      user: user.value,
      accessToken: accessToken.value,
      refreshToken: refreshToken.value,
      accessTokenExpiresAt: accessTokenExpiresAt.value,
    }
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(stored))
  }

  function clear(): void {
    user.value = null
    accessToken.value = null
    refreshToken.value = null
    accessTokenExpiresAt.value = null
    error.value = null
    localStorage.removeItem(AUTH_STORAGE_KEY)
  }

  function applyTokenSet(authUser: AuthUser, tokens: TokenSet): void {
    user.value = authUser
    accessToken.value = tokens.access_token
    refreshToken.value = tokens.refresh_token
    accessTokenExpiresAt.value = Date.now() + tokens.expires_in * 1000
    error.value = null
    persist()
  }

  // ─── Actions ──────────────────────────────────────────────────────────────

  /** Synchronous. Rehydrates state from localStorage on app boot. */
  function init(): void {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    if (!raw) return
    try {
      const stored = JSON.parse(raw) as StoredAuth
      user.value = stored.user
      accessToken.value = stored.accessToken
      refreshToken.value = stored.refreshToken
      accessTokenExpiresAt.value = stored.accessTokenExpiresAt
    } catch {
      clear()
    }
  }

  async function register(payload: {
    email: string
    password: string
    name: string
    role: Role | 'admin'
  }): Promise<void> {
    error.value = null
    const res = await authFetch('/auth/register', payload)
    if (!res.ok) {
      const json = await res.json().catch(() => ({ message: res.statusText }))
      error.value = (json.message as string | undefined) ?? 'Registration failed.'
      throw new Error(error.value)
    }
    const { user: authUser, tokens } = (await res.json()) as AuthApiResponse
    applyTokenSet(authUser, tokens)
  }

  async function login(email: string, password: string): Promise<void> {
    error.value = null
    const res = await authFetch('/auth/login', { email, password })
    if (!res.ok) {
      const json = await res.json().catch(() => ({ message: res.statusText }))
      error.value = (json.message as string | undefined) ?? 'Invalid credentials.'
      throw new Error(error.value)
    }
    const { user: authUser, tokens } = (await res.json()) as AuthApiResponse
    applyTokenSet(authUser, tokens)
  }

  async function logout(): Promise<void> {
    // Best-effort — ignore network failures.
    if (refreshToken.value) {
      await authFetch('/auth/logout', { refresh_token: refreshToken.value }, accessToken.value ?? undefined).catch(
        () => undefined
      )
    }
    clear()
  }

  /**
   * Rotates the access/refresh token pair.
   * Called by apiClient on 401 — must set auth:false on the underlying fetch to avoid loops.
   */
  async function refreshTokens(): Promise<void> {
    if (!refreshToken.value) {
      await logout()
      throw new Error('No refresh token available.')
    }
    const res = await authFetch('/auth/refresh', { refresh_token: refreshToken.value })
    if (!res.ok) {
      await logout()
      throw new Error('Token refresh failed.')
    }
    const { user: authUser, tokens } = (await res.json()) as AuthApiResponse
    applyTokenSet(authUser, tokens)
  }

  /** Fetches /auth/me and updates user state. Useful on app boot when token is present. */
  async function fetchMe(): Promise<void> {
    if (!accessToken.value) return
    const res = await fetch(`${API_BASE}/auth/me`, {
      headers: {
        Authorization: `Bearer ${accessToken.value}`,
        'ngrok-skip-browser-warning': 'true',
      },
    })
    if (!res.ok) throw new Error('Failed to fetch user profile.')
    user.value = (await res.json()) as AuthUser
    persist()
  }

  return {
    user,
    accessToken,
    refreshToken,
    accessTokenExpiresAt,
    error,
    isAuthenticated,
    init,
    register,
    login,
    logout,
    refreshTokens,
    fetchMe,
  }
})
