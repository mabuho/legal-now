<script setup lang="ts">
import { ref } from 'vue'
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
import { useRouter } from 'vue-router'

interface User {
  name: string
  email: string
  avatar?: string
  role: 'client' | 'lawyer' | 'admin'
}

const router = useRouter()
const isMenuOpen = ref(false)
const isUserMenuOpen = ref(false)

// Mock user data - this should come from your auth store
const currentUser = ref<User>({
  name: 'Juan Pérez',
  email: 'juan@example.com',
  avatar: 'https://api.dicebear.com/9.x/avataaars/svg?seed=Sarah',
  role: 'client'
})

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

const unreadNotifications = ref(3)

const navigateTo = (route: string) => {
  router.push(route)
  isMenuOpen.value = false
}
</script>

<template>
  <nav class="sticky top-0 z-40 w-full bg-white/80 backdrop-blur-xl supports-[backdrop-filter]:bg-white/60 dark:bg-gray-900/80 border-b border-gray-200 dark:border-gray-800">
    <div class="mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex h-16 justify-between">
        <!-- Left side -->
        <div class="flex">
          <!-- Mobile menu button -->
          <div class="flex items-center lg:hidden">
            <button
              @click="isMenuOpen = !isMenuOpen"
              type="button"
              class="inline-flex items-center justify-center rounded-md p-2 text-gray-500 hover:bg-gray-100 hover:text-gray-600 dark:text-gray-400 dark:hover:bg-gray-800 dark:hover:text-gray-300"
            >
              <Bars3Icon class="h-6 w-6" />
            </button>
          </div>
          
          <!-- Desktop menu -->
          <div class="hidden lg:flex lg:space-x-8">
            <a href="#" class="flex items-center space-x-2 group">
              <span
                class="text-xl font-bold bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent group-hover:opacity-80 transition-opacity"
                >LegalNow</span
              >
            </a>
            <a
              v-for="item in menuItems"
              :key="item.name"
              :href="item.route"
              class="inline-flex items-center px-1 pt-1 text-sm font-medium text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white"
            >
              <component :is="item.icon" class="h-5 w-5 mr-2" />
              {{ item.name }}
            </a>
          </div>
        </div>

        <!-- Right side -->
        <div class="flex items-center space-x-4">
          <!-- Notifications -->
          <button
            type="button"
            class="relative rounded-full p-1 text-gray-500 hover:text-gray-600 dark:text-gray-400 dark:hover:text-gray-300"
          >
            <BellIcon class="h-6 w-6" />
            <span
              v-if="unreadNotifications > 0"
              class="absolute top-0 right-0 -mt-1 -mr-1 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-xs text-white"
            >
              {{ unreadNotifications }}
            </span>
          </button>

          <!-- Profile dropdown -->
          <div class="relative">
            <button
              @click="isUserMenuOpen = !isUserMenuOpen"
              type="button"
              class="flex items-center space-x-3 rounded-full text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
            >
              <img
                :src="currentUser.avatar"
                alt=""
                class="h-8 w-8 rounded-full"
              />
              <span class="hidden lg:flex lg:items-center">
                <span class="text-sm font-medium text-gray-700 dark:text-gray-300">{{ currentUser.name }}</span>
                <svg class="ml-2 h-5 w-5 text-gray-400" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
                </svg>
              </span>
            </button>

            <!-- Profile dropdown menu -->
            <div
              v-if="isUserMenuOpen"
              class="absolute right-0 mt-2 w-48 rounded-md bg-white py-1 shadow-lg ring-1 ring-black ring-opacity-5 dark:bg-gray-800 dark:ring-gray-700"
            >
              <a
                href="/dashboard/profile"
                class="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
              >
                <UserCircleIcon class="mr-3 h-5 w-5 text-gray-400" />
                Datos personales
              </a>
              <a
                href="/dashboard/payment-methods"
                class="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
              >
                <CreditCardIcon class="mr-3 h-5 w-5 text-gray-400" />
                Datos de pago
              </a>
              <button
                class="flex w-full items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
              >
                <ArrowLeftOnRectangleIcon class="mr-3 h-5 w-5 text-gray-400" />
                Cerrar sesión
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Mobile menu -->
    <div v-if="isMenuOpen" class="lg:hidden">
      <div class="space-y-1 pb-3 pt-2">
        <a
          v-for="item in menuItems"
          :key="item.name"
          :href="item.route"
          class="flex items-center px-4 py-2 text-base font-medium text-gray-600 hover:bg-gray-100 hover:text-gray-900 dark:text-gray-300 dark:hover:bg-gray-700 dark:hover:text-white"
        >
          <component :is="item.icon" class="mr-4 h-6 w-6 text-gray-400" />
          <div>
            <div>{{ item.name }}</div>
            <div class="text-sm text-gray-500 dark:text-gray-400">{{ item.description }}</div>
          </div>
        </a>
      </div>
    </div>
  </nav>
</template> 