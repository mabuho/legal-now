<template>
    <div class="flex h-[calc(100vh-3.5rem)] bg-surface-base overflow-hidden">
        <!-- Chat List -->
        <div class="w-80 flex-shrink-0 border-r border-border-subtle flex flex-col bg-surface-base">
            <div class="bg-surface-base flex-1 flex flex-col overflow-hidden">
                <ChatListHeader />
                <ChatList :chats="chatStore.sessions" :consultations="consultations" :selected-chat="selectedChat" @selectChat="selectChat" />
            </div>
        </div>
        <!-- Chat Area -->
        <div class="flex-1 flex flex-col bg-surface-base overflow-hidden">
            <div class="flex-1 flex flex-col overflow-hidden">
                <ChatMessageHeader v-if="selectedChat" :selected-chat="selectedChat" :consultation="selectedConsultation" :can-call="canCall"
                    @goBack="goBackToChatList" />
                <div class="flex-1 min-h-0 overflow-y-auto">
                    <ChatMessages v-if="selectedChat" :selected-chat="selectedChat" :consultation="selectedConsultation" :messages="chatMessages" />
                </div>
                <ChatMessageInput v-if="selectedChat" :chat="selectedChat" @sendMessage="handleSendMessage"
                    @sendFiles="handleSendFiles" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { storeToRefs } from 'pinia'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth';
import { useMemoryStore } from '@/stores/memoryStore'
import { useChatSessionStore } from '@/stores/chatSessionStore';
import {
    initJanusLib,
    iniciarTextRoom,
    enviarMensajeTextRoomPorChat,
    cerrarSesionTextRoomChat
} from '@/services/janusService';
import ChatList from '@/components/chat/ChatList.vue'
import ChatMessages from '@/components/chat/ChatMessages.vue'
import ChatMessageHeader from '@/components/chat/ChatMessageHeader.vue'
import ChatMessageInput from '@/components/chat/ChatMessageInput.vue'
import {
    type Consultation,
    type ChatSession,
    ConsultationStatus,
} from '@/types/chat'
import ChatListHeader from '@/components/chat/ChatListHeader.vue';

const authStore = useAuthStore()
const consultationStore = useMemoryStore()
const chatStore = useChatSessionStore()
const route = useRoute()

const {
    selectedSession: selectedChat,
    messagesBySession: chatMessages,
} = storeToRefs(chatStore)

const currentUser = authStore.user
const canCall = ref(false)
const consultations = ref<Consultation[]>([])
const lastJanusChatId = ref<string | null>(null)

const selectedConsultation = computed(() =>
    consultations.value.find(c => c.id === selectedChat.value?.consultation_id) ?? null
)

let messagePollInterval: ReturnType<typeof setInterval> | null = null

function stopMessagePolling() {
    if (messagePollInterval) {
        clearInterval(messagePollInterval)
        messagePollInterval = null
    }
}

function startMessagePolling(sessionId: string) {
    stopMessagePolling()
    // TODO: Phase 6 — replace with WebSocket subscription.
    messagePollInterval = setInterval(() => {
        chatStore.fetchMessages(sessionId).catch(err =>
            console.warn('[ChatPanel] poll fetchMessages failed:', err)
        )
    }, 5000)
}

watch(selectedChat, (chat) => {
    if (chat) startMessagePolling(chat.id)
    else stopMessagePolling()
})

const isMobile = ref(window.innerWidth < 768)

function handleResize() {
    isMobile.value = window.innerWidth < 768
}

onMounted(async () => {
    window.addEventListener('resize', handleResize);
    await loadChatDetails()
    const preselectId = route.query.consultation_id
    if (typeof preselectId === 'string') {
        const session = chatStore.sessions.find(s => s.consultation_id === preselectId)
        if (session) selectChat(session)
    }
    // TODO: Revisar la forma de mantener la sesión activa,
    //       Desconectar al usuario e iniciar sesión nuevamente,
});

onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
    stopMessagePolling()
    cerrarSesionJanus()
});

const cerrarSesionJanus = () => {
    if (lastJanusChatId.value) {
        cerrarSesionTextRoomChat(lastJanusChatId.value)
        lastJanusChatId.value = null
    }
}

const loadChatDetails = async () => {
    await chatStore.fetchSessions()
    await consultationStore.fetchMine({ size: 100 })
    consultations.value = consultationStore.consultations.filter(c =>
        [ConsultationStatus.ACCEPTED, ConsultationStatus.SCHEDULED, ConsultationStatus.IN_PROGRESS].includes(c.status)
    )
}

async function selectChat(chat: ChatSession) {
    try {
        if (!chat) {
            console.warn('[selectChat] chat is undefined or null')
            return
        }
        canCall.value = chat.ended_at == null

        if (lastJanusChatId.value && lastJanusChatId.value !== chat.id) {
            cerrarSesionJanus()
        }

        chatStore.selectSession(chat)
        await chatStore.fetchMessages(chat.id)

        // Phase 3: Join pre-created Janus TextRoom (backend allocates on IN_PROGRESS)
        const consultation = selectedConsultation.value
        const roomId = consultation?.janus_room_id
        const pin = consultation?.janus_pin
        if (roomId && currentUser) {
            await initJanusLib()
            await iniciarTextRoom(chat.id, roomId, currentUser, pin)
        }
        lastJanusChatId.value = chat.id
    } catch (error) {
        console.error('[selectChat] Error:', error)
    }
}

function goBackToChatList() {
    cerrarSesionJanus()
    chatStore.selectSession(null)
}

const handleSendMessage = async (message: string) => {
    if (!selectedChat.value) return
    await chatStore.sendMessage(selectedChat.value.id, message)
    // Optimistic refresh so sender sees their own message without waiting for the 5s poll.
    chatStore.fetchMessages(selectedChat.value.id).catch(err =>
        console.warn('[ChatPanel] post-send fetchMessages failed:', err)
    )
    // Phase 3: Broadcast via Janus DataChannel
    if (lastJanusChatId.value) {
        enviarMensajeTextRoomPorChat(lastJanusChatId.value, { text: message, sender: currentUser?.email }).catch(err =>
            console.warn('[ChatPanel] Janus DataChannel send failed:', err)
        )
    }
}

const handleSendFiles = async (_filesData: { files: File[], caption: string }) => {
    // TODO: Phase 5 — file upload not yet wired to API
    console.warn('[handleSendFiles] TODO: Phase 5 — file upload not yet wired to API')
}
</script>
