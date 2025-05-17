<script setup lang="ts">
import { ref } from 'vue'
import {
  UserCircleIcon,
  EnvelopeIcon,
  PhoneIcon,
  MapPinIcon,
  IdentificationIcon
} from '@heroicons/vue/24/outline'

interface UserProfile {
  name: string
  email: string
  phone: string
  address: string
  identification: string
  avatar?: string
}

const userProfile = ref<UserProfile>({
  name: 'Juan Pérez',
  email: 'juan@example.com',
  phone: '+52 55 1234 5678',
  address: 'Calle Principal 123, Ciudad de México',
  identification: 'PEXJ890101ABC',
  avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Juan'
})

const isEditing = ref(false)
const editedProfile = ref({ ...userProfile.value })

const saveChanges = () => {
  userProfile.value = { ...editedProfile.value }
  isEditing.value = false
}

const cancelEdit = () => {
  editedProfile.value = { ...userProfile.value }
  isEditing.value = false
}
</script>

<template>
  <div class="space-y-6">
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
          Perfil de Usuario
        </h1>
        <p class="mt-2 text-sm text-gray-700 dark:text-gray-300">
          Gestiona tu información personal
        </p>
      </div>
    </div>

    <div class="bg-white dark:bg-gray-800 shadow sm:rounded-lg">
      <div class="px-4 py-5 sm:p-6">
        <!-- Profile Header -->
        <div class="flex items-center space-x-6">
          <div class="flex-shrink-0">
            <img
              :src="userProfile.avatar"
              alt=""
              class="h-24 w-24 rounded-full"
            />
          </div>
          <div>
            <h2 class="text-xl font-semibold text-gray-900 dark:text-white">
              {{ userProfile.name }}
            </h2>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              ID: {{ userProfile.identification }}
            </p>
          </div>
        </div>

        <!-- Profile Form -->
        <div class="mt-8 space-y-6">
          <div class="grid grid-cols-1 gap-6 sm:grid-cols-2">
            <!-- Name -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Nombre Completo
              </label>
              <div class="mt-1 relative rounded-md shadow-sm">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <UserCircleIcon class="h-5 w-5 text-gray-400" />
                </div>
                <input
                  v-if="isEditing"
                  v-model="editedProfile.name"
                  type="text"
                  class="pl-10 block w-full rounded-md border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-indigo-500 focus:border-indigo-500"
                />
                <p v-else class="pl-10 py-2 text-gray-900 dark:text-white">
                  {{ userProfile.name }}
                </p>
              </div>
            </div>

            <!-- Email -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Correo Electrónico
              </label>
              <div class="mt-1 relative rounded-md shadow-sm">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <EnvelopeIcon class="h-5 w-5 text-gray-400" />
                </div>
                <input
                  v-if="isEditing"
                  v-model="editedProfile.email"
                  type="email"
                  class="pl-10 block w-full rounded-md border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-indigo-500 focus:border-indigo-500"
                />
                <p v-else class="pl-10 py-2 text-gray-900 dark:text-white">
                  {{ userProfile.email }}
                </p>
              </div>
            </div>

            <!-- Phone -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Teléfono
              </label>
              <div class="mt-1 relative rounded-md shadow-sm">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <PhoneIcon class="h-5 w-5 text-gray-400" />
                </div>
                <input
                  v-if="isEditing"
                  v-model="editedProfile.phone"
                  type="tel"
                  class="pl-10 block w-full rounded-md border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-indigo-500 focus:border-indigo-500"
                />
                <p v-else class="pl-10 py-2 text-gray-900 dark:text-white">
                  {{ userProfile.phone }}
                </p>
              </div>
            </div>

            <!-- Address -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Dirección
              </label>
              <div class="mt-1 relative rounded-md shadow-sm">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <MapPinIcon class="h-5 w-5 text-gray-400" />
                </div>
                <input
                  v-if="isEditing"
                  v-model="editedProfile.address"
                  type="text"
                  class="pl-10 block w-full rounded-md border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-indigo-500 focus:border-indigo-500"
                />
                <p v-else class="pl-10 py-2 text-gray-900 dark:text-white">
                  {{ userProfile.address }}
                </p>
              </div>
            </div>

            <!-- Identification -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Identificación
              </label>
              <div class="mt-1 relative rounded-md shadow-sm">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <IdentificationIcon class="h-5 w-5 text-gray-400" />
                </div>
                <input
                  v-if="isEditing"
                  v-model="editedProfile.identification"
                  type="text"
                  class="pl-10 block w-full rounded-md border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-indigo-500 focus:border-indigo-500"
                />
                <p v-else class="pl-10 py-2 text-gray-900 dark:text-white">
                  {{ userProfile.identification }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="mt-8 flex justify-end space-x-4">
          <button
            v-if="!isEditing"
            @click="isEditing = true"
            type="button"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Editar Perfil
          </button>
          <template v-else>
            <button
              @click="cancelEdit"
              type="button"
              class="inline-flex items-center px-4 py-2 border border-gray-300 dark:border-gray-600 text-sm font-medium rounded-md text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              Cancelar
            </button>
            <button
              @click="saveChanges"
              type="button"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              Guardar Cambios
            </button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template> 