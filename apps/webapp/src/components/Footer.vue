<script setup lang="ts">
const currentYear = new Date().getFullYear()
</script>

<template>
  <!-- FOOTER -->
  <footer id="contact" class="border-t bg-zinc-950 border-zinc-800">
    <div class="mx-auto max-w-7xl px-6 py-16 grid gap-8 md:grid-cols-4 text-sm">
      <div class="space-y-4">
        <div class="flex items-center gap-2">
          <img src="@/assets/logo.png" alt="Consulta Legal" class="h-13 w-auto py-2 object-contain">
          <span
            class="font-heading font-extrabold text-xl bg-gradient-to-r from-brand-primary to-indigo-400 bg-clip-text text-transparent select-none">
            Consulta Legal
          </span>
        </div>
        <p class="text-zinc-400">Asesoramiento legal rápido y fiable cuando lo necesite.</p>
        <!--<div class="flex gap-4 mt-4">
          <a href="#" class="hover:text-indigo-400">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
              data-lucide="twitter" strokewidth="1.5" class="lucide lucide-twitter w-5 h-5">
              <path
                d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z">
              </path>
            </svg>
          </a>
          <a href="#" class="hover:text-indigo-400">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
              data-lucide="linkedin" strokewidth="1.5" class="lucide lucide-linkedin w-5 h-5">
              <path d="M16 8a6 6 0 0 1 6 6v7h-4v-7a2 2 0 0 0-2-2 2 2 0 0 0-2 2v7h-4v-7a6 6 0 0 1 6-6z"></path>
              <rect width="4" height="12" x="2" y="9"></rect>
              <circle cx="4" cy="4" r="2"></circle>
            </svg>
          </a>
          <a href="#" class="hover:text-indigo-400">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
              data-lucide="facebook" strokewidth="1.5" class="lucide lucide-facebook w-5 h-5">
              <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path>
            </svg>
          </a>
        </div> -->
      </div>
      <!-- Update this section -->
      <div class="space-y-2">
        <h4 class="font-body text-xs font-bold text-text-muted uppercase tracking-widest mb-4">Producto</h4>
        <a href="#" class="block font-body text-sm text-text-secondary hover:text-indigo-400">Como funciona</a>
        <a href="#" class="block font-body text-sm text-text-secondary hover:text-indigo-400">Abogados</a>
        <a href="#" class="block font-body text-sm text-text-secondary hover:text-indigo-400">Especialidades</a>
      </div>
      <div class="space-y-2">
        <h4 class="font-body text-xs font-bold text-text-muted uppercase tracking-widest mb-4">Abogados</h4>
        <a href="#" class="block font-body text-sm text-text-secondary hover:text-indigo-400">Unirse</a>
        <a href="#" class="block font-body text-sm text-text-secondary hover:text-indigo-400">Requisitos</a>
        <a href="#" class="block font-body text-sm text-text-secondary hover:text-indigo-400">Pagos</a>
      </div>
      <div class="space-y-2">
        <h4 class="font-body text-xs font-bold text-text-muted uppercase tracking-widest mb-4">Legal</h4>
        <a href="#" class="block font-body text-sm text-text-secondary hover:text-indigo-400">Privacidad</a>
        <a href="#" class="block font-body text-sm text-text-secondary hover:text-indigo-400">Terminos</a>
        <a href="#" class="block font-body text-sm text-text-secondary hover:text-indigo-400">Contacto</a>
      </div>
    </div>
    <div class="border-t text-center py-6 text-xs text-zinc-500 border-zinc-800">
      © <span id="year">{{ currentYear }}</span> LegalNow Inc. Todos los derechos reservados.
    </div>
  </footer>
</template>

<style scoped>
@reference "tailwindcss";

/* Base styles */
.footer-link {
  @apply text-base text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white transition-colors duration-200;
}

.social-icon {
  @apply w-10 h-10 flex items-center justify-center rounded-full bg-gradient-to-br from-gray-50 to-gray-100 hover:from-gray-100 hover:to-gray-200 dark:from-gray-800 dark:to-gray-700 dark:hover:from-gray-700 dark:hover:to-gray-600 transform transition-all duration-300 hover:scale-110 hover:shadow-md dark:shadow-gray-900/30;
}

/* Enhanced hover effects */
a {
  position: relative;
  transition: all 0.3s ease;
}

a::after {
  content: '';
  position: absolute;
  width: 0;
  height: 2px;
  bottom: -2px;
  left: 0;
  @apply bg-gradient-to-r from-indigo-600 to-purple-600;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 1px;
}

a:hover::after {
  width: 100%;
}

/* Social icons enhancements */
.social-icon svg {
  @apply transition-transform duration-300;
}

.social-icon:hover svg {
  @apply transform scale-110 text-indigo-600 dark:text-indigo-400;
}

/* Language selector styling */
select {
  @apply bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-700 border-gray-200 dark:border-gray-700 rounded-lg px-4 py-2 text-sm text-gray-700 dark:text-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-300 cursor-pointer hover:shadow-md dark:shadow-gray-900/30;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236B7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 0.5rem center;
  background-repeat: no-repeat;
  background-size: 1.5em 1.5em;
  padding-right: 2.5rem;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
}

/* Container animations */
.container {
  @apply transition-all duration-300;
}

/* Grid item hover effects */
.grid>div {
  @apply transition-all duration-300 hover:transform hover:translate-y-[-2px];
}

/* Brand section enhancement */
h3.text-2xl {
  @apply relative inline-block;
}

h3.text-2xl::after {
  content: '';
  @apply absolute bottom-0 left-0 w-full h-[2px] bg-gradient-to-r from-indigo-600/50 to-purple-600/50 transform scale-x-0 transition-transform duration-300 origin-left;
}

h3.text-2xl:hover::after {
  @apply transform scale-x-100;
}
</style>
