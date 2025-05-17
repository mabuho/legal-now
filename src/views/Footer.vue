<script setup lang="ts">
import { Linkedin, Twitter, Instagram, Facebook } from 'lucide-vue-next'

const currentYear = new Date().getFullYear()
</script>

<template>
  <footer class="border-t py-12 mt-auto bg-background dark:border-gray-800">
    <div class="container mx-auto px-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8 mb-12">
        <!-- Brand -->
        <div class="col-span-1 md:col-span-2">
          <h3
            class="text-2xl font-bold mb-4 bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent"
          >
            LegalNow
          </h3>
          <p class="text-base text-gray-600 dark:text-gray-400 max-w-md">
            Conectando personas con servicios legales de calidad. Nuestra plataforma facilita el
            acceso a asesoría legal profesional y confiable.
          </p>
          <!-- <div class="mt-6 flex space-x-4">
            <a
              href="#"
              class="w-10 h-10 flex items-center justify-center rounded-full bg-gray-100 hover:bg-gray-200 dark:bg-gray-800 dark:hover:bg-gray-700 transition-colors duration-200"
              aria-label="LinkedIn"
            >
              <Linkedin class="w-5 h-5 text-gray-600 dark:text-gray-400" />
            </a>
            <a
              href="#"
              class="w-10 h-10 flex items-center justify-center rounded-full bg-gray-100 hover:bg-gray-200 dark:bg-gray-800 dark:hover:bg-gray-700 transition-colors duration-200"
              aria-label="Twitter"
            >
              <Twitter class="w-5 h-5 text-gray-600 dark:text-gray-400" />
            </a>
            <a
              href="#"
              class="w-10 h-10 flex items-center justify-center rounded-full bg-gray-100 hover:bg-gray-200 dark:bg-gray-800 dark:hover:bg-gray-700 transition-colors duration-200"
              aria-label="Instagram"
            >
              <Instagram class="w-5 h-5 text-gray-600 dark:text-gray-400" />
            </a>
            <a
              href="#"
              class="w-10 h-10 flex items-center justify-center rounded-full bg-gray-100 hover:bg-gray-200 dark:bg-gray-800 dark:hover:bg-gray-700 transition-colors duration-200"
              aria-label="Facebook"
            >
              <Facebook class="w-5 h-5 text-gray-600 dark:text-gray-400" />
            </a>
          </div> -->
        </div>

        <!-- Quick Links -->
        <div>
          <h4 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Enlaces Rápidos</h4>
          <ul class="space-y-3">
            <li>
              <a
                href="/#"
                class="text-base text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white transition-colors duration-200"
              >
                Servicios
              </a>
            </li>
            <li>
              <a
                href="/#"
                class="text-base text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white transition-colors duration-200"
              >
                Nosotros
              </a>
            </li>
            <li>
              <a
                href="/#"
                class="text-base text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white transition-colors duration-200"
              >
                Contacto
              </a>
            </li>
          </ul>
        </div>

        <!-- Legal -->
        <div>
          <h4 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Legal</h4>
          <ul class="space-y-3">
            <li>
              <a
                href="/#"
                class="text-base text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white transition-colors duration-200"
              >
                Términos y Condiciones
              </a>
            </li>
            <li>
              <a
                href="/#"
                class="text-base text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white transition-colors duration-200"
              >
                Política de Privacidad
              </a>
            </li>
            <li>
              <a
                href="/#"
                class="text-base text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white transition-colors duration-200"
              >
                Política de Cookies
              </a>
            </li>
          </ul>
        </div>
      </div>

      <div class="border-t border-gray-200 dark:border-gray-700 pt-8">
        <!-- <div class="flex flex-col md:flex-row justify-between items-center"> -->
        <div class="flex flex-col justify-between items-center">
          <p class="text-base text-gray-600 dark:text-gray-400 text-center md:text-left">
            © {{ currentYear }} LegalNow. Todos los derechos reservados.
          </p>
          <!-- <div class="mt-4 md:mt-0">
            <select 
              class="bg-transparent border border-gray-300 dark:border-gray-700 rounded-lg px-3 py-1.5 text-sm text-gray-600 dark:text-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              aria-label="Seleccionar idioma"
            >
              <option value="es">Español</option>
              <option value="en">English</option>
              <option value="pt">Português</option>
            </select>
          </div> -->
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
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
.grid > div {
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
