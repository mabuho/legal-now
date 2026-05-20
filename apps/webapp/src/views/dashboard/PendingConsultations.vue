<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useConsultationStore } from '@/stores/consultationStore'
import { useChatSessionStore } from '@/stores/chatSessionStore'
import { ChatBubbleLeftRightIcon } from '@heroicons/vue/24/outline'
import { type Consultation, ConsultationStatus } from '@/types/chat'

const cons = useConsultationStore()
const chat = useChatSessionStore()
const router = useRouter()

// TODO: Phase 6 — WebSocket subscription replaces polling/refresh here.
onMounted(async () => {
  await Promise.all([cons.fetchMine({ role: 'lawyer', size: 100 }), chat.fetchSessions()])
})

type TabKey = 'pending' | 'active' | 'history'
const activeTab = ref<TabKey>('pending')

const PENDING_STATUSES = new Set([ConsultationStatus.PENDING])
const ACTIVE_STATUSES = new Set([
  ConsultationStatus.ACCEPTED,
  ConsultationStatus.SCHEDULED,
  ConsultationStatus.IN_PROGRESS,
])
const HISTORY_STATUSES = new Set([
  ConsultationStatus.COMPLETED,
  ConsultationStatus.REJECTED,
  ConsultationStatus.CANCELLED,
])

const pendingList = computed(() =>
  cons.consultations.filter((c) => PENDING_STATUSES.has(c.status)),
)
const activeList = computed(() =>
  cons.consultations.filter((c) => ACTIVE_STATUSES.has(c.status)),
)
const historyList = computed(() =>
  cons.consultations.filter((c) => HISTORY_STATUSES.has(c.status)),
)

const tabs: { key: TabKey; label: string }[] = [
  { key: 'pending', label: 'Pendientes' },
  { key: 'active', label: 'Activas' },
  { key: 'history', label: 'Histórico' },
]

function listForTab(key: TabKey): Consultation[] {
  if (key === 'pending') return pendingList.value
  if (key === 'active') return activeList.value
  return historyList.value
}

const emptyMessages: Record<TabKey, string> = {
  pending: 'No tienes consultas pendientes.',
  active: 'No tienes consultas activas.',
  history: 'Sin histórico.',
}

const schedulingId = ref<string | null>(null)
const schedulingDate = ref('')

function openScheduler(id: string) {
  schedulingId.value = id
  schedulingDate.value = ''
}

async function submitSchedule(c: Consultation) {
  if (!schedulingDate.value) return
  await cons.update(c.id, { scheduled_at: new Date(schedulingDate.value).toISOString() })
  await cons.transition(c.id, ConsultationStatus.SCHEDULED)
  schedulingId.value = null
}

async function goToChat(c: Consultation) {
  if (!chat.sessions.some((s) => s.consultation_id === c.id)) {
    await chat.createSession(c.id)
  }
  router.push({ path: '/dashboard/chat', query: { consultation_id: c.id } })
}

async function acceptConsultation(c: Consultation) {
  await cons.transition(c.id, ConsultationStatus.ACCEPTED)
  if (!chat.sessions.some((s) => s.consultation_id === c.id)) {
    try {
      await chat.createSession(c.id)
    } catch {
      // idempotent — session already exists
    }
  }
  router.push({ path: '/dashboard/chat', query: { consultation_id: c.id } })
}

async function rejectConsultation(c: Consultation) {
  await cons.transition(c.id, ConsultationStatus.REJECTED)
}

async function startConsultation(c: Consultation) {
  await cons.transition(c.id, ConsultationStatus.IN_PROGRESS)
  await goToChat(c)
}

async function completeConsultation(c: Consultation) {
  await cons.transition(c.id, ConsultationStatus.COMPLETED)
}

async function cancelConsultation(c: Consultation) {
  await cons.transition(c.id, ConsultationStatus.CANCELLED)
}
</script>

<template>
  <div class="px-4 py-6 max-w-3xl mx-auto">
    <h1 class="font-heading font-bold text-xl text-text-primary mb-5">Mis Consultas</h1>

    <!-- Tab bar -->
    <div class="flex gap-2 mb-6">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="flex items-center gap-1.5 px-4 py-1.5 rounded-tag font-body text-sm font-semibold transition-colors border"
        :class="
          activeTab === tab.key
            ? 'bg-brand-primary/10 text-brand-primary border-brand-primary/20'
            : 'text-text-muted border-border-default hover:text-text-secondary'
        "
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
        <span
          class="inline-flex items-center justify-center rounded-full px-1.5 py-0.5 text-xs font-semibold"
          :class="activeTab === tab.key ? 'bg-brand-primary/20 text-brand-primary' : 'bg-surface-raised text-text-muted'"
        >
          {{ listForTab(tab.key).length }}
        </span>
      </button>
    </div>

    <!-- Empty state -->
    <div v-if="listForTab(activeTab).length === 0" class="py-16 text-center">
      <div class="mx-auto h-16 w-16 text-text-muted mb-4">
        <ChatBubbleLeftRightIcon class="h-full w-full" />
      </div>
      <p class="font-body text-sm text-text-muted">{{ emptyMessages[activeTab] }}</p>
    </div>

    <!-- Consultation cards -->
    <div v-else class="space-y-4">
      <div
        v-for="item in listForTab(activeTab)"
        :key="item.id"
        class="bg-surface-card border border-border-default rounded-card shadow-glow p-4 hover:border-brand-primary/20 transition-colors"
      >
        <!-- Header row -->
        <div class="flex items-start justify-between gap-2 mb-1">
          <h2 class="font-heading font-semibold text-sm text-text-primary leading-snug">
            {{ item.title }}
          </h2>
          <span
            class="shrink-0 font-body text-xs font-semibold px-2 py-0.5 rounded-tag border"
            :class="{
              'bg-status-payment/10 text-status-payment border-status-payment/20': item.status === ConsultationStatus.PENDING,
              'bg-brand-primary/10 text-brand-primary border-brand-primary/20': item.status === ConsultationStatus.ACCEPTED || item.status === ConsultationStatus.SCHEDULED,
              'bg-status-active/10 text-status-active border-status-active/20': item.status === ConsultationStatus.IN_PROGRESS,
              'bg-surface-raised text-text-muted border-border-default': item.status === ConsultationStatus.COMPLETED,
              'bg-status-error/10 text-status-error border-status-error/20': item.status === ConsultationStatus.REJECTED || item.status === ConsultationStatus.CANCELLED,
            }"
          >
            {{
              item.status === ConsultationStatus.PENDING ? 'Pendiente' :
              item.status === ConsultationStatus.ACCEPTED ? 'Aceptada' :
              item.status === ConsultationStatus.SCHEDULED ? 'Programada' :
              item.status === ConsultationStatus.IN_PROGRESS ? 'En curso' :
              item.status === ConsultationStatus.COMPLETED ? 'Completada' :
              item.status === ConsultationStatus.REJECTED ? 'Rechazada' :
              'Cancelada'
            }}
          </span>
        </div>

        <!-- Details -->
        <p class="font-body text-sm text-text-secondary mb-1">
          {{ item.description || '—' }}
        </p>
        <p class="font-body text-xs text-text-muted">
          Cliente: <span class="font-semibold text-text-secondary">{{ item.client?.name ?? '—' }}</span>
          &nbsp;·&nbsp;
          Fecha: {{ new Date(item.created_at).toLocaleDateString('es-MX') }}
        </p>

        <p v-if="item.janus_room_id === null && activeTab === 'active'" class="font-body text-xs text-text-muted mt-1">
          <!-- TODO: Phase 3 — Janus room allocation -->
          Sala no asignada
        </p>

        <!-- PENDING actions -->
        <div v-if="activeTab === 'pending'" class="flex gap-2 mt-4">
          <button
            class="font-body text-sm font-semibold px-4 py-1.5 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white shadow-glow-btn hover:opacity-90 transition-opacity"
            @click="acceptConsultation(item)"
          >
            Aceptar
          </button>
          <button
            class="font-body text-sm font-semibold px-4 py-1.5 rounded-btn border border-status-error/40 text-status-error hover:bg-status-error/10 transition-colors"
            @click="rejectConsultation(item)"
          >
            Rechazar
          </button>
        </div>

        <!-- ACTIVE actions -->
        <div v-else-if="activeTab === 'active'" class="mt-4 space-y-3">
          <!-- Inline date-time picker -->
          <div v-if="schedulingId === item.id" class="flex items-center gap-2 flex-wrap">
            <input
              v-model="schedulingDate"
              type="datetime-local"
              class="bg-surface-raised border border-border-default rounded-btn px-3 py-1.5 font-body text-sm text-text-primary focus:outline-none focus:border-brand-primary/40"
            />
            <button
              class="font-body text-sm font-semibold px-3 py-1.5 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white shadow-glow-btn hover:opacity-90 transition-opacity"
              @click="submitSchedule(item)"
            >
              Confirmar
            </button>
            <button
              class="font-body text-sm text-text-muted hover:text-text-secondary px-2 py-1.5 transition-colors"
              @click="schedulingId = null"
            >
              Cancelar
            </button>
          </div>

          <!-- Action buttons row -->
          <div v-else class="flex flex-wrap gap-2">
            <!-- ACCEPTED -->
            <template v-if="item.status === ConsultationStatus.ACCEPTED">
              <button
                class="font-body text-sm font-semibold px-4 py-1.5 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white shadow-glow-btn hover:opacity-90 transition-opacity"
                @click="openScheduler(item.id)"
              >
                Programar
              </button>
              <button
                class="font-body text-sm font-semibold px-4 py-1.5 rounded-btn bg-surface-raised border border-border-default text-text-secondary hover:border-brand-primary/20 transition-colors"
                @click="goToChat(item)"
              >
                Ir al chat
              </button>
              <button
                class="font-body text-sm font-semibold px-4 py-1.5 rounded-btn border border-status-error/40 text-status-error hover:bg-status-error/10 transition-colors"
                @click="cancelConsultation(item)"
              >
                Cancelar
              </button>
            </template>

            <!-- SCHEDULED -->
            <template v-else-if="item.status === ConsultationStatus.SCHEDULED">
              <button
                class="font-body text-sm font-semibold px-4 py-1.5 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white shadow-glow-btn hover:opacity-90 transition-opacity"
                @click="startConsultation(item)"
              >
                Iniciar
              </button>
              <button
                class="font-body text-sm font-semibold px-4 py-1.5 rounded-btn bg-surface-raised border border-border-default text-text-secondary hover:border-brand-primary/20 transition-colors"
                @click="goToChat(item)"
              >
                Ir al chat
              </button>
              <button
                class="font-body text-sm font-semibold px-4 py-1.5 rounded-btn border border-status-error/40 text-status-error hover:bg-status-error/10 transition-colors"
                @click="cancelConsultation(item)"
              >
                Cancelar
              </button>
            </template>

            <!-- IN_PROGRESS -->
            <template v-else-if="item.status === ConsultationStatus.IN_PROGRESS">
              <button
                class="font-body text-sm font-semibold px-4 py-1.5 rounded-btn bg-surface-raised border border-border-default text-text-secondary hover:border-brand-primary/20 transition-colors"
                @click="completeConsultation(item)"
              >
                Completar
              </button>
              <button
                class="font-body text-sm font-semibold px-4 py-1.5 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white shadow-glow-btn hover:opacity-90 transition-opacity"
                @click="goToChat(item)"
              >
                Ir al chat
              </button>
            </template>
          </div>
        </div>

        <!-- HISTORY — read-only, no buttons -->
      </div>
    </div>
  </div>
</template>
