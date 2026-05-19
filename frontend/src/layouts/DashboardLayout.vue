<script setup lang="ts">
import { ref, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import DashboardNavigation from '@/components/layouts/DashboardNavigation.vue'
import BottomNav from '@/components/layouts/BottomNav.vue'
import AIAssistantChat from '@/components/chat/AIAssistantChat.vue'

const authStore = useAuthStore()
const canSeeAIAssistant = authStore.user?.role === 'client'

// Global AI Assistant overlay state
const showMobileAIAssistant = ref(false)
const chatMessages = ref([
  { id: 1, from: 'user', text: 'Hello, I need help with my plumbing request.' },
  { id: 2, from: 'assistant', text: 'Hello! I can help you with that. What is your request ID?' },
  { id: 3, from: 'user', text: "It's PR-12345." },
  { id: 4, from: 'assistant', text: 'Thank you. I\'m checking the status for you.' },
  { id: 5, from: 'user', text: 'What is the status of my request?' },
  { id: 6, from: 'assistant', text: 'I\'m checking the status for you.' },
  { id: 7, from: 'user', text: 'What is the status of my request?' },
  { id: 8, from: 'assistant', text: 'I\'m checking the status for you.' },
  { id: 9, from: 'user', text: 'What is the status of my request?' },
  { id: 10, from: 'assistant', text: 'I\'m checking the status for you.' },
  { id: 11, from: 'user', text: 'What is the status of my request?' },
  { id: 12, from: 'assistant', text: 'I\'m checking the status for you.' },
  { id: 13, from: 'user', text: 'What is the status of my request?' },
  { id: 14, from: 'assistant', text: 'I\'m checking the status for you.' },
  { id: 15, from: 'user', text: 'What is the status of my request?' },
  { id: 16, from: 'assistant', text: 'I\'m checking the status for you.' },
])
const chatInput = ref('')
function sendMessage() {
  if (chatInput.value.trim()) {
    chatMessages.value.push({ id: Date.now(), from: 'user', text: chatInput.value })
    chatInput.value = ''
  }
}
function openMobileAIAssistant() {
  showMobileAIAssistant.value = true
}
function closeMobileAIAssistant() {
  showMobileAIAssistant.value = false
  window.onAIAssistantCloseClick?.()
}
// Global event handlers
if (typeof window !== 'undefined') {
  window.openAIAssistantChat = openMobileAIAssistant
  window.closeAIAssistantChat = closeMobileAIAssistant
}
// Prevent background scroll
watch(showMobileAIAssistant, (val) => {
  if (typeof document !== 'undefined') {
    document.body.classList.toggle('overflow-hidden', val)
    document.documentElement.classList.toggle('overflow-hidden', val)
  }
})
</script>

<template>
  <div>
    <DashboardNavigation />
    <main class="pb-16 md:pb-20">
      <div>
        <router-view />
      </div>
    </main>
    <BottomNav />
    <!-- Global Mobile AI Assistant Overlay -->
    <transition v-if="canSeeAIAssistant" name="fade">
      <div v-if="showMobileAIAssistant" class="fixed inset-0 flex justify-center lg:hidden ">
        <div class="w-full max-w-md mx-auto bg-transparent mb-10">
          <div class="overflow-hidden">
            <AIAssistantChat :chatMessages="chatMessages" :chatInput="chatInput"
              @update:chatInput="val => chatInput = val" @send="sendMessage" :showClose="true"
              @close="closeMobileAIAssistant" />
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