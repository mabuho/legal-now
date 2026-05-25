<template>
  <Header />
  <div class="min-h-screen flex items-center justify-center bg-surface-base px-4">
    <div class="w-full max-w-md p-8 rounded-card shadow-2xl bg-surface-card border border-border-default">
      <h2 class="text-2xl font-heading font-bold text-center text-text-primary mb-8">
        Iniciar sesión
      </h2>

      <form @submit.prevent="handleLogin" novalidate class="space-y-5">
        <div>
          <label for="email" class="block mb-1 font-body text-sm text-text-secondary">
            Correo electrónico
          </label>
          <input v-model="formData.email" type="email" id="email" placeholder="tu@correo.com" aria-required="true"
            :aria-invalid="errors.email ? true : false" aria-describedby="email-error" :class="[
              'w-full px-4 py-3 rounded-input border bg-surface-raised text-text-primary placeholder-text-muted focus:outline-none focus:ring-2 focus:ring-brand-primary/50 focus:border-brand-primary/60 transition-colors',
              errors.email ? 'border-status-error' : 'border-border-default',
            ]" />
          <p v-if="errors.email" id="email-error" role="alert" class="text-status-error text-xs mt-1">
            {{ errors.email }}
          </p>
        </div>

        <div>
          <label for="password" class="block mb-1 font-body text-sm text-text-secondary">
            Contraseña
          </label>
          <div class="relative">
            <input v-model="formData.password" :type="showPassword ? 'text' : 'password'" id="password"
              placeholder="••••••••" aria-required="true" :aria-invalid="errors.password ? true : false"
              aria-describedby="password-error" :class="[
                'w-full px-4 py-3 rounded-input border bg-surface-raised text-text-primary placeholder-text-muted focus:outline-none focus:ring-2 focus:ring-brand-primary/50 focus:border-brand-primary/60 transition-colors',
                errors.password ? 'border-status-error' : 'border-border-default',
              ]" />
            <button type="button" @click="showPassword = !showPassword" aria-label="Mostrar u ocultar contraseña"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-text-muted hover:text-text-secondary transition-colors">
              <EyeIcon v-if="showPassword" class="w-5 h-5" />
              <EyeSlashIcon v-else class="w-5 h-5" />
            </button>
          </div>
          <p v-if="errors.password" id="password-error" role="alert" class="text-status-error text-xs mt-1">
            {{ errors.password }}
          </p>
        </div>

        <div class="flex justify-end">
          <a href="#" class="font-body text-sm text-brand-primary hover:text-brand-primary/80 transition-colors">
            ¿Olvidaste tu contraseña?
          </a>
        </div>

        <button type="submit" :disabled="isLoading"
          class="w-full py-3 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold shadow-glow-btn hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed">
          {{ isLoading ? 'Iniciando sesión...' : 'Iniciar sesión' }}
        </button>
      </form>

      <p class="mt-6 text-center text-sm font-body text-text-muted">
        ¿No tienes cuenta?
        <router-link to="/register" class="text-brand-primary hover:text-brand-primary/80 font-semibold">
          Regístrate
        </router-link>
      </p>
    </div>
  </div>
  <Footer />
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { EyeIcon, EyeSlashIcon } from '@heroicons/vue/24/outline'
import Footer from '@/components/Footer.vue'
import Header from '@/components/Header.vue'

const router = useRouter()
const auth = useAuthStore()

const isLoading = ref(false)
const showPassword = ref(false)

const formData = reactive({
  email: '',
  password: '',
})

const errors = reactive({
  email: '',
  password: '',
})

const validateForm = () => {
  let isValid = true
  errors.email = ''
  errors.password = ''
  if (!formData.email) {
    errors.email = 'El correo electrónico es requerido'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    errors.email = 'Ingrese un correo electrónico válido'
    isValid = false
  }
  if (!formData.password) {
    errors.password = 'La contraseña es requerida'
    isValid = false
  }
  return isValid
}

const handleLogin = async () => {
  if (!validateForm()) return
  isLoading.value = true
  try {
    await auth.login(formData.email, formData.password)
    if (auth.user) {
      router.push(auth.user.role === 'lawyer' ? '/dashboard/lawyer' : '/dashboard/client')
    }
  } catch (err: unknown) {
    errors.email = auth.error ?? (err instanceof Error ? err.message : 'Error al iniciar sesión.')
  } finally {
    isLoading.value = false
  }
}
</script>
