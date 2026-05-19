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

// ---------------------------------------------------------------------------
// Tabs
// ---------------------------------------------------------------------------
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

// ---------------------------------------------------------------------------
// Status badge helpers
// ---------------------------------------------------------------------------
interface StatusMeta {
  label: string
  classes: string
}

const STATUS_META: Record<ConsultationStatus, StatusMeta> = {
  [ConsultationStatus.PENDING]: { label: 'Pendiente', classes: 'bg-orange-100 text-orange-700' },
  [ConsultationStatus.ACCEPTED]: { label: 'Aceptada', classes: 'bg-blue-100 text-blue-700' },
  [ConsultationStatus.SCHEDULED]: { label: 'Programada', classes: 'bg-indigo-100 text-indigo-700' },
  [ConsultationStatus.IN_PROGRESS]: { label: 'En curso', classes: 'bg-green-100 text-green-700' },
  [ConsultationStatus.COMPLETED]: { label: 'Completada', classes: 'bg-slate-100 text-slate-700' },
  [ConsultationStatus.REJECTED]: { label: 'Rechazada', classes: 'bg-red-100 text-red-700' },
  [ConsultationStatus.CANCELLED]: { label: 'Cancelada', classes: 'bg-gray-100 text-gray-500' },
}

// ---------------------------------------------------------------------------
// Scheduling (inline date-time picker visibility per consultation id)
// ---------------------------------------------------------------------------
const schedulingId = ref<string | null>(null)
const schedulingDate = ref('')

function openScheduler(id: string) {
  schedulingId.value = id
  schedulingDate.value = ''
}

async function submitSchedule(c: Consultation) {
  if (!schedulingDate.value) return
  // update patches scheduled_at then transition status to SCHEDULED
  await cons.update(c.id, { scheduled_at: new Date(schedulingDate.value).toISOString() })
  await cons.transition(c.id, ConsultationStatus.SCHEDULED)
  schedulingId.value = null
}

// ---------------------------------------------------------------------------
// Actions
// ---------------------------------------------------------------------------
async function goToChat(c: Consultation) {
  if (!chat.sessions.some((s) => s.consultation_id === c.id)) {
    await chat.createSession(c.id)
  }
  router.push({ path: '/dashboard/chat', query: { consultation_id: c.id } })
}

