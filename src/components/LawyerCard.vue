<script setup lang="ts">
import {
  StarIcon,
  MapPinIcon,
  CurrencyDollarIcon
} from '@heroicons/vue/24/solid'

interface Lawyer {
  id: string
  name: string
  avatar: string
  specialization: string
  description: string
  rating: number
  reviewCount: number
  location: string
  baseRate: number
  workerType: string
}

defineProps<{
  lawyer: Lawyer
}>()

const getworkerTypeColor = (workerType: string) => {
  switch (workerType.toLowerCase()) {
    case 'independiente':
      return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
    case 'despacho':
      return 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400'
    default:
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
  }
}
</script>

<template>
  <div class="bg-white dark:bg-gray-800 overflow-hidden shadow rounded-lg">
    <div class="p-6">
      <!-- Avatar and Name -->
      <div class="flex items-center mb-4">
        <img
          :src="lawyer.avatar"
          :alt="lawyer.name"
          class="h-12 w-12 rounded-full"
        />
        <div class="ml-4">
          <h3 class="text-lg font-medium text-gray-900 dark:text-white">
            {{ lawyer.name }}
          </h3>
          <p class="text-sm text-gray-500 dark:text-gray-400">
            {{ lawyer.specialization }}
          </p>
        </div>
      </div>

      <!-- Description -->
      <p class="text-sm text-gray-600 dark:text-gray-300 mb-4">
        {{ lawyer.description }}
      </p>

      <!-- Stats -->
      <div class="grid grid-cols-2 gap-4 mb-4">
        <!-- Rating -->
        <div class="flex items-center">
          <StarIcon class="h-5 w-5 text-yellow-400" />
          <span class="ml-2 text-sm text-gray-600 dark:text-gray-300">
            {{ lawyer.rating }} ({{ lawyer.reviewCount }})
          </span>
        </div>

        <!-- Location -->
        <div class="flex items-center">
          <MapPinIcon class="h-5 w-5 text-gray-400" />
          <span class="ml-2 text-sm text-gray-600 dark:text-gray-300">
            {{ lawyer.location }}
          </span>
        </div>

        <!-- Rate -->
        <div class="flex items-center">
          <CurrencyDollarIcon class="h-5 w-5 text-gray-400" />
          <span class="ml-2 text-sm text-gray-600 dark:text-gray-300">
            ${{ lawyer.baseRate }}
          </span>
        </div>

        <!-- Worker Type -->
        <div class="flex items-center">
          <span
            class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
            :class="getworkerTypeColor(lawyer.workerType)"
          >
            {{ lawyer.workerType }}
          </span>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="flex space-x-2">
        <button
          type="button"
          class="flex-1 inline-flex justify-center items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          Contactar
        </button>
        <button
          type="button"
          class="flex-1 inline-flex justify-center items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          Ver perfil
        </button>
      </div>
    </div>
  </div>
</template> 