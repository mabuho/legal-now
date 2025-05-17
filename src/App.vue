<script setup lang="ts">
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import NavigationHeader from '@/views/header/NavigationHeader.vue'
import Footer from '@/views/Footer.vue'
import FloatingChatbot from '@/components/chat/FloatingChatbot.vue'

const route = useRoute()

const isDashboardRoute = computed(() => {
  return route.path.startsWith('/dashboard')
})
</script>

<template>
  <div class="min-h-screen bg-white dark:bg-gray-900">
    <!-- Show header and footer only for non-dashboard routes -->
    <template v-if="!isDashboardRoute">
      <NavigationHeader />
      <main>
        <router-view />
      </main>
      <Footer />
    </template>

    <!-- Show only router view for dashboard routes -->
    <template v-else>
      <router-view />
      <FloatingChatbot />
    </template>
  </div>
</template>

<style>
/* Base transitions */
.page-enter-active,
.page-leave-active {
  transition: opacity 0.3s ease;
}

.page-enter-from,
.page-leave-to {
  opacity: 0;
}

/* Smooth scrolling */
html {
  scroll-behavior: smooth;
}

/* Focus styles */
*:focus-visible {
  outline: 2px solid theme('colors.indigo.500');
  outline-offset: 2px;
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 10px;
}

::-webkit-scrollbar-track {
  background: theme('colors.gray.100');
  border-radius: 5px;
}

::-webkit-scrollbar-thumb {
  background: theme('colors.gray.300');
  border-radius: 5px;
}

::-webkit-scrollbar-thumb:hover {
  background: theme('colors.gray.400');
}

/* Dark mode scrollbar */
.dark ::-webkit-scrollbar-track {
  background: theme('colors.gray.800');
}

.dark ::-webkit-scrollbar-thumb {
  background: theme('colors.gray.700');
}

.dark ::-webkit-scrollbar-thumb:hover {
  background: theme('colors.gray.600');
}
</style>
