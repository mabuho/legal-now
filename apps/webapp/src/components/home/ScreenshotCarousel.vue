<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ChevronLeftIcon, ChevronRightIcon } from '@heroicons/vue/24/outline'

const screenshots = [
  {
    src: './screenshots/dashboard1.png',
    alt: 'Panel de Control',
    title: 'Panel de Control Intuitivo',
    description: 'Gestiona todos tus casos y consultas legales desde un solo lugar',
  },
  {
    src: './screenshots/dashboard2.png',
    alt: 'Panel de Control',
    title: 'Panel de Control Intuitivo',
    description: 'Gestiona todos tus casos y consultas legales desde un solo lugar',
  },
  {
    src: './screenshots/marketplace1.png',
    alt: 'Marketplace de Abogados',
    title: 'Marketplace de Abogados',
    description: 'Encuentra al profesional ideal para tu caso',
  },
  {
    src: './screenshots/chat1.png',
    alt: 'Chat con Abogados',
    title: 'Chat en Tiempo Real',
    description: 'Comunícate directamente con tu abogado',
  },
  {
    src: './screenshots/chatbot_ai1.png',
    alt: 'Asistente Legal IA',
    title: 'Asistente Legal IA',
    description: 'Obtén respuestas inmediatas a tus dudas legales',
  },
  {
    src: './screenshots/user_profile1.png',
    alt: 'Perfil de Usuario',
    title: 'Perfil Personalizado',
    description: 'Gestiona tu información y preferencias',
  },
  {
    src: './screenshots/user_profile2.png',
    alt: 'Perfil de Usuario',
    title: 'Gestión de información personal',
    description: 'Gestiona tu información y preferencias',
  },
  {
    src: './screenshots/user_payments1.png',
    alt: 'Metodos de Pago',
    title: 'Metodos de Pago',
    description: 'Gestiona tus metodos de pago',
  },
  {
    src: './screenshots/documents1.png',
    alt: 'Documentos',
    title: 'Genera documentos con IA',
    description: 'Genera y revisa documentos legales de manera eficiente',
  },
]

const currentIndex = ref(0)
const autoplayInterval = ref<number | null>(null)
const isHovered = ref(false)

function next() {
  currentIndex.value = (currentIndex.value + 1) % screenshots.length
}
function prev() {
  currentIndex.value = (currentIndex.value - 1 + screenshots.length) % screenshots.length
}
function goTo(index: number) {
  currentIndex.value = index
}

onMounted(() => {
  autoplayInterval.value = window.setInterval(() => {
    if (!isHovered.value) next()
  }, 5000)
})
onUnmounted(() => {
  if (autoplayInterval.value) clearInterval(autoplayInterval.value)
})
</script>

<template>
  <div
    class="relative w-full overflow-hidden rounded-card bg-surface-card border border-border-default"
    @mouseenter="isHovered = true"
    @mouseleave="isHovered = false"
  >
    <div class="relative w-full" style="padding-top: 56.25%;">
      <div
        class="absolute inset-0 flex transition-transform duration-500 ease-in-out"
        :style="{ transform: `translateX(-${currentIndex * 100}%)` }"
      >
        <div
          v-for="(screenshot, index) in screenshots"
          :key="index"
          class="relative w-full flex-none"
        >
          <div class="absolute inset-0 flex items-center justify-center bg-surface-raised">
            <img
              :src="screenshot.src"
              :alt="screenshot.alt"
              class="max-w-full max-h-full object-contain"
              loading="lazy"
            />
          </div>
          <div class="absolute bottom-0 left-0 right-0 px-6 py-5 bg-gradient-to-t from-surface-base/90 to-transparent">
            <h3 class="font-heading font-semibold text-text-primary text-base">{{ screenshot.title }}</h3>
            <p class="font-body text-text-secondary text-sm mt-1">{{ screenshot.description }}</p>
          </div>
        </div>
      </div>
    </div>

    <button
      @click="prev"
      class="absolute left-3 top-1/2 -translate-y-1/2 w-9 h-9 flex items-center justify-center rounded-full bg-surface-base/60 hover:bg-surface-base/90 border border-border-default text-text-secondary hover:text-text-primary transition-colors"
      aria-label="Anterior"
    >
      <ChevronLeftIcon class="w-5 h-5" />
    </button>

    <button
      @click="next"
      class="absolute right-3 top-1/2 -translate-y-1/2 w-9 h-9 flex items-center justify-center rounded-full bg-surface-base/60 hover:bg-surface-base/90 border border-border-default text-text-secondary hover:text-text-primary transition-colors"
      aria-label="Siguiente"
    >
      <ChevronRightIcon class="w-5 h-5" />
    </button>

    <div class="flex justify-center gap-2 py-3">
      <div
        v-for="(_, index) in screenshots"
        :key="index"
        @click="goTo(index)"
        class="w-2 h-2 rounded-full cursor-pointer transition-colors"
        :class="index === currentIndex ? 'bg-brand-primary' : 'bg-border-default hover:bg-text-muted'"
      />
    </div>
  </div>
</template>
