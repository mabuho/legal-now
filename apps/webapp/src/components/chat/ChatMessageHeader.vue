<template>
  <div
    v-if="selectedChat"
    class="flex items-center justify-between bg-slate-900 px-4 md:px-6 py-2 border-b border-slate-800 rounded-2xl bg-slate-800"
  >
    <div class="flex-1 min-w-0">
      <h2 class="font-bold text-base md:text-lg text-slate-100 truncate">
        {{ counterpartName }}
      </h2>
      <p class="text-xs md:text-sm text-slate-400 flex items-center gap-2">
        <span class="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
        <span class="truncate">
          <!-- TODO: Phase 3 — janus_room_id will be populated by room allocation service -->
          {{ consultation ? consultation.title : 'Cargando…' }}
          <template v-if="consultation?.janus_room_id">
            — Sala #{{ consultation.janus_room_id }}
          </template>
        </span>
      </p>
    </div>
    <button
      :disabled="!canCall"
      class="p-2 md:p-3 rounded-full bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 disabled:from-slate-300 disabled:to-slate-400 disabled:cursor-not-allowed text-white shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-110 disabled:hover:scale-100 flex-shrink-0"
    >
      <PhoneIcon class="w-4 h-4 md:w-5 md:h-5" />
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { PhoneIcon } from '@lucide/vue';
import { type ChatSession, type Consultation } from '@/types/chat';

const authStore = useAuthStore();
const currentUser = authStore.user;

const props = defineProps<{
  selectedChat: ChatSession | null;
  canCall: boolean;
  consultation: Consultation | null;
}>();

const counterpartName = computed<string>(() => {
  if (!props.consultation) return 'Cargando…';
  return currentUser?.role === 'client'
    ? props.consultation.lawyer.name
    : props.consultation.client.name;
});
</script>
