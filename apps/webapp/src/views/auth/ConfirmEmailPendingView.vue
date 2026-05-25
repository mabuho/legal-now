<template>
  <div class="min-h-screen flex items-center justify-center bg-surface-base px-4">
    <div class="w-full max-w-md p-8 rounded-card shadow-2xl bg-surface-card border border-border-default">
      <div class="flex flex-col items-center text-center">
        <div class="mb-6 p-4 rounded-full bg-brand-primary/10">
          <EnvelopeIcon class="w-12 h-12 text-brand-primary" />
        </div>

        <h2 class="text-2xl font-heading font-bold text-text-primary mb-3">
          Revisa tu correo
        </h2>

        <p class="font-body text-sm text-text-secondary mb-8">
          Enviamos un enlace de confirmación a
          <span class="font-semibold text-text-primary">{{ userEmail }}</span>.
          Haz clic en el enlace para activar tu cuenta.
        </p>

        <p v-if="sent" role="status"
          class="w-full mb-4 px-4 py-3 rounded-btn bg-green-500/10 border border-green-500/20 text-green-400 text-sm font-body">
          Correo reenviado correctamente.
        </p>

        <p v-if="resendError" role="alert"
          class="w-full mb-4 px-4 py-3 rounded-btn bg-status-error/10 border border-status-error/30 text-status-error text-sm font-body">
          {{ resendError }}
        </p>

        <button type="button" :disabled="sending || sent" @click="resend"
          class="w-full py-3 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold shadow-glow-btn hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed mb-4">
          {{ sending ? 'Enviando...' : sent ? 'Correo enviado' : 'Reenviar correo' }}
        </button>

        <button type="button" @click="router.push('/')"
          class="font-body text-sm text-text-muted hover:text-text-secondary transition-colors">
          Volver al inicio
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { EnvelopeIcon } from '@heroicons/vue/24/outline'
import { useAuthStore } from '@/stores/auth'
import { apiPost } from '@/services/apiClient'

const auth = useAuthStore()
const router = useRouter()
const userEmail = auth.user?.email ?? ''
const sending = ref(false)
const sent = ref(false)
const resendError = ref<string | null>(null)

async function resend() {
  sending.value = true
  resendError.value = null
  const { error } = await apiPost('/auth/resend-confirmation', { email: userEmail }, { auth: false })
  if (error) {
    resendError.value = error.message
  } else {
    sent.value = true
  }
  sending.value = false
}
</script>
