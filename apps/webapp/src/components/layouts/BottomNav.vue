<template>
  <nav class="fixed bottom-0 left-0 w-full z-50 bg-surface-raised border-t border-border-subtle flex justify-around items-center h-16 md:hidden">
    <button
      class="flex flex-col items-center justify-center flex-1 py-2 transition-colors"
      :class="currentMenu === 'dashboard' ? 'text-brand-primary' : 'text-text-muted'"
      @click="selectMenu('dashboard', dashboardPath)"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M3 12l2-2m0 0l7-7 7 7m-9 2v8m4-8v8m5 0h2a2 2 0 002-2v-7a2 2 0 00-.586-1.414l-7-7a2 2 0 00-2.828 0l-7 7A2 2 0 003 10v7a2 2 0 002 2h2" />
      </svg>
      <span class="font-body text-xs mt-0.5">Consultas</span>
    </button>

    <button
      v-if="canSeeAIAssistant"
      class="flex flex-col items-center justify-center flex-1 py-2 transition-colors"
      :class="isMobileAIAssistantOpen ? 'bg-gradient-to-br from-cyan-500 via-indigo-500 to-blue-500 text-transparent bg-clip-text' : 'text-text-muted'"
      @click="onAIAssistantClick"
    >
      <svg v-if="!isMobileAIAssistantOpen" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <circle cx="12" cy="12" r="10" stroke-width="2" />
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4" />
      </svg>
      <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24">
        <defs>
          <linearGradient id="grad1" gradientUnits="userSpaceOnUse" x1="0%" y1="25%" x2="75%" y2="0%" gradientTransform="rotate(65)">
            <stop offset="0%" stop-color="var(--tw-gradient-from, #00b09b)" />
            <stop offset="100%" stop-color="var(--tw-gradient-to, #96c93d)" />
          </linearGradient>
        </defs>
        <circle cx="12" cy="12" r="10" stroke="url(#grad1)" stroke-width="2" fill="none" />
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4" stroke="url(#grad1)" />
      </svg>
      <span class="font-body text-xs mt-0.5">Asistente</span>
    </button>

    <button
      class="flex flex-col items-center justify-center flex-1 py-2 transition-colors"
      :class="currentMenu === 'history' ? 'text-brand-primary' : 'text-text-muted'"
      @click="selectMenu('history', '/dashboard/history')"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3" />
        <circle cx="12" cy="12" r="10" stroke-width="2" />
      </svg>
      <span class="font-body text-xs mt-0.5">Historial</span>
    </button>

    <button
      class="flex flex-col items-center justify-center flex-1 py-2 transition-colors"
      :class="currentMenu === 'profile' ? 'text-brand-primary' : 'text-text-muted'"
      @click="selectMenu('profile', '/dashboard/profile')"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M5.121 17.804A9.969 9.969 0 0112 15c2.21 0 4.254.722 5.879 1.804M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
      </svg>
      <span class="font-body text-xs mt-0.5">Cuenta</span>
    </button>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

declare global {
  interface Window {
    openAIAssistantChat?: () => void
    closeAIAssistantChat?: () => void
    onAIAssistantCloseClick?: () => void
  }
}

const dashboardPath = computed(() => {
  if (authStore.user?.role === 'client') return '/dashboard/client'
  if (authStore.user?.role === 'lawyer') return '/dashboard/lawyer'
  return '/dashboard/client'
})

const isMobileAIAssistantOpen = ref(false)
const currentMenu = ref('dashboard')
const previousMenu = ref('dashboard')
const canSeeAIAssistant = authStore.user?.role === 'client'

function selectMenu(menu: string, path: string) {
  if (menu === currentMenu.value) return
  previousMenu.value = currentMenu.value
  currentMenu.value = menu
  closeAIAssistant()
  router.push(path)
}

function closeAIAssistant() {
  if (!canSeeAIAssistant) return
  if (isMobileAIAssistantOpen.value) {
    isMobileAIAssistantOpen.value = false
    window.closeAIAssistantChat?.()
  }
}

function handleAIAssistantClose() {
  isMobileAIAssistantOpen.value = false
  if (previousMenu.value && previousMenu.value !== 'ai') {
    switch (previousMenu.value) {
      case 'dashboard':
        router.push('/dashboard/client')
        break
      case 'history':
        router.push('/dashboard/history')
        break
      case 'profile':
        router.push('/dashboard/profile')
        break
      default:
        router.push('/dashboard/client')
    }
    currentMenu.value = previousMenu.value
  }
}

function onAIAssistantClick() {
  if (!canSeeAIAssistant) return
  if (!isMobileAIAssistantOpen.value) {
    previousMenu.value = currentMenu.value
    currentMenu.value = 'ai'
    isMobileAIAssistantOpen.value = true
    window.openAIAssistantChat?.()
  } else {
    isMobileAIAssistantOpen.value = false
    currentMenu.value = previousMenu.value
    window.closeAIAssistantChat?.()
  }
}

onMounted(() => {
  if (typeof window !== 'undefined') {
    window.onAIAssistantCloseClick = handleAIAssistantClose
  }
  const menu = router.currentRoute.value.name?.toString().includes('dashboard')
    ? 'dashboard'
    : router.currentRoute.value.name as string
  selectMenu(menu, router.currentRoute.value.path as string)
})

onUnmounted(() => {
  if (typeof window !== 'undefined') {
    window.onAIAssistantCloseClick = undefined
  }
})
</script>
