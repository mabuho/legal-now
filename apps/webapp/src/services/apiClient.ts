// apiClient depends on authStore; authStore uses raw fetch to avoid a circular import.
// useAuthStore() is called lazily (inside request()) so Pinia is always initialized first.

// eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
const API_BASE: string = import.meta.env.VITE_API_BASE || (() => { throw new Error('[apiClient] VITE_API_BASE is not defined. Check your .env / docker-compose environment.') })()

export interface ApiError {
  status: number
  code: string
  message: string
  fields?: Array<{ field: string; message: string }>
}

export interface ApiResponse<T> {
  data: T | null
  error: ApiError | null
}

type Params = Record<string, string | number | boolean | undefined>

interface RequestOptions {
  body?: unknown
  params?: Params
  headers?: Record<string, string>
  /** Set to false to skip Bearer injection (used by refreshTokens to avoid loops). */
  auth?: boolean
}

// Shared in-flight refresh promise — deduplicates concurrent 401 retries.
let refreshingPromise: Promise<void> | null = null

function buildUrl(path: string, params?: Params): string {
  const url = new URL(`${API_BASE}${path}`)
  if (params) {
    for (const [k, v] of Object.entries(params)) {
      if (v !== undefined) url.searchParams.set(k, String(v))
    }
  }
  return url.toString()
}

async function parseErrorBody(res: Response): Promise<ApiError> {
  const contentType = res.headers.get('content-type') ?? ''
  if (contentType.includes('application/json')) {
    try {
      const json = await res.json()
      return {
        status: res.status,
        code: (json.error as string | undefined) ?? 'unknown_error',
        message: (json.message as string | undefined) ?? res.statusText,
        fields: json.fields,
      }
    } catch {
      // fall through to text
    }
  }
  const text = await res.text().catch(() => res.statusText)
  return { status: res.status, code: 'unknown_error', message: text }
}

async function request<T>(
  method: string,
  path: string,
  options: RequestOptions = {}
): Promise<ApiResponse<T>> {
  const { body, params, headers: extraHeaders = {}, auth = true } = options

  // Lazy import prevents circular dependency at module evaluation time.
  const { useAuthStore } = await import('@/stores/auth')
  const authStore = useAuthStore()

  const buildHeaders = (): Record<string, string> => {
    const h: Record<string, string> = {
      'Content-Type': 'application/json',
      'ngrok-skip-browser-warning': 'true',
      ...extraHeaders,
    }
    if (auth && authStore.accessToken) {
      h['Authorization'] = `Bearer ${authStore.accessToken}`
    }
    return h
  }

  const doFetch = () =>
    fetch(buildUrl(path, params), {
      method,
      headers: buildHeaders(),
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })

  let res = await doFetch()

  if (res.status === 401 && auth) {
    // Deduplicate concurrent refreshes.
    if (!refreshingPromise) {
      refreshingPromise = authStore.refreshTokens().finally(() => {
        refreshingPromise = null
      })
    }
    const pending = refreshingPromise as Promise<void>
    try {
      await pending
    } catch {
      // refreshTokens() called logout() internally; propagate as error.
      return { data: null, error: { status: 401, code: 'session_expired', message: 'Session expired. Please log in again.' } }
    }
    // Retry once with the new token.
    res = await doFetch()
  }

  if (!res.ok) {
    const error = await parseErrorBody(res)
    return { data: null, error }
  }

  // 204 No Content
  if (res.status === 204) {
    return { data: null, error: null }
  }

  try {
    const data = (await res.json()) as T
    return { data, error: null }
  } catch {
    return { data: null, error: { status: res.status, code: 'parse_error', message: 'Failed to parse response body.' } }
  }
}

// Convenience helpers — every call must specify an explicit path.

export function apiGet<T>(
  path: string,
  opts?: { params?: Params; headers?: Record<string, string>; auth?: boolean }
): Promise<ApiResponse<T>> {
  return request<T>('GET', path, opts)
}

export function apiPost<T>(
  path: string,
  body?: unknown,
  opts?: { headers?: Record<string, string>; auth?: boolean }
): Promise<ApiResponse<T>> {
  return request<T>('POST', path, { ...opts, body })
}

export function apiPut<T>(
  path: string,
  body?: unknown,
  opts?: { headers?: Record<string, string>; auth?: boolean }
): Promise<ApiResponse<T>> {
  return request<T>('PUT', path, { ...opts, body })
}

export function apiPatch<T>(
  path: string,
  body?: unknown,
  opts?: { headers?: Record<string, string>; auth?: boolean }
): Promise<ApiResponse<T>> {
  return request<T>('PATCH', path, { ...opts, body })
}

export function apiDelete<T>(
  path: string,
  opts?: { params?: Params; headers?: Record<string, string>; auth?: boolean }
): Promise<ApiResponse<T>> {
  return request<T>('DELETE', path, opts)
}
