import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { ChatSession, ChatMessage, ChatMessagesHistory, PageResponse } from '@/types/chat'
import { apiGet, apiPost } from '@/services/apiClient'

export const useChatSessionStore = defineStore('chatSession', () => {
  const selectedSession = ref<ChatSession | null>(null)
  const sessions = ref<ChatSession[]>([])
  const messagesBySession = ref<ChatMessagesHistory>({})
  const loading = ref(false)
  const error = ref<string | null>(null)

  function selectSession(session: ChatSession | null) {
    if (selectedSession.value?.id === session?.id) return
    selectedSession.value = session
    if (session && !messagesBySession.value[session.id]) {
      fetchMessages(session.id)
    }
  }

  async function fetchSessions(page = 0, size = 50) {
    loading.value = true
    error.value = null
    const { data, error: err } = await apiGet<PageResponse<ChatSession>>('/chat-sessions', {
      params: { page, size },
    })
    loading.value = false
    if (err) {
      error.value = err.message
      return
    }
    sessions.value = data?.items ?? []
  }

  async function createSession(consultationId: string) {
    loading.value = true
    error.value = null
    const { data, error: err } = await apiPost<ChatSession>('/chat-sessions', {
      consultation_id: consultationId,
    })
    loading.value = false
    if (err) {
      error.value = err.message
      return null
    }
    if (data) {
      sessions.value.push(data)
    }
    return data
  }

  async function endSession(sessionId: string) {
    loading.value = true
    error.value = null
    const { data, error: err } = await apiPost<ChatSession>(`/chat-sessions/${sessionId}/end`)
    loading.value = false
    if (err) {
      error.value = err.message
      return null
    }
    if (data) {
      const idx = sessions.value.findIndex((s) => s.id === sessionId)
      if (idx !== -1) sessions.value[idx] = data
      if (selectedSession.value?.id === sessionId) selectedSession.value = data
    }
    return data
  }

  async function fetchMessages(sessionId: string, page = 0, size = 100) {
    loading.value = true
    error.value = null
    const { data, error: err } = await apiGet<PageResponse<ChatMessage>>(
      `/chat-sessions/${sessionId}/messages`,
      { params: { page, size } },
    )
    loading.value = false
    if (err) {
      error.value = err.message
      return
    }
    messagesBySession.value[sessionId] = data?.items ?? []
  }

  async function sendMessage(sessionId: string, body: string) {
    const { data, error: err } = await apiPost<ChatMessage>(
      `/chat-sessions/${sessionId}/messages`,
      { body },
    )
    if (err) {
      error.value = err.message
      return null
    }
    if (data) {
      appendIncomingMessage(sessionId, data)
    }
    return data
  }

  // Called for messages arriving via Janus DataChannel (Phase 3). No API call.
  function appendIncomingMessage(sessionId: string, msg: ChatMessage) {
    if (!messagesBySession.value[sessionId]) {
      messagesBySession.value[sessionId] = []
    }
    const existing = messagesBySession.value[sessionId]
    if (!existing.some((m) => m.id === msg.id)) {
      existing.push(msg)
    }
  }

  return {
    selectedSession,
    sessions,
    messagesBySession,
    loading,
    error,
    selectSession,
    fetchSessions,
    createSession,
    endSession,
    fetchMessages,
    sendMessage,
    appendIncomingMessage,
  }
})
