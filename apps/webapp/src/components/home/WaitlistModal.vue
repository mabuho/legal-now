<script setup lang="ts">
import { ref } from 'vue'
import { XMarkIcon } from '@heroicons/vue/24/outline'

const props = defineProps<{
  show: boolean
}>()

const emit = defineEmits<{
  'close': []
  'submit': [email: string]
}>()

const email = ref('')
const error = ref('')

const validateEmail = (email: string) => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

const handleSubmit = () => {
  if (!email.value) {
    error.value = 'Por favor ingresa tu correo electrónico'
    return
  }
  
  if (!validateEmail(email.value)) {
    error.value = 'Por favor ingresa un correo electrónico válido'
    return
  }

  error.value = ''
  emit('submit', email.value)
  email.value = ''
}
</script>

<template>
  <Teleport to="body">
    <div
      v-if="show"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
      @click="emit('close')"
    >
      <div
        class="bg-white dark:bg-gray-900 rounded-xl p-6 w-full max-w-md relative"
        @click.stop
      >
        <button
          class="absolute right-4 top-4 text-gray-400 hover:text-gray-500 dark:hover:text-gray-300"
          @click="emit('close')"
        >
          <XMarkIcon class="h-5 w-5" />
        </button>

        <div class="text-center mb-6">
          <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-2">
            ¡Únete a nuestra lista de espera!
          </h3>
          <p class="text-gray-600 dark:text-gray-400">
            Te notificaremos cuando estemos listos para brindarte nuestros servicios legales.
          </p>
        </div>

        <div class="space-y-4">
          <div>
            <label
              for="email"
              class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
            >
              Correo electrónico
            </label>
            <input
              id="email"
              v-model="email"
              type="email"
              class="block w-full px-4 py-3 rounded-xl border bg-white dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200"
              placeholder="tu@email.com"
              @keyup.enter="handleSubmit"
            />
            <p
              v-if="error"
              class="mt-2 text-sm text-red-600 dark:text-red-400"
            >
              {{ error }}
            </p>
          </div>

          <button
            class="w-full inline-flex items-center justify-center rounded-xl bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 px-4 py-3 text-sm font-medium text-white shadow-sm hover:shadow-indigo-500/25 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transform transition-all duration-200 hover:-translate-y-0.5"
            @click="handleSubmit"
          >
            Unirme a la lista
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template> 