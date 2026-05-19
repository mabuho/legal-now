<template>
  <nav class="sticky top-0 z-40 w-full backdrop-blur bg-slate-950/70 border-b border-slate-800">
    <div class="max-w-7xl flex mr-auto ml-auto pt-4 pr-6 pb-4 pl-6 items-center justify-between">
      <a href="/" class="flex items-center gap-2 select-none">
        <img src="/logo-cropped.svg" alt="LegalNow" class="h-8 w-8" />
        <span class="text-xl font-semibold tracking-tight font-manrope text-white">LegalNow</span>
      </a>
      <nav class="hidden md:flex items-center gap-8 text-sm font-medium">
        <a href="/" class="transition-colors font-manrope hover:text-cyan-400 text-slate-200">Inicio</a>
        <a href="/dashboard/marketplace"
          class="transition-colors font-manrope hover:text-cyan-400 text-slate-200">Marketplace</a>
        <a href="/dashboard/chat" class="transition-colors font-manrope hover:text-cyan-400 text-slate-200">Chat</a>
        <a href="/dashboard/documents"
          class="transition-colors font-manrope hover:text-cyan-400 text-slate-200">Documentos</a>
      </nav>
    </div>
    <div class="border-t border-slate-800"></div>
  </nav>
</template>

<script setup lang="ts">
// No additional logic needed for static navigation
</script>
