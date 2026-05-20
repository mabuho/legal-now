<template>
    <div class="flex flex-col items-center justify-center h-full py-12">
        <svg v-if="type === 'success'" class="w-16 h-16 text-green-500 mb-4" fill="none" viewBox="0 0 24 24"
            stroke="currentColor">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" fill="none" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4" />
        </svg>
        <svg v-else-if="type === 'error'" class="w-16 h-16 text-red-500 mb-4" fill="none" viewBox="0 0 24 24"
            stroke="currentColor">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" fill="none" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 9l-6 6m0-6l6 6" />
        </svg>
        <h2 class="text-2xl font-bold mb-2 text-center"> {{ title }}</h2>
        <p class="text-center mb-6"> {{ message }}</p>
        <button @click="$emit('close-alert')"
            class="px-6 py-2 rounded-md bg-gradient-to-r from-brand-primary-dark to-brand-accent hover:opacity-90 transition-opacity text-white font-semibold">
            {{ buttonText }}
        </button>
    </div>
</template>

<script setup lang="ts">
const props = defineProps({
    title: {
        type: String,
        required: true
    },
    message: {
        type: String,
        required: true
    },
    type: {
        type: String,
        required: false
    },
    buttonText: {
        type: String,
        required: false,
        default: 'Done'
    }
})
const emit = defineEmits(['close-alert'])
</script>