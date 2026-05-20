<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useConsultationStore } from '@/stores/consultationStore'
import { useChatSessionStore } from '@/stores/chatSessionStore'
import BaseModal from '@/components/BaseModal.vue'
import AlertCard from '@/components/AlertCard.vue'
import PaymentConfirmation from '@/components/PaymentConfirmation.vue'
import { type Consultation, ConsultationStatus } from '@/types/chat'
import { type PaymentMethod, PaymentMethodType, CardType } from '@/types/payment'

const router = useRouter()
const auth = useAuthStore()
const cons = useConsultationStore()
const chat = useChatSessionStore()

const loading = ref(false)

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

// Master-detail selection
const selectedConsultationId = ref<string | null>(null)
const selectedDetail = computed(() => cons.consultations.find(c => c.id === selectedConsultationId.value) ?? null)

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

async function cancelActive() {
  if (!selectedDetail.value) return
  deleteRequestModalVisible.value = false
  await cons.cancel(selectedDetail.value.id)
  await cons.fetchMine({ role: 'client', size: 50 })
}

function onNewConsult() {
  router.push('/')
}

// TODO: Phase 6 — wire stub message send to triage service
function sendStubMessage() {
  chatInput.value = ''
}

const pendingList = computed(() => cons.consultations.filter(c => c.status === ConsultationStatus.PENDING))
const activeList = computed(() => cons.consultations.filter(c =>
  [ConsultationStatus.ACCEPTED, ConsultationStatus.SCHEDULED, ConsultationStatus.IN_PROGRESS].includes(c.status)
))

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
  <div class="flex h-[calc(100vh-3.5rem)] bg-surface-base overflow-hidden">
    <!-- Loading overlay -->
    <div v-if="loading" class="absolute inset-0 flex items-center justify-center bg-surface-base z-10">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-brand-primary"></div>
    </div>

    <!-- LEFT: consultation list -->
    <div class="w-80 flex-shrink-0 border-r border-border-subtle flex flex-col bg-surface-base overflow-y-auto">
      <div class="px-4 pt-5 pb-3">
        <p class="font-body text-xs font-bold text-text-muted uppercase tracking-widest">Mis consultas</p>
      </div>

      <!-- Pendiente de autorización -->
      <div class="mb-2">
        <p class="font-body text-xs font-bold text-text-muted uppercase tracking-widest px-4 pb-2">
          Pendiente de autorización
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
                <p class="font-body text-xs text-text-muted mt-0.5">{{ c.lawyer.name }} · {{ formatDate(c.created_at) }}</p>
              </div>
              <span :class="['font-body text-xs font-semibold px-2 py-0.5 rounded-tag shrink-0', statusBadgeClass(c.status)]">
                {{ statusLabel(c.status) }}
              </span>
            </div>
          </div>
        </template>
        <p v-else class="font-body text-xs text-text-muted text-center py-3 px-3">Sin consultas</p>
      </div>

      <!-- En Curso -->
      <div class="mb-2">
        <p class="font-body text-xs font-bold text-text-muted uppercase tracking-widest px-4 pb-2">
          En curso
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
                <p class="font-body text-xs text-text-muted mt-0.5">{{ c.lawyer.name }} · {{ formatDate(c.created_at) }}</p>
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
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 3v1m0 16v1M3 12h1m16 0h1M5.636 5.636l.707.707M17.657 17.657l.707.707M5.636 18.364l.707-.707M17.657 6.343l.707-.707M9 12a3 3 0 106 0 3 3 0 00-6 0z" />
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
            <p class="font-body text-sm text-text-muted mt-0.5">{{ selectedDetail.lawyer.name }} · {{ formatDate(selectedDetail.created_at) }}</p>
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
            <button
              v-if="[ConsultationStatus.PENDING, ConsultationStatus.ACCEPTED].includes(selectedDetail.status)"
              @click="deleteRequestModalVisible = true"
              class="font-body text-sm font-semibold px-3 py-1.5 rounded-btn border border-status-error/40 text-status-error hover:bg-status-error/10 transition-colors"
            >
              Cancelar
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
            <p class="font-body text-xs text-text-muted uppercase tracking-widest mb-2">Abogado</p>
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-lg bg-gradient-to-br from-brand-primary-dark to-brand-accent flex items-center justify-center font-heading font-bold text-xs text-white shrink-0">
                {{ selectedDetail.lawyer.name?.[0]?.toUpperCase() }}
              </div>
              <div>
                <p class="font-heading font-semibold text-sm text-text-primary">{{ selectedDetail.lawyer.name }}</p>
              </div>
            </div>
          </div>
        </div>
      </template>
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
      message="¿Estás seguro de que deseas cancelar esta consulta?"
      type="error"
      buttonText="Cancelar consulta"
      @close-alert="cancelActive"
    />
  </BaseModal>
</template>
