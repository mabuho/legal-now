<template>
  <div
    class="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-950 via-slate-950 to-black px-4">
    <div class="w-full max-w-md p-8 rounded-2xl shadow-2xl bg-slate-900 border border-slate-800">
      <h2
        class="text-3xl font-semibold text-center font-montserrat bg-gradient-to-r from-cyan-400 to-blue-500 bg-clip-text text-transparent mb-8">
        Iniciar sesión</h2>
      <form @submit.prevent="handleLogin" novalidate class="space-y-6">
        <div>
          <input v-model="formData.email" type="email" id="email"
            :class="['w-full px-4 py-3 rounded-xl border border-slate-700 bg-slate-800 text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-cyan-500', errors.email && 'border-red-500']"
            placeholder="Correo electrónico" aria-required="true" :aria-invalid="errors.email ? true : false"
            aria-describedby="email-error" />
          <label for="email" class="block mt-2 text-sm text-slate-400 font-manrope">Correo electrónico</label>
          <p v-if="errors.email" id="email-error" role="alert" class="text-red-500 text-xs mt-1">{{ errors.email }}</p>
        </div>
        <div>
          <div class="relative">
            <input v-model="formData.password" :type="showPassword ? 'text' : 'password'" id="password"
              :class="['w-full px-4 py-3 rounded-xl border border-slate-700 bg-slate-800 text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-cyan-500', errors.password && 'border-red-500']"
              placeholder="Contraseña" aria-required="true" :aria-invalid="errors.password ? true : false"
              aria-describedby="password-error" />
            <button type="button" @click="showPassword = !showPassword" aria-label="Toggle password visibility"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-cyan-400">
              <EyeIcon v-if="showPassword" />
              <EyeSlashIcon v-else />
            </button>
          </div>
          <label for="password" class="block mt-2 text-sm text-slate-400 font-manrope">Contraseña</label>
          <p v-if="errors.password" id="password-error" role="alert" class="text-red-500 text-xs mt-1">{{
            errors.password }}</p>
        </div>
        <div class="flex items-center justify-between mt-4">
          <a href="#" class="text-cyan-400 hover:underline text-sm font-manrope">¿Olvidaste tu contraseña?</a>
        </div>
        <button type="submit" :disabled="isLoading"
          class="w-full py-3 rounded-xl bg-cyan-600 hover:bg-cyan-500 transition-colors text-white font-semibold font-manrope text-lg mt-6 shadow-lg disabled:opacity-50 disabled:cursor-not-allowed">
          {{ isLoading ? 'Iniciando sesión...' : 'Iniciar sesión' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { EyeIcon, EyeSlashIcon } from '@heroicons/vue/24/outline'

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

<style scoped>
/* Remove legacy .input class and use Tailwind for all styling */
</style>
