<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useConsultationStore } from '@/stores/consultationStore'
import { useChatSessionStore } from '@/stores/chatSessionStore'
import { useAuthStore } from '@/stores/auth'
import { type Consultation, ConsultationStatus } from '@/types/chat'

const router = useRouter()
const auth = useAuthStore()
const cons = useConsultationStore()
const chat = useChatSessionStore()

const selectedConsultationId = ref<string | null>(null)
const selectedDetail = computed(() => cons.consultations.find(c => c.id === selectedConsultationId.value) ?? null)

const pendingList = computed(() => cons.consultations.filter(c => c.status === ConsultationStatus.PENDING))
const activeList = computed(() => cons.consultations.filter(c =>
  [ConsultationStatus.ACCEPTED, ConsultationStatus.SCHEDULED, ConsultationStatus.IN_PROGRESS].includes(c.status)
))

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('es-MX', { day: '2-digit', month: 'short' })
}

function statusLabel(status: ConsultationStatus): string {
  const labels: Record<ConsultationStatus, string> = {
    [ConsultationStatus.PENDING]: 'Pendiente',
    [ConsultationStatus.ACCEPTED]: 'Aceptada',
    [ConsultationStatus.SCHEDULED]: 'Programada',
    [ConsultationStatus.IN_PROGRESS]: 'En curso',
    [ConsultationStatus.COMPLETED]: 'Completada',
    [ConsultationStatus.REJECTED]: 'Rechazada',
    [ConsultationStatus.CANCELLED]: 'Cancelada',
  }
  return labels[status] ?? status
}

function statusBadgeClass(status: ConsultationStatus): string {
  switch (status) {
    case ConsultationStatus.PENDING: return 'bg-status-payment/10 text-status-payment border border-status-payment/20'
    case ConsultationStatus.ACCEPTED:
    case ConsultationStatus.SCHEDULED: return 'bg-brand-primary/10 text-brand-primary border border-brand-primary/20'
    case ConsultationStatus.IN_PROGRESS: return 'bg-status-active/10 text-status-active border border-status-active/20'
    case ConsultationStatus.COMPLETED: return 'bg-surface-raised text-text-muted border border-border-default'
    case ConsultationStatus.REJECTED:
    case ConsultationStatus.CANCELLED: return 'bg-status-error/10 text-status-error border border-status-error/20'
    default: return 'bg-surface-raised text-text-muted border border-border-default'
  }
}

function hasChatSession(consultationId: string): boolean {
  return chat.sessions.some((s) => s.consultation_id === consultationId)
}

function goToChat(consultation: Consultation) {
  router.push({ path: '/dashboard/chat', query: { consultation_id: consultation.id } })
}

onMounted(async () => {
  if (auth.user) {
    // TODO: Phase 6 — replace fetchMine polling with WebSocket subscription.
    await Promise.all([cons.fetchMine({ role: 'lawyer', size: 100 }), chat.fetchSessions()])
  }
})
</script>

