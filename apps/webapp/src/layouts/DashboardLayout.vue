<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useMemoryStore } from '@/stores/memoryStore'
import BottomNav from '@/components/layouts/BottomNav.vue'
import AIAssistantChat from '@/components/chat/AIAssistantChat.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const memory = useMemoryStore()

const consultasPath = computed(() =>
  authStore.user?.role === 'lawyer' ? '/dashboard/lawyer' : '/dashboard/client'
)
const initials = computed(() => authStore.user?.name?.[0]?.toUpperCase() ?? '?')
const canSeeAIAssistant = authStore.user?.role === 'client'

const showAvatarDropdown = ref(false)

async function handleLogout() {
  memory.stopPollingConsultations()
  await authStore.logout()
  router.push('/login')
}

const showAIAssistant = ref(false)
const chatMessages = ref([
  { id: 1, from: 'user', text: 'Hola, necesito ayuda con mi consulta legal.' },
  { id: 2, from: 'assistant', text: '¡Hola! Con gusto te ayudo. ¿Cuál es tu consulta?' },
])
const chatInput = ref('')

function sendMessage() {
  if (chatInput.value.trim()) {
    chatMessages.value.push({ id: Date.now(), from: 'user', text: chatInput.value })
    chatInput.value = ''
  }
}

function openAIAssistant() {
  showAIAssistant.value = true
}

function closeAIAssistant() {
  showAIAssistant.value = false
  window.onAIAssistantCloseClick?.()
}

if (typeof window !== 'undefined') {
  window.openAIAssistantChat = openAIAssistant
  window.closeAIAssistantChat = closeAIAssistant
}

watch(showAIAssistant, (val) => {
  if (typeof document !== 'undefined') {
    document.body.classList.toggle('overflow-hidden', val)
    document.documentElement.classList.toggle('overflow-hidden', val)
  }
})
</script>

<template>
  <div>
    <nav class="sticky top-0 z-40 w-full hidden md:block bg-surface-base/90 backdrop-blur-md border-b border-border-subtle">
      <div class="max-w-7xl mx-auto px-6 h-14 flex items-center justify-between">
        <router-link :to="consultasPath" class="select-none">
          <span class="font-heading font-extrabold text-xl bg-gradient-to-r from-brand-primary to-indigo-400 bg-clip-text text-transparent">LN</span>
        </router-link>

        <div class="flex items-center gap-1">
          <router-link
            :to="consultasPath"
            :class="[
              'font-body text-sm px-3 py-1.5 rounded-lg transition-colors',
              route.path === consultasPath
                ? 'bg-brand-primary/10 text-brand-primary border border-brand-primary/20'
                : 'text-text-muted hover:text-text-secondary'
            ]"
          >Consultas</router-link>
          <router-link
            to="/dashboard/history"
            :class="[
              'font-body text-sm px-3 py-1.5 rounded-lg transition-colors',
              route.path === '/dashboard/history'
                ? 'bg-brand-primary/10 text-brand-primary border border-brand-primary/20'
                : 'text-text-muted hover:text-text-secondary'
            ]"
          >Historial</router-link>
        </div>

        <div class="flex items-center gap-3">
          <button
            v-if="canSeeAIAssistant"
            @click="openAIAssistant"
            class="bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold text-sm px-3 py-1.5 rounded-btn shadow-glow-btn hover:opacity-90 transition-opacity"
          >＋ Nueva consulta</button>

          <div class="relative">
            <div
              @click="showAvatarDropdown = !showAvatarDropdown"
              class="w-[30px] h-[30px] bg-gradient-to-br from-brand-primary-dark to-brand-accent rounded-lg flex items-center justify-center font-heading font-bold text-xs text-white cursor-pointer select-none"
            >{{ initials }}</div>
            <div
              v-if="showAvatarDropdown"
              class="absolute right-0 mt-2 w-36 bg-surface-card border border-border-default rounded-lg shadow-lg overflow-hidden"
              @mouseleave="showAvatarDropdown = false"
            >
              <button
                @click="handleLogout"
                class="w-full text-left px-4 py-2.5 font-body text-sm text-text-secondary hover:text-text-primary hover:bg-surface-raised transition-colors"
              >Salir</button>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <main class="min-h-screen bg-surface-base pb-16 md:pb-0">
      <router-view />
    </main>

    <BottomNav />

    <transition v-if="canSeeAIAssistant" name="fade">
      <div v-if="showAIAssistant" class="fixed inset-0 flex justify-center z-50">
        <div class="w-full max-w-md mx-auto bg-transparent mb-10">
          <div class="overflow-hidden">
            <AIAssistantChat
              :chatMessages="chatMessages"
              :chatInput="chatInput"
              @update:chatInput="val => chatInput = val"
              @send="sendMessage"
              :showClose="true"
              @close="closeAIAssistant"
            />
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
