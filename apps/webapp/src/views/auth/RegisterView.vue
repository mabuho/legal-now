<template>
  <Header />
  <div class="min-h-screen flex flex-col items-center justify-center bg-surface-base px-4 py-12">
    <div class="w-full max-w-2xl p-10 rounded-card shadow-2xl bg-surface-card border border-border-default">
      <h2 class="text-2xl font-heading font-bold text-center text-text-primary mb-8">
        Crear cuenta
      </h2>

      <p v-if="registerError" role="alert"
        class="mb-4 px-4 py-3 rounded-btn bg-status-error/10 border border-status-error/30 text-status-error text-sm font-body">
        {{ registerError }}
      </p>

      <form @submit.prevent class="space-y-8">
        <!-- Paso 1: Tipo de cuenta -->
        <div v-if="currentStep === 1" class="space-y-6">
          <h3 class="font-heading font-semibold text-text-primary text-lg text-center">
            Selecciona el tipo de cuenta
          </h3>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mt-8">
            <div @click="selectUserType('client')" :class="[
              'rounded-card border-2 p-6 cursor-pointer transition-all hover:border-brand-primary/40',
              userType === 'client'
                ? 'border-brand-primary bg-brand-primary/[0.08]'
                : 'border-border-default',
            ]">
              <div class="flex items-center gap-4">
                <div class="p-3 bg-brand-primary/10 rounded-lg">
                  <UserIcon class="w-6 h-6 text-brand-primary" />
                </div>
                <div>
                  <h4 class="font-heading font-semibold text-text-primary">Cliente</h4>
                  <p class="font-body text-sm text-text-secondary">Busco asesoría legal profesional</p>
                </div>
              </div>
            </div>

            <div @click="selectUserType('lawyer')" :class="[
              'rounded-card border-2 p-6 cursor-pointer transition-all hover:border-brand-primary/40',
              userType === 'lawyer'
                ? 'border-brand-primary bg-brand-primary/[0.08]'
                : 'border-border-default',
            ]">
              <div class="flex items-center gap-4">
                <div class="p-3 bg-brand-primary/10 rounded-lg">
                  <ScaleIcon class="w-6 h-6 text-brand-primary" />
                </div>
                <div>
                  <h4 class="font-heading font-semibold text-text-primary">Abogado</h4>
                  <p class="font-body text-sm text-text-secondary">Quiero ofrecer mis servicios legales</p>
                </div>
              </div>
            </div>
          </div>

          <div class="flex justify-end mt-8">
            <button type="button" @click="handleNext" :disabled="!userType"
              class="inline-flex items-center gap-2 px-6 py-3 rounded-btn font-body font-semibold text-white bg-gradient-to-r from-brand-primary-dark to-brand-accent shadow-glow-btn hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed">
              Siguiente
              <ChevronRightIcon class="w-5 h-5" aria-hidden="true" />
            </button>
          </div>
        </div>

        <!-- Paso 2: Credenciales -->
        <div v-if="currentStep === 2">
          <CredentialsStep v-model:email="formData.email" v-model:password="formData.password"
            v-model:confirmPassword="formData.confirmPassword" @valid="nextStep" @prev="prevStep" />
        </div>

        <!-- Paso 3: Información personal -->
        <div v-if="currentStep === 3">
          <PersonalInformationStep v-model:firstName="formData.firstName" v-model:lastName="formData.lastName"
            v-model:phone="formData.phone" :isFinalStep="userType === 'client'"
            @valid="userType === 'client' ? handleRegister() : nextStep()" @prev="prevStep" />
        </div>

        <!-- Paso 4: Información profesional (solo abogados) -->
        <div v-if="currentStep === 4">
          <ProfessionalInformationStep v-model:licenseNumber="formData.licenseNumber"
            v-model:specialization="formData.specialization" :isFinalStep="true" @valid="handleRegister"
            @prev="prevStep" />
        </div>
      </form>

      <div v-if="isLoading" class="mt-4 text-center text-brand-primary text-sm font-body animate-pulse">
        Registrando...
      </div>
    </div>

    <p class="mt-4 text-center text-sm font-body text-text-muted">
      ¿Ya tienes cuenta?
      <router-link to="/login" class="text-brand-primary hover:text-brand-primary/80 font-semibold">
        Iniciar sesión
      </router-link>
    </p>
  </div>
  <Footer />
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import Footer from '@/components/Footer.vue'
import Header from '@/components/Header.vue'
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
  specialization: '',
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
      router.push('/confirm-email-pending')
    }
  } catch (err: unknown) {
    registerError.value = auth.error ?? (err instanceof Error ? err.message : 'Error al registrarse.')
  } finally {
    isLoading.value = false
  }
}
</script>
