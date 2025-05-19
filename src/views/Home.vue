<template>
  <div class="relative">
    <!-- Background Pattern -->
    <div class="absolute inset-0 bg-pattern opacity-[0.03] dark:opacity-[0.02]"></div>

    <!-- Hero Background -->
    <div class="absolute inset-0 h-[600px] bg-gradient-to-b from-indigo-50 dark:from-indigo-950/30 to-transparent"></div>
    
    <!-- Hero Decoration -->
    <div class="absolute right-0 top-0 h-[600px] w-1/2">
      <div class="absolute right-0 top-1/4 h-64 w-64 bg-gradient-to-br from-indigo-600/20 to-purple-600/20 rounded-full blur-3xl"></div>
      <div class="absolute right-48 top-1/3 h-48 w-48 bg-gradient-to-br from-blue-600/20 to-indigo-600/20 rounded-full blur-3xl"></div>
    </div>

    <!-- Content -->
    <div class="relative">
      <!-- Hero Section -->
      <section class="py-24 space-y-8 md:space-y-16">
        <div class="container mx-auto px-4">
          <div class="mx-auto flex max-w-[64rem] flex-col items-center gap-4 text-center">
            <h1
              class="text-6xl font-bold sm:text-5xl md:text-6xl lg:text-8xl bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent"
            >
              Servicios Legales Simplificados
            </h1>
            <p
              class="max-w-[42rem] leading-normal text-gray-600 dark:text-gray-400 sm:text-xl sm:leading-8"
            >
              Conectamos a personas y empresas con los mejores profesionales legales. <br>
              Obtén asesoría legal de calidad de manera rápida y eficiente.
            </p>
            <!-- <div class="flex gap-4">
              <a
                href="/register"
                class="inline-flex items-center justify-center rounded-xl bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 px-6 py-3 text-sm font-medium text-white shadow-sm hover:shadow-indigo-500/25 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transform transition-all duration-200 hover:-translate-y-0.5"
              >
                Comenzar Ahora
              </a>
              <a
                href="/services"
                class="inline-flex items-center justify-center rounded-xl border border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-900 px-6 py-3 text-sm font-medium text-gray-900 dark:text-gray-100 shadow-sm hover:bg-gray-50 dark:hover:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-gray-200 dark:focus:ring-gray-700 transform transition-all duration-200 hover:-translate-y-0.5"
              >
                Ver Servicios
              </a>
            </div> -->
            <button
              @click="showWaitlistModal = true"
              class="inline-flex items-center justify-center rounded-xl bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:shadow-indigo-500/25 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transform transition-all duration-200 hover:-translate-y-0.5"
            >
              Lista de espera
            </button>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section class="py-16 space-y-16">
        <div class="container mx-auto px-4">
          <div class="mx-auto flex max-w-[58rem] flex-col items-center space-y-4 text-center">
            <h2
              class="text-3xl font-bold leading-[1.1] sm:text-3xl md:text-5xl bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent"
            >
              Características
            </h2>
            <p
              class="max-w-[85%] leading-normal text-gray-600 dark:text-gray-400 sm:text-lg sm:leading-7"
            >
              Descubre por qué LegalNow es la plataforma preferida para servicios legales
            </p>
          </div>

          <div
            class="mx-auto grid justify-center gap-6 sm:grid-cols-2 md:max-w-[64rem] md:grid-cols-3 mt-12"
          >
            <div
              class="relative overflow-hidden flex flex-col rounded-xl border border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-900 p-2 hover:shadow-xl transition-all duration-200 hover:-translate-y-1"
            >
              <div
                class="absolute right-2 top-2 h-7 w-7 rounded-lg bg-gradient-to-br from-indigo-500 to-purple-500 opacity-10"
              ></div>
              <div class="flex flex-col space-y-1.5 p-6">
                <h3
                  class="text-2xl font-semibold leading-none tracking-tight bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent"
                >
                  Consultas Rápidas
                </h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">
                  Conecta con un abogado en minutos para resolver tus dudas legales
                </p>
              </div>
              <div class="p-6 pt-0">
                <p class="text-sm text-gray-600 dark:text-gray-400">
                  Nuestro sistema de matching te conecta con el profesional ideal para tu caso
                </p>
              </div>
            </div>

            <div
              class="relative overflow-hidden flex flex-col rounded-xl border border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-900 p-2 hover:shadow-xl transition-all duration-200 hover:-translate-y-1"
            >
              <div
                class="absolute right-2 top-2 h-7 w-7 rounded-lg bg-gradient-to-br from-blue-500 to-indigo-500 opacity-10"
              ></div>
              <div class="flex flex-col space-y-1.5 p-6">
                <h3
                  class="text-2xl font-semibold leading-none tracking-tight bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent"
                >
                  Documentos Legales
                </h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">
                  Genera y revisa documentos legales de manera eficiente
                </p>
              </div>
              <div class="p-6 pt-0">
                <p class="text-sm text-gray-600 dark:text-gray-400">
                  Plantillas personalizables y revisión por expertos legales
                </p>
              </div>
            </div>

            <div
              class="relative overflow-hidden flex flex-col rounded-xl border border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-900 p-2 hover:shadow-xl transition-all duration-200 hover:-translate-y-1"
            >
              <div
                class="absolute right-2 top-2 h-7 w-7 rounded-lg bg-gradient-to-br from-purple-500 to-blue-500 opacity-10"
              ></div>
              <div class="flex flex-col space-y-1.5 p-6">
                <h3
                  class="text-2xl font-semibold leading-none tracking-tight bg-gradient-to-r from-purple-600 to-blue-600 bg-clip-text text-transparent"
                >
                  Seguimiento de Casos
                </h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">
                  Mantente informado sobre el progreso de tus casos
                </p>
              </div>
              <div class="p-6 pt-0">
                <p class="text-sm text-gray-600 dark:text-gray-400">
                  Panel de control intuitivo para gestionar todos tus asuntos legales
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Screenshots Carousel Section -->
      <section class="py-16 bg-gray-50 dark:bg-gray-800/50">
        <div class="container mx-auto px-4">
          <div class="mx-auto flex max-w-[58rem] flex-col items-center space-y-4 text-center mb-12">
            <h2
              class="text-3xl font-bold leading-[1.1] sm:text-3xl md:text-5xl bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent"
            >
              Conoce la Plataforma
            </h2>
            <p
              class="max-w-[85%] leading-normal text-gray-600 dark:text-gray-400 sm:text-lg sm:leading-7"
            >
              Explora las características que hacen de LegalNow la solución ideal para tus necesidades legales
            </p>
          </div>
          
          <div class="max-w-6xl mx-auto">
            <ScreenshotCarousel />
          </div>
        </div>
      </section>
    </div>
    <WaitlistModal
        :show="showWaitlistModal"
        @close="showWaitlistModal = false"
        @submit="handleWaitlistSubmit"
      />
  </div>
