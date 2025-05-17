<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import {
  ChatBubbleLeftRightIcon,
  XMarkIcon,
  PaperAirplaneIcon,
  ArrowTopRightOnSquareIcon,
  CheckCircleIcon
} from '@heroicons/vue/24/outline'
import { legalTriageService, type ChatMessage } from '@/services/legalTriage'

const router = useRouter()
const isOpen = ref(false)
const message = ref('')
const messages = legalTriageService.getMessages()
const chatContainer = ref<HTMLElement | null>(null)
const isTyping = ref(false)

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    nextTick(() => {
      scrollToBottom()
    })
  }
}

const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

const formatTime = (timestamp: string) => {
  return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const handleSuggestedAction = (message: ChatMessage) => {
  if (!message.response?.suggestedAction) return

  const { type, specialization } = message.response.suggestedAction

  switch (type) {
    case 'consult_lawyer':
      router.push({
        path: '/dashboard/marketplace',
        query: { specialization }
      })
      break
    case 'document_review':
      router.push('/dashboard/documents')
      break
    case 'legal_information':
      router.push('/resources')
      break
    case 'emergency':
      // Handle emergency cases
      break
  }
}

const sendMessage = async () => {
  if (!message.value.trim()) return
  
  const content = message.value
  message.value = ''
  isTyping.value = true

  try {
    await legalTriageService.sendMessage(content)
    nextTick(() => {
      scrollToBottom()
    })
  } catch (error) {
    console.error('Error sending message:', error)
    // Handle error appropriately
  } finally {
    isTyping.value = false
  }
}
</script>

<template>
  <div class="fixed bottom-4 right-4 z-50">
    <!-- Chat Button -->
    <button
      @click="toggleChat"
      class="flex h-14 w-14 items-center justify-center rounded-full bg-indigo-600 text-white shadow-lg hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
      :class="{ 'rotate-0': !isOpen, 'rotate-90': isOpen }"
    >
      <component
        :is="isOpen ? XMarkIcon : ChatBubbleLeftRightIcon"
        class="h-6 w-6 transition-transform duration-200"
      />
    </button>

    <!-- Chat Window -->
    <div
      v-if="isOpen"
      class="absolute bottom-16 right-0 w-96 rounded-lg bg-white shadow-xl dark:bg-gray-900 transition-all duration-200"
    >
      <!-- Header -->
      <div class="border-b border-gray-200 dark:border-gray-800 p-4">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white">
          Bot Legal IA
        </h3>
        <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
          ¿En qué puedo ayudarte hoy?
        </p>
      </div>

      <!-- Chat Messages -->
      <div ref="chatContainer" class="h-96 overflow-y-auto p-4">
        <div class="space-y-4">
          <div
            v-for="msg in messages"
            :key="msg.id"
            class="flex items-start space-x-2"
            :class="{ 'justify-end': msg.sender === 'user' }"
          >
            <!-- Bot Avatar -->
            <div
              v-if="msg.sender === 'bot'"
              class="flex-shrink-0 h-8 w-8 rounded-full bg-indigo-100 dark:bg-indigo-900/30 flex items-center justify-center"
            >
              <ChatBubbleLeftRightIcon class="h-5 w-5 text-indigo-600 dark:text-indigo-400" />
            </div>

            <div class="flex flex-col max-w-[80%]">
              <!-- Message Bubble -->
              <div
                class="rounded-lg px-4 py-2"
                :class="{
                  'bg-indigo-600 text-white': msg.sender === 'user',
                  'bg-gray-100 dark:bg-gray-800 text-gray-900 dark:text-white': msg.sender === 'bot'
                }"
              >
                <p class="text-sm">{{ msg.content }}</p>
              </div>

              <!-- Suggested Actions -->
              <div
                v-if="msg.response?.suggestedAction"
                class="mt-2 flex flex-col space-y-2"
              >
                <button
                  @click="handleSuggestedAction(msg)"
                  class="inline-flex items-center space-x-2 text-sm text-indigo-600 dark:text-indigo-400 hover:text-indigo-500"
                >
                  <ArrowTopRightOnSquareIcon class="h-4 w-4" />
                  <span>Ver abogados disponibles</span>
                </button>
              </div>

              <!-- Next Steps -->
              <div
                v-if="msg.response?.nextSteps"
                class="mt-2 space-y-1"
              >
                <div
                  v-for="(step, index) in msg.response.nextSteps"
                  :key="index"
                  class="flex items-center space-x-2 text-sm text-gray-500 dark:text-gray-400"
                >
                  <CheckCircleIcon class="h-4 w-4 text-green-500" />
                  <span>{{ step }}</span>
                </div>
              </div>

              <span class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                {{ formatTime(msg.timestamp) }}
              </span>
            </div>
          </div>

          <!-- Typing Indicator -->
          <div v-if="isTyping" class="flex items-center space-x-2">
            <div class="flex-shrink-0 h-8 w-8 rounded-full bg-indigo-100 dark:bg-indigo-900/30 flex items-center justify-center">
              <ChatBubbleLeftRightIcon class="h-5 w-5 text-indigo-600 dark:text-indigo-400" />
            </div>
            <div class="bg-gray-100 dark:bg-gray-800 rounded-lg px-4 py-2">
              <div class="flex space-x-1">
                <div class="w-2 h-2 bg-gray-500 rounded-full animate-bounce"></div>
                <div class="w-2 h-2 bg-gray-500 rounded-full animate-bounce" style="animation-delay: 0.2s"></div>
                <div class="w-2 h-2 bg-gray-500 rounded-full animate-bounce" style="animation-delay: 0.4s"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Input Area -->
      <div class="border-t border-gray-200 dark:border-gray-800 p-4">
        <div class="flex items-center space-x-3">
          <input
            v-model="message"
            type="text"
            placeholder="Escribe tu mensaje..."
            class="flex-1 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 px-4 py-2 text-sm text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
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
</template> 