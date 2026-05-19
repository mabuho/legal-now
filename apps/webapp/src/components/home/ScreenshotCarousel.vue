<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

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
  <v-card class="pa-0" flat>
    <div style="position: relative; width: 100%; overflow: hidden; border-radius: 16px;">
      <div style="position: relative; width: 100%; padding-top: 56.25%;">
        <div style="position: absolute; inset: 0; display: flex; transition: transform 0.5s;" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
          <div v-for="(screenshot, index) in screenshots" :key="index" style="position: relative; width: 100%; flex: none;">
            <div style="position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; background: #f3f4f6;">
              <img :src="screenshot.src" :alt="screenshot.alt" style="max-width: 100%; max-height: 100%; object-fit: contain;" loading="lazy" />
            </div>
            <div style="position: absolute; inset: 0; pointer-events: none;"></div>
            <div style="position: absolute; bottom: 0; left: 0; right: 0; padding: 24px; color: white;">
              <h3 style="font-size: 1.25rem; font-weight: 600; margin-bottom: 8px;">{{ screenshot.title }}</h3>
              <p style="font-size: 0.9rem;">{{ screenshot.description }}</p>
            </div>
          </div>
        </div>
      </div>
      <v-btn icon @click="prev" style="position: absolute; left: 16px; top: 50%; transform: translateY(-50%); background: rgba(0,0,0,0.3); color: white;">
        <v-icon>mdi-chevron-left</v-icon>
      </v-btn>
      <v-btn icon @click="next" style="position: absolute; right: 16px; top: 50%; transform: translateY(-50%); background: rgba(0,0,0,0.3); color: white;">
        <v-icon>mdi-chevron-right</v-icon>
      </v-btn>
    </div>
    <div style="display: flex; justify-content: center; margin-top: 8px;">
      <v-btn v-for="(screenshot, index) in screenshots" :key="index" icon size="x-small" :color="index === currentIndex ? 'primary' : 'grey'" @click="goTo(index)">
        <v-icon>mdi-circle</v-icon>
      </v-btn>
    </div>
  </v-card>
</template>

<style scoped>
.flex-none {
  flex: 0 0 100%;
}
</style> 