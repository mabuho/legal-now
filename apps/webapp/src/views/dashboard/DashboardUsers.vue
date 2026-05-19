<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useConsultationStore } from '@/stores/consultationStore'
import { useChatSessionStore } from '@/stores/chatSessionStore'
import AIAssistantChat from '@/components/chat/AIAssistantChat.vue'
import BaseModal from '@/components/BaseModal.vue'
import AlertCard from '@/components/AlertCard.vue'
import PaymentConfirmation from '@/components/PaymentConfirmation.vue'
import { type Consultation, ConsultationStatus } from '@/types/chat'
import { type PaymentMethod, PaymentMethodType, CardType } from '@/types/payment'

const router = useRouter()
const auth = useAuthStore()
const cons = useConsultationStore()
const chat = useChatSessionStore()

// Loading state
const loading = ref(false)

// Tab state
type RequestTab = 'active' | 'completed' | 'rejected'
const selectedTab = ref<RequestTab>('active')
const tabs: { key: RequestTab; label: string }[] = [
  { key: 'active', label: 'En curso' },
  { key: 'completed', label: 'Completadas' },
  { key: 'rejected', label: 'Rechazadas' },
]

const tabStatuses: Record<RequestTab, ConsultationStatus[]> = {
  active: [ConsultationStatus.ACCEPTED, ConsultationStatus.SCHEDULED, ConsultationStatus.IN_PROGRESS],
  completed: [ConsultationStatus.COMPLETED],
  rejected: [ConsultationStatus.REJECTED, ConsultationStatus.CANCELLED],
}

const filteredRequests = computed(() =>
  cons.consultations.filter((c) => tabStatuses[selectedTab.value].includes(c.status)),
)

const activeConsultation = computed<Consultation | null>(() => {
  const activeStatuses = [
    ConsultationStatus.PENDING,
    ConsultationStatus.ACCEPTED,
    ConsultationStatus.SCHEDULED,
    ConsultationStatus.IN_PROGRESS,
  ]
  const candidates = cons.consultations
    .filter((c) => activeStatuses.includes(c.status))
    .slice()
    .sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
  return candidates[0] ?? null
})

// TODO: Phase 5 — load payment methods from API
const paymentMethods = ref<PaymentMethod[]>([
  {
    id: '1',
    type: PaymentMethodType.CREDIT,
    cardNumber: '•••• •••• •••• 4242',
    cardType: CardType.VISA,
    expiryDate: '12/25',
    cardHolder: 'Juan Pérez',
    isDefault: true,
  },
  {
    id: '2',
    type: PaymentMethodType.DEBIT,
    cardNumber: '•••• •••• •••• 5555',
    cardType: CardType.MASTERCARD,
    expiryDate: '09/24',
    cardHolder: 'Juan Pérez',
    isDefault: false,
  },
])

const paymentDetails = computed(() =>
  activeConsultation.value
    ? {
        service: activeConsultation.value.title,
        professional: activeConsultation.value.lawyer.name,
        date: new Date(activeConsultation.value.created_at).toLocaleDateString(),
        time: new Date(activeConsultation.value.created_at).toLocaleTimeString(),
        amount: 0, // TODO: Phase 5 — load from /payments or lawyer.consultation_rate
      }
    : null,
)

const chatInput = ref('')
const paymentModalVisible = ref(false)
const deleteRequestModalVisible = ref(false)

// Prevent background scroll when modals are open
watch([paymentModalVisible, deleteRequestModalVisible], ([payment, deleteReq]) => {
  if (typeof document !== 'undefined') {
    document.body.classList.toggle('overflow-hidden', payment || deleteReq)
    document.documentElement.classList.toggle('overflow-hidden', payment || deleteReq)
  }
})

// Stub messages for AI sidebar — TODO: Phase 6 — replace with real triage service
const stubMessages = ref([
  { id: 1, from: 'assistant', text: 'Hola, soy tu asistente de soporte legal.' },
  {
    id: 2,
    from: 'assistant',
    text: 'Para iniciar una nueva consulta, usa el boton "Nueva consulta". Te conectare con el abogado mas adecuado para tu caso.',
  },
  {
    id: 3,
    from: 'assistant',
    text: 'Si tienes una consulta activa, puedes acceder al chat directamente desde esta pantalla.',
  },
])

// Helpers
function statusLabel(status: ConsultationStatus): string {
  const labels: Record<ConsultationStatus, string> = {
    [ConsultationStatus.PENDING]: 'Pendiente de aceptacion',
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
    case ConsultationStatus.PENDING:
      return 'bg-orange-600 text-white'
    case ConsultationStatus.ACCEPTED:
    case ConsultationStatus.SCHEDULED:
      return 'bg-blue-600 text-white'
    case ConsultationStatus.IN_PROGRESS:
    case ConsultationStatus.COMPLETED:
      return 'bg-green-600 text-white'
    case ConsultationStatus.REJECTED:
    case ConsultationStatus.CANCELLED:
      return 'bg-red-500 text-white'
    default:
      return 'bg-slate-700 text-slate-300'
  }
}

