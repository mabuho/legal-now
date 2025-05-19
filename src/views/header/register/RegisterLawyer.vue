<template>
  <form @submit.prevent="handleSubmit" class="space-y-8" novalidate>
    <div class="space-y-6" role="group" aria-labelledby="personal-info-heading">
      <!-- <h2 id="personal-info-heading" class="sr-only">Información Personal</h2> -->

      <!-- Personal Information -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="relative">
          <input
            v-model="formData.firstName"
            type="text"
            id="firstName"
            class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-indigo-600 bg-transparent focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
            :class="{ 'border-red-500': errors.firstName }"
            placeholder="Nombre"
            aria-required="true"
            :aria-invalid="errors.firstName ? true : false"
            aria-describedby="firstName-error"
          />
          <label
            for="firstName"
            class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
          >
            Nombre
          </label>
          <p
            v-if="errors.firstName"
            id="firstName-error"
            class="mt-1 text-sm text-red-500"
            role="alert"
          >
            {{ errors.firstName }}
          </p>
        </div>

        <div class="relative">
          <input
            v-model="formData.lastName"
            type="text"
            id="lastName"
            class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-indigo-600 bg-transparent focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
            :class="{ 'border-red-500': errors.lastName }"
            placeholder="Apellido"
            aria-required="true"
            :aria-invalid="errors.lastName ? true : false"
            aria-describedby="lastName-error"
          />
          <label
            for="lastName"
            class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
          >
            Apellido
          </label>
          <p
            v-if="errors.lastName"
            id="lastName-error"
            class="mt-1 text-sm text-red-500"
            role="alert"
          >
            {{ errors.lastName }}
          </p>
        </div>
      </div>

      <!-- Contact Information -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="relative">
          <input
            v-model="formData.email"
            type="email"
            id="email"
            class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-indigo-600 bg-transparent"
            :class="{ 'border-red-500': errors.email }"
            placeholder="Correo Electrónico"
          />
          <label
            for="email"
            class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
          >
            Correo Electrónico
          </label>
          <p v-if="errors.email" class="mt-1 text-sm text-red-500">{{ errors.email }}</p>
        </div>

        <div class="relative">
          <input
            v-model="formData.phone"
            type="tel"
            id="phone"
            class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-indigo-600 bg-transparent"
            :class="{ 'border-red-500': errors.phone }"
            placeholder="Teléfono"
          />
          <label
            for="phone"
            class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
          >
            Teléfono
          </label>
          <p v-if="errors.phone" class="mt-1 text-sm text-red-500">{{ errors.phone }}</p>
        </div>
      </div>

      <!-- Professional Information -->
      <div class="relative">
        <input
          v-model="formData.licenseNumber"
          type="text"
          id="licenseNumber"
          class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-indigo-600 bg-transparent"
          :class="{ 'border-red-500': errors.licenseNumber }"
          placeholder="Número de Colegiatura"
        />
        <label
          for="licenseNumber"
          class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
        >
          Número de Colegiatura
        </label>
        <p v-if="errors.licenseNumber" class="mt-1 text-sm text-red-500">
          {{ errors.licenseNumber }}
        </p>
      </div>

      <!-- Specialization Dropdown -->
      <div class="relative">
        <div
          class="select-menu"
          id="specialization"
          :class="{ active: isActive }"
          @keydown.esc="isActive = false"
          @keydown.enter.prevent="isActive ? selectOption(focusedOption as SpecializationOption) : toggleDropdown()"
          @keydown.space.prevent="isActive ? selectOption(focusedOption as SpecializationOption) : toggleDropdown()"
          @keydown.up.prevent="navigateOptions('up')"
          @keydown.down.prevent="navigateOptions('down')"
          tabindex="0"
          role="combobox"
          aria-haspopup="listbox"
          :aria-expanded="isActive"
          aria-labelledby="specialization-label"
          aria-controls="specialization-options"
        >
          <label id="specialization-label" class="sr-only">Especialización</label>
          <div class="select-btn" @click="toggleDropdown">
            <span class="sBtn-text">{{ selectedOption || 'Seleccione una especialización' }}</span>
            <ChevronUpDownIcon
              class="h-5 w-5 text-gray-500 transition-transform duration-300"
              :class="{ 'rotate-180': isActive }"
              aria-hidden="true"
            />
          </div>
          <ul
            id="specialization-options"
            class="options"
            role="listbox"
            :aria-activedescendant="focusedOption ? `option-${focusedOption.value}` : undefined"
          >
            <li
              v-for="option in specializationOptions"
              :key="option.value"
              :id="'option-' + option.value"
              class="option"
              :class="{ 'bg-indigo-50': focusedOption?.value === option.value }"
              @click="selectOption(option)"
              role="option"
              :aria-selected="selectedOption === option.label"
            >
              <component
                :is="option.icon"
                class="h-5 w-5"
                :class="option.iconClass"
                aria-hidden="true"
              />
              <span class="option-text">{{ option.label }}</span>
            </li>
          </ul>
        </div>
        <p
          v-if="errors.specialization"
          id="specialization-error"
          class="mt-1 text-sm text-red-500"
          role="alert"
        >
          {{ errors.specialization }}
        </p>
      </div>

      <!-- Password Fields -->
      <div class="relative">
        <div class="relative">
          <input
            v-model="formData.password"
            :type="showPassword ? 'text' : 'password'"
            id="password"
            class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-indigo-600 bg-transparent pr-10"
            :class="{ 'border-red-500': errors.password }"
            placeholder="Contraseña"
            aria-label="Password input"
          />
          <label
            for="password"
            class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
          >
            Contraseña
          </label>
          <button
            type="button"
            @click="showPassword = !showPassword"
            class="absolute inset-y-0 right-0 flex items-center pr-3"
            aria-label="Toggle password visibility"
          >
            <EyeIcon v-if="showPassword" class="h-5 w-5 text-gray-400" />
            <EyeSlashIcon v-else class="h-5 w-5 text-gray-400" />
          </button>
        </div>
        <div class="mt-2" v-if="formData.password">
          <div class="flex items-center gap-2">
            <div class="h-2 flex-1 rounded-full bg-gray-200 overflow-hidden">
              <div
                class="h-full transition-all duration-300"
                :class="[passwordStrength.color]"
                :style="{ width: `${(passwordStrength.score / 5) * 100}%` }"
              ></div>
            </div>
            <span class="text-sm text-gray-600">{{ passwordStrength.label }}</span>
          </div>
          <ul class="mt-2 text-sm text-gray-600 space-y-1">
            <li class="flex items-center gap-2">
              <div
                class="w-2 h-2 rounded-full"
                :class="formData.password.length >= 8 ? 'bg-green-500' : 'bg-gray-300'"
              ></div>
              Mínimo 8 caracteres
            </li>
            <li class="flex items-center gap-2">
              <div
                class="w-2 h-2 rounded-full"
                :class="/[A-Z]/.test(formData.password) ? 'bg-green-500' : 'bg-gray-300'"
              ></div>
              Una mayúscula
            </li>
            <li class="flex items-center gap-2">
              <div
                class="w-2 h-2 rounded-full"
                :class="/[0-9]/.test(formData.password) ? 'bg-green-500' : 'bg-gray-300'"
              ></div>
              Un número
            </li>
            <li class="flex items-center gap-2">
              <div
                class="w-2 h-2 rounded-full"
                :class="/[^A-Za-z0-9]/.test(formData.password) ? 'bg-green-500' : 'bg-gray-300'"
              ></div>
              Un carácter especial
            </li>
          </ul>
        </div>
        <p v-if="errors.password" class="mt-1 text-sm text-red-500">{{ errors.password }}</p>
      </div>

      <div class="relative">
        <div class="relative">
          <input
            v-model="formData.confirmPassword"
            :type="showConfirmPassword ? 'text' : 'password'"
            id="confirmPassword"
            class="peer h-12 w-full border-b-2 border-gray-300 text-gray-900 placeholder-transparent focus:outline-none focus:border-indigo-600 bg-transparent pr-10"
            :class="{ 'border-red-500': errors.confirmPassword }"
            placeholder="Confirmar Contraseña"
            aria-label="Confirm password input"
          />
          <label
            for="confirmPassword"
            class="absolute left-0 -top-3.5 text-gray-600 text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3 peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
          >
            Confirmar Contraseña
          </label>
          <button
            type="button"
            @click="showConfirmPassword = !showConfirmPassword"
            class="absolute inset-y-0 right-0 flex items-center pr-3"
            aria-label="Toggle confirm password visibility"
          >
            <EyeIcon v-if="showConfirmPassword" class="h-5 w-5 text-gray-400" />
            <EyeSlashIcon v-else class="h-5 w-5 text-gray-400" />
          </button>
        </div>
        <p v-if="errors.confirmPassword" class="mt-1 text-sm text-red-500">
          {{ errors.confirmPassword }}
        </p>
      </div>

      <!-- Terms and Conditions -->
      <!-- We might need a link to the terms and conditions -->
      <div class="flex items-center space-x-3">
        <div class="relative flex items-start">
          <div class="flex items-center h-5">
            <input
              type="checkbox"
              v-model="formData.terms"
              id="terms"
              class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-600 transition-colors cursor-pointer"
              aria-required="true"
              :aria-invalid="errors.terms ? true : false"
              aria-describedby="terms-error"
            />
          </div>
          <div class="ml-3 text-sm">
            <label for="terms" class="font-medium text-gray-700 cursor-pointer">
              Acepto los términos y condiciones
            </label>
          </div>
        </div>
      </div>
      <p v-if="errors.terms" id="terms-error" class="mt-1 text-sm text-red-500" role="alert">
        {{ errors.terms }}
      </p>
    </div>

    <!-- Submit registration button -->
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
        Registrando...
      </template>
      <template v-else> Registrarse como Abogado </template>
    </button>
  </form>
