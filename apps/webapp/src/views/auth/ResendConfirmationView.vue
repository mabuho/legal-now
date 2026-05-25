<template>
  <div class="min-h-screen flex items-center justify-center bg-surface-base px-4">
    <div class="w-full max-w-md p-8 rounded-card shadow-2xl bg-surface-card border border-border-default">
      <div class="flex flex-col items-center text-center mb-6">
        <div class="mb-4 p-4 rounded-full bg-brand-primary/10">
          <EnvelopeIcon class="w-10 h-10 text-brand-primary" />
        </div>
        <h2 class="text-2xl font-heading font-bold text-text-primary mb-2">
          Reenviar confirmación
        </h2>
        <p class="font-body text-sm text-text-secondary">
          Ingresa tu correo y te enviaremos un nuevo enlace de activación.
        </p>
      </div>

      <template v-if="!sent">
        <p v-if="resendError" role="alert"
          class="mb-4 px-4 py-3 rounded-btn bg-status-error/10 border border-status-error/30 text-status-error text-sm font-body">
          {{ resendError }}
        </p>

        <form @submit.prevent="submit" class="space-y-5">
          <div>
            <label for="email" class="block mb-1 font-body text-sm text-text-secondary">
              Correo electrónico
            </label>
            <input
              id="email"
              v-model="email"
              type="email"
              placeholder="tu@correo.com"
              required
              class="w-full px-4 py-3 rounded-input border border-border-default bg-surface-raised text-text-primary placeholder-text-muted focus:outline-none focus:ring-2 focus:ring-brand-primary/50 focus:border-brand-primary/60 transition-colors"
            />
          </div>

          <button
            type="submit"
            :disabled="sending"
            class="w-full py-3 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold shadow-glow-btn hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ sending ? 'Enviando...' : 'Enviar enlace' }}
          </button>
        </form>
      </template>

      <template v-else>
        <div class="text-center py-4">
          <div class="mb-4 p-3 rounded-full bg-green-500/10 inline-flex">
            <CheckCircleIcon class="w-8 h-8 text-green-400" />
          </div>
          <p class="font-body text-sm text-text-secondary">
            Enlace enviado a <span class="font-semibold text-text-primary">{{ email }}</span>.
            Revisa tu bandeja de entrada.
          </p>
        </div>
      </template>

      <p class="mt-6 text-center">
        <router-link to="/login" class="font-body text-sm text-text-muted hover:text-text-secondary transition-colors">
          Volver al inicio de sesión
        </router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { EnvelopeIcon, CheckCircleIcon } from '@heroicons/vue/24/outline'
import { useAuthStore } from '@/stores/auth'
import { apiPost } from '@/services/apiClient'

const auth = useAuthStore()
const email = ref(auth.user?.email ?? '')
const sending = ref(false)
const sent = ref(false)
const resendError = ref<string | null>(null)

async function submit() {
  sending.value = true
  resendError.value = null
  const { error } = await apiPost('/auth/resend-confirmation', { email: email.value }, { auth: false })
  if (error) resendError.value = error.message
  else sent.value = true
  sending.value = false
}
</script>
