<template>
    <transition name="fade">
        <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
            <div class="relative w-full max-w-md mx-auto bg-slate-900 text-slate-100 rounded-2xl shadow-lg p-6 md:p-8 flex flex-col min-h-[60vh] md:min-h-0 md:w-[400px]"
                :class="{ 'h-full rounded-none md:rounded-2xl': isMobile }">
                <!-- Close (X) Button -->
                <button @click="handleClose"
                    class="absolute top-4 right-4 text-slate-400 hover:text-slate-200 text-2xl">
                    &times;
                </button>
                <!-- Optional header slot -->
                <slot name="header"></slot>
                <!-- Default slot for modal content -->
                <slot></slot>
            </div>
        </div>
    </transition>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps({
    visible: Boolean
})

const isMobile = computed(() => window.innerWidth < 768)

const emit = defineEmits(['close'])

function handleClose() {
    console.log('handleClose')
    emit('close')
}

</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}
</style>