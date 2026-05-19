<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ChevronRightIcon, UserIcon, ScaleIcon } from '@heroicons/vue/24/outline'
import { useAuthStore } from '@/stores/auth'
import CredentialsStep from '@/components/register/CredentialsStep.vue'
import PersonalInformationStep from '@/components/register/PersonalInformationStep.vue'
import ProfessionalInformationStep from '@/components/register/ProfessionalInformationStep.vue'
import type { Role } from '@/types/user'

const router = useRouter()
const auth = useAuthStore()

const currentStep = ref(1)
const userType = ref<'client' | 'lawyer'>('client')
const isLoading = ref(false)
const registerError = ref<string | null>(null)

const formData = ref({
  email: '',
  password: '',
  confirmPassword: '',
  firstName: '',
  lastName: '',
  phone: '',
  licenseNumber: '',
  specialization: ''
})

const totalSteps = computed(() => (userType.value === 'client' ? 3 : 4))

const nextStep = () => {
  if (currentStep.value < totalSteps.value) currentStep.value++
}
const prevStep = () => {
  if (currentStep.value > 1) currentStep.value--
}
const selectUserType = (type: 'client' | 'lawyer') => {
  userType.value = type
}
const handleNext = () => { nextStep() }

/**
 * Called when the last step emits @valid.
 * For clients that is step 3 (PersonalInformation); for lawyers step 4 (ProfessionalInformation).
 */
const handleRegister = async () => {
  isLoading.value = true
  registerError.value = null
  try {
    const name = `${formData.value.firstName} ${formData.value.lastName}`.trim()
    await auth.register({
      email: formData.value.email,
      password: formData.value.password,
      name,
      role: userType.value as Role,
    })
    if (auth.user) {
      router.push(auth.user.role === 'lawyer' ? '/dashboard/lawyer' : '/dashboard/client')
    }
  } catch (err: unknown) {
    registerError.value = auth.error ?? (err instanceof Error ? err.message : 'Error al registrarse.')
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div
    class="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-950 via-slate-950 to-black px-4">
    <div class="w-full max-w-2xl p-10 rounded-2xl shadow-2xl bg-slate-900 border border-slate-800">
      <h2
        class="text-3xl font-semibold text-center font-montserrat bg-gradient-to-r from-cyan-400 to-blue-500 bg-clip-text text-transparent mb-8">
        Registro</h2>

      <p v-if="registerError" role="alert"
        class="mb-4 px-4 py-3 rounded-xl bg-red-900/40 border border-red-700 text-red-300 text-sm font-manrope">
        {{ registerError }}
      </p>

      <form @submit.prevent class="space-y-8">
        <!-- Step 1: Account type -->
        <div v-if="currentStep === 1" class="space-y-6">
          <h3
            class="text-2xl font-bold text-center bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent">
            Selecciona el tipo de cuenta</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mt-8">
            <div @click="selectUserType('client')"
              :class="['relative rounded-xl border-2 p-6 cursor-pointer transition-all duration-300 transform hover:scale-[1.02]', userType === 'client' ? 'border-cyan-600 bg-cyan-900/20' : 'border-slate-700 hover:border-cyan-400', userType === 'lawyer' ? 'opacity-50' : '']">
              <div class="flex items-center space-x-4">
                <div class="p-3 bg-cyan-600 rounded-lg">
                  <UserIcon class="w-6 h-6 text-white" />
                </div>
                <div>
                  <h4 class="text-lg font-semibold text-slate-100">Cliente</h4>
                  <p class="text-sm text-slate-400">Busco asesoría legal profesional</p>
                </div>
              </div>
            </div>
            <div @click="selectUserType('lawyer')"
              :class="['relative rounded-xl border-2 p-6 cursor-pointer transition-all duration-300 transform hover:scale-[1.02]', userType === 'lawyer' ? 'border-cyan-600 bg-cyan-900/20' : 'border-slate-700 hover:border-cyan-400', userType === 'client' ? 'opacity-50' : '']">
              <div class="flex items-center space-x-4">
                <div class="p-3 bg-cyan-600 rounded-lg">
                  <ScaleIcon class="w-6 h-6 text-white" />
                </div>
                <div>
                  <h4 class="text-lg font-semibold text-slate-100">Abogado</h4>
                  <p class="text-sm text-slate-400">Quiero ofrecer mis servicios legales</p>
                </div>
              </div>
            </div>
          </div>
          <div class="flex justify-end mt-8 space-x-4">
            <button type="button" @click="handleNext" :disabled="!userType"
              :class="['inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-xl shadow-sm text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-cyan-500 transition-all duration-200', userType ? 'bg-gradient-to-r from-cyan-600 to-blue-600 hover:from-cyan-700 hover:to-blue-700 hover:-translate-y-0.5' : 'bg-slate-700 cursor-not-allowed']">
              Siguiente
              <ChevronRightIcon class="ml-2 -mr-1 h-5 w-5" aria-hidden="true" />
            </button>
          </div>
        </div>

        <!-- Step 2: Credentials -->
        <div v-if="currentStep === 2">
          <CredentialsStep v-model:email="formData.email" v-model:password="formData.password"
            v-model:confirmPassword="formData.confirmPassword" @valid="nextStep" @prev="prevStep" />
        </div>

        <!-- Step 3: Personal information -->
        <div v-if="currentStep === 3">
          <PersonalInformationStep v-model:firstName="formData.firstName" v-model:lastName="formData.lastName"
            v-model:phone="formData.phone" :isFinalStep="userType === 'client'"
            @valid="userType === 'client' ? handleRegister() : nextStep()"
            @prev="prevStep" />
        </div>

        <!-- Step 4: Professional information (lawyers only) -->
        <div v-if="currentStep === 4">
          <ProfessionalInformationStep v-model:licenseNumber="formData.licenseNumber"
            v-model:specialization="formData.specialization" :isFinalStep="true"
            @valid="handleRegister" @prev="prevStep" />
        </div>
      </form>

      <div v-if="isLoading" class="mt-4 text-center text-cyan-400 text-sm font-manrope animate-pulse">
        Registrando...
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Remove legacy styles, use Tailwind for all styling */
</style>
