<script setup lang="ts">
import { ref } from 'vue'
import {
  ClockIcon,
  ChatBubbleLeftRightIcon,
  UserGroupIcon,
  ArrowTopRightOnSquareIcon,
  ScaleIcon,
  DocumentTextIcon,
  CalendarIcon
} from '@heroicons/vue/24/outline'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

// Get the user details from the auth store
const currentUser = auth.user

interface LegalCase {
  id: string
  title: string
  lawyer: {
    name: string
    specialization: string
    avatar: string
  }
  status: 'en_progreso' | 'esperando_pago' | 'finalizado'
  lastUpdate: string
  nextAppointment?: string
}

interface ChatConsultation {
  id: string
  topic: string
  date: string
  status: 'pendiente_pago' | 'pendiente_asignacion'
  summary: string
}

// Mock data
const activeCases = ref<LegalCase[]>([
  {
    id: 'C001',
    title: 'Disputa Laboral',
    lawyer: {
      name: 'Dr. Carlos Rodríguez',
      specialization: 'Derecho Laboral',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Carlos'
    },
    status: 'en_progreso',
    lastUpdate: '2025-05-17',
    nextAppointment: '2025-06-25 09:00'
  },
  {
    id: 'C002',
    title: 'Divorcio',
    lawyer: {
      name: 'Dra. Ana López',
      specialization: 'Derecho Familiar',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jessica'
    },
    status: 'en_progreso',
    lastUpdate: '2025-04-23',
    nextAppointment: '2025-05-25 11:00'
  },
  {
    id: 'C003',
    title: 'Accidente de tránsito',
    lawyer: {
      name: 'Lic. Roberto Méndez',
      specialization: 'Derecho Civil',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Kingston'
    },
    status: 'finalizado',
    lastUpdate: '2024-06-20'
  },
  {
    id: 'C004',
    title: 'Pension alimenticia',
    lawyer: {
      name: 'Dra. Ana López',
      specialization: 'Derecho Familiar',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jessica'
    },
    status: 'finalizado',
    lastUpdate: '2024-11-20'
  },
  {
    id: 'C005',
    title: 'Guardia y custodia',
    lawyer: {
      name: 'Dra. Ana López',
      specialization: 'Derecho Familiar',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jessica'
    },
    status: 'finalizado',
    lastUpdate: '2025-01-19'
  }
])

const pendingConsultations = ref<ChatConsultation[]>([
  {
    id: 'CH001',
    topic: 'Consulta sobre contrato de arrendamiento',
    date: '2024-03-19',
    status: 'pendiente_pago',
    summary: 'Revisión de cláusulas y términos del contrato de arrendamiento'
  },
])

const quickActions = [
  {
    name: 'Nueva Consulta',
    description: 'Inicia una consulta con un abogado',
    icon: ChatBubbleLeftRightIcon,
    href: '/dashboard/chat/new',
    color: 'text-blue-600 dark:text-blue-400',
    bgColor: 'bg-blue-100 dark:bg-blue-900/30'
  },
  {
    name: 'Buscar Abogado',
    description: 'Encuentra el profesional ideal',
    icon: UserGroupIcon,
    href: '/dashboard/marketplace',
    color: 'text-purple-600 dark:text-purple-400',
    bgColor: 'bg-purple-100 dark:bg-purple-900/30'
  },
  {
    name: 'Documentos Legales',
    description: 'Genera documentos con IA',
    icon: DocumentTextIcon,
    href: '/dashboard/documents',
    color: 'text-green-600 dark:text-green-400',
    bgColor: 'bg-green-100 dark:bg-green-900/30'
  }
]

const getStatusColor = (status: string) => {
  switch (status) {
    case 'en_progreso':
      return 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400'
    case 'esperando_pago':
    case 'pendiente_pago':
      return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400'
    case 'pendiente_asignacion':
      return 'bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400'
    case 'finalizado':
      return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
    default:
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
  }
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('es-MX', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  }).format(date)
}

