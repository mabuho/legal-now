<script setup lang="ts">
import { onMounted } from 'vue';
import { useConsultationStore } from '@/stores/consultationStore';
import { useAuthStore } from '@/stores/auth';
import PendingConsultations from './PendingConsultations.vue';
import { ConsultationStatus } from '@/types/chat';

const auth = useAuthStore()
const cons = useConsultationStore()

// Active lawyer consultations are those the lawyer has acted on or is working through.
// PAID is gone — use ACCEPTED, SCHEDULED, IN_PROGRESS as the active lifecycle phases.
const activeStatuses = [
  ConsultationStatus.ACCEPTED,
  ConsultationStatus.SCHEDULED,
  ConsultationStatus.IN_PROGRESS,
]

onMounted(async () => {
  if (auth.user) {
    // Fetch all consultations for this lawyer; filter client-side by active statuses.
    // TODO: Phase 6 — replace fetchMine polling with WebSocket subscription.
    await cons.fetchMine({ role: 'lawyer' })
  }
})

</script>

<template>
  <div>
      <PendingConsultations />
  </div>
</template>
