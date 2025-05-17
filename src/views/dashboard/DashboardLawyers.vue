<script setup lang="ts">
import { ref } from 'vue'
import {
  MagnifyingGlassIcon,
  ChevronUpDownIcon,
  EllipsisHorizontalIcon,
  CheckBadgeIcon,
  XCircleIcon
} from '@heroicons/vue/24/outline'

interface Lawyer {
  id: string
  name: string
  email: string
  licenseNumber: string
  specialization: string
  status: 'verified' | 'pending' | 'rejected'
  registeredDate: string
}

const lawyers = ref<Lawyer[]>([
  {
    id: '1',
    name: 'Carlos Rodríguez',
    email: 'carlos@example.com',
    licenseNumber: '12345678',
    specialization: 'Derecho Civil',
    status: 'verified',
    registeredDate: '2024-03-15'
  },
  {
    id: '2',
    name: 'Ana López',
    email: 'ana@example.com',
    licenseNumber: '87654321',
    specialization: 'Derecho Penal',
    status: 'pending',
    registeredDate: '2024-03-14'
  },
  // Add more mock data as needed
])

const searchQuery = ref('')

const filteredLawyers = computed(() => {
  const query = searchQuery.value.toLowerCase()
  return lawyers.value.filter(lawyer => 
    lawyer.name.toLowerCase().includes(query) ||
    lawyer.email.toLowerCase().includes(query) ||
    lawyer.licenseNumber.includes(query) ||
    lawyer.specialization.toLowerCase().includes(query)
  )
})

const getStatusColor = (status: string) => {
  switch (status) {
    case 'verified':
      return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
    case 'pending':
      return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400'
    case 'rejected':
      return 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
    default:
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
  }
}

const getStatusIcon = (status: string) => {
  switch (status) {
    case 'verified':
      return CheckBadgeIcon
    case 'rejected':
      return XCircleIcon
    default:
      return null
  }
}
</script>

<template>
  <div class="p-6 space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
        Abogados
      </h1>
      <div class="flex items-center gap-4">
        <!-- Search -->
        <div class="relative">
          <MagnifyingGlassIcon class="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Buscar abogados..."
            class="pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
          />
        </div>
      </div>
    </div>

    <!-- Lawyers Table -->
    <div class="overflow-x-auto rounded-xl border border-gray-200 dark:border-gray-800">
      <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-800">
        <thead class="bg-gray-50 dark:bg-gray-900/50">
          <tr>
            <th
              scope="col"
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              <div class="flex items-center gap-1">
                Nombre
                <ChevronUpDownIcon class="h-4 w-4" />
              </div>
            </th>
            <th
              scope="col"
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              Email
            </th>
            <th
              scope="col"
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              Cédula
            </th>
            <th
              scope="col"
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              Especialización
            </th>
            <th
              scope="col"
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              Estado
            </th>
            <th
              scope="col"
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              Fecha de registro
            </th>
            <th scope="col" class="relative px-6 py-3">
              <span class="sr-only">Acciones</span>
            </th>
          </tr>
        </thead>
        <tbody class="bg-white dark:bg-gray-900 divide-y divide-gray-200 dark:divide-gray-800">
          <tr v-for="lawyer in filteredLawyers" :key="lawyer.id">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm font-medium text-gray-900 dark:text-white">
                {{ lawyer.name }}
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-500 dark:text-gray-400">
                {{ lawyer.email }}
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-500 dark:text-gray-400">
                {{ lawyer.licenseNumber }}
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-500 dark:text-gray-400">
                {{ lawyer.specialization }}
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                class="px-2 inline-flex items-center text-xs leading-5 font-semibold rounded-full gap-1"
                :class="getStatusColor(lawyer.status)"
              >
                <component
                  :is="getStatusIcon(lawyer.status)"
                  v-if="getStatusIcon(lawyer.status)"
                  class="h-4 w-4"
                />
                {{ lawyer.status }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
              {{ new Date(lawyer.registeredDate).toLocaleDateString() }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button class="text-gray-400 hover:text-gray-500 dark:hover:text-gray-300">
                <EllipsisHorizontalIcon class="h-5 w-5" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template> 