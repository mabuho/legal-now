<template>
  <div class="min-h-screen flex items-center justify-center bg-surface-base px-4 py-12">
    <div class="w-full max-w-lg p-8 rounded-card shadow-2xl bg-surface-card border border-border-default">

      <!-- Progress indicator -->
      <div class="flex items-center justify-center gap-3 mb-8">
        <template v-for="step in 4" :key="step">
          <div
            :class="[
              'w-8 h-8 rounded-full flex items-center justify-center text-xs font-body font-semibold transition-all',
              currentStep === step
                ? 'bg-brand-primary text-white shadow-glow-btn'
                : currentStep > step
                  ? 'bg-green-500 text-white'
                  : 'bg-surface-raised text-text-muted border border-border-default',
            ]"
          >
            <CheckIcon v-if="currentStep > step" class="w-4 h-4" />
            <span v-else>{{ step }}</span>
          </div>
          <div
            v-if="step < 4"
            :class="[
              'flex-1 h-px max-w-12 transition-all',
              currentStep > step ? 'bg-green-500' : 'bg-border-default',
            ]"
          ></div>
        </template>
      </div>

      <!-- Error banner -->
      <p v-if="stepError" role="alert"
        class="mb-5 px-4 py-3 rounded-btn bg-status-error/10 border border-status-error/30 text-status-error text-sm font-body">
        {{ stepError }}
      </p>

      <!-- Step 1: Perfil -->
      <div v-if="currentStep === 1" class="space-y-5">
        <h2 class="text-xl font-heading font-bold text-text-primary">Cuéntanos sobre ti</h2>
        <p class="font-body text-sm text-text-secondary">Completa tu perfil profesional para que los clientes te conozcan.</p>

        <div>
          <label class="block mb-1 font-body text-sm text-text-secondary">Biografía profesional</label>
          <textarea
            v-model="bio"
            rows="4"
            placeholder="Describe tu experiencia, logros y áreas de especialización..."
            class="w-full px-4 py-3 rounded-input border border-border-default bg-surface-raised text-text-primary placeholder-text-muted focus:outline-none focus:ring-2 focus:ring-brand-primary/50 focus:border-brand-primary/60 transition-colors resize-none"
          ></textarea>
        </div>

        <div>
          <label class="block mb-1 font-body text-sm text-text-secondary">Idiomas (separados por coma)</label>
          <input
            v-model="languagesInput"
            type="text"
            placeholder="Español, Inglés, Francés"
            class="w-full px-4 py-3 rounded-input border border-border-default bg-surface-raised text-text-primary placeholder-text-muted focus:outline-none focus:ring-2 focus:ring-brand-primary/50 focus:border-brand-primary/60 transition-colors"
          />
        </div>

        <div class="flex justify-end">
          <button
            type="button"
            :disabled="saving"
            @click="saveStep1"
            class="px-6 py-3 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold shadow-glow-btn hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ saving ? 'Guardando...' : 'Siguiente' }}
          </button>
        </div>
      </div>

      <!-- Step 2: Cédula + Documentos -->
      <div v-if="currentStep === 2" class="space-y-5">
        <h2 class="text-xl font-heading font-bold text-text-primary">Cédula profesional</h2>
        <p class="font-body text-sm text-text-secondary">Tu número de cédula nos permite verificar tu registro ante la SEP.</p>

        <div>
          <label class="block mb-1 font-body text-sm text-text-secondary">Número de cédula (Bar ID)</label>
          <input
            v-model="barId"
            type="text"
            placeholder="Ej. 12345678"
            class="w-full px-4 py-3 rounded-input border border-border-default bg-surface-raised text-text-primary placeholder-text-muted focus:outline-none focus:ring-2 focus:ring-brand-primary/50 focus:border-brand-primary/60 transition-colors"
          />
        </div>

        <div class="flex justify-between">
          <button
            type="button"
            @click="currentStep = 1"
            class="px-6 py-3 rounded-btn border border-border-default bg-surface-raised text-text-secondary font-body font-semibold hover:bg-surface-card transition-colors"
          >
            Anterior
          </button>
          <button
            type="button"
            :disabled="saving"
            @click="saveStep2"
            class="px-6 py-3 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold shadow-glow-btn hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ saving ? 'Guardando...' : 'Siguiente' }}
          </button>
        </div>
      </div>

      <!-- Step 3: Especialidades -->
      <div v-if="currentStep === 3" class="space-y-5">
        <h2 class="text-xl font-heading font-bold text-text-primary">Especialidades</h2>
        <p class="font-body text-sm text-text-secondary">Selecciona las áreas del derecho en las que te especializas.</p>

        <div class="grid grid-cols-2 gap-3">
          <label
            v-for="spec in specs"
            :key="spec.code"
            :class="[
              'flex items-center gap-3 p-3 rounded-card border cursor-pointer transition-all',
              selectedCodes.includes(spec.code)
                ? 'border-brand-primary bg-brand-primary/[0.08]'
                : 'border-border-default bg-surface-raised hover:border-brand-primary/40',
            ]"
          >
            <input
              type="checkbox"
              :value="spec.code"
              v-model="selectedCodes"
              class="w-4 h-4 text-brand-primary rounded accent-brand-primary"
            />
            <span class="font-body text-sm text-text-primary">{{ spec.name }}</span>
          </label>
        </div>

        <div class="flex justify-between">
          <button
            type="button"
            @click="currentStep = 2"
            class="px-6 py-3 rounded-btn border border-border-default bg-surface-raised text-text-secondary font-body font-semibold hover:bg-surface-card transition-colors"
          >
            Anterior
          </button>
          <button
            type="button"
            :disabled="saving"
            @click="saveStep3"
            class="px-6 py-3 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold shadow-glow-btn hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ saving ? 'Guardando...' : 'Siguiente' }}
          </button>
        </div>
      </div>

      <!-- Step 4: Finalizar -->
      <div v-if="currentStep === 4" class="space-y-5">
        <h2 class="text-xl font-heading font-bold text-text-primary">Finalizar perfil</h2>

        <template v-if="!completed">
          <p class="font-body text-sm text-text-secondary">
            Revisa el resumen de tu perfil antes de completar el onboarding.
          </p>

          <div class="space-y-3 p-4 rounded-card bg-surface-raised border border-border-subtle">
            <div>
              <span class="font-body text-xs text-text-muted uppercase tracking-wide">Biografía</span>
              <p class="font-body text-sm text-text-primary mt-0.5">{{ bio || 'Sin especificar' }}</p>
            </div>
            <div>
              <span class="font-body text-xs text-text-muted uppercase tracking-wide">Idiomas</span>
              <p class="font-body text-sm text-text-primary mt-0.5">{{ languagesInput || 'Sin especificar' }}</p>
            </div>
            <div>
              <span class="font-body text-xs text-text-muted uppercase tracking-wide">Cédula</span>
              <p class="font-body text-sm text-text-primary mt-0.5">{{ barId || 'Sin especificar' }}</p>
            </div>
            <div>
              <span class="font-body text-xs text-text-muted uppercase tracking-wide">Especialidades</span>
              <p class="font-body text-sm text-text-primary mt-0.5">
                {{ selectedCodes.length > 0 ? specs.filter(s => selectedCodes.includes(s.code)).map(s => s.name).join(', ') : 'Sin seleccionar' }}
              </p>
            </div>
          </div>

          <div class="flex justify-between">
            <button
              type="button"
              @click="currentStep = 3"
              class="px-6 py-3 rounded-btn border border-border-default bg-surface-raised text-text-secondary font-body font-semibold hover:bg-surface-card transition-colors"
            >
              Anterior
            </button>
            <button
              type="button"
              :disabled="completing"
              @click="completeOnboarding"
              class="px-6 py-3 rounded-btn bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold shadow-glow-btn hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ completing ? 'Completando...' : 'Completar onboarding' }}
            </button>
          </div>
        </template>

        <template v-else>
          <div class="text-center py-6">
            <div class="mb-4 p-4 rounded-full bg-green-500/10 inline-flex">
              <CheckIcon class="w-10 h-10 text-green-400" />
            </div>
            <h3 class="font-heading font-semibold text-text-primary mb-2">Verificacion en proceso</h3>
            <p class="font-body text-sm text-text-secondary">
              Tu perfil ha sido enviado para revisión. Te notificaremos cuando sea verificado.
            </p>
          </div>
        </template>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { CheckIcon } from '@heroicons/vue/24/outline'