</template>

<style>
.select-menu {
  width: 100%;
  position: relative;
  margin-bottom: 1rem;
}
.select-menu .select-btn {
  display: flex;
  height: 55px;
  background: #fff;
  padding: 20px;
  font-size: 18px;
  font-weight: 400;
  border-radius: 8px;
  align-items: center;
  cursor: pointer;
  justify-content: space-between;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
}
.select-menu.active .select-btn {
  border-color: #4f46e5;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.1);
}
.select-menu .options {
  position: absolute;
  width: 100%;
  padding: 10px;
  margin-top: 10px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  display: none;
  z-index: 100;
}
.select-menu.active .options {
  display: block;
}
.options .option {
  display: flex;
  height: 55px;
  cursor: pointer;
  padding: 0 16px;
  border-radius: 8px;
  align-items: center;
  background: #fff;
  gap: 12px;
}
.options .option:hover {
  background: #f2f2f2;
}
.option .option-text {
  font-size: 18px;
  color: #333;
}

/* Add transition for smoother animations */
.select-menu .options {
  transition: all 0.2s ease;
  transform-origin: top;
  opacity: 0;
  transform: scaleY(0);
}
.select-menu.active .options {
  opacity: 1;
  transform: scaleY(1);
}

.select-menu:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
  border-radius: 8px;
}

