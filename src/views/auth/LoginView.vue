<template>
  <div class="max-w-md mx-auto mt-16 p-8 bg-white shadow-md rounded-xl dark:bg-gray-800">
    <h2 class="text-2xl font-bold mb-8 text-center text-gray-900 dark:text-white">
      Iniciar sesión
    </h2>

    <form @submit.prevent="handleLogin" class="space-y-6" novalidate>
      <!-- Email Field -->
      <div class="relative">
        <input
          v-model="formData.email"
          type="email"
          id="email"
          class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-indigo-600 bg-transparent focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 dark:text-white dark:border-gray-600 dark:focus:border-indigo-500"
          :class="{ 'border-red-500 dark:border-red-500': errors.email }"
          placeholder="Correo electrónico"
          aria-required="true"
          :aria-invalid="errors.email ? true : false"
          aria-describedby="email-error"
        />
        <label
          for="email"
          class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm dark:text-gray-400 dark:peer-focus:text-gray-300"
        >
          Correo electrónico
        </label>
        <p
          v-if="errors.email"
          id="email-error"
          class="mt-1 text-sm text-red-500 dark:text-red-400"
          role="alert"
        >
          {{ errors.email }}
        </p>
      </div>

      <!-- Password Field -->
      <div class="relative">
        <div class="relative">
          <input
            v-model="formData.password"
            :type="showPassword ? 'text' : 'password'"
            id="password"
            class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-indigo-600 bg-transparent pr-10 focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 dark:text-white dark:border-gray-600 dark:focus:border-indigo-500"
            :class="{ 'border-red-500 dark:border-red-500': errors.password }"
            placeholder="Contraseña"
            aria-required="true"
            :aria-invalid="errors.password ? true : false"
            aria-describedby="password-error"
          />
          <label
            for="password"
            class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm dark:text-gray-400 dark:peer-focus:text-gray-300"
          >
            Contraseña
          </label>
          <button
            type="button"
            @click="showPassword = !showPassword"
            class="absolute inset-y-0 right-0 flex items-center pr-3"
            aria-label="Toggle password visibility"
          >
            <EyeIcon v-if="showPassword" class="h-5 w-5 text-gray-400 dark:text-gray-500" />
            <EyeSlashIcon v-else class="h-5 w-5 text-gray-400 dark:text-gray-500" />
          </button>
        </div>
        <p
          v-if="errors.password"
          id="password-error"
          class="mt-1 text-sm text-red-500 dark:text-red-400"
          role="alert"
        >
          {{ errors.password }}
        </p>
      </div>

      <!-- Forgot Password Link -->
      <div class="flex justify-end">
        <RouterLink
          to="/forgot-password"
          class="text-sm text-indigo-600 hover:text-indigo-500 dark:text-indigo-400 dark:hover:text-indigo-300"
        >
          ¿Olvidaste tu contraseña?
        </RouterLink>
      </div>

      <!-- Submit Button -->
      <button
        type="submit"
        :disabled="isLoading"
        class="w-full flex justify-center py-3 px-4 border border-transparent rounded-xl shadow-sm text-lg font-medium text-white bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transform transition-all duration-200 hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0"
      >
        <template v-if="isLoading">
          <svg
            class="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              class="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              stroke-width="4"
            ></circle>
            <path
              class="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            ></path>
          </svg>
          Iniciando sesión...
        </template>
        <template v-else> Iniciar sesión </template>
      </button>
    </form>

    <!-- Register Link -->
    <p class="text-sm text-center text-gray-500 mt-8 dark:text-gray-400">
      ¿No tienes cuenta?
      <RouterLink
        to="/register"
        class="font-medium text-indigo-600 hover:text-indigo-500 dark:text-indigo-400 dark:hover:text-indigo-300"
      >
        Regístrate aquí
      </RouterLink>
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { EyeIcon, EyeSlashIcon } from '@heroicons/vue/24/outline'

const router = useRouter()
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

  // Reset errors
  errors.email = ''
  errors.password = ''

  // Email validation
  if (!formData.email) {
    errors.email = 'El correo electrónico es requerido'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    errors.email = 'Ingrese un correo electrónico válido'
    isValid = false
  }

  // Password validation
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
    // TODO: Add your API call here
    await new Promise((resolve) => setTimeout(resolve, 1500)) // Simulated API call

    // Handle successful login
    router.push('/dashboard')
  } catch (error) {
    console.error('Login error:', error)
    // Handle login error
    errors.email = 'Credenciales inválidas'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.input {
  display: block;
  width: 100%;
  margin-bottom: 12px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
}
.btn-green {
  background-color: #1e824c;
  color: white;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
</style>
