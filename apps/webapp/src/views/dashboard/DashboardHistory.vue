<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useConsultationStore } from '@/stores/consultationStore'
import { useAuthStore } from '@/stores/auth'
import { ConsultationStatus } from '@/types/chat'

const auth = useAuthStore()
const cons = useConsultationStore()

const activeFilter = ref<string>('all')

const ALL_FILTERS = [
  { key: 'all', label: 'Todas' },
  { key: 'active', label: 'Activas', statuses: [ConsultationStatus.ACCEPTED, ConsultationStatus.SCHEDULED, ConsultationStatus.IN_PROGRESS] },
  { key: 'pending', label: 'Pend. autorización', statuses: [ConsultationStatus.PENDING] },
  { key: 'completed', label: 'Completadas', statuses: [ConsultationStatus.COMPLETED] },
  { key: 'cancelled', label: 'Canceladas', statuses: [ConsultationStatus.REJECTED, ConsultationStatus.CANCELLED] },
]

const filteredList = computed(() => {
  if (activeFilter.value === 'all') return cons.consultations
  const filter = ALL_FILTERS.find(f => f.key === activeFilter.value)
  if (!filter || !filter.statuses) return cons.consultations
  return cons.consultations.filter(c => filter.statuses!.includes(c.status))
})

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

onMounted(async () => {
  if (auth.user) {
    const role = auth.user.role === 'lawyer' ? 'lawyer' : 'client'
    await cons.fetchMine({ role, size: 100 })
  }
})
</script>

<template>
  <div class="min-h-screen bg-surface-base p-6">
    <div class="max-w-3xl mx-auto">
      <h1 class="font-heading font-bold text-xl text-text-primary mb-6">Historial</h1>

      <!-- Filter chips -->
      <div class="flex gap-2 flex-wrap mb-6">
        <button
          v-for="f in ALL_FILTERS"
          :key="f.key"
          @click="activeFilter = f.key"
          :class="['font-body text-xs font-semibold px-3 py-1.5 rounded-tag transition-colors border',
            activeFilter === f.key
              ? 'bg-brand-primary/10 text-brand-primary border-brand-primary/20'
              : 'text-text-muted border-border-default hover:text-text-secondary hover:border-border-subtle']"
        >
          {{ f.label }}
        </button>
      </div>

      <!-- List -->
      <div class="flex flex-col gap-3">
        <div
          v-for="c in filteredList"
          :key="c.id"
          class="bg-surface-card border border-border-default rounded-card p-4 shadow-glow"
        >
          <div class="flex items-start justify-between gap-3 mb-2">
            <div class="min-w-0">
              <p class="font-heading font-semibold text-sm text-text-primary truncate">{{ c.title }}</p>
              <p class="font-body text-xs text-text-muted mt-0.5">{{ c.lawyer.name }} · {{ formatDate(c.created_at) }}</p>
            </div>
            <span :class="['font-body text-xs font-semibold px-2 py-0.5 rounded-tag shrink-0', statusBadgeClass(c.status)]">
              {{ statusLabel(c.status) }}
            </span>
          </div>
          <p v-if="c.description" class="font-body text-xs text-text-muted line-clamp-2">{{ c.description }}</p>
        </div>

        <div v-if="filteredList.length === 0" class="text-center py-12 text-text-muted font-body text-sm">
          Sin consultas en esta categoría
        </div>
      </div>
    </div>
  </div>
</template>
