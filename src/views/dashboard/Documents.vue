<script setup lang="ts">
import { ref } from 'vue'
import { 
  DocumentTextIcon, 
  DocumentDuplicateIcon,
  DocumentMagnifyingGlassIcon,
  ArrowPathIcon,
  CheckCircleIcon
} from '@heroicons/vue/24/outline'

interface Template {
  id: string
  name: string
  description: string
  category: string
}

interface Document {
  id: string
  name: string
  status: 'draft' | 'review' | 'completed'
  createdAt: string
  lastModified: string
}

const templates = ref<Template[]>([
  {
    id: '1',
    name: 'Contrato de Arrendamiento',
    description: 'Contrato estándar para el arrendamiento de propiedades residenciales',
    category: 'Contratos'
  },
  {
    id: '2',
    name: 'Contrato Laboral',
    description: 'Contrato de trabajo conforme a la legislación vigente',
    category: 'Contratos'
  },
  {
    id: '3',
    name: 'Poder Notarial',
    description: 'Documento para otorgar facultades legales a un representante',
    category: 'Poderes'
  },
  {
    id: '4',
    name: 'Acuerdo de Confidencialidad',
    description: 'Protege información sensible entre partes',
    category: 'Acuerdos'
  }
])

const recentDocuments = ref<Document[]>([
  {
    id: '1',
    name: 'Contrato de Arrendamiento - Casa Principal',
    status: 'completed',
    createdAt: '2024-03-15',
    lastModified: '2024-03-16'
  },
  {
    id: '2',
    name: 'Acuerdo de Confidencialidad - Proyecto X',
    status: 'review',
    createdAt: '2024-03-14',
    lastModified: '2024-03-14'
  },
  {
    id: '3',
    name: 'Contrato Laboral - Nuevo Empleado',
    status: 'draft',
    createdAt: '2024-03-13',
    lastModified: '2024-03-13'
  }
])

const selectedTemplate = ref<Template | null>(null)

const selectTemplate = (template: Template) => {
  selectedTemplate.value = template
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'draft':
      return 'text-yellow-500'
    case 'review':
      return 'text-blue-500'
    case 'completed':
      return 'text-green-500'
    default:
      return 'text-gray-500'
  }
}

const getStatusIcon = (status: string) => {
  switch (status) {
    case 'draft':
      return DocumentTextIcon
    case 'review':
      return DocumentMagnifyingGlassIcon
    case 'completed':
      return CheckCircleIcon
    default:
      return DocumentTextIcon
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-white">
          Documentos Legales
        </h1>
        <p class="mt-2 text-gray-600 dark:text-gray-300">
          Genera y gestiona documentos legales con asistencia de IA y revisión por expertos
        </p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Templates Section -->
        <div class="lg:col-span-2">
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
            <h2 class="text-xl font-semibold mb-4 text-gray-900 dark:text-white">
              Plantillas Disponibles
            </h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div
                v-for="template in templates"
                :key="template.id"
                class="border border-gray-200 dark:border-gray-700 rounded-lg p-4 hover:border-blue-500 dark:hover:border-blue-400 cursor-pointer transition-colors"
                :class="{ 'border-blue-500 dark:border-blue-400': selectedTemplate?.id === template.id }"
                @click="selectTemplate(template)"
              >
                <div class="flex items-start">
                  <DocumentDuplicateIcon class="h-6 w-6 text-blue-500 dark:text-blue-400 mr-3" />
                  <div>
                    <h3 class="font-medium text-gray-900 dark:text-white">
                      {{ template.name }}
                    </h3>
                    <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
                      {{ template.description }}
                    </p>
                    <span class="mt-2 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200">
                      {{ template.category }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Recent Documents Section -->
        <div class="lg:col-span-1">
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
            <h2 class="text-xl font-semibold mb-4 text-gray-900 dark:text-white">
              Documentos Recientes
            </h2>
            <div class="space-y-4">
              <div
                v-for="document in recentDocuments"
                :key="document.id"
                class="border border-gray-200 dark:border-gray-700 rounded-lg p-4"
              >
                <div class="flex items-start">
                  <component
                    :is="getStatusIcon(document.status)"
                    class="h-5 w-5 mr-3"
                    :class="getStatusColor(document.status)"
                  />
                  <div class="flex-1 min-w-0">
                    <h3 class="text-sm font-medium text-gray-900 dark:text-white truncate">
                      {{ document.name }}
                    </h3>
                    <div class="mt-1 flex items-center text-xs text-gray-500 dark:text-gray-400">
                      <span class="capitalize" :class="getStatusColor(document.status)">
                        {{ document.status }}
                      </span>
                      <span class="mx-2">•</span>
                      <span>Modificado: {{ document.lastModified }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Document Generation Section -->
      <div class="mt-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-xl font-semibold text-gray-900 dark:text-white">
            Generar Documento
          </h2>
          <button
            type="button"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 dark:focus:ring-offset-gray-800"
          >
            <ArrowPathIcon class="h-5 w-5 mr-2" />
            Generar con IA
          </button>
        </div>
        
        <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
          <p class="text-sm text-gray-600 dark:text-gray-300">
            Selecciona una plantilla para comenzar. Nuestro sistema de IA te ayudará a completar el documento
            y un experto legal lo revisará antes de su finalización.
          </p>
        </div>
      </div>
    </div>
  </div>
</template> 