import { useAuthStore } from '@/stores/auth'
import { apiPatch, apiPost, apiGet } from '@/services/apiClient'

const FALLBACK_SPECS = [
  { code: 'CIVIL', name: 'Derecho Civil' },
  { code: 'PENAL', name: 'Derecho Penal' },
  { code: 'LABORAL', name: 'Derecho Laboral' },
  { code: 'FAMILIAR', name: 'Derecho Familiar' },
  { code: 'MERCANTIL', name: 'Derecho Mercantil' },
  { code: 'FISCAL', name: 'Derecho Fiscal' },
  { code: 'ADMINISTRATIVO', name: 'Derecho Administrativo' },
  { code: 'CONSTITUCIONAL', name: 'Derecho Constitucional' },
]

const auth = useAuthStore()
const router = useRouter()
const currentStep = ref(1)
const saving = ref(false)
const stepError = ref<string | null>(null)

// Step 1
const bio = ref('')
const languagesInput = ref('')

// Step 2
const barId = ref('')

// Step 3
interface Spec { code: string; name: string }
const specs = ref<Spec[]>([...FALLBACK_SPECS])
const selectedCodes = ref<string[]>([])

// Step 4
const completing = ref(false)
const completed = ref(false)

onMounted(async () => {
  const { data } = await apiGet<Spec[]>('/api/v1/specializations')
  if (data) specs.value = data
})

async function saveStep1() {
  saving.value = true
  stepError.value = null
  const languages = languagesInput.value.split(',').map(l => l.trim()).filter(Boolean)
  const { error } = await apiPatch('/api/v1/lawyers/me', { bio: bio.value, languages })
  if (error) stepError.value = error.message
  else currentStep.value = 2
  saving.value = false
}

async function saveStep2() {
  saving.value = true
  stepError.value = null
  const { error } = await apiPatch('/api/v1/lawyers/me', { barId: barId.value })
  if (error) stepError.value = error.message
  else currentStep.value = 3
  saving.value = false
}

async function saveStep3() {
  saving.value = true
  stepError.value = null
  const { error } = await apiPatch('/api/v1/lawyers/me', { specializationCodes: selectedCodes.value })
  if (error) stepError.value = error.message
  else currentStep.value = 4
  saving.value = false
}

async function completeOnboarding() {
  completing.value = true
  stepError.value = null
  const { error } = await apiPost('/api/v1/lawyers/me/onboarding/complete', {})
  if (error) {
    stepError.value = error.message
    completing.value = false
    return
  }
  await auth.fetchMe().catch(() => undefined)
  completed.value = true
  setTimeout(() => router.push('/dashboard/lawyer'), 2000)
}
</script>
