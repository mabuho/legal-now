<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  MagnifyingGlassIcon,
  ChevronUpDownIcon,
  EllipsisHorizontalIcon,
  ChatBubbleLeftRightIcon,
  UserGroupIcon,
  CreditCardIcon,
  ClockIcon,
  BellIcon
} from '@heroicons/vue/24/outline'

interface User {
  id: string
  name: string
  email: string
  status: 'active' | 'pending' | 'inactive'
  registeredDate: string
  currentCase?: {
    id: string
    title: string
    lawyer: string
    status: 'en_progreso' | 'esperando_pago' | 'finalizado'
    lastUpdate: string
    specialization: string
  }
  lastBotInteraction?: string
  unreadMessages: number
}

const users = ref<User[]>([
  {
    id: '1',
    name: 'Juan Pérez',
    email: 'juan@example.com',
    status: 'active',
    registeredDate: '2024-03-15',
    currentCase: {
      id: 'C001',
      title: 'Disputa Laboral',
      lawyer: 'Dr. Carlos Rodríguez',
      status: 'en_progreso',
      lastUpdate: '2024-03-20',
      specialization: 'Laboral'
    },
    lastBotInteraction: '2024-03-19',
    unreadMessages: 3
  },
  {
    id: '2',
    name: 'María García',
    email: 'maria@example.com',
    status: 'active',
    registeredDate: '2024-03-14',
    currentCase: {
      id: 'C002',
      title: 'Divorcio',
      lawyer: 'Dra. Ana López',
      status: 'esperando_pago',
      lastUpdate: '2024-03-18',
      specialization: 'Familiar'
    },
    lastBotInteraction: '2024-03-17',
    unreadMessages: 0
  }
])

const searchQuery = ref('')
const selectedTab = ref('casos')

const filteredUsers = computed(() => {
  const query = searchQuery.value.toLowerCase()
  return users.value.filter(user => 
    user.name.toLowerCase().includes(query) ||
    user.email.toLowerCase().includes(query)
  )
})

const getStatusColor = (status: string) => {
  switch (status) {
    case 'active':
      return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
    case 'pending':
      return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400'
    case 'inactive':
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
    default:
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
  }
}

const getCaseStatusColor = (status: string) => {
  switch (status) {
    case 'en_progreso':
      return 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400'
    case 'esperando_pago':
      return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400'
    case 'finalizado':
      return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
    default:
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
  }
}

const tabs = [
  { id: 'casos', name: 'Casos', icon: ClockIcon },
  { id: 'bot', name: 'Bot Legal IA', icon: ChatBubbleLeftRightIcon },
  { id: 'pagos', name: 'Pagos', icon: CreditCardIcon },
  { id: 'notificaciones', name: 'Notificaciones', icon: BellIcon }
]
</script>

