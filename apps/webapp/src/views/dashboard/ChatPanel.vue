<template>
    <div class="bg-gradient-to-br from-cyan-950 via-blue-950 to-indigo-950 px-4 py-8 md:h-[calc(100vh-4rem)]">
        <div class="max-w-7xl mx-auto flex flex-col lg:grid lg:grid-cols-3 gap-4 lg:gap-8 h-full min-h-0">
            <!-- Chat List -->
            <div class="lg:col-span-1 flex flex-col gap-4 h-full min-h-0">
                <div class="bg-slate-900 border border-slate-800 rounded-2xl shadow p-4 md:p-6 flex-1 flex flex-col">
                    <ChatListHeader />
                    <ChatList :chats="chatStore.sessions" :consultations="consultations" :selected-chat="selectedChat" @selectChat="selectChat" />
                </div>
            </div>
            <!-- Chat Area -->
            <div class="lg:col-span-2 flex flex-col gap-4 h-full min-h-0">
                <div class="bg-slate-900 border border-slate-800 rounded-2xl shadow p-4 md:p-6 flex flex-col h-full">
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
} from '@/services/initTextRoomPerChat';
import ChatList from '@/components/chat/ChatList.vue'
import ChatMessages from '@/components/chat/ChatMessages.vue'
import ChatMessageHeader from '@/components/chat/ChatMessageHeader.vue'
import ChatMessageInput from '@/components/chat/ChatMessageInput.vue'
import {
    type Consultation,
    type ChatSession, // A Chat
    ConsultationStatus,
} from '@/types/chat'
import ChatListHeader from '@/components/chat/ChatListHeader.vue';
import { useJanus } from '@/composables/initJanusLib';

const authStore = useAuthStore()
const consultationStore = useMemoryStore()
const chatStore = useChatSessionStore()
const janusLib = useJanus()
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

// Responsive state
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

        // Cierra la sesión Janus del chat anterior si existe
        if (lastJanusChatId.value && lastJanusChatId.value !== chat.id) {
            cerrarSesionJanus()
        }

        chatStore.selectSession(chat)
        await chatStore.fetchMessages(chat.id)

        // Phase 3: Join pre-created Janus TextRoom (backend allocates on IN_PROGRESS)
        const consultation = selectedConsultation.value
        const roomId = consultation?.janus_room_id
        if (roomId && currentUser) {
            await initJanusLib()
            await iniciarTextRoom(chat.id, roomId, currentUser)
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

<style scoped>
/* Estilos específicos si son necesarios */
.h-screen {
    height: 87vh !important;
}

/* Estilos específicos para mobile */
@media (max-width: 768px) {
    .h-screen {
        height: 87vh;
        height: 87dvh;
        /* Dynamic viewport height para mobile */
    }
}

/* Mejorar scroll en mobile */
.overflow-y-auto {
    -webkit-overflow-scrolling: touch;
}

/* Asegurar que los botones sean fáciles de tocar en mobile */
button {
    min-height: 44px;
    /* Tamaño mínimo recomendado para touch */
}

/* Mejorar el input en mobile */
input {
    font-size: 16px;
    /* Evita zoom en iOS */
}
</style>
