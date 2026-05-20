export type Role = 'client' | 'lawyer' | 'admin'

export interface PublicUser {
  id: string
  name: string
  role: Role
  avatar_url: string | null
}

export enum ConsultationStatus {
  PENDING = 'pending',
  ACCEPTED = 'accepted',
  REJECTED = 'rejected',
  SCHEDULED = 'scheduled',
  IN_PROGRESS = 'in_progress',
  COMPLETED = 'completed',
  CANCELLED = 'cancelled',
  // TODO Phase 5: map PENDING_PAYMENT / PAID when payments table is introduced
}

export interface Consultation {
  id: string
  status: ConsultationStatus
  title: string
  description: string | null
  client_id: string
  lawyer_id: string
  client: PublicUser
  lawyer: PublicUser
  scheduled_at: string | null
  janus_room_id: number | null
  janus_pin: string | null
  created_at: string
  updated_at: string
}

export interface ChatSession {
  id: string
  consultation_id: string
  started_at: string
  ended_at: string | null
  recording_path: string | null
  transcript_path: string | null
}

export interface ChatMessage {
  id: string
  session_id: string
  sender_id: string  // UUID; resolve display name via GET /users/{id}
  body: string
  sent_at: string
}

export interface PageResponse<T> {
  items: T[]
  page: number
  size: number
  total_elements: number
  total_pages: number
}

// === Local/UI-only types (NOT from API) ===

// Local cache of messages per session_id
export type ChatMessagesHistory = Record<string, ChatMessage[]>

// Kept for legacy chat composables/components during Phase 3 (Janus integration).
// Nothing serialized to/from the API uses this enum.
export enum ChatMessageType {
  TEXT = 'text',
  CALL = 'call',
  CARD = 'card',
  FILE = 'file',
}