.select-menu .options .option:focus {
  outline: none;
  background-color: rgba(79, 70, 229, 0.1);
}
</style>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  ChevronUpDownIcon,
  ScaleIcon,
  BookOpenIcon,
  BriefcaseIcon,
  BuildingStorefrontIcon,
  HomeIcon,
  BuildingLibraryIcon,
  EyeIcon,
  EyeSlashIcon,
} from '@heroicons/vue/24/outline'
import { onClickOutside } from '@vueuse/core'

const router = useRouter()
const isLoading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const isActive = ref(false)
const selectedOption = ref('')
const focusedOption = ref<SpecializationOption | null>(null)

const specializationOptions = [
  {
    value: 'civil',
    label: 'Derecho Civil',
    icon: BookOpenIcon,
    iconClass: 'text-blue-600',
  },
  {
    value: 'penal',
    label: 'Derecho Penal',
    icon: ScaleIcon,
    iconClass: 'text-red-600',
  },
  {
    value: 'laboral',
    label: 'Derecho Laboral',
    icon: BriefcaseIcon,
    iconClass: 'text-sky-600',
  },
  {
    value: 'comercial',
    label: 'Derecho Comercial',
    icon: BuildingStorefrontIcon,
    iconClass: 'text-indigo-600',
  },
  {
    value: 'familiar',
    label: 'Derecho Familiar',
    icon: HomeIcon,
    iconClass: 'text-pink-600',
  },
  {
    value: 'constitucional',
    label: 'Derecho Constitucional',
    icon: BuildingLibraryIcon,
    iconClass: 'text-gray-700',
  },
]

interface SpecializationOption {
  value: string
  label: string
  icon: any
  iconClass: string
}

const navigateOptions = (direction: 'up' | 'down') => {
  if (!isActive.value) {
    isActive.value = true
    focusedOption.value = specializationOptions[0]
    return
  }

  const currentIndex = specializationOptions.findIndex(
    (option) => option.value === focusedOption.value?.value,
  )
  let nextIndex

  if (direction === 'up') {
    nextIndex = currentIndex > 0 ? currentIndex - 1 : specializationOptions.length - 1
  } else {
    nextIndex = currentIndex < specializationOptions.length - 1 ? currentIndex + 1 : 0
  }

  focusedOption.value = specializationOptions[nextIndex]
}

