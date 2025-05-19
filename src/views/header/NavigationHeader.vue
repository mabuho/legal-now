<template>
  <!-- Navigation -->
  <nav
    class="sticky top-0 z-40 w-full border-b bg-white/80 backdrop-blur-xl supports-[backdrop-filter]:bg-white/60 dark:bg-gray-900/80 dark:border-gray-800"
  >
    <div class="container mx-auto px-4">
      <div class="flex h-16 items-center justify-between">
        <!-- Logo and Desktop Navigation -->
        <div class="flex items-center">
          <a href="/" class="flex items-center space-x-2 group">
            <span
              class="text-xl font-bold bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent group-hover:opacity-80 transition-opacity"
              >LegalNow</span
            >
          </a>

          <!-- Desktop Navigation -->
          <NavigationMenuRoot class="relative ml-8 hidden md:block">
            <NavigationMenuList class="flex flex-row gap-2">
              <NavigationMenuItem>
                <NavigationMenuTrigger
                  class="group inline-flex h-9 w-max items-center justify-center rounded-lg bg-transparent px-4 py-2 text-sm font-medium transition-all hover:bg-gray-100 dark:hover:bg-gray-800 focus:bg-gray-100 dark:focus:bg-gray-800 focus:outline-none disabled:pointer-events-none disabled:opacity-50 data-[active]:bg-gray-100 dark:data-[active]:bg-gray-800 data-[state=open]:bg-gray-100 dark:data-[state=open]:bg-gray-800"
                >
                  Servicios
                </NavigationMenuTrigger>
                <!-- <NavigationMenuContent class="absolute left-0 top-0 w-full sm:w-auto">
                  <div
                    class="grid gap-3 p-4 md:w-[400px] lg:w-[500px] lg:grid-cols-[.75fr_1fr] bg-white dark:bg-gray-900 rounded-xl shadow-lg border border-gray-200 dark:border-gray-800"
                  >
                    <div class="grid gap-1">
                      <NavigationMenuLink
                        href="/services"
                        class="block select-none space-y-1 rounded-lg p-3 leading-none no-underline outline-none transition-colors hover:bg-gray-100 dark:hover:bg-gray-800"
                      >
                        <div class="text-sm font-medium leading-none">Servicios Legales</div>
                        <p
                          class="line-clamp-2 text-sm leading-snug text-gray-600 dark:text-gray-400"
                        >
                          Encuentra el servicio legal que necesitas
                        </p>
                      </NavigationMenuLink>
                    </div>
                    <ul class="grid gap-2">
                      <li>
                        <NavigationMenuLink
                          href="/services/consultation"
                          class="block select-none space-y-1 rounded-lg p-3 leading-none no-underline outline-none transition-colors hover:bg-gray-100 dark:hover:bg-gray-800"
                        >
                          <div class="text-sm font-medium leading-none">Consulta Legal</div>
                          <p
                            class="line-clamp-2 text-sm leading-snug text-gray-600 dark:text-gray-400"
                          >
                            Agenda una consulta con un abogado especializado
                          </p>
                        </NavigationMenuLink>
                      </li>
                      <li>
                        <NavigationMenuLink
                          href="/services/documents"
                          class="block select-none space-y-1 rounded-lg p-3 leading-none no-underline outline-none transition-colors hover:bg-gray-100 dark:hover:bg-gray-800"
                        >
                          <div class="text-sm font-medium leading-none">Documentos Legales</div>
                          <p
                            class="line-clamp-2 text-sm leading-snug text-gray-600 dark:text-gray-400"
                          >
                            Redacción y revisión de documentos legales
                          </p>
                        </NavigationMenuLink>
                      </li>
                    </ul>
                  </div>
                </NavigationMenuContent> -->
              </NavigationMenuItem>
              <NavigationMenuItem>
                <NavigationMenuLink
                  href="#"
                  :class="[
                    'group inline-flex h-9 w-max items-center justify-center rounded-lg px-4 py-2 text-sm font-medium transition-all focus:outline-none disabled:pointer-events-none disabled:opacity-50',
                    isCurrentRoute('/about')
                      ? 'bg-gray-100 dark:bg-gray-800'
                      : 'hover:bg-gray-100 dark:hover:bg-gray-800',
                  ]"
                >
                  Nosotros
                </NavigationMenuLink>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <NavigationMenuLink
                  href="#"
                  :class="[
                    'group inline-flex h-9 w-max items-center justify-center rounded-lg px-4 py-2 text-sm font-medium transition-all focus:outline-none disabled:pointer-events-none disabled:opacity-50',
                    isCurrentRoute('/contact')
                      ? 'bg-gray-100 dark:bg-gray-800'
                      : 'hover:bg-gray-100 dark:hover:bg-gray-800',
                  ]"
                >
                  Contacto
                </NavigationMenuLink>
              </NavigationMenuItem>
            </NavigationMenuList>
          </NavigationMenuRoot>
        </div>

        <!-- Right Side Navigation -->
        <div class="flex items-center gap-4">
          <!-- Theme Toggle -->
          <!-- <button
            @click="toggleDarkMode" 
            class="rounded-lg p-2 text-gray-500 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-gray-800 transition-all"
            aria-label="Toggle theme"
          >
            <SunIcon v-if="isDarkMode" class="h-5 w-5" />
            <MoonIcon v-else class="h-5 w-5" />
          </button> -->

          <!-- Auth Links -->
          <div class="hidden md:flex items-center gap-4">
            <button
              @click="showWaitlistModal = true"
              class="inline-flex items-center justify-center rounded-xl bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:shadow-indigo-500/25 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transform transition-all duration-200 hover:-translate-y-0.5"
            >
              Lista de espera
            </button>
            <!-- <a
              href="/login"
              class="text-sm font-medium text-gray-700 hover:text-gray-900 dark:text-gray-300 dark:hover:text-white transition-all"
            >
              Iniciar Sesión
            </a>
            <a
              href="/register"
              class="inline-flex items-center justify-center rounded-xl bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:shadow-indigo-500/25 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transform transition-all duration-200 hover:-translate-y-0.5"
            >
              Registrarse
            </a> -->
          </div>

          <!-- Mobile Menu Button -->
          <button
            @click="isMobileMenuOpen = !isMobileMenuOpen"
            class="md:hidden rounded-lg p-2 text-gray-500 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-gray-800 transition-all"
            aria-label="Toggle mobile menu"
          >
            <Bars3Icon v-if="!isMobileMenuOpen" class="h-6 w-6" />
            <XMarkIcon v-else class="h-6 w-6" />
          </button>
        </div>
      </div>

      <!-- Mobile Menu -->
      <div v-show="isMobileMenuOpen" class="md:hidden" @click.self="isMobileMenuOpen = false">
        <div class="space-y-1 pb-3 pt-2">
          <a
            href="/#"
            class="block rounded-lg px-3 py-2 text-base font-medium text-gray-700 hover:bg-gray-100 hover:text-gray-900 dark:text-gray-300 dark:hover:bg-gray-800 dark:hover:text-white transition-all"
          >
            Servicios
          </a>
          <a
            href="/#"
            class="block rounded-lg px-3 py-2 text-base font-medium text-gray-700 hover:bg-gray-100 hover:text-gray-900 dark:text-gray-300 dark:hover:bg-gray-800 dark:hover:text-white transition-all"
          >
            Nosotros
          </a>
          <a
            href="/#"
            class="block rounded-lg px-3 py-2 text-base font-medium text-gray-700 hover:bg-gray-100 hover:text-gray-900 dark:text-gray-300 dark:hover:bg-gray-800 dark:hover:text-white transition-all"
          >
            Contacto
          </a>
          <div class="border-t border-gray-200 dark:border-gray-800 pt-4 pb-3">
            <button
              @click="showWaitlistModal = true"
              class="inline-flex items-center justify-center rounded-xl bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:shadow-indigo-500/25 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transform transition-all duration-200 hover:-translate-y-0.5"
            >
              Lista de espera
            </button>
            <!-- <div class="space-y-1">
              <a
                href="/login"
                class="block rounded-lg px-3 py-2 text-base font-medium text-gray-700 hover:bg-gray-100 hover:text-gray-900 dark:text-gray-300 dark:hover:bg-gray-800 dark:hover:text-white transition-all"
              >
                Iniciar Sesión
              </a>
              <a
                href="/register"
                class="block rounded-lg px-3 py-2 text-base font-medium text-indigo-600 hover:bg-indigo-50 hover:text-indigo-700 dark:text-indigo-400 dark:hover:bg-indigo-900/50 dark:hover:text-indigo-300 transition-all"
              >
                Registrarse
              </a>
            </div> -->
          </div>
        </div>
      </div>

      <WaitlistModal
        :show="showWaitlistModal"
        @close="showWaitlistModal = false"
        @submit="handleWaitlistSubmit"
      />
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import {
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuRoot,
  NavigationMenuTrigger,
} from 'radix-vue'
import { SunIcon, MoonIcon, Bars3Icon, XMarkIcon } from '@heroicons/vue/24/outline'
import WaitlistModal from '@/components/WaitlistModal.vue'

const isDarkMode = ref(true)
const isMobileMenuOpen = ref(false)
const route = useRoute()
const showWaitlistModal = ref(false)
document.documentElement.classList.toggle('dark')
/*const toggleDarkMode = () => {
    isDarkMode.value = !isDarkMode.value
    document.documentElement.classList.toggle('dark')
  }*/

const isCurrentRoute = (path: string) => route.path === path

const handleWaitlistSubmit = (email: string) => {
  console.log('Waitlist email:', email)
  showWaitlistModal.value = false
}
</script>