function hasChatSession(consultationId: string): boolean {
  return chat.sessions.some((s) => s.consultation_id === consultationId)
}

function goToChat(consultation: Consultation) {
  // Navigate to chat panel; ChatPanel will select the right session.
  // Future: pass consultation id via query string so ChatPanel pre-selects.
  router.push({ path: '/dashboard/chat', query: { consultation_id: consultation.id } })
}

async function cancelActive() {
  if (!activeConsultation.value) return
  deleteRequestModalVisible.value = false
  await cons.cancel(activeConsultation.value.id)
  await cons.fetchMine({ role: 'client', size: 50 })
}

// Redirect to landing AI flow — no in-dashboard consultation creation
function onNewConsult() {
  router.push('/')
}

// TODO: Phase 6 — wire stub message send to triage service
function sendStubMessage() {
  // no-op for now
  chatInput.value = ''
}

onMounted(async () => {
  if (!auth.user) return
  loading.value = true
  try {
    await Promise.all([cons.fetchMine({ role: 'client', size: 50 }), chat.fetchSessions()])
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="bg-gradient-to-br from-cyan-950 via-blue-950 to-indigo-950 px-4 py-8 md:h-[calc(100vh-4rem)]">
    <div class="max-w-7xl mx-auto flex flex-col lg:grid lg:grid-cols-3 gap-4 lg:gap-8 h-full min-h-0">
      <!-- Main Column -->
      <div class="lg:col-span-2 flex flex-col gap-4 h-full min-h-0 pr-0 lg:pr-2">

        <!-- Loading state -->
        <div v-if="loading" class="flex items-center justify-center py-12">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-cyan-400"></div>
        </div>

        <template v-else>
          <!-- Consulta Activa Card -->
          <div class="bg-slate-900 border border-slate-800 rounded-2xl shadow p-4 md:p-8 md:mb-4 lg:mb-0">
            <h2 class="text-xl font-bold font-montserrat text-slate-100 mb-6 flex items-center gap-2">
              <span class="inline-block">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-cyan-400" fill="none" viewBox="0 0 24 24"
                  stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3" />
                  <circle cx="12" cy="12" r="10" />
                </svg>
              </span>
              Consulta Activa
            </h2>

            <!-- Empty state -->
            <div v-if="!activeConsultation"
              class="bg-slate-800 rounded-xl p-8 flex flex-col items-center justify-center border border-slate-700 gap-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 text-slate-600" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                  d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <p class="text-slate-400 font-manrope text-center">No tienes consultas activas</p>
              <button @click="router.push('/')"
                class="px-5 py-2 rounded-md bg-gradient-to-br from-cyan-600 via-indigo-700 to-blue-900 text-white font-semibold font-manrope transition-colors">
                Iniciar nueva consulta
              </button>
            </div>

            <!-- Active consultation details -->
            <div v-else class="bg-slate-800 rounded-xl p-6 flex flex-col items-center justify-center border border-slate-700">
              <div class="w-full flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                <div>
                  <div class="text-xl md:text-3xl lg:text-3xl font-bold text-slate-100 font-manrope">
                    {{ activeConsultation.title }}
                  </div>
                  <div class="text-sm md:text-base lg:text-xl text-slate-400 font-manrope mb-1">
                    Abogado: {{ activeConsultation.lawyer.name }}
                  </div>
                  <div class="text-sm md:text-base lg:text-xl text-slate-400 font-manrope mb-1">
                    Creada: {{ new Date(activeConsultation.created_at).toLocaleDateString() }}
                  </div>
                </div>
                <div class="text-center md:text-right">
                  <span :class="['px-3 py-1 rounded-full text-xs font-bold font-manrope', statusBadgeClass(activeConsultation.status)]">
                    {{ statusLabel(activeConsultation.status) }}
                  </span>
                  <div class="flex gap-2 justify-center md:justify-end mt-4 flex-wrap">
                    <!-- Ir al chat — only when a session exists and status allows it -->
                    <button
                      v-if="hasChatSession(activeConsultation.id) && [ConsultationStatus.ACCEPTED, ConsultationStatus.SCHEDULED, ConsultationStatus.IN_PROGRESS].includes(activeConsultation.status)"
                      @click="goToChat(activeConsultation)"
                      class="px-5 py-2 rounded-md bg-cyan-600 hover:bg-cyan-500 text-white font-semibold font-manrope transition-colors">
                      Ir al chat
                    </button>
                    <!-- Pagar — disabled, Phase 5 placeholder -->
                    <button disabled
                      title="Proximo (Fase 5)"
                      class="px-5 py-2 rounded-md bg-green-800 text-green-400 font-semibold font-manrope cursor-not-allowed opacity-60">
                      <!-- TODO: Phase 5 — enable when payments API is ready -->
                      Pagar
                    </button>
                    <!-- Cancelar — only for cancellable statuses -->
                    <button
                      v-if="[ConsultationStatus.PENDING, ConsultationStatus.ACCEPTED].includes(activeConsultation.status)"
                      @click="deleteRequestModalVisible = true"
                      class="px-5 py-2 rounded-md border bg-red-600 hover:bg-red-700 text-white font-semibold font-manrope transition-colors">
                      Cancelar
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Historial de consultas Card -->
          <div class="bg-slate-900 border border-slate-800 rounded-2xl shadow p-4 md:p-8 md:mb-4 lg:mb-0">
            <h2 class="text-xl font-bold font-montserrat text-slate-100 mb-6 flex items-center gap-2">
              <span class="inline-block">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-cyan-400" fill="none" viewBox="0 0 24 24"
                  stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3" />
                  <circle cx="12" cy="12" r="10" />
                </svg>
              </span>
              Historial de consultas
            </h2>

            <!-- Tabs -->
            <div class="flex gap-2 mb-6">
              <button v-for="tab in tabs" :key="tab.key" @click="selectedTab = tab.key"
                :class="['px-4 py-2 rounded-full font-manrope text-sm font-semibold transition',
                  selectedTab === tab.key ? 'bg-cyan-600 text-white' : 'bg-slate-800 text-slate-400 hover:bg-slate-700']">
                {{ tab.label }}
              </button>
            </div>

            <!-- Requests list -->
            <div class="flex-col justify-end">
              <div class="flex flex-col gap-4 max-h-req-list overflow-y-auto hide-scrollbar">

                <!-- Empty state per tab -->
                <div v-if="filteredRequests.length === 0"
                  class="text-slate-500 font-manrope text-sm text-center py-8">
                  No hay consultas en esta categoria
                </div>

                <div
                  v-for="request in filteredRequests"
                  :key="request.id"
                  :class="['flex items-center justify-between bg-slate-800 border border-slate-700 rounded-xl p-4 transition',
                    tabStatuses.active.includes(request.status) && hasChatSession(request.id)
                      ? 'cursor-pointer hover:bg-slate-700'
                      : 'cursor-not-allowed']"
                  @click="tabStatuses.active.includes(request.status) && hasChatSession(request.id) ? goToChat(request) : undefined">
                  <div>
                    <div class="text-base font-semibold text-slate-100 font-manrope">{{ request.title }}</div>
                    <div class="text-xs text-slate-400 font-manrope">Abogado: {{ request.lawyer.name }}</div>
                    <div class="text-xs text-slate-400 font-manrope">
                      Fecha: {{ new Date(request.created_at).toLocaleDateString() }}
                    </div>
                  </div>
                  <div class="flex flex-col items-end gap-2">
                    <span :class="['px-3 py-1 rounded-full text-xs font-bold font-manrope', statusBadgeClass(request.status)]">
                      {{ statusLabel(request.status) }}
                    </span>
                    <span class="inline-block">
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-cyan-400" fill="none"
                        viewBox="0 0 24 24" stroke="currentColor">
                        <circle cx="12" cy="12" r="10" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4" />
                      </svg>
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- Sidebar: AI Assistant -->
      <div class="hidden lg:flex flex-col h-full min-h-0 md:mt-4 lg:mt-0">
        <!-- TODO: Phase 6 — wire real AI triage chat here -->
        <AIAssistantChat
          :chatMessages="stubMessages"
          :chatInput="chatInput"
          @update:chatInput="val => chatInput = val"
          @send="sendStubMessage"
          @new-consult="onNewConsult"
        />
      </div>
    </div>
  </div>

  <!-- Payment modal — disabled pending Phase 5 -->
  <BaseModal :visible="paymentModalVisible" @close="paymentModalVisible = false">
    <PaymentConfirmation
      v-if="paymentDetails"
      :paymentDetails="paymentDetails"
      :paymentMethods="paymentMethods"
      @close="paymentModalVisible = false"
    />
  </BaseModal>

  <!-- Cancel consultation confirmation modal -->
  <BaseModal :visible="deleteRequestModalVisible" @close="deleteRequestModalVisible = false">
    <AlertCard
      title="Cancelar consulta"
      message="Esta seguro de que desea cancelar esta consulta?"
      type="error"
      buttonText="Cancelar consulta"
      @close-alert="cancelActive"
    />
  </BaseModal>
</template>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
