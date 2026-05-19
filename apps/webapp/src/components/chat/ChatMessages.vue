<template>
  <div class="bg-slate-900 border border-slate-800 rounded-2xl shadow p-4 md:p-6 h-full flex flex-col">
    <main
      v-if="selectedChat && sessionMessages.length > 0"
      ref="messagesEl"
      class="flex-1 overflow-y-auto space-y-2"
    >
      <MessageBubble
        v-for="msg in sessionMessages"
        :key="msg.id"
        :message="msg"
        :mine="msg.sender_id === currentUser?.id"
        :sender-name="resolveSenderName(msg.sender_id)"
      />
    </main>
    <div v-else class="flex flex-col items-center justify-center flex-1 text-slate-400 py-8">
      <div class="mb-2">
        <svg fill="currentColor" viewBox="0 0 20 20" class="w-8 h-8 opacity-60">
          <path d="M2 5a2 2 0 0 1 2-2h7a2 2 0 0 1 2 2v4a2 2 0 0 1-2 2H9l-3 3v-3H4a2 2 0 0 1-2-2V5z" />
        </svg>
      </div>
      <h3 class="font-semibold text-lg">Selecciona un chat</h3>
      <p class="text-sm">Elige una conversación para comenzar</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { type PropType, ref, computed, defineComponent, h, nextTick, watch, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import {
  type Consultation,
  type ChatMessage,
  type ChatMessagesHistory,
  type ChatSession,
} from '@/types/chat';
// ChatMessageType (TEXT/CALL/CARD/FILE) will be used in Phase 3 for Janus DataChannel events.

const authStore = useAuthStore();
const currentUser = authStore.user;

const props = defineProps<{
  selectedChat: ChatSession | null;
  messages: ChatMessagesHistory;
  // The consultation associated with the selected session, resolved by the parent.
  consultation: Consultation | null;
}>();

const messagesEl = ref<HTMLElement>();

const sessionMessages = computed<ChatMessage[]>(() => {
  if (!props.selectedChat) return [];
  return props.messages[props.selectedChat.id] ?? [];
});

/** Resolve a sender's display name from the embedded consultation users. */
function resolveSenderName(senderId: string): string {
  if (!props.consultation) return 'Usuario';
  if (senderId === props.consultation.client.id) return props.consultation.client.name;
  if (senderId === props.consultation.lawyer.id) return props.consultation.lawyer.name;
  return 'Usuario';
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesEl.value) {
      messagesEl.value.scrollTop = messagesEl.value.scrollHeight;
    }
  });
}

defineExpose({ scrollToBottom });

onMounted(() => {
  nextTick(scrollToBottom);
});

watch(
  () => props.messages,
  () => {
    if (props.selectedChat && props.messages[props.selectedChat.id]) {
      scrollToBottom();
    }
  },
  { deep: true },
);

watch(
  () => props.selectedChat,
  () => {
    if (props.selectedChat && props.messages[props.selectedChat.id]) {
      nextTick(scrollToBottom);
    }
  },
);

// MessageBubble — inline component; only TEXT and CALL types survive Phase 2b.
// FILE and CARD bubbles are preserved as stubs pending Phase 5.
const MessageBubble = defineComponent({
  name: 'MessageBubble',
  props: {
    message: { type: Object as PropType<ChatMessage>, required: true },
    mine: { type: Boolean, required: true },
    senderName: { type: String, required: true },
  },
  setup(props) {
    function renderContent(msg: ChatMessage) {
      // TEXT bubble (default for all messages — type field removed from API shape).
      // Remaining ChatMessageType values (CALL, CARD, FILE) are handled via
      // Janus DataChannel events in Phase 3; the API only delivers text bodies.
      return h('div', { class: 'space-y-1' }, [
        // Sender name (only for received messages)
        !props.mine
          ? h('p', { class: 'text-xs font-semibold opacity-70 mb-1' }, props.senderName)
          : null,
        h('p', { class: 'leading-relaxed' }, msg.body),
        h(
          'p',
          { class: 'text-xs opacity-60 mt-1 text-right' },
          new Date(msg.sent_at).toLocaleTimeString(),
        ),
      ]);
    }

    return () =>
      h('div', { class: ['flex', props.mine ? 'justify-end' : 'justify-start'] }, [
        h(
          'div',
          {
            class: [
              'max-w-[280px] md:max-w-xs p-3 md:p-4 rounded-2xl shadow-lg backdrop-blur-sm',
              props.mine
                ? 'bg-gradient-to-r from-blue-500 to-purple-500 text-white'
                : 'bg-white dark:bg-slate-700 border border-slate-200 dark:border-slate-600',
            ],
          },
          [renderContent(props.message)],
        ),
      ]);
  },
});
</script>
