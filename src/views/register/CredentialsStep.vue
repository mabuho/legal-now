<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { EyeIcon, EyeSlashIcon, ChevronLeftIcon, ChevronRightIcon } from '@heroicons/vue/24/outline'
import NavigationButtons from './NavigationButtons.vue'

const props = defineProps<{
  email: string
  password: string
  confirmPassword: string
}>()

const emit = defineEmits<{
  'update:email': [value: string]
  'update:password': [value: string]
  'update:confirmPassword': [value: string]
  'valid': []
  'prev': []
}>()

const showPassword = ref(false)
const showConfirmPassword = ref(false)

const email = ref(props.email)
const password = ref(props.password)
const confirmPassword = ref(props.confirmPassword)

// Validation states
const errors = ref({
  email: '',
  password: '',
  confirmPassword: ''
})

// Password requirements
const passwordRequirements = [
  { regex: /.{8,}/, text: 'Mínimo 8 caracteres' },
  { regex: /[A-Z]/, text: 'Al menos una mayúscula' },
  { regex: /[a-z]/, text: 'Al menos una minúscula' },
  { regex: /[0-9]/, text: 'Al menos un número' },
  { regex: /[!@#$%^&*]/, text: 'Al menos un carácter especial (!@#$%^&*)' }
]

// Computed properties for validation
const isEmailValid = computed(() => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email.value)
})

const passwordStrength = computed(() => {
  return passwordRequirements.reduce((count, req) => {
    return count + (req.regex.test(password.value) ? 1 : 0)
  }, 0)
})

const passwordRequirementsMet = computed(() => {
  return passwordRequirements.map(req => ({
    ...req,
    met: req.regex.test(password.value)
  }))
})

const isPasswordValid = computed(() => {
  return passwordStrength.value === passwordRequirements.length
})

const isConfirmPasswordValid = computed(() => {
  return password.value === confirmPassword.value && password.value !== ''
})

const isFormValid = computed(() => {
  return isEmailValid.value && isPasswordValid.value && isConfirmPasswordValid.value
})

// Watchers for validation
watch(email, (newValue) => {
  emit('update:email', newValue)
  errors.value.email = isEmailValid.value ? '' : 'Ingrese un correo electrónico válido'
})

watch(password, (newValue) => {
  emit('update:password', newValue)
  errors.value.password = isPasswordValid.value ? '' : 'La contraseña no cumple con los requisitos'
})

watch(confirmPassword, (newValue) => {
  emit('update:confirmPassword', newValue)
  errors.value.confirmPassword = isConfirmPasswordValid.value ? '' : 'Las contraseñas no coinciden'
})
</script>

<template>
  <div class="space-y-6">
    <h2 class="text-2xl font-bold text-center bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent">
      Configura tus credenciales
    </h2>

    <div class="space-y-4">
      <!-- Email Field -->
      <div>
        <label for="email" class="block text-sm font-medium text-gray-700 dark:text-gray-300">
          Correo Electrónico
        </label>
        <div class="mt-1">
          <input
            id="email"
            type="email"
            v-model="email"
            :class="[
              'block w-full px-4 py-3 rounded-xl border bg-white dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200',
              errors.email
                ? 'border-red-300 dark:border-red-700'
                : 'border-gray-300 dark:border-gray-700'
            ]"
            placeholder="tu@email.com"
          />
          <p v-if="errors.email" class="mt-2 text-sm text-red-600 dark:text-red-400">
            {{ errors.email }}
          </p>
        </div>
      </div>

      <!-- Password Field -->
      <div>
        <label for="password" class="block text-sm font-medium text-gray-700 dark:text-gray-300">
          Contraseña
        </label>
        <div class="mt-1 relative">
          <input
            id="password"
            :type="showPassword ? 'text' : 'password'"
            v-model="password"
            :class="[
              'block w-full px-4 py-3 rounded-xl border bg-white dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200',
              errors.password
                ? 'border-red-300 dark:border-red-700'
                : 'border-gray-300 dark:border-gray-700'
            ]"
            placeholder="••••••••"
          />
          <button
            type="button"
            class="absolute inset-y-0 right-0 pr-3 flex items-center"
            @click="showPassword = !showPassword"
          >
            <EyeIcon v-if="!showPassword" class="h-5 w-5 text-gray-400" />
            <EyeSlashIcon v-else class="h-5 w-5 text-gray-400" />
          </button>
        </div>

        <!-- Password Requirements -->
        <div class="mt-4 space-y-2">
          <h4 class="text-sm font-medium text-gray-700 dark:text-gray-300">
            Requisitos de la contraseña:
          </h4>
          <div class="space-y-1">
            <div
              v-for="(req, index) in passwordRequirementsMet"
              :key="index"
              class="flex items-center space-x-2"
            >
              <div
                :class="[
                  'w-2 h-2 rounded-full transition-colors duration-200',
                  req.met ? 'bg-green-500' : 'bg-gray-300 dark:bg-gray-700'
                ]"
              ></div>
              <span
                :class="[
                  'text-sm',
                  req.met
                    ? 'text-green-600 dark:text-green-400'
                    : 'text-gray-500 dark:text-gray-400'
                ]"
              >
                {{ req.text }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Confirm Password Field -->
      <div>
        <label for="confirmPassword" class="block text-sm font-medium text-gray-700 dark:text-gray-300">
          Confirmar Contraseña
        </label>
        <div class="mt-1 relative">
          <input
            id="confirmPassword"
            :type="showConfirmPassword ? 'text' : 'password'"
            v-model="confirmPassword"
            :class="[
              'block w-full px-4 py-3 rounded-xl border bg-white dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200',
              errors.confirmPassword
                ? 'border-red-300 dark:border-red-700'
                : 'border-gray-300 dark:border-gray-700'
            ]"
            placeholder="••••••••"
          />
          <button
            type="button"
            class="absolute inset-y-0 right-0 pr-3 flex items-center"
            @click="showConfirmPassword = !showConfirmPassword"
          >
            <EyeIcon v-if="!showConfirmPassword" class="h-5 w-5 text-gray-400" />
            <EyeSlashIcon v-else class="h-5 w-5 text-gray-400" />
          </button>
        </div>
        <p v-if="errors.confirmPassword" class="mt-2 text-sm text-red-600 dark:text-red-400">
          {{ errors.confirmPassword }}
        </p>
      </div>
    </div>

    <!-- Navigation Buttons -->
    <NavigationButtons
      :is-valid="isFormValid"
      :show-next="true"
      :show-previous="true"
      next-text="Siguiente"
      @next="$emit('valid')"
      @previous="$emit('prev')"
    />
  </div>
</template> 