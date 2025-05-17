<script setup lang="ts">
import DashboardNavigation from '@/components/dashboard/DashboardNavigation.vue'
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <DashboardNavigation />
    
    <main class="relative">
      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-6">
        <router-view />
      </div>
    </main>
  </div>
</template> 