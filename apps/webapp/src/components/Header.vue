<template>
  <header class="sticky top-0 z-20 bg-surface-base/80 backdrop-blur-md border-b border-border-subtle">
    <div v-if="!authStore.user" class="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">
      <a href="/"
        class="font-heading font-extrabold text-xl bg-gradient-to-r from-brand-primary to-indigo-400 bg-clip-text text-transparent select-none">
        LegalNow
      </a>

      <!-- Header Public -->
      <div class="hidden md:flex items-center gap-8">
        <template v-for="(item, index) in navItems" :key="index">
          <a v-if="item.sectionId && item.visible" :href="item.sectionId"
            class="font-body text-sm text-text-muted hover:text-text-primary transition-colors"
            @click.prevent="scrollToSection(item.sectionId!)">
            {{ item.label }}
          </a>
        </template>
      </div>

      <router-link v-if="route.name === 'home'" to="/login">
        <div
          class="font-body text-sm text-text-primary border border-border-default px-4 py-2 rounded-btn hover:border-brand-primary/40 transition-colors">
          Iniciar sesión
        </div>
      </router-link>
    </div>

    <!-- Header Auth -->
    <div v-if="authStore.user" class="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">
      <a :href="currentPath"
        class="font-heading font-extrabold text-xl bg-gradient-to-r from-brand-primary to-indigo-400 bg-clip-text text-transparent select-none">
        LegalNow
      </a>
      <div class="flex items-center gap-1">
        <nav class="hidden md:flex items-center gap-8">
          <template v-for="(item, index) in navItems" :key="index">
            <router-link v-if="item.to && item.visible && !item.highlight" :to="item.to" :class="['font-body text-sm px-3 py-1.5 rounded-lg transition-colors',
              route.path === item.to
                ? 'bg-brand-primary/10 text-brand-primary border border-brand-primary/20'
                : 'text-text-muted hover:text-text-secondary']">
              {{ item.label }}
            </router-link>
            <!-- AI Assistant -->
            <router-link v-if="item.to && item.visible && item.highlight" :to="item.to" @click="openAIAssistant"
              class="bg-gradient-to-r from-brand-primary-dark to-brand-accent text-white font-body font-semibold text-sm px-3 py-1.5 rounded-btn shadow-glow-btn hover:opacity-90 transition-opacity">
              ＋ {{ item.label }}
            </router-link>
            <span v-if="!item.to && item.visible"
              class="font-body text-xs text-amber-400 px-2 py-1 rounded bg-amber-400/10">
              {{ item.label }}
            </span>
          </template>
        </nav>
      </div>


      <div class="flex items-center gap-3">
        <div class="relative">
          <div @click="showAvatarDropdown = !showAvatarDropdown"
            class="w-[30px] h-[30px] bg-gradient-to-br from-brand-primary-dark to-brand-accent rounded-lg flex items-center justify-center font-heading font-bold text-xs text-white cursor-pointer select-none">
            {{ initials }}</div>
          <div v-if="showAvatarDropdown"
            class="absolute right-0 mt-2 w-36 bg-surface-card border border-border-default rounded-lg shadow-lg overflow-hidden"
            @mouseleave="showAvatarDropdown = false">
            <button @click="handleLogout"
              class="w-full text-left px-4 py-2.5 font-body text-sm text-text-secondary hover:text-text-primary hover:bg-surface-raised transition-colors">Salir</button>
          </div>
        </div>
      </div>

      <BottomNav />

      <transition v-if="canSeeAIAssistant" name="fade">
        <div v-if="showAIAssistant" class="fixed inset-0 flex justify-center z-50">
          <div class="w-full max-w-md mx-auto bg-transparent mb-10">
            <div class="overflow-hidden">
              <AIAssistantChat :chatMessages="chatMessages" :chatInput="chatInput"
                @update:chatInput="val => chatInput = val" @send="sendMessage" :showClose="true"
                @close="closeAIAssistant" />
            </div>
          </div>
        </div>
      </transition>

    </div>
  </header>
</template>
<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useMemoryStore } from '@/stores/memoryStore'
import { useNavMenu } from '@/composables/useNavMenu'
import BottomNav from '@/components/layouts/BottomNav.vue'
import AIAssistantChat from '@/components/chat/AIAssistantChat.vue'

const authStore = useAuthStore()
const memory = useMemoryStore()
const route = useRoute()
const router = useRouter()
const { navItems } = useNavMenu()

const initials = computed(() => authStore.user?.name?.[0]?.toUpperCase() ?? '?')
const canSeeAIAssistant = authStore.user?.role === 'client'
const showAvatarDropdown = ref(false)
const showAIAssistant = ref(false)
const currentPath = !authStore.user ? '/' : route.path
const chatInput = ref('')

declare global {
  interface Window {
    openAIAssistantChat?: () => void
    closeAIAssistantChat?: () => void
    onAIAssistantCloseClick?: () => void
  }
}

function scrollToSection(sectionId: string) {
  const element = document.querySelector(sectionId);
  if (element) {
    // Smoothly scroll to the target ID
    element.scrollIntoView({ behavior: 'smooth', block: 'start' });

    // (Optional) Update the URL hash without triggering router page reloading
    window.history.pushState(null, '', sectionId);
  }
};

const chatMessages = ref([
  { id: 1, from: 'user', text: 'Hola, necesito ayuda con mi consulta legal.' },
  { id: 2, from: 'assistant', text: '¡Hola! Con gusto te ayudo. ¿Cuál es tu consulta?' },
]);

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

watch(showAIAssistant, (val) => {
  if (typeof document !== 'undefined') {
    document.body.classList.toggle('overflow-hidden', val)
    document.documentElement.classList.toggle('overflow-hidden', val)
  }
})

async function handleLogout() {
  memory.stopPollingConsultations()
  await authStore.logout()
  router.push('/login')
}

</script>