async function acceptConsultation(c: Consultation) {
  await cons.transition(c.id, ConsultationStatus.ACCEPTED)
  // Create chat session on accept so the client can chat immediately.
  // 409 "already exists" is benign — swallow it.
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
  // MVP: no prompt. Future: prompt for reason and pass as third arg.
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
    <h1 class="text-xl font-bold mb-5 text-gray-900 dark:text-white">Mis Consultas</h1>

    <!-- Tab bar -->
    <div class="flex gap-2 mb-6">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="flex items-center gap-1.5 px-4 py-1.5 rounded-full text-sm font-medium transition-colors"
        :class="
          activeTab === tab.key
            ? 'bg-indigo-600 text-white shadow'
            : 'bg-gray-100 text-gray-600 hover:bg-gray-200 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700'
        "
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
        <span
          class="inline-flex items-center justify-center rounded-full px-1.5 py-0.5 text-xs font-semibold"
          :class="activeTab === tab.key ? 'bg-white/20 text-white' : 'bg-gray-300 text-gray-700 dark:bg-gray-600 dark:text-gray-200'"
        >
          {{ listForTab(tab.key).length }}
        </span>
      </button>
    </div>

    <!-- Empty state -->
    <div v-if="listForTab(activeTab).length === 0" class="py-16 text-center">
      <div class="mx-auto h-16 w-16 text-gray-300 dark:text-gray-600 mb-4">
        <ChatBubbleLeftRightIcon class="h-full w-full" />
      </div>
      <p class="text-sm text-gray-500 dark:text-gray-400">{{ emptyMessages[activeTab] }}</p>
    </div>

    <!-- Consultation cards -->
    <div v-else class="space-y-4">
      <div
        v-for="item in listForTab(activeTab)"
        :key="item.id"
        class="border border-gray-200 dark:border-gray-700 rounded-xl shadow-sm bg-white dark:bg-gray-800 p-4"
      >
        <!-- Header row -->
        <div class="flex items-start justify-between gap-2 mb-1">
          <h2 class="text-base font-semibold text-gray-900 dark:text-white leading-snug">
            {{ item.title }}
          </h2>
          <span
            class="shrink-0 rounded-full px-2.5 py-0.5 text-xs font-medium"
            :class="STATUS_META[item.status].classes"
          >
            {{ STATUS_META[item.status].label }}
          </span>
        </div>

        <!-- Details -->
        <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">
          {{ item.description || '—' }}
        </p>
        <p class="text-xs text-gray-500 dark:text-gray-500">
          Cliente: <span class="font-medium">{{ item.client?.name ?? '—' }}</span>
          &nbsp;·&nbsp;
          Fecha: {{ new Date(item.created_at).toLocaleDateString() }}
        </p>

        <!-- Janus room note (non-blocking) -->
        <p v-if="item.janus_room_id === null && activeTab === 'active'" class="text-xs text-gray-400 mt-1">
          <!-- TODO: Phase 3 — Janus room allocation -->
          Sala no asignada
        </p>

        <!-- ---------------------------------------------------------------- -->
        <!-- PENDING actions                                                   -->
        <!-- ---------------------------------------------------------------- -->
        <div v-if="activeTab === 'pending'" class="flex gap-2 mt-4">
          <button
            class="bg-green-600 hover:bg-green-700 text-white text-sm px-4 py-1.5 rounded-lg font-medium transition-colors"
            @click="acceptConsultation(item)"
          >
            Aceptar
          </button>
          <button
            class="bg-red-600 hover:bg-red-700 text-white text-sm px-4 py-1.5 rounded-lg font-medium transition-colors"
            @click="rejectConsultation(item)"
          >
            Rechazar
          </button>
        </div>

        <!-- ---------------------------------------------------------------- -->
        <!-- ACTIVE actions (vary by sub-status)                              -->
        <!-- ---------------------------------------------------------------- -->
        <div v-else-if="activeTab === 'active'" class="mt-4 space-y-3">
          <!-- Inline date-time picker for scheduling -->
          <div
            v-if="schedulingId === item.id"
            class="flex items-center gap-2 flex-wrap"
          >
            <input
              v-model="schedulingDate"
              type="datetime-local"
              class="border border-gray-300 dark:border-gray-600 rounded-lg px-3 py-1.5 text-sm bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
            />
            <button
              class="bg-indigo-600 hover:bg-indigo-700 text-white text-sm px-3 py-1.5 rounded-lg font-medium transition-colors"
              @click="submitSchedule(item)"
            >
              Confirmar
            </button>
            <button
              class="text-sm text-gray-500 hover:text-gray-700 dark:text-gray-400 px-2 py-1.5"
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
                class="bg-indigo-600 hover:bg-indigo-700 text-white text-sm px-4 py-1.5 rounded-lg font-medium transition-colors"
                @click="openScheduler(item.id)"
              >
                Programar
              </button>
              <button
                class="bg-blue-600 hover:bg-blue-700 text-white text-sm px-4 py-1.5 rounded-lg font-medium transition-colors"
                @click="goToChat(item)"
              >
                Ir al chat
              </button>
              <button
                class="bg-gray-200 hover:bg-gray-300 text-gray-700 text-sm px-4 py-1.5 rounded-lg font-medium transition-colors dark:bg-gray-700 dark:text-gray-300 dark:hover:bg-gray-600"
                @click="cancelConsultation(item)"
              >
                Cancelar
              </button>
            </template>

            <!-- SCHEDULED -->
            <template v-else-if="item.status === ConsultationStatus.SCHEDULED">
              <button
                class="bg-green-600 hover:bg-green-700 text-white text-sm px-4 py-1.5 rounded-lg font-medium transition-colors"
                @click="startConsultation(item)"
              >
                Iniciar
              </button>
              <button
                class="bg-blue-600 hover:bg-blue-700 text-white text-sm px-4 py-1.5 rounded-lg font-medium transition-colors"
                @click="goToChat(item)"
              >
                Ir al chat
              </button>
              <button
                class="bg-gray-200 hover:bg-gray-300 text-gray-700 text-sm px-4 py-1.5 rounded-lg font-medium transition-colors dark:bg-gray-700 dark:text-gray-300 dark:hover:bg-gray-600"
                @click="cancelConsultation(item)"
              >
                Cancelar
              </button>
            </template>

            <!-- IN_PROGRESS -->
            <template v-else-if="item.status === ConsultationStatus.IN_PROGRESS">
              <button
                class="bg-slate-600 hover:bg-slate-700 text-white text-sm px-4 py-1.5 rounded-lg font-medium transition-colors"
                @click="completeConsultation(item)"
              >
                Completar
              </button>
              <button
                class="bg-blue-600 hover:bg-blue-700 text-white text-sm px-4 py-1.5 rounded-lg font-medium transition-colors"
                @click="goToChat(item)"
              >
                Ir al chat
              </button>
            </template>
          </div>
        </div>

        <!-- ---------------------------------------------------------------- -->
        <!-- HISTORY — read-only, no buttons                                  -->
        <!-- ---------------------------------------------------------------- -->
      </div>
    </div>
  </div>
</template>
