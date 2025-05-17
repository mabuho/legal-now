<script setup lang="ts">
import { Cog, Wrench } from 'lucide-vue-next'

defineProps<{
  title?: string
  description?: string
}>()
</script>

<template>
  <div class="min-h-[50vh] flex items-center justify-center">
    <div class="max-w-2xl mx-auto px-4 text-center">
      <div class="relative">
        <!-- Animated Gears -->
        <div
          class="absolute -top-16 left-1/2 -translate-x-1/2 flex items-center justify-center gap-2"
        >
          <Cog class="w-12 h-12 text-indigo-600 dark:text-indigo-400 animate-spin-slow" />
          <Wrench
            class="w-10 h-10 text-purple-600 dark:text-purple-400 animate-bounce-slow transform -rotate-45"
          />
          <Cog class="w-8 h-8 text-blue-600 dark:text-blue-400 animate-spin-reverse-slow" />
        </div>

        <!-- Content -->
        <div
          class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200 dark:border-gray-800 p-8 mt-8 relative overflow-hidden"
        >
          <div class="absolute inset-0 bg-grid opacity-5"></div>

          <h2
            class="text-3xl font-bold bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent mb-4"
          >
            {{ title || 'Página en Construcción' }}
          </h2>

          <p class="text-gray-600 dark:text-gray-400 text-lg mb-8">
            {{
              description ||
              'Estamos trabajando para brindarte la mejor experiencia. ¡Vuelve pronto!'
            }}
          </p>

          <div class="flex justify-center gap-4">
            <a
              href="/"
              class="inline-flex items-center justify-center rounded-xl bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 px-6 py-3 text-sm font-medium text-white shadow-sm hover:shadow-indigo-500/25 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transform transition-all duration-200 hover:-translate-y-0.5"
            >
              Volver al Inicio
            </a>
            <a
              href="/contact"
              class="inline-flex items-center justify-center rounded-xl border border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-900 px-6 py-3 text-sm font-medium text-gray-900 dark:text-gray-100 shadow-sm hover:bg-gray-50 dark:hover:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-gray-200 dark:focus:ring-gray-700 transform transition-all duration-200 hover:-translate-y-0.5"
            >
              Contactar
            </a>
          </div>

          <!-- Progress Bar -->
          <div class="mt-12 w-full bg-gray-100 dark:bg-gray-800 rounded-full h-1.5 overflow-hidden">
            <div
              class="bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 h-full rounded-full animate-progress"
            ></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.bg-grid {
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 32 32' width='32' height='32' fill='none' stroke='currentColor'%3e%3cpath d='M0 .5H31.5V32'/%3e%3c/svg%3e");
}

@keyframes spin-slow {
  to {
    transform: rotate(360deg);
  }
}

@keyframes spin-reverse-slow {
  to {
    transform: rotate(-360deg);
  }
}

@keyframes bounce-slow {
  0%,
  100% {
    transform: translateY(-10%) rotate(-45deg);
  }
  50% {
    transform: translateY(10%) rotate(-45deg);
  }
}

@keyframes progress {
  0% {
    width: 0%;
  }
  50% {
    width: 70%;
  }
  100% {
    width: 90%;
  }
}

.animate-spin-slow {
  animation: spin-slow 8s linear infinite;
}

.animate-spin-reverse-slow {
  animation: spin-reverse-slow 6s linear infinite;
}

.animate-bounce-slow {
  animation: bounce-slow 3s ease-in-out infinite;
}

.animate-progress {
  animation: progress 3s ease-in-out infinite;
}
</style>
