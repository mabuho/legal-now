<template>
  <nav
    class="fixed bottom-0 left-0 w-full z-50 bg-surface-raised border-t border-border-subtle flex justify-around items-center h-16 md:hidden">
    <template v-for="item in bottomItems" :key="item.icon">
      <!-- AI button — special modal trigger -->
      <button v-if="item.icon === 'ai'" class="flex flex-col items-center justify-center flex-1 py-2 transition-colors"
        :class="isMobileAIAssistantOpen ? 'bg-gradient-to-br from-cyan-500 via-indigo-500 to-blue-500 text-transparent bg-clip-text' : 'text-text-muted'"
        @click="onAIAssistantClick">
        <svg v-if="!isMobileAIAssistantOpen" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none"
          viewBox="0 0 24 24" stroke="currentColor">
          <circle cx="12" cy="12" r="10" stroke-width="2" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4" />
        </svg>
        <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24">
          <defs>
            <linearGradient id="grad1" gradientUnits="userSpaceOnUse" x1="0%" y1="25%" x2="75%" y2="0%"
              gradientTransform="rotate(65)">
              <stop offset="0%" stop-color="var(--tw-gradient-from, #00b09b)" />
              <stop offset="100%" stop-color="var(--tw-gradient-to, #96c93d)" />
            </linearGradient>
          </defs>
          <circle cx="12" cy="12" r="10" stroke="url(#grad1)" stroke-width="2" fill="none" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4"
            stroke="url(#grad1)" />
        </svg>
        <span class="font-body text-xs mt-0.5">{{ item.label }}</span>
      </button>

      <!-- Regular nav button -->
      <button v-else-if="item.to" class="flex flex-col items-center justify-center flex-1 py-2 transition-colors"
        :class="route.path === item.to ? 'text-brand-primary' : 'text-text-muted'" @click="selectMenu(item.to)">
        <!-- Home icon -->
        <svg v-if="item.icon === 'home'" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none"
          viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M3 12l2-2m0 0l7-7 7 7m-9 2v8m4-8v8m5 0h2a2 2 0 002-2v-7a2 2 0 00-.586-1.414l-7-7a2 2 0 00-2.828 0l-7 7A2 2 0 003 10v7a2 2 0 002 2h2" />
        </svg>
        <!-- History icon -->
        <svg v-if="item.icon === 'history'" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none"
          viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3" />
          <circle cx="12" cy="12" r="10" stroke-width="2" />
        </svg>
        <!-- Profile icon -->
        <svg v-if="item.icon === 'profile'" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none"
          viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M5.121 17.804A9.969 9.969 0 0112 15c2.21 0 4.254.722 5.879 1.804M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
        <span class="font-body text-xs mt-0.5">{{ item.label }}</span>
      </button>
    </template>

  </nav>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNavMenu } from '@/composables/useNavMenu'
import { useRoute } from 'vue-router'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { bottomItems } = useNavMenu()

declare global {
  interface Window {
    openAIAssistantChat?: () => void
    closeAIAssistantChat?: () => void
    onAIAssistantCloseClick?: () => void
  }
}

const isMobileAIAssistantOpen = ref(false)
const previousPath = ref('')
const canSeeAIAssistant = authStore.user?.role === 'client'

function selectMenu(path: string) {
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
  if (previousPath.value) {
    router.push(previousPath.value)
  }
}

function onAIAssistantClick() {
  if (!isMobileAIAssistantOpen.value) {
    previousPath.value = route.path
    isMobileAIAssistantOpen.value = true
    window.openAIAssistantChat?.()
  } else {
    isMobileAIAssistantOpen.value = false
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
  selectMenu(router.currentRoute.value.path as string)
})

onUnmounted(() => {
  if (typeof window !== 'undefined') {
    window.onAIAssistantCloseClick = undefined
  }
})
</script>