const toggleDropdown = () => {
  isActive.value = !isActive.value
  if (isActive.value && !focusedOption.value) {
    focusedOption.value = specializationOptions[0]
  }
}

const selectOption = (option: { value: string; label: string }) => {
  selectedOption.value = option.label
  formData.specialization = option.value
  isActive.value = false
}

// Close dropdown when clicking outside
const dropdownRef = ref(null)
onClickOutside(dropdownRef, () => {
  isActive.value = false
  focusedOption.value = null
})

const formData = reactive({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  licenseNumber: '',
  specialization: '',
  password: '',
  confirmPassword: '',
  terms: false,
})

const errors = reactive<{
  firstName: string
  lastName: string
  email: string
  phone: string
  licenseNumber: string
  specialization: string
  password: string
  confirmPassword: string
  terms: string
}>({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  licenseNumber: '',
  specialization: '',
  password: '',
  confirmPassword: '',
  terms: '',
})

// Password strength computation
const passwordStrength = computed<any>(() => {
  const password = formData.password
  if (!password) return { score: 0, label: '', color: 'bg-gray-200' }

  let score = 0
  if (password.length >= 8) score++
  if (/[A-Z]/.test(password)) score++
  if (/[a-z]/.test(password)) score++
  if (/[0-9]/.test(password)) score++
  if (/[^A-Za-z0-9]/.test(password)) score++

  const strengthMap = {
    0: { label: 'Muy débil', color: 'bg-red-500' },
    1: { label: 'Débil', color: 'bg-orange-500' },
    2: { label: 'Regular', color: 'bg-yellow-500' },
    3: { label: 'Buena', color: 'bg-blue-500' },
    4: { label: 'Fuerte', color: 'bg-green-500' },
    5: { label: 'Muy fuerte', color: 'bg-green-600' },
  }

  return strengthMap[score as keyof typeof strengthMap]
})

// Phone number formatting
const formatPhoneNumber = (value: string) => {
  const phone = value.replace(/\D/g, '')
  if (phone.length <= 3) return phone
  if (phone.length <= 6) return `${phone.slice(0, 3)}-${phone.slice(3)}`
  return `${phone.slice(0, 3)}-${phone.slice(3, 6)}-${phone.slice(6, 10)}`
}

// Watch phone input for formatting
watch(
  () => formData.phone,
  (newValue) => {
    if (!newValue) return
    formData.phone = formatPhoneNumber(newValue)
  },
)

const validateForm = () => {
  let isValid = true

  // Reset errors
  Object.keys(errors).forEach((key) => {
    errors[key as keyof typeof errors] = ''
  })

  if (!formData.firstName) {
    errors.firstName = 'El nombre es requerido'
    isValid = false
  }

  if (!formData.lastName) {
    errors.lastName = 'El apellido es requerido'
    isValid = false
  }

  if (!formData.email) {
    errors.email = 'El correo electrónico es requerido'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    errors.email = 'Ingrese un correo electrónico válido'
    isValid = false
  }

  if (!formData.phone) {
    errors.phone = 'El teléfono es requerido'
    isValid = false
  }

  if (!formData.licenseNumber) {
    errors.licenseNumber = 'El número de colegiatura es requerido'
    isValid = false
  }

  if (!formData.specialization) {
    errors.specialization = 'Seleccione una especialización'
    isValid = false
  }

  if (!formData.password) {
    errors.password = 'La contraseña es requerida'
    isValid = false
  } else if (formData.password.length < 8) {
    errors.password = 'La contraseña debe tener al menos 8 caracteres'
    isValid = false
  }

  if (!formData.confirmPassword) {
    errors.confirmPassword = 'Confirme su contraseña'
    isValid = false
  } else if (formData.password !== formData.confirmPassword) {
    errors.confirmPassword = 'Las contraseñas no coinciden'
    isValid = false
  }

  if (!formData.terms) {
    errors.terms = 'Debe aceptar los términos y condiciones'
    isValid = false
  }

  return isValid
}

const handleSubmit = async () => {
  isLoading.value = true
  try {
    // Validate form
    const validationErrors = validateForm()
    if (Object.keys(validationErrors).length > 0) {
      //errors.value = validationErrors
      return
    }

    // TODO: Add your API call here
    await new Promise((resolve) => setTimeout(resolve, 1500)) // Simulated API call

    // Handle success
    console.log('Form submitted successfully')
    router.push('/login')
  } catch (error) {
    console.error('Error submitting form:', error)
  } finally {
    isLoading.value = false
  }
}
</script>
