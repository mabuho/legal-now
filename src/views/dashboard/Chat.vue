<script setup lang="ts">
import { ref } from 'vue'
import {
  ChatBubbleLeftRightIcon,
  UserCircleIcon,
  PaperAirplaneIcon,
  ClockIcon
} from '@heroicons/vue/24/outline'

interface Message {
  id: string
  content: string
  sender: 'user' | 'bot' | 'lawyer'
  timestamp: string
  senderName?: string
  senderAvatar?: string
}

interface Conversation {
  id: string
  title: string
  type: 'bot' | 'lawyer'
  unreadCount: number
  lastMessage: string
  lastMessageTime: string
  participants: {
    name: string
    avatar?: string
    role: 'user' | 'bot' | 'lawyer'
  }[]
}

const conversations = ref<Conversation[]>([
  {
    id: '1',
    title: 'Consulta sobre Contrato Laboral',
    type: 'bot',
    unreadCount: 0,
    lastMessage: '¿Necesitas ayuda con algo más?',
    lastMessageTime: '2024-03-20T10:30:00',
    participants: [
      {
        name: 'Bot Legal',
        role: 'bot'
      }
    ]
  },
  {
    id: '2',
    title: 'Caso Laboral',
    type: 'lawyer',
    unreadCount: 2,
    lastMessage: 'Le envío los documentos solicitados',
    lastMessageTime: '2024-03-20T09:15:00',
    participants: [
      {
        name: 'Dr. Carlos Rodríguez',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Carlos',
        role: 'lawyer'
      }
    ]
  }
])

const selectedConversation = ref<string>('1')
const newMessage = ref('')

const messages = ref<Message[]>([
  {
    id: '1',
    content: '¡Hola! ¿En qué puedo ayudarte hoy?',
    sender: 'bot',
    timestamp: '2024-03-20T10:00:00'
  },
  {
    id: '2',
    content: 'Necesito ayuda con un contrato laboral',
    sender: 'user',
    timestamp: '2024-03-20T10:01:00'
  },
  {
    id: '3',
    content: 'Por supuesto, puedo ayudarte con eso. ¿Podrías proporcionarme más detalles sobre el contrato?',
    sender: 'bot',
    timestamp: '2024-03-20T10:02:00'
  }
])

const sendMessage = () => {
  if (!newMessage.value.trim()) return

  messages.value.push({
    id: Date.now().toString(),
    content: newMessage.value,
    sender: 'user',
    timestamp: new Date().toISOString()
  })

  newMessage.value = ''
}

const formatTime = (timestamp: string) => {
  return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const selectConversation = (conversationId: string) => {
  selectedConversation.value = conversationId
  // Aquí se cargarían los mensajes de la conversación seleccionada
}
</script>

<template>
  <div class="h-[calc(100vh-10rem)]">
    <div class="h-full flex rounded-lg overflow-hidden bg-white dark:bg-gray-800 shadow">
      <!-- Sidebar -->
      <div class="w-80 border-r border-gray-200 dark:border-gray-700">
        <div class="h-full flex flex-col">
          <div class="p-4 border-b border-gray-200 dark:border-gray-700">
            <h2 class="text-lg font-medium text-gray-900 dark:text-white">
              Conversaciones
            </h2>
          </div>

          <!-- Conversations List -->
          <div class="flex-1 overflow-y-auto">
            <div class="divide-y divide-gray-200 dark:divide-gray-700">
              <button
                v-for="conversation in conversations"
                :key="conversation.id"
                @click="selectConversation(conversation.id)"
                class="w-full p-4 flex items-start space-x-4 hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors duration-200"
                :class="{ 'bg-gray-50 dark:bg-gray-700/50': selectedConversation === conversation.id }"
              >
                <div class="flex-shrink-0">
                  <div
                    v-if="conversation.type === 'bot'"
                    class="h-10 w-10 rounded-full bg-indigo-100 dark:bg-indigo-900/30 flex items-center justify-center"
                  >
                    <ChatBubbleLeftRightIcon class="h-6 w-6 text-indigo-600 dark:text-indigo-400" />
                  </div>
                  <img
                    v-else
                    :src="conversation.participants[0].avatar"
                    :alt="conversation.participants[0].name"
                    class="h-10 w-10 rounded-full"
                  />
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center justify-between">
                    <p class="text-sm font-medium text-gray-900 dark:text-white truncate">
                      {{ conversation.title }}
                    </p>
                    <span class="text-xs text-gray-500 dark:text-gray-400">
                      {{ formatTime(conversation.lastMessageTime) }}
                    </span>
                  </div>
                  <p class="mt-1 text-sm text-gray-500 dark:text-gray-400 truncate">
                    {{ conversation.lastMessage }}
                  </p>
                </div>
                <div
                  v-if="conversation.unreadCount > 0"
                  class="flex-shrink-0 ml-2"
                >
                  <span class="inline-flex items-center justify-center h-5 w-5 rounded-full bg-indigo-600 text-xs font-medium text-white">
                    {{ conversation.unreadCount }}
                  </span>
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Chat Area -->
      <div class="flex-1 flex flex-col">
        <!-- Chat Header -->
        <div class="p-4 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <h2 class="text-lg font-medium text-gray-900 dark:text-white">
              {{ conversations.find(c => c.id === selectedConversation)?.title }}
            </h2>
          </div>
        </div>

        <!-- Messages -->
        <div class="flex-1 overflow-y-auto p-4 space-y-4">
          <div
            v-for="message in messages"
            :key="message.id"
            class="flex items-start space-x-4"
            :class="{ 'justify-end': message.sender === 'user' }"
          >
            <template v-if="message.sender !== 'user'">
              <div
                v-if="message.sender === 'bot'"
                class="flex-shrink-0 h-8 w-8 rounded-full bg-indigo-100 dark:bg-indigo-900/30 flex items-center justify-center"
              >
                <ChatBubbleLeftRightIcon class="h-5 w-5 text-indigo-600 dark:text-indigo-400" />
              </div>
              <img
                v-else
                :src="message.senderAvatar"
                :alt="message.senderName"
                class="h-8 w-8 rounded-full"
              />
            </template>

            <div
              class="flex flex-col"
              :class="{ 'items-end': message.sender === 'user' }"
            >
              <div
                class="inline-block rounded-lg px-4 py-2 max-w-lg"
                :class="{
                  'bg-indigo-600 text-white': message.sender === 'user',
                  'bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white': message.sender !== 'user'
                }"
              >
                <p class="text-sm">{{ message.content }}</p>
              </div>
              <span class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                {{ formatTime(message.timestamp) }}
              </span>
            </div>

            <template v-if="message.sender === 'user'">
              <div class="flex-shrink-0 h-8 w-8 rounded-full bg-gray-100 dark:bg-gray-700 flex items-center justify-center">
                <UserCircleIcon class="h-5 w-5 text-gray-500 dark:text-gray-400" />
              </div>
            </template>
          </div>
        </div>

        <!-- Input Area -->
        <div class="p-4 border-t border-gray-200 dark:border-gray-700">
          <div class="flex items-center space-x-4">
            <input
              v-model="newMessage"
              type="text"
              placeholder="Escribe un mensaje..."
              class="flex-1 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 px-4 py-2 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              @keyup.enter="sendMessage"
            />
            <button
              @click="sendMessage"
              class="inline-flex items-center justify-center rounded-lg bg-indigo-600 p-2 text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
            >
              <PaperAirplaneIcon class="h-5 w-5" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template> 