<template>
  <div class="flex h-[calc(100vh-3.5rem)] bg-surface-base overflow-hidden">
    <!-- LEFT: consultation list -->
    <div class="w-80 flex-shrink-0 border-r border-border-subtle flex flex-col bg-surface-base overflow-y-auto">
      <div class="px-4 pt-5 pb-3">
        <p class="font-body text-xs font-bold text-text-muted uppercase tracking-widest">Consultas</p>
      </div>

      <!-- Pendientes -->
      <div class="mb-2">
        <p class="font-body text-xs font-bold text-text-muted uppercase tracking-widest px-4 pb-2">
          Pendientes
        </p>
        <template v-if="pendingList.length > 0">
          <div
            v-for="c in pendingList"
            :key="c.id"
            :class="['mx-3 mb-2 p-3 rounded-card cursor-pointer transition-all border',
              selectedConsultationId === c.id
                ? 'bg-surface-card border-brand-primary/40 shadow-glow'
                : 'bg-surface-raised border-border-default hover:border-brand-primary/20']"
            @click="selectedConsultationId = c.id"
          >
            <div class="flex justify-between items-start gap-2">
              <div class="min-w-0">
                <p class="font-heading font-semibold text-sm text-text-primary truncate">{{ c.title }}</p>
                <p class="font-body text-xs text-text-muted mt-0.5">{{ c.client?.name ?? '—' }} · {{ formatDate(c.created_at) }}</p>
              </div>
              <span :class="['font-body text-xs font-semibold px-2 py-0.5 rounded-tag shrink-0', statusBadgeClass(c.status)]">
                {{ statusLabel(c.status) }}
              </span>
            </div>
          </div>
        </template>
        <p v-else class="font-body text-xs text-text-muted text-center py-3 px-3">Sin consultas</p>
      </div>

      <!-- Activas -->
      <div class="mb-2">
        <p class="font-body text-xs font-bold text-text-muted uppercase tracking-widest px-4 pb-2">
          Activas
        </p>
        <template v-if="activeList.length > 0">
          <div
            v-for="c in activeList"
            :key="c.id"
            :class="['mx-3 mb-2 p-3 rounded-card cursor-pointer transition-all border',
              selectedConsultationId === c.id
                ? 'bg-surface-card border-brand-primary/40 shadow-glow'
                : 'bg-surface-raised border-border-default hover:border-brand-primary/20']"
            @click="selectedConsultationId = c.id"
          >
            <div class="flex justify-between items-start gap-2">
              <div class="min-w-0">
                <p class="font-heading font-semibold text-sm text-text-primary truncate">{{ c.title }}</p>
                <p class="font-body text-xs text-text-muted mt-0.5">{{ c.client?.name ?? '—' }} · {{ formatDate(c.created_at) }}</p>
              </div>
              <span :class="['font-body text-xs font-semibold px-2 py-0.5 rounded-tag shrink-0', statusBadgeClass(c.status)]">
                {{ statusLabel(c.status) }}
              </span>
            </div>
          </div>
        </template>
        <p v-else class="font-body text-xs text-text-muted text-center py-3 px-3">Sin consultas</p>
      </div>
    </div>

    <!-- RIGHT: detail panel -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- Empty state -->
      <div v-if="!selectedDetail" class="flex-1 flex flex-col items-center justify-center gap-4 text-center p-8">
        <div class="w-16 h-16 rounded-card bg-surface-card border border-border-default flex items-center justify-center">
          <svg class="w-8 h-8 text-text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        </div>
        <p class="font-heading font-semibold text-text-secondary">Selecciona una consulta</p>
        <p class="font-body text-sm text-text-muted max-w-xs">Elige una consulta de la lista para ver el detalle y acceder al chat</p>
      </div>

      <!-- Detail -->
      <template v-else>
        <!-- Header bar -->
        <div class="px-6 py-4 border-b border-border-subtle bg-surface-raised flex items-start justify-between gap-4">
          <div class="min-w-0">
            <h2 class="font-heading font-semibold text-text-primary truncate">{{ selectedDetail.title }}</h2>
            <p class="font-body text-sm text-text-muted mt-0.5">{{ selectedDetail.client?.name ?? '—' }} · {{ formatDate(selectedDetail.created_at) }}</p>
          </div>
          <div class="flex items-center gap-2 shrink-0">
            <span :class="['font-body text-xs font-semibold px-2 py-0.5 rounded-tag', statusBadgeClass(selectedDetail.status)]">
              {{ statusLabel(selectedDetail.status) }}
            </span>
            <button
              v-if="hasChatSession(selectedDetail.id) && [ConsultationStatus.ACCEPTED, ConsultationStatus.SCHEDULED, ConsultationStatus.IN_PROGRESS].includes(selectedDetail.status)"
              @click="goToChat(selectedDetail)"
              class="font-body text-sm font-semibold px-3 py-1.5 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white shadow-glow-btn hover:opacity-90 transition-opacity"
            >
              Abrir chat →
            </button>
          </div>
        </div>

        <!-- Consultation info -->
        <div class="p-6 flex-1 overflow-y-auto">
          <div class="bg-surface-card border border-border-default rounded-card p-4 mb-4">
            <p class="font-body text-xs text-text-muted uppercase tracking-widest mb-2">Descripción</p>
            <p class="font-body text-sm text-text-secondary">{{ selectedDetail.description ?? 'Sin descripción' }}</p>
          </div>
          <div class="bg-surface-card border border-border-default rounded-card p-4">
            <p class="font-body text-xs text-text-muted uppercase tracking-widest mb-2">Cliente</p>
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-lg bg-gradient-to-br from-brand-primary-dark to-brand-accent flex items-center justify-center font-heading font-bold text-xs text-white shrink-0">
                {{ selectedDetail.client?.name?.[0]?.toUpperCase() ?? '?' }}
              </div>
              <div>
                <p class="font-heading font-semibold text-sm text-text-primary">{{ selectedDetail.client?.name ?? '—' }}</p>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>
