<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import {
  AcademicCapIcon,
  BuildingOfficeIcon,
  ScaleIcon,
  BriefcaseIcon,
  HomeModernIcon,
  UserGroupIcon
} from '@heroicons/vue/24/outline'
import NavigationButtons from './NavigationButtons.vue'
import CustomSelect from '@/components/common/CustomSelect.vue'

const props = defineProps<{
  licenseNumber: string
  specialization: string
  isFinalStep: boolean
}>()

const emit = defineEmits<{
  'update:licenseNumber': [value: string]
  'update:specialization': [value: string]
  'valid': []
  'prev': []
}>()

const licenseNumber = ref(props.licenseNumber)
const selectedSpecialization = ref(props.specialization)

// Validation states
const errors = ref({
  licenseNumber: '',
  specialization: ''
})

// Specialization options
const specializationOptions = [
  { value: 'civil', label: 'Derecho Civil', icon: ScaleIcon, iconClass: 'text-blue-600' },
  { value: 'penal', label: 'Derecho Penal', icon: BriefcaseIcon, iconClass: 'text-red-600' },
  { value: 'laboral', label: 'Derecho Laboral', icon: UserGroupIcon, iconClass: 'text-green-600' },
  { value: 'familiar', label: 'Derecho Familiar', icon: HomeModernIcon, iconClass: 'text-purple-600' },
  { value: 'corporativo', label: 'Derecho Corporativo', icon: BuildingOfficeIcon, iconClass: 'text-gray-600' },
  { value: 'academico', label: 'Académico', icon: AcademicCapIcon, iconClass: 'text-yellow-600' }
]

// Format license number
const formatLicenseNumber = (value: string) => {
  return value.replace(/\D/g, '').slice(0, 8)
}

const handleLicenseInput = (e: Event) => {
  const input = e.target as HTMLInputElement
  const formatted = formatLicenseNumber(input.value)
  licenseNumber.value = formatted
}

const isLicenseValid = computed(() => {
  return licenseNumber.value.length === 8
})

const isSpecializationValid = computed(() => {
  return !!selectedSpecialization.value
})

const isFormValid = computed(() => {
  return isLicenseValid.value && isSpecializationValid.value
})

// Watchers for validation
watch(licenseNumber, (newValue) => {
  emit('update:licenseNumber', newValue)
  errors.value.licenseNumber = isLicenseValid.value ? '' : 'La cédula profesional debe tener 8 dígitos'
})

watch(selectedSpecialization, (newValue) => {
  emit('update:specialization', newValue)
  errors.value.specialization = ''
})
</script>

<template>
  <div class="space-y-6">
    <h2 class="text-2xl font-bold text-center bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent">
      Información Profesional
    </h2>

    <div class="space-y-4">
      <!-- License Number Field -->
      <div>
        <label for="licenseNumber" class="block text-sm font-medium text-gray-700 dark:text-gray-300">
          Cédula Profesional
        </label>
        <div class="mt-1">
          <input
            id="licenseNumber"
            type="text"
            inputmode="numeric"
            v-model="licenseNumber"
            @input="handleLicenseInput"
            :class="[
              'block w-full px-4 py-3 rounded-xl border bg-white dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200',
              errors.licenseNumber
                ? 'border-red-300 dark:border-red-700'
                : 'border-gray-300 dark:border-gray-700'
            ]"
            placeholder="12345678"
            maxlength="8"
          />
          <p v-if="errors.licenseNumber" class="mt-2 text-sm text-red-600 dark:text-red-400">
            {{ errors.licenseNumber }}
          </p>
        </div>
      </div>

      <!-- Specialization Dropdown -->
      <div>
        <label for="specialization" class="block text-sm font-medium text-gray-700 dark:text-gray-300">
          Especialización
        </label>
        <div class="mt-1">
          <CustomSelect
            id="specialization"
            v-model="selectedSpecialization"
            :options="specializationOptions"
            :clearable="true"
            placeholder="Selecciona tu especialidad"
            @change="option => { 
              selectedSpecialization = option?.value || ''
              errors.specialization = ''
            }"
          />
          <p v-if="errors.specialization" class="mt-2 text-sm text-red-600 dark:text-red-400">
            {{ errors.specialization }}
          </p>
        </div>
      </div>
    </div>

    <p class="mt-2 text-sm text-gray-500 dark:text-gray-400 italic">
      Por cuestiones de seguridad, validaremos tu información con las instancias correspondientes antes de completar tu registro.
    </p>

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

<style scoped>
/* Component styles */
</style> 