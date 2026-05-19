import { ref } from 'vue'
import { defineStore } from 'pinia'
import { type Consultation, type PageResponse, ConsultationStatus } from '@/types/chat'
import { apiGet, apiPost, apiPatch, apiDelete } from '@/services/apiClient'

export const useConsultationStore = defineStore('consultation', () => {
  const consultations = ref<Consultation[]>([])
  const selected = ref<Consultation | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchMine(opts?: {
    status?: ConsultationStatus
    role?: 'client' | 'lawyer'
    page?: number
    size?: number
  }) {
    loading.value = true
    error.value = null
    const { data, error: err } = await apiGet<PageResponse<Consultation>>('/consultations', {
      params: {
        status: opts?.status,
        role: opts?.role,
        page: opts?.page ?? 0,
        size: opts?.size ?? 20,
      },
    })
    loading.value = false
    if (err) {
      error.value = err.message
      return
    }
    consultations.value = data?.items ?? []
  }

  async function getById(id: string) {
    // Return from cache when already loaded.
    const cached = consultations.value.find((c) => c.id === id)
    if (cached) {
      selected.value = cached
      return cached
    }
    loading.value = true
    error.value = null
    const { data, error: err } = await apiGet<Consultation>(`/consultations/${id}`)
    loading.value = false
    if (err) {
      error.value = err.message
      return null
    }
    if (data) {
      consultations.value.push(data)
      selected.value = data
    }
    return data
  }

  async function create(req: {
    lawyer_id: string
    title: string
    description?: string
    scheduled_at?: string
  }) {
    loading.value = true
    error.value = null
    const { data, error: err } = await apiPost<Consultation>('/consultations', req)
    loading.value = false
    if (err) {
      error.value = err.message
      return null
    }
    if (data) {
      consultations.value.unshift(data)
    }
    return data
  }

  async function update(
    id: string,
    patch: { title?: string; description?: string; scheduled_at?: string },
  ) {
    loading.value = true
    error.value = null
    const { data, error: err } = await apiPatch<Consultation>(`/consultations/${id}`, patch)
    loading.value = false
    if (err) {
      error.value = err.message
      return null
    }
    if (data) {
      replaceInState(data)
    }
    return data
  }

  async function transition(id: string, status: ConsultationStatus, reason?: string) {
    loading.value = true
    error.value = null
    const { data, error: err } = await apiPost<Consultation>(`/consultations/${id}/status`, {
      status,
      reason,
    })
    loading.value = false
    if (err) {
      error.value = err.message
      return null
    }
    if (data) {
      replaceInState(data)
    }
    return data
  }

  async function cancel(id: string) {
    // Prefer a status transition so the server applies business rules.
    return transition(id, ConsultationStatus.CANCELLED)
  }

  // apiDelete is available for hard deletes if the server ever exposes 204.
  // Currently unused — cancel() drives via status transition.
  async function hardDelete(id: string) {
    loading.value = true
    error.value = null
    const { error: err } = await apiDelete(`/consultations/${id}`)
    loading.value = false
    if (err) {
      error.value = err.message
      return false
    }
    consultations.value = consultations.value.filter((c) => c.id !== id)
    if (selected.value?.id === id) selected.value = null
    return true
  }

  // Internal helper — replaces a consultation in both list and selected ref.
  function replaceInState(updated: Consultation) {
    const idx = consultations.value.findIndex((c) => c.id === updated.id)
    if (idx !== -1) {
      consultations.value[idx] = updated
    } else {
      consultations.value.unshift(updated)
    }
    if (selected.value?.id === updated.id) {
      selected.value = updated
    }
  }

  // Noop shims — polling was a Redis-era workaround; real-time updates
  // will arrive via WebSocket/Janus DataChannel in Phase 3.
  function startPollingConsultations(..._args: unknown[]) {
    console.warn('[consultationStore] polling removed; use WebSocket subscription (TODO Phase 3)')
  }
  function stopPollingConsultations() {
    console.warn('[consultationStore] polling removed; use WebSocket subscription (TODO Phase 3)')
  }

  return {
    consultations,
    selected,
    loading,
    error,
    fetchMine,
    getById,
    create,
    update,
    transition,
    cancel,
    hardDelete,
    startPollingConsultations,
    stopPollingConsultations,
  }
})