</template>

<script setup lang="ts">
import ScreenshotCarousel from '@/components/ScreenshotCarousel.vue'
import WaitlistModal from '@/components/WaitlistModal.vue'
import { ref } from 'vue'

const showWaitlistModal = ref(false)

const handleWaitlistSubmit = (email: string) => {
  console.log('Waitlist email:', email)
  showWaitlistModal.value = false
}
</script>

<style scoped>
.bg-pattern {
  background-color: #ffffff;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='60' height='60' viewBox='0 0 60 60'%3E%3Cpath d='M54.627,0.393 L59.088,0.393 L59.088,3.936 L54.627,3.936 L54.627,8.396 L50.176,8.396 L50.176,3.936 L45.715,3.936 L45.715,0.393 L50.176,0.393 L50.176,-4.068 L54.627,-4.068 L54.627,0.393 Z' fill='%236366f1' fill-opacity='0.4'%3E%3C/path%3E%3C/svg%3E");
}

:deep(.dark) .bg-pattern {
  background-color: #111827;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='60' height='60' viewBox='0 0 60 60'%3E%3Cpath d='M54.627,0.393 L59.088,0.393 L59.088,3.936 L54.627,3.936 L54.627,8.396 L50.176,8.396 L50.176,3.936 L45.715,3.936 L45.715,0.393 L50.176,0.393 L50.176,-4.068 L54.627,-4.068 L54.627,0.393 Z' fill='%236366f1' fill-opacity='0.2'%3E%3C/path%3E%3C/svg%3E");
}
</style>
