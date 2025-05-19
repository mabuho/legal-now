<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ChevronLeftIcon, ChevronRightIcon } from '@heroicons/vue/24/outline'

const screenshots = [
  {
    src: './screenshots/dashboard1.png',
    alt: 'Panel de Control',
    title: 'Panel de Control Intuitivo',
    description: 'Gestiona todos tus casos y consultas legales desde un solo lugar'
  },
  {
    src: './screenshots/dashboard2.png',
    alt: 'Panel de Control',
    title: 'Panel de Control Intuitivo',
    description: 'Gestiona todos tus casos y consultas legales desde un solo lugar'
  },
  {
    src: './screenshots/marketplace1.png',
    alt: 'Marketplace de Abogados',
    title: 'Marketplace de Abogados',
    description: 'Encuentra al profesional ideal para tu caso'
  },
  {
    src: './screenshots/chat1.png',
    alt: 'Chat con Abogados',
    title: 'Chat en Tiempo Real',
    description: 'Comunícate directamente con tu abogado'
  },
  {
    src: './screenshots/chatbot_ai1.png',
    alt: 'Asistente Legal IA',
    title: 'Asistente Legal IA',
    description: 'Obtén respuestas inmediatas a tus dudas legales'
  },
  {
    src: './screenshots/user_profile1.png',
    alt: 'Perfil de Usuario',
    title: 'Perfil Personalizado',
    description: 'Gestiona tu información y preferencias'
  },
  {
    src: './screenshots/user_profile2.png',
    alt: 'Perfil de Usuario',
    title: 'Gestión de información personal',
    description: 'Gestiona tu información y preferencias'
  },
  {
    src: './screenshots/user_payments1.png',
    alt: 'Metodos de Pago',
    title: 'Metodos de Pago',
    description: 'Gestiona tus metodos de pago'
  },
  {
    src: './screenshots/documents1.png',
    alt: 'Documentos',
    title: 'Genera documentos con IA',
    description: 'Genera y revisa documentos legales de manera eficiente'
  }
]

const currentIndex = ref(0)
const autoplayInterval = ref<number | null>(null)
const isHovered = ref(false)

const next = () => {
  currentIndex.value = (currentIndex.value + 1) % screenshots.length
}

const prev = () => {
  currentIndex.value = (currentIndex.value - 1 + screenshots.length) % screenshots.length
}

const goToSlide = (index: number) => {
  currentIndex.value = index
}

const startAutoplay = () => {
  if (!isHovered.value) {
    autoplayInterval.value = window.setInterval(() => {
      next()
    }, 5000)
  }
}

const stopAutoplay = () => {
  if (autoplayInterval.value) {
    clearInterval(autoplayInterval.value)
    autoplayInterval.value = null
  }
}

const handleMouseEnter = () => {
  isHovered.value = true
  stopAutoplay()
}

const handleMouseLeave = () => {
  isHovered.value = false
  startAutoplay()
}

onMounted(() => {
  startAutoplay()
})

onUnmounted(() => {
  stopAutoplay()
})
</script>

<template>
  <div 
    class="relative w-full overflow-hidden rounded-2xl bg-white dark:bg-gray-800 shadow-xl"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
  >
    <!-- Screenshots Container -->
    <div class="relative w-full" style="padding-top: 56.25%"> <!-- 16:9 Aspect Ratio -->
      <div 
        class="absolute inset-0 flex transition-transform duration-500 ease-in-out"
        :style="{ transform: `translateX(-${currentIndex * 100}%)` }"
      >
        <div 
          v-for="(screenshot, index) in screenshots" 
          :key="index"
          class="relative w-full flex-none"
        >
          <div class="absolute inset-0 flex items-center justify-center bg-gray-100 dark:bg-gray-900">
            <img
              :src="screenshot.src"
              :alt="screenshot.alt"
              class="max-w-full max-h-full object-contain"
              loading="lazy"
            />
          </div>
          <!-- Gradient overlay -->
          <div class="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent pointer-events-none"></div>
          <!-- Caption -->
          <div class="absolute bottom-0 left-0 right-0 p-6 text-white">
            <h3 class="text-xl font-semibold mb-2">{{ screenshot.title }}</h3>
            <p class="text-sm text-gray-200">{{ screenshot.description }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Navigation Buttons -->
    <button
      @click="prev"
      class="absolute left-4 top-1/2 -translate-y-1/2 rounded-full bg-black/30 p-2 text-white backdrop-blur-sm transition hover:bg-black/50 focus:outline-none"
    >
      <ChevronLeftIcon class="h-6 w-6" />
    </button>
    <button
      @click="next"
      class="absolute right-4 top-1/2 -translate-y-1/2 rounded-full bg-black/30 p-2 text-white backdrop-blur-sm transition hover:bg-black/50 focus:outline-none"
    >
      <ChevronRightIcon class="h-6 w-6" />
    </button>

    <!-- Indicators -->
    <div class="absolute bottom-4 left-1/2 flex -translate-x-1/2 space-x-2">
      <button
        v-for="(_, index) in screenshots"
        :key="index"
        @click="goToSlide(index)"
        class="h-1.5 rounded-full transition-all duration-300"
        :class="[
          currentIndex === index 
            ? 'w-6 bg-white' 
            : 'w-1.5 bg-white/50 hover:bg-white/75'
        ]"
      />
    </div>
  </div>
</template>

<style scoped>
.flex-none {
  flex: 0 0 100%;
}
</style> 