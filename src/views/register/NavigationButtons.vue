<script setup lang="ts">
import { ChevronRightIcon, ChevronLeftIcon } from '@heroicons/vue/24/outline'

const props = defineProps<{
  isValid: boolean
  showNext?: boolean
  nextText?: string
  showPrevious?: boolean
}>()

const emit = defineEmits<{
  'next': []
  'previous': []
}>()
</script>

<template>
  <div class="flex justify-between mt-8">
    <button
      v-if="showPrevious"
      type="button"
      @click="$emit('previous')"
      class="inline-flex items-center px-6 py-3 text-base font-medium text-gray-700 bg-white border border-gray-300 rounded-xl hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 dark:bg-gray-800 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
    >
      <ChevronLeftIcon class="w-5 h-5 mr-2" />
      Anterior
    </button>
    <div v-else />
    <button
      v-if="showNext"
      type="button"
      :disabled="!isValid"
      @click="$emit('next')"
      :class="[
        'inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-xl shadow-sm text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all duration-200',
        isValid
          ? 'bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 hover:from-indigo-700 hover:via-blue-700 hover:to-purple-700 hover:-translate-y-0.5'
          : 'bg-gray-300 dark:bg-gray-700 cursor-not-allowed'
      ]"
    >
      {{ nextText || 'Siguiente' }}
      <ChevronRightIcon v-if="nextText === 'Siguiente'" class="ml-2 -mr-1 h-5 w-5" aria-hidden="true" />
    </button>
  </div>
</template> 