<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth';
import { useConsultationStore } from '@/stores/consultationStore';
import {
  type Consultation,
  ConsultationStatus
} from '@/types/chat';

const auth = useAuthStore()
const cons = useConsultationStore()

const emit = defineEmits<{
    (e: 'consultaPagada', payload: Consultation): void
}>()

const props = defineProps<{
    consultation: Consultation | undefined
}>()

const loading = ref(false)
const pagoRealizado = ref(false)
const consultation = ref(props.consultation)

onMounted(async () => { })

const iniciarPago = async () => {
  console.log('[CONSULT_PAYMENT] Simulando pago....!!')
  loading.value = true

  const tiempo = Math.floor(Math.random() * 5000) + 5000 // 5 a 10 segundos
  await new Promise((r) => setTimeout(r, tiempo))

  loading.value = false
  pagoRealizado.value = true

  // TODO: Phase 5 — real payment flow: POST /payments, then backend advances
  // consultation.status via payments table. For now, transition to ACCEPTED as placeholder.
  if (consultation.value && consultation.value.status === ConsultationStatus.PENDING) {
    await cons.transition(consultation.value.id, ConsultationStatus.ACCEPTED)
  }

  // Refresh consultations and find the updated record
  await cons.fetchMine({})
  const consultationFound: Consultation | undefined = cons.consultations
    .find((c) => c.id === consultation.value?.id)
  console.log('[CONSULT_PAYMENT] consultationFound:', consultationFound)

  if (consultationFound) {
    emit('consultaPagada', consultationFound)
  }
}

</script>

<template>
  <div>
    <button
      v-if="!loading && !pagoRealizado"
      @click="iniciarPago()"
      type="button"
    >
      Pagar ahora
    </button>

    <p v-if="loading" class="text-sm text-gray-500">Pagando consulta...</p>
    <p v-if="pagoRealizado" class="text-green-600 font-semibold">
      ¡Pago realizado!
    </p>
  </div>
</template>
