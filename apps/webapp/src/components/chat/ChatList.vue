<template>
  <div class="flex flex-col h-full">
    <div class="flex-1 overflow-y-auto p-2 md:p-4 space-y-3">
      <div v-for="session in chats" :key="session.id" @click="$emit('selectChat', session)"
        class="bg-slate-800 border border-slate-700 rounded-xl p-4 mb-2 cursor-pointer shadow hover:shadow-lg transition-all duration-150 hover:bg-slate-700 flex items-center gap-3"
        :class="selectedChat && selectedChat.id === session.id ? 'ring-2 ring-cyan-500' : ''">
        <div class="flex-1 min-w-0">
          <span class="block font-semibold text-slate-100 truncate">
            {{ resolveCounterpartName(session) }}
          </span>
          <p class="text-xs text-slate-400 truncate">
            {{ formatSessionDate(session.started_at) }}
          </p>
        </div>
        <StatusPill :status="resolveSessionStatus(session)" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, defineComponent, h } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { type ChatSession, type Consultation } from '@/types/chat';

const authStore = useAuthStore();
const currentUser = authStore.user;

const props = defineProps<{
  selectedChat: ChatSession | null;
  chats: ChatSession[];
  consultations?: Consultation[];
}>();

defineEmits<{
  'selectChat': [session: ChatSession];
}>();

function resolveCounterpartName(session: ChatSession): string {
  const consultation = props.consultations?.find(c => c.id === session.consultation_id);
  if (!consultation) return 'Cargando…';
  return currentUser?.role === 'client'
    ? consultation.lawyer.name
    : consultation.client.name;
}

function resolveSessionStatus(session: ChatSession): 'active' | 'closed' {
  //console.log("DEBUG: Chat_Session - StartedAt: " + session.started_at + ", EndedAt: " + session.ended_at)
  return session.ended_at ? 'closed' : 'active';
}

function formatSessionDate(started_at: string): string {
  return new Date(started_at).toLocaleDateString();
}

// StatusPill: inline component reused in this file only.
const StatusPill = defineComponent({
  name: 'StatusPill',
  props: {
    status: { type: String as PropType<'active' | 'closed'>, required: true },
  },
  setup(props) {
    const colorMap: Record<string, string> = {
      active: 'bg-gradient-to-r from-blue-400 to-cyan-400 text-white',
      closed: 'bg-gradient-to-r from-orange-400 to-red-400 text-white',
    };
    return () =>
      h(
        'span',
        { class: ['text-xs px-3 py-1 rounded-full font-medium shadow-sm', colorMap[props.status]] },
        props.status === 'active' ? 'Activa' : 'Cerrada',
      );
  },
});
</script>
