<script setup lang="ts">
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()

const isDashboardRoute = computed(() => {
  return route.path.startsWith('/dashboard')
})
</script>

<template>
  <div>
    <!-- Show header and footer only for non-dashboard routes -->
    <template v-if="!isDashboardRoute">
      <main>
        <router-view />
      </main>
    </template>

    <!-- Show only router view for dashboard routes -->
    <template v-else>
      <router-view />
    </template>
  </div>
</template>
