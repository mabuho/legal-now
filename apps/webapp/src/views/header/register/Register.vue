<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-4xl mx-auto">
      <!-- Instructions Card -->
      <div
        class="mb-8 rounded-xl border bg-white/95 backdrop-blur-xl shadow-lg transition-all duration-300 hover:shadow-xl dark:bg-gray-900/95 dark:border-gray-800"
      >
        <div class="flex flex-col space-y-1.5 p-8">
          <h2
            class="text-4xl font-bold text-center bg-gradient-to-r from-indigo-600 via-blue-600 to-purple-600 bg-clip-text text-transparent"
          >
            Registro
          </h2>
          <p class="text-lg text-center text-gray-600 dark:text-gray-400 mt-2">
            Por favor seleccione el tipo de cuenta y siga el proceso de registro:
          </p>
        </div>
        <div class="p-6 pt-0">
          <div class="grid md:grid-cols-2 gap-6 mt-8">
            <!-- Client -->
            <div
              :class="[
                'rounded-xl border-2 p-1 bg-gradient-to-br transition-all duration-300 cursor-pointer transform hover:scale-[1.02]',
                selectedTab === 'client'
                  ? 'from-blue-500 via-blue-400 to-indigo-500 shadow-lg shadow-blue-500/25'
                  : 'from-transparent to-transparent border-blue-100 dark:border-blue-900 hover:shadow-lg hover:from-blue-500/10 hover:to-indigo-500/10',
              ]"
              @click="selectedTab = 'client'"
            >
              <div class="rounded-lg bg-white dark:bg-gray-900 p-7">
                <div class="flex items-center space-x-3">
                  <div
                    class="p-2 bg-gradient-to-br from-blue-500 to-indigo-500 rounded-lg shadow-lg shadow-blue-500/30"
                  >
                    <svg
                      class="w-6 h-6 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                      />
                    </svg>
                  </div>
                  <h3
                    class="text-xl font-semibold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent"
                  >
                    Para Clientes
                  </h3>
                </div>
                <p class="text-gray-600 dark:text-gray-400 mt-3">
                  Cree una cuenta para encontrar y conectarse con profesionales legales. Acceda a
                  asesoría legal de calidad de manera rápida y eficiente.
                </p>
              </div>
            </div>
            <!-- Lawyer -->
            <div
              @click="selectedTab = 'lawyer'"
              :class="[
                'rounded-xl border-2 p-1 bg-gradient-to-br transition-all duration-300 cursor-pointer transform hover:scale-[1.02]',
                selectedTab === 'lawyer'
                  ? 'from-indigo-500 via-purple-400 to-purple-500 shadow-lg shadow-indigo-500/25'
                  : 'from-transparent to-transparent border-indigo-100 dark:border-indigo-900 hover:shadow-lg hover:from-indigo-500/10 hover:to-purple-500/10',
              ]"
            >
              <div class="rounded-lg bg-white dark:bg-gray-900 p-7">
                <div class="flex items-center space-x-3">
                  <div
                    class="p-2 bg-gradient-to-br from-indigo-500 to-purple-500 rounded-lg shadow-lg shadow-indigo-500/30"
                  >
                    <svg
                      class="w-6 h-6 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                      />
                    </svg>
                  </div>
                  <h3
                    class="text-xl font-semibold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent"
                  >
                    Para Abogados
                  </h3>
                </div>
                <p class="text-gray-600 dark:text-gray-400 mt-3">
                  Cree su perfil profesional y ofrezca sus servicios. Conecte con clientes
                  potenciales y expanda su práctica legal.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Tab Navigation -->
      <div class="flex flex-col items-center space-y-8 mb-12">
        <!-- Registration Form Container -->
        <div class="w-full rounded-xl border bg-white shadow-lg p-1">
          <div class="p-8">
            <transition
              enter-active-class="transition-opacity duration-300 ease-out"
              enter-from-class="opacity-0"
              enter-to-class="opacity-100"
              leave-active-class="transition-opacity duration-300 ease-in"
              leave-from-class="opacity-100"
              leave-to-class="opacity-0"
              mode="out-in"
            >
              <component
                :is="selectedTab === 'client' ? RegisterClient : RegisterLawyer"
                :key="selectedTab"
              />
            </transition>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import RegisterClient from './RegisterClient.vue'
import RegisterLawyer from './RegisterLawyer.vue'

const selectedTab = ref('client')
</script>

<style scoped>
.register-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.instructions {
  /*background-color: #f5f5f5;*/
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.instructions h2 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.instructions ul {
  margin-top: 1rem;
  padding-left: 1.5rem;
}

.toggle-container {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.toggle-container button {
  padding: 0.75rem 2rem;
  border: 2px solid #2c3e50;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.toggle-container button.active {
  background-color: #2c3e50;
  color: white;
}

.toggle-container button:hover {
  opacity: 0.9;
}
</style>
