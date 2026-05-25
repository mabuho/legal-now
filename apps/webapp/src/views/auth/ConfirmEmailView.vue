<template>
  <div class="min-h-screen flex items-center justify-center bg-surface-base px-4">
    <div class="w-full max-w-md p-8 rounded-card shadow-2xl bg-surface-card border border-border-default">
      <div class="flex flex-col items-center text-center">

        <!-- Loading -->
        <div v-if="status === 'loading'" class="py-8">
          <div class="w-12 h-12 rounded-full border-4 border-brand-primary/20 border-t-brand-primary animate-spin mx-auto mb-4"></div>
          <p class="font-body text-sm text-text-muted">Verificando tu correo...</p>
        </div>

        <!-- Success -->
        <template v-else-if="status === 'success'">
          <div class="mb-6 p-4 rounded-full bg-green-500/10">
            <CheckCircleIcon class="w-12 h-12 text-green-400" />
          </div>
          <h2 class="text-2xl font-heading font-bold text-text-primary mb-3">
            Correo confirmado
          </h2>
          <p class="font-body text-sm text-text-secondary mb-8">
            Tu cuenta ha sido activada correctamente. Ya puedes acceder a tu dashboard.
          </p>
          <button
            type="button"
            @click="goToDashboard"
            class="w-full py-3 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold shadow-glow-btn hover:opacity-90 transition-opacity"
          >
            Ir al dashboard
          </button>
        </template>

        <!-- Error -->
        <template v-else>
          <div class="mb-6 p-4 rounded-full bg-status-error/10">
            <XCircleIcon class="w-12 h-12 text-status-error" />
          </div>
          <h2 class="text-2xl font-heading font-bold text-text-primary mb-3">
            Error de verificación
          </h2>
          <p class="font-body text-sm text-text-secondary mb-8">
            {{ errorMsg }}
          </p>
          <router-link
            to="/resend-confirmation"
            class="w-full inline-block text-center py-3 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold shadow-glow-btn hover:opacity-90 transition-opacity"
          >
            Reenviar confirmación
          </router-link>
        </template>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CheckCircleIcon, XCircleIcon } from '@heroicons/vue/24/outline'
import { useAuthStore } from '@/stores/auth'
import { apiPost } from '@/services/apiClient'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const status = ref<'loading' | 'success' | 'error'>('loading')
const errorMsg = ref('')

onMounted(async () => {
  const token = route.query.token as string | undefined
  if (!token) {
    status.value = 'error'
    errorMsg.value = 'Token inválido o expirado.'
    return
  }
  const { error } = await apiPost('/auth/confirm-email', { token }, { auth: false })
  if (error) {
    status.value = 'error'
    errorMsg.value = error.message
    return
  }
  if (auth.accessToken) {
    await auth.fetchMe().catch(() => undefined)
  }
  status.value = 'success'
})

function goToDashboard() {
  const role = auth.user?.role
  router.push(role === 'lawyer' ? '/dashboard/lawyer' : '/dashboard/client')
}
</script>