<template>
  <div class="p-6 space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
        Panel de Usuario
      </h1>
      <div class="flex items-center gap-4">
        <!-- Search -->
        <div class="relative">
          <MagnifyingGlassIcon class="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Buscar usuarios..."
            class="pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
          />
        </div>
      </div>
    </div>

    <!-- Navigation Tabs -->
    <div class="border-b border-gray-200 dark:border-gray-800">
      <nav class="-mb-px flex space-x-8" aria-label="Tabs">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="selectedTab = tab.id"
          :class="[
            selectedTab === tab.id
              ? 'border-indigo-500 text-indigo-600 dark:text-indigo-400'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 dark:text-gray-400 dark:hover:text-gray-300',
            'group inline-flex items-center py-4 px-1 border-b-2 font-medium text-sm'
          ]"
        >
          <component
            :is="tab.icon"
            :class="[
              selectedTab === tab.id ? 'text-indigo-500' : 'text-gray-400 group-hover:text-gray-500',
              '-ml-0.5 mr-2 h-5 w-5'
            ]"
            aria-hidden="true"
          />
          <span>{{ tab.name }}</span>
        </button>
      </nav>
    </div>

    <!-- Main Content -->
    <div class="mt-6">
      <!-- Cases Tab -->
      <div v-if="selectedTab === 'casos'" class="space-y-6">
        <div class="overflow-x-auto rounded-xl border border-gray-200 dark:border-gray-800">
          <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-800">
            <thead class="bg-gray-50 dark:bg-gray-900/50">
              <tr>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Usuario
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Caso Actual
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Abogado Asignado
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Estado
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Última Actualización
                </th>
                <th scope="col" class="relative px-6 py-3">
                  <span class="sr-only">Acciones</span>
                </th>
              </tr>
            </thead>
            <tbody class="bg-white dark:bg-gray-900 divide-y divide-gray-200 dark:divide-gray-800">
              <tr v-for="user in filteredUsers" :key="user.id" class="hover:bg-gray-50 dark:hover:bg-gray-800/50">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div>
                      <div class="text-sm font-medium text-gray-900 dark:text-white">
                        {{ user.name }}
                      </div>
                      <div class="text-sm text-gray-500 dark:text-gray-400">
                        {{ user.email }}
                      </div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900 dark:text-white">{{ user.currentCase?.title }}</div>
                  <div class="text-sm text-gray-500 dark:text-gray-400">{{ user.currentCase?.specialization }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900 dark:text-white">{{ user.currentCase?.lawyer }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    v-if="user.currentCase"
                    class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                    :class="getCaseStatusColor(user.currentCase.status)"
                  >
                    {{ user.currentCase.status }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                  {{ user.currentCase?.lastUpdate ? new Date(user.currentCase.lastUpdate).toLocaleDateString() : '-' }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button class="text-indigo-600 hover:text-indigo-900 dark:text-indigo-400 dark:hover:text-indigo-300">
                    Ver detalles
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Bot Legal IA Tab -->
      <div v-if="selectedTab === 'bot'" class="space-y-6">
        <div class="bg-white dark:bg-gray-900 shadow-sm rounded-lg border border-gray-200 dark:border-gray-800">
          <div class="px-4 py-5 sm:p-6">
            <h3 class="text-lg font-medium leading-6 text-gray-900 dark:text-white">Bot Legal con IA</h3>
            <div class="mt-2 max-w-xl text-sm text-gray-500 dark:text-gray-400">
              <p>Sistema de triage legal automático para clasificación de casos</p>
            </div>
            <div class="mt-5">
              <button type="button" class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                Iniciar Nueva Consulta
              </button>
            </div>
          </div>
          <div class="px-4 py-5 sm:p-6 border-t border-gray-200 dark:border-gray-800">
            <h4 class="text-sm font-medium text-gray-900 dark:text-white">Últimas Interacciones</h4>
            <ul class="mt-3 divide-y divide-gray-200 dark:divide-gray-800">
              <li v-for="user in filteredUsers.slice(0, 3)" :key="user.id" class="py-4">
                <div class="flex items-center space-x-4">
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-900 dark:text-white truncate">
                      {{ user.name }}
                    </p>
                    <p class="text-sm text-gray-500 dark:text-gray-400">
                      Última interacción: {{ user.lastBotInteraction }}
                    </p>
                  </div>
                  <div>
                    <button class="inline-flex items-center px-2.5 py-1.5 border border-transparent text-xs font-medium rounded text-indigo-700 bg-indigo-100 hover:bg-indigo-200 dark:bg-indigo-900/30 dark:text-indigo-400">
                      Ver detalles
                    </button>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Payments Tab -->
      <div v-if="selectedTab === 'pagos'" class="space-y-6">
        <div class="bg-white dark:bg-gray-900 shadow-sm rounded-lg border border-gray-200 dark:border-gray-800">
          <div class="px-4 py-5 sm:p-6">
            <h3 class="text-lg font-medium leading-6 text-gray-900 dark:text-white">Pasarela de Pagos</h3>
            <div class="mt-2 max-w-xl text-sm text-gray-500 dark:text-gray-400">
              <p>Gestión de pagos y transacciones</p>
            </div>
          </div>
          <div class="px-4 py-5 sm:p-6 border-t border-gray-200 dark:border-gray-800">
            <h4 class="text-sm font-medium text-gray-900 dark:text-white">Pagos Pendientes</h4>
            <ul class="mt-3 divide-y divide-gray-200 dark:divide-gray-800">
              <li v-for="user in filteredUsers.filter(u => u.currentCase?.status === 'esperando_pago')" :key="user.id" class="py-4">
                <div class="flex items-center space-x-4">
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-900 dark:text-white truncate">
                      {{ user.name }} - {{ user.currentCase?.title }}
                    </p>
                    <p class="text-sm text-gray-500 dark:text-gray-400">
                      Abogado: {{ user.currentCase?.lawyer }}
                    </p>
                  </div>
                  <div>
                    <button class="inline-flex items-center px-3 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
                      Procesar Pago
                    </button>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Notifications Tab -->
      <div v-if="selectedTab === 'notificaciones'" class="space-y-6">
        <div class="bg-white dark:bg-gray-900 shadow-sm rounded-lg border border-gray-200 dark:border-gray-800">
          <div class="px-4 py-5 sm:p-6">
            <h3 class="text-lg font-medium leading-6 text-gray-900 dark:text-white">Notificaciones y Mensajes</h3>
            <div class="mt-2 max-w-xl text-sm text-gray-500 dark:text-gray-400">
              <p>Seguimiento de casos y comunicaciones</p>
            </div>
          </div>
          <div class="px-4 py-5 sm:p-6 border-t border-gray-200 dark:border-gray-800">
            <ul class="divide-y divide-gray-200 dark:divide-gray-800">
              <li v-for="user in filteredUsers.filter(u => u.unreadMessages > 0)" :key="user.id" class="py-4">
                <div class="flex items-center space-x-4">
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center justify-between">
                      <p class="text-sm font-medium text-gray-900 dark:text-white truncate">
                        {{ user.name }}
                      </p>
                      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400">
                        {{ user.unreadMessages }} nuevos
                      </span>
                    </div>
                    <p class="text-sm text-gray-500 dark:text-gray-400">
                      Caso: {{ user.currentCase?.title }}
                    </p>
                  </div>
                  <div>
                    <button class="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm leading-4 font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-700 dark:hover:bg-gray-700">
                      Ver mensajes
                    </button>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template> 