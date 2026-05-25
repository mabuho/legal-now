<script setup lang="ts">
defineProps<{ verifiedAt?: string | null }>()
</script>

<template>
  <span v-if="verifiedAt"
    class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-body font-semibold bg-green-500/10 text-green-400 border border-green-500/20">
    <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
      <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 00-1.414 0L8 12.586 4.707 9.293a1 1 0 00-1.414 1.414l4 4a1 1 0 001.414 0l8-8a1 1 0 000-1.414z" clip-rule="evenodd"/>
    </svg>
    Verificado
  </span>
  <span v-else
    class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-body font-semibold bg-surface-raised text-text-muted border border-border-subtle">
    Pendiente
  </span>
</template>
