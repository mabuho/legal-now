<script setup lang="ts">
import { ref, computed } from 'vue'
import { ChevronRightIcon, ChevronLeftIcon, UserIcon, ScaleIcon, CheckCircleIcon } from '@heroicons/vue/24/outline'
import { useRouter } from 'vue-router'
import CredentialsStep from '../register/CredentialsStep.vue'
import PersonalInformationStep from '../register/PersonalInformationStep.vue'
import ProfessionalInformationStep from '../register/ProfessionalInformationStep.vue'

const router = useRouter()
const isRegistrationComplete = ref(false)

// Form state management
const currentStep = ref(1)
const userType = ref('client') // 'client' or 'lawyer'
const totalSteps = computed(() => userType.value === 'client' ? 3 : 4)

// Form data
const formData = ref({
  email: '',
  password: '',
  confirmPassword: '',
  firstName: '',
  lastName: '',
  phone: '',
  licenseNumber: ''
})

// Progress calculation
const progress = computed(() => {
  return (currentStep.value / totalSteps.value) * 100
})

// Navigation methods
const nextStep = () => {
  if (currentStep.value < totalSteps.value) {
    currentStep.value++
  } else {
    // Registration is complete
    handleRegistrationComplete()
  }
}

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const selectUserType = (type: 'client' | 'lawyer') => {
  userType.value = type
}

const handleNext = () => {
  nextStep()
}

const handleRegistrationComplete = () => {
  // Here you would typically make an API call to register the user
  // For now, we'll just show the success message
  isRegistrationComplete.value = true
}

const navigateToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-12 px-4 sm:px-6 lg:px-8 dark:from-gray-900 dark:to-gray-800">
    <div class="max-w-3xl mx-auto">
      <!-- Registration Success Message -->
      <div v-if="isRegistrationComplete" class="bg-white dark:bg-gray-900 rounded-2xl shadow-xl overflow-hidden">
        <div class="p-8">
          <div class="text-center space-y-6">
            <div class="flex justify-center">
              <CheckCircleIcon class="h-16 w-16 text-green-500" />
            </div>
            <h2 class="text-2xl font-bold text-gray-900 dark:text-white">
              ¡Registro Exitoso!
            </h2>
            <p class="text-gray-600 dark:text-gray-400">
              Tu cuenta ha sido creada correctamente. <br> 
              Verifica tu correo electrónico para activar tu cuenta.
            </p>
            <p class="text-md text-center text-gray-500 mt-8 dark:text-gray-400">
              <RouterLink
                to="/login"
                class="font-medium text-indigo-600 hover:text-indigo-500 dark:text-indigo-400 dark:hover:text-indigo-300"
              >
                Haz click aquí para iniciar sesión →
              </RouterLink>
            </p>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              Si no recibes el correo en unos minutos, revisa tu carpeta de spam o haz click en el siguiente enlace para reenviarlo.
            </p>
            <div class="pt-4">
              <a
                href="#"
                class="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-xl shadow-sm text-white bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 hover:from-indigo-700 hover:via-blue-700 hover:to-purple-700 hover:-translate-y-0.5 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all duration-200"
              >
                Reenviar correo de verificación
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- Registration Form -->
      <div v-else>
        <!-- Progress Bar -->
        <div class="mb-8">
          <div class="flex justify-between mb-2">
            <span class="text-sm font-medium text-gray-700 dark:text-gray-300">
              Paso {{ currentStep }} de {{ totalSteps }}
            </span>
            <span class="text-sm font-medium text-gray-700 dark:text-gray-300">
              {{ Math.round(progress) }}% Completado
            </span>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-2 dark:bg-gray-700">
            <div
              class="bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 h-2 rounded-full transition-all duration-300 ease-in-out"
              :style="{ width: `${progress}%` }"
            ></div>
          </div>
        </div>

        <!-- Form Container -->
        <div class="bg-white dark:bg-gray-900 rounded-2xl shadow-xl overflow-hidden">
          <div class="p-8">
            <!-- Step 1: User Type Selection -->
            <div v-if="currentStep === 1" class="space-y-6">
              <h2 class="text-2xl font-bold text-center bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent">
                Selecciona el tipo de cuenta
              </h2>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mt-8">
                <!-- Client Card -->
                <div
                  @click="selectUserType('client')"
                  :class="[
                    'relative rounded-xl border-2 p-6 cursor-pointer transition-all duration-300 transform hover:scale-[1.02]',
                    userType === 'client'
                      ? 'border-indigo-600 bg-indigo-50 dark:bg-indigo-900/20'
                      : 'border-gray-200 dark:border-gray-700 hover:border-indigo-300 dark:hover:border-indigo-700',
                    userType === 'lawyer' ? 'opacity-50' : ''
                  ]"
                >
                  <div class="flex items-center space-x-4">
                    <div class="p-3 bg-indigo-600 rounded-lg">
                      <UserIcon class="w-6 h-6 text-white" />
                    </div>
                    <div>
                      <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Cliente</h3>
                      <p class="text-sm text-gray-600 dark:text-gray-400">
                        Busco asesoría legal profesional
                      </p>
                    </div>
                  </div>
                </div>

                <!-- Lawyer Card -->
                <div
                  @click="selectUserType('lawyer')"
                  :class="[
                    'relative rounded-xl border-2 p-6 cursor-pointer transition-all duration-300 transform hover:scale-[1.02]',
                    userType === 'lawyer'
                      ? 'border-indigo-600 bg-indigo-50 dark:bg-indigo-900/20'
                      : 'border-gray-200 dark:border-gray-700 hover:border-indigo-300 dark:hover:border-indigo-700',
                    userType === 'client' ? 'opacity-50' : ''
                  ]"
                >
                  <div class="flex items-center space-x-4">
                    <div class="p-3 bg-purple-600 rounded-lg">
                      <ScaleIcon class="w-6 h-6 text-white" />
                    </div>
                    <div>
                      <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Profesional</h3>
                      <p class="text-sm text-gray-600 dark:text-gray-400">
                        Soy un abogado que ofrece servicios
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Navigation Buttons -->
              <div class="flex justify-end mt-8 space-x-4">
                <button
                  type="button"
                  @click="handleNext"
                  :disabled="!userType"
                  :class="[
                    'inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-xl shadow-sm text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all duration-200',
                    userType
                      ? 'bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 hover:from-indigo-700 hover:via-blue-700 hover:to-purple-700 hover:-translate-y-0.5'
                      : 'bg-gray-300 dark:bg-gray-700 cursor-not-allowed'
                  ]"
                >
                  Siguiente
                  <ChevronRightIcon class="ml-2 -mr-1 h-5 w-5" aria-hidden="true" />
                </button>
              </div>
            </div>

            <!-- Step 2: Credentials -->
            <div v-if="currentStep === 2">
              <CredentialsStep
                v-model:email="formData.email"
                v-model:password="formData.password"
                v-model:confirmPassword="formData.confirmPassword"
                @valid="nextStep"
                @prev="prevStep"
              />
            </div>

            <!-- Step 3: Personal Information -->
            <div v-if="currentStep === 3">
              <PersonalInformationStep
                v-model:firstName="formData.firstName"
                v-model:lastName="formData.lastName"
                v-model:phone="formData.phone"
                :isFinalStep="userType === 'client'"
                @valid="nextStep"
                @prev="prevStep"
              />
            </div>

            <!-- Step 4: Professional Information (Only for lawyers) -->
            <div v-if="currentStep === 4 && userType === 'lawyer'">
              <ProfessionalInformationStep
                v-model:licenseNumber="formData.licenseNumber"
                @valid="nextStep"
                @prev="prevStep"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.form-container {
  @apply max-w-md mx-auto;
}

.step-enter-active,
.step-leave-active {
  transition: all 0.3s ease-in-out;
}

.step-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.step-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style> 