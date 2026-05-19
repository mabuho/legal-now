<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useMemoryStore } from '@/stores/memoryStore'
import {
  HomeIcon,
  ShoppingBagIcon,
  ChatBubbleLeftRightIcon,
  DocumentTextIcon,
  BellIcon,
  UserCircleIcon,
  CreditCardIcon,
  ArrowLeftOnRectangleIcon,
  Bars3Icon
} from '@heroicons/vue/24/outline'
import { ConsultationStatus } from '@/types/chat'
import { emit } from 'process'

const router = useRouter()
const auth = useAuthStore()
const memory = useMemoryStore()
const currentUser = auth.user

const handleLogout = async () => {
  memory.stopPollingConsultations()
  await auth.logout()
  router.push('/login')
}

const menuItems = [
  {
    name: 'Inicio',
    icon: HomeIcon,
    route: '/dashboard',
    description: 'Panel principal con resumen de casos'
  },
  {
    name: 'Marketplace',
    icon: ShoppingBagIcon,
    route: '/dashboard/marketplace',
    description: 'Encuentra abogados por especialidad'
  },
  {
    name: 'Chat',
    icon: ChatBubbleLeftRightIcon,
    route: '/dashboard/chat',
    description: 'Historial de conversaciones'
  },
  {
    name: 'Documentos',
    icon: DocumentTextIcon,
    route: '/dashboard/documents',
    description: 'Genera documentos con IA'
  }
]
</script>

<template>
  <div class="hidden md:block"> <!-- We might not need this -->
    <nav class="sticky top-0 z-40 w-full backdrop-blur bg-slate-950/70 border-b border-slate-800">
      <div class="max-w-7xl flex mr-auto ml-auto pt-4 pr-6 pb-4 pl-6 items-center justify-between">
        <a href="/dashboard" class="flex items-center gap-2 select-none">
          <span class="text-xl font-semibold tracking-tight font-manrope text-white">LegalNow</span>
        </a>
        <div class="hidden md:flex items-center gap-8 text-sm font-medium">
          <router-link to="/dashboard"
            class="transition-colors font-manrope hover:text-cyan-400 text-slate-200">Dashboard</router-link>
          <router-link to="/dashboard/chat"
            class="transition-colors font-manrope hover:text-cyan-400 text-slate-200">Chat</router-link>
          <router-link to="/dashboard/history"
            class="transition-colors font-manrope hover:text-cyan-400 text-slate-200">History</router-link>
        </div>
        <div class="flex items-center gap-4">
          <img v-if="currentUser?.avatar" :src="currentUser.avatar" alt="avatar"
            class="h-8 w-8 rounded-full border border-cyan-500" />
          <span class="text-sm font-manrope text-slate-200">{{ currentUser?.name }}</span>
          <button @click="handleLogout"
            class="ml-2 px-3 py-1 rounded-md bg-cyan-600 hover:bg-cyan-500 text-white font-semibold font-manrope transition-colors">Salir</button>
        </div>
      </div>
      <div class="border-t border-slate-800"></div>
    </nav>
  </div>
</template>