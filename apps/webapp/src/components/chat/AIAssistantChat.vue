<template>
  <div :class="['bg-surface-card border border-surface-raised rounded-t-2xl shadow flex flex-col flex-1',
    'w-full max-w-md mx-auto p-4', 'lg:h-full', 'md:rounded-2xl', 'md:h-full', 'h-[calc(100dvh-4rem)]']">
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-xl font-bold font-heading text-text-primary flex items-center gap-2">
        <span class="inline-block"><svg xmlns='http://www.w3.org/2000/svg' class='w-5 h-5 text-cyan-400' fill='none'
            viewBox='0 0 24 24' stroke='currentColor'>
            <rect x='3' y='3' width='18' height='18' rx='2' stroke-width='2' />
          </svg></span>
        AI Assistant
      </h2>
      <!-- TODO: Phase 6 — wire real AI triage chat here -->
      <!-- <button @click="$emit('new-consult')"
                class="px-4 py-2 rounded-md bg-gradient-to-br from-brand-primary via-brand-accent to-blue-900 text-white font-semibold font-body transition-colors text-sm">
                Nueva consulta
            </button> -->
      <button v-if="showClose" @click="$emit('close')"
        class="ml-2 p-2 rounded-full hover:bg-surface-raised text-text-muted">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
    <!-- Chat Messages (scrollable) -->
    <div class="flex-1 min-h-0 flex flex-col gap-4 overflow-y-auto hide-scrollbar">
      <div v-for="msg in chatMessages" :key="(msg as any).id"
        :class="['rounded-xl px-4 py-2 max-w-[80%] font-body', (msg as any).from === 'user' ? 'bg-brand-primary text-white self-end' : 'bg-surface-raised text-text-primary self-start']">
        {{ (msg as any).text }}
      </div>
    </div>
    <!-- Chat Input (always at bottom) -->
    <div class="flex items-center gap-2 mt-4 pb-0 md:pb-4 sm:pb-24">
      <input v-model="inputMsg" type="text" placeholder="Type your message..."
        class="flex-1 rounded-xl px-4 py-2 bg-surface-raised border border-border-default text-text-primary placeholder-text-muted focus:outline-none"
        @keyup.enter="sendMessage" />
      <button @click="sendMessage" class="p-3 rounded-xl bg-brand-primary hover:bg-cyan-500 transition-colors">
        <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24"
          stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M22 2L11 13" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M22 2L15 22l-4-9-9-4 20-7z" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs } from 'vue'
const props = defineProps({
  chatMessages: {
    type: Array,
    required: true
  },
  chatInput: {
    type: String,
    required: true
  },
  showClose: {
    type: Boolean,
    default: false
  }
})
const emit = defineEmits(['update:chatInput', 'send', 'close', 'new-consult'])
const inputMsg = ref(props.chatInput)

function sendMessage() {
  if (props.chatInput.trim()) {
    emit('send')
  }
}
</script>

<style scoped>
.hide-scrollbar {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.hide-scrollbar::-webkit-scrollbar {
  display: none;
}
</style>
