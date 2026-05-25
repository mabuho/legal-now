<template>
  <div class="bg-surface-base min-h-screen font-body">

    <Header />

    <main class="min-h-screen bg-surface-base pb-16 md:pb-0">
      <router-view />
    </main>

  </div>
</template>
<script setup lang="ts">
import Header from '@/components/Header.vue'
</script>
<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