const formatDateTime = (dateString: string) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('es-MX', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: 'numeric',
    minute: 'numeric'
  }).format(date)
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-indigo-950 via-slate-950 to-black px-4 py-8">
    <div class="space-y-6 max-w-7xl mx-auto">
      <!-- Header with Welcome Message -->
      <div class="bg-gradient-to-r from-cyan-600 via-blue-600 to-indigo-700 rounded-2xl shadow-2xl p-10">
        <div class="max-w-4xl">
          <h1 class="text-4xl font-bold text-white mb-2 font-montserrat">
            ¡Bienvenido {{ currentUser?.name }}!
          </h1>
          <p class="text-cyan-100 text-lg font-manrope">
            {{ currentUser?.role === 'lawyer'
              ? 'Gestiona tus casos y consultas legales de manera eficiente.'
              : 'Tu asistente legal inteligente. ¿En qué podemos ayudarte hoy?'
            }}
          </p>
        </div>
      </div>
      <!-- Quick Actions Grid -->
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <a v-for="action in quickActions" :key="action.name" :href="action.href"
          class="relative group bg-slate-900 border border-slate-800 rounded-2xl shadow-lg overflow-hidden hover:shadow-2xl transition-all duration-200 p-6">
          <div class="flex items-center">
            <div :class="[action.bgColor, 'rounded-lg p-3']">
              <component :is="action.icon" :class="[action.color, 'h-6 w-6']" aria-hidden="true" />
            </div>
            <div class="ml-4">
              <h3 class="text-lg font-semibold text-slate-100 font-manrope">
                {{ action.name }}
              </h3>
              <p class="mt-1 text-sm text-slate-400 font-manrope">
                {{ action.description }}
              </p>
            </div>
          </div>
          <div class="mt-4 flex justify-end">
            <ArrowTopRightOnSquareIcon
              class="h-5 w-5 text-cyan-400 group-hover:text-cyan-300 transition-colors duration-200" />
          </div>
        </a>
      </div>
      <div class="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <!-- Active Cases -->
        <div class="bg-slate-900 border border-slate-800 shadow-2xl rounded-2xl overflow-hidden">
          <div class="border-b border-slate-800 p-6">
            <div class="flex items-center justify-between">
              <h2 class="text-xl font-semibold text-slate-100 flex items-center font-manrope">
                <ScaleIcon class="h-5 w-5 text-cyan-400 mr-2" />
                Casos Activos
              </h2>
              <span class="bg-cyan-100 text-cyan-800 py-1 px-2.5 rounded-full text-xs font-medium">
                {{ activeCases.length }} activos
              </span>
            </div>
          </div>
          <div v-if="activeCases.length > 0"
            class="divide-y divide-slate-800 max-h-[32rem] overflow-y-auto scrollbar-thin scrollbar-thumb-slate-700 scrollbar-track-transparent">
            <div v-for="legalCase in activeCases" :key="legalCase.id"
              class="p-6 hover:bg-slate-800 transition-colors duration-200">
              <div class="flex items-center gap-4">
                <img :src="legalCase.lawyer.avatar" alt="avatar"
                  class="h-10 w-10 rounded-full border border-cyan-500" />
                <div>
                  <h3 class="text-lg font-semibold text-slate-100 font-manrope">{{ legalCase.title }}</h3>
                  <p class="text-sm text-slate-400 font-manrope">{{ legalCase.lawyer.name }} — {{
                    legalCase.lawyer.specialization }}</p>
                </div>
                <span class="ml-auto text-xs font-medium px-2 py-1 rounded-full" :class="{
                  'bg-cyan-700 text-white': legalCase.status === 'en_progreso',
                  'bg-yellow-600 text-white': legalCase.status === 'esperando_pago',
                  'bg-green-700 text-white': legalCase.status === 'finalizado'
                }">
                  {{ legalCase.status }}
                </span>
              </div>
              <div class="mt-2 text-xs text-slate-400 font-manrope">
                Última actualización: {{ legalCase.lastUpdate }}
                <span v-if="legalCase.nextAppointment"> | Próxima cita: {{ legalCase.nextAppointment }}</span>
              </div>
            </div>
          </div>
        </div>
        <!-- Pending Consultations -->
        <div class="bg-slate-900 border border-slate-800 shadow-2xl rounded-2xl overflow-hidden">
          <div class="border-b border-slate-800 p-6">
            <div class="flex items-center justify-between">
              <h2 class="text-xl font-semibold text-slate-100 flex items-center font-manrope">
                <ChatBubbleLeftRightIcon class="h-5 w-5 text-cyan-400 mr-2" />
                Consultas Pendientes
              </h2>
              <span
                class="bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400 py-1 px-2.5 rounded-full text-xs font-medium">
                {{ pendingConsultations.length }} pendientes
              </span>
            </div>
          </div>
          <div v-if="pendingConsultations.length > 0"
            class="divide-y divide-slate-800 max-h-[32rem] overflow-y-auto scrollbar-thin scrollbar-thumb-slate-700 scrollbar-track-transparent">
            <div v-for="consultation in pendingConsultations" :key="consultation.id"
              class="p-6 hover:bg-slate-800 transition-colors duration-200">
              <div class="mb-4">
                <h3 class="text-lg font-semibold text-slate-100 font-manrope">{{ consultation.topic }}</h3>
                <p class="text-sm text-slate-400 font-manrope">{{ consultation.summary }}</p>
              </div>
              <div class="flex items-center justify-between">
                <div class="flex items-center text-sm text-slate-400 font-manrope">
                  <CalendarIcon class="h-4 w-4 mr-1" />
                  {{ formatDate(consultation.date) }}
                </div>
                <div class="flex items-center space-x-3">
                  <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                    :class="getStatusColor(consultation.status)">
                    {{ consultation.status.replace('_', ' ') }}
                  </span>
                  <button v-if="consultation.status === 'pendiente_pago'" type="button"
                    class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-full shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                    Pagar ahora
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>