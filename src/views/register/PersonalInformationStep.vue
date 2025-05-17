<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { ChevronRightIcon, ChevronLeftIcon } from '@heroicons/vue/24/outline'
import NavigationButtons from './NavigationButtons.vue'

const props = defineProps<{
  firstName: string
  lastName: string
  phone: string
  isFinalStep: boolean
}>()

const emit = defineEmits<{
  'update:firstName': [value: string]
  'update:lastName': [value: string]
  'update:phone': [value: string]
  'valid': []
  'prev': []
}>()

const firstName = ref(props.firstName)
const lastName = ref(props.lastName)
const phone = ref(props.phone)

// Validation states
const errors = ref({
  firstName: '',
  lastName: '',
  phone: ''
})

// Computed properties for validation
const isFirstNameValid = computed(() => {
  return firstName.value.length >= 2
})

const isLastNameValid = computed(() => {
  return lastName.value.length >= 2
})

const formatPhoneNumber = (value: string) => {
  // Remove all non-digits
  const digits = value.replace(/\D/g, '').slice(0, 10)
  
  // Add hyphens
  const parts = []
  if (digits.length > 0) parts.push(digits.slice(0, 2))
  if (digits.length > 2) parts.push(digits.slice(2, 6))
  if (digits.length > 6) parts.push(digits.slice(6))
  
  return parts.join('-')
}

const handlePhoneInput = (e: Event) => {
  const input = e.target as HTMLInputElement
  const formatted = formatPhoneNumber(input.value)
    phone.value = formatted
}

const isPhoneValid = computed(() => {
  const cleaned = phone.value.replace(/\D/g, '')
  return cleaned.length === 10
})

const isFormValid = computed(() => {
  return isFirstNameValid.value && isLastNameValid.value && isPhoneValid.value
})

// Watchers for validation
watch(firstName, (newValue) => {
  emit('update:firstName', newValue)
  errors.value.firstName = isFirstNameValid.value ? '' : 'El nombre es requerido (mínimo 2 caracteres)'
})

watch(lastName, (newValue) => {
  emit('update:lastName', newValue)
  errors.value.lastName = isLastNameValid.value ? '' : 'Los apellidos son requeridos (mínimo 2 caracteres)'
})

watch(phone, (newValue) => {
  emit('update:phone', newValue)
  errors.value.phone = isPhoneValid.value ? '' : 'Ingrese un número de teléfono válido (10 dígitos)'
})
</script>

<template>
  <div class="space-y-6">
    <h2 class="text-2xl font-bold text-center bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent">
      Información Personal
    </h2>

    <div class="space-y-4">
      <!-- First Name Field -->
      <div>
        <label for="firstName" class="block text-sm font-medium text-gray-700 dark:text-gray-300">
          Nombre(s)
        </label>
        <div class="mt-1">
          <input
            id="firstName"
            type="text"
            v-model="firstName"
            :class="[
              'block w-full px-4 py-3 rounded-xl border bg-white dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200',
              errors.firstName
                ? 'border-red-300 dark:border-red-700'
                : 'border-gray-300 dark:border-gray-700'
            ]"
            placeholder="Juan"
          />
          <p v-if="errors.firstName" class="mt-2 text-sm text-red-600 dark:text-red-400">
            {{ errors.firstName }}
          </p>
        </div>
      </div>

      <!-- Last Name Field -->
      <div>
        <label for="lastName" class="block text-sm font-medium text-gray-700 dark:text-gray-300">
          Apellidos
        </label>
        <div class="mt-1">
          <input
            id="lastName"
            type="text"
            v-model="lastName"
            :class="[
              'block w-full px-4 py-3 rounded-xl border bg-white dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200',
              errors.lastName
                ? 'border-red-300 dark:border-red-700'
                : 'border-gray-300 dark:border-gray-700'
            ]"
            placeholder="Pérez González"
          />
          <p v-if="errors.lastName" class="mt-2 text-sm text-red-600 dark:text-red-400">
            {{ errors.lastName }}
          </p>
        </div>
      </div>

      <!-- Phone Field -->
      <div>
        <label for="phone" class="block text-sm font-medium text-gray-700 dark:text-gray-300">
          Teléfono
        </label>
        <div class="mt-1">
          <input
            id="phone"
            type="tel"
            v-model="phone"
            @input="handlePhoneInput"
            :class="[
              'block w-full px-4 py-3 rounded-xl border bg-white dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200',
              errors.phone
                ? 'border-red-300 dark:border-red-700'
                : 'border-gray-300 dark:border-gray-700'
            ]"
            placeholder="55-1234-5678"
            maxlength="12"
          />
          <p v-if="errors.phone" class="mt-2 text-sm text-red-600 dark:text-red-400">
            {{ errors.phone }}
          </p>
        </div>
      </div>
    </div>

    <!-- Navigation Buttons -->
    <NavigationButtons
      :is-valid="isFormValid"
      :show-next="true"
      :show-previous="true"
      :next-text="isFinalStep ? 'Registrarse' : 'Siguiente'"
      @next="$emit('valid')"
      @previous="$emit('prev')"
    />
  </div>
</template> 