<script setup lang="ts">
import { computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { type Consultation, ConsultationStatus } from '@/types/chat';

const authStore = useAuthStore();
const currentUser = authStore.user;

const props = defineProps<{
  consultation: Consultation;
  onAceptar?: () => void;  // Accept consultation / join room
  onRechazar?: () => void; // Reject consultation / notify client
}>();

/** The party visible to the current user (counterpart). */
const counterpartName = computed<string>(() => {
  if (currentUser?.role === 'lawyer') return props.consultation.client.name;
  return props.consultation.lawyer.name;
});

/** Show action buttons only when consultation is pending (lawyer deciding). */
const showActions = computed<boolean>(() =>
  props.consultation.status === ConsultationStatus.PENDING,
);
</script>

<template>
  <div class="border rounded-xl shadow p-4 mb-4">
    <p><strong>{{ currentUser?.role === 'lawyer' ? 'Cliente' : 'Abogado' }}:</strong> {{ counterpartName }}</p>
    <h3 class="text-lg font-bold"><strong>Titulo:</strong> {{ consultation.title }}</h3>
    <p class="text-gray-600">
      <strong>Descripcion:</strong>
      <!-- TODO: Phase 5 — replace with payment.status check from /payments endpoint -->
      {{ consultation.description ?? '—' }}
    </p>
    <p class="text-xs text-gray-400 mt-1">
      Estado: {{ consultation.status }}
    </p>

    <div v-if="showActions" class="flex gap-2 mt-4">
      <button @click="onAceptar" class="bg-status-active/20 text-status-active px-3 py-1 rounded">
        Aceptar
      </button>
      <button @click="onRechazar" class="bg-status-error/20 text-status-error px-3 py-1 rounded">
        Rechazar
      </button>
    </div>
  </div>
</template>
