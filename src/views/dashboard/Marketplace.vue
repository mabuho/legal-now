<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import {
  MagnifyingGlassIcon,
  MapPinIcon,
  StarIcon,
  CurrencyDollarIcon,
  AdjustmentsHorizontalIcon,
  ChevronUpDownIcon,
  ScaleIcon,
  BriefcaseIcon,
  HomeModernIcon,
  BuildingOfficeIcon,
  AcademicCapIcon,
  UserGroupIcon
} from '@heroicons/vue/24/outline'
import CustomSelect from '@/components/common/CustomSelect.vue'
import LawyerCard from '@/components/LawyerCard.vue'

interface Lawyer {
  id: string
  name: string
  avatar: string
  specialization: string
  location: string
  rating: number
  reviewCount: number
  baseRate: number
  description: string
  workerType: 'independiente' | 'firma'
  //status: 'available' | 'busy' | 'offline'
}

const searchQuery = ref('')
const selectedSpecialization = ref('')
const selectedLocation = ref('')
const selectedPrice = ref('')
const minRate = ref(0)
const maxRate = ref(1000)

const specializationOptions = [
  { value: 'Derecho Civil', label: 'Derecho Civil', icon: ScaleIcon, iconClass: 'text-blue-600' },
  { value: 'Derecho Penal', label: 'Derecho Penal', icon: BriefcaseIcon, iconClass: 'text-red-600' },
  { value: 'Derecho Laboral', label: 'Derecho Laboral', icon: BriefcaseIcon, iconClass: 'text-green-600' },
  { value: 'Derecho Familiar', label: 'Derecho Familiar', icon: HomeModernIcon, iconClass: 'text-purple-600' },
  { value: 'Derecho Mercantil', label: 'Derecho Mercantil', icon: BuildingOfficeIcon, iconClass: 'text-gray-600' },
  { value: 'Derecho Fiscal', label: 'Derecho Fiscal', icon: AcademicCapIcon, iconClass: 'text-yellow-600' }
]

const locationOptions = [
  { value: 'Ciudad de México', label: 'Ciudad de México' },
  { value: 'Guadalajara', label: 'Guadalajara' },
  { value: 'Monterrey', label: 'Monterrey' },
  { value: 'Puebla', label: 'Puebla' },
  { value: 'Querétaro', label: 'Querétaro' }
]

const priceOptions = [
  { value: 200, label: 'Hasta $200/hr' },
  { value: 300, label: 'Hasta $300/hr' },
  { value: 500, label: 'Hasta $500/hr' },
  { value: 1000, label: 'Hasta $1000/hr' },
]

const locations = [
  'Ciudad de México',
  'Guadalajara',
  'Monterrey',
  'Puebla',
  'Querétaro'
]

// Mock data
const lawyers = ref<Lawyer[]>([
  {
    id: '1',
    name: 'Lic. Carlos Rodríguez',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Carlos',
    specialization: 'Derecho Laboral',
    location: 'Ciudad de México',
    rating: 4.8,
    reviewCount: 127,
    baseRate: 250,
    description: 'Especialista en derecho laboral con más de 15 años de experiencia.',
    workerType: 'independiente'
  },
  {
    id: '2',
    name: 'Lic. Ana López',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Ana',
    specialization: 'Derecho Familiar',
    location: 'Guadalajara',
    rating: 4.9,
    reviewCount: 89,
    baseRate: 300,
    description: 'Experta en derecho familiar y mediación de conflictos.',
    workerType: 'independiente'
  },
  {
    id: '3',
    name: 'Lic. Roberto Méndez',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Roberto',
    specialization: 'Derecho Civil',
    location: 'Monterrey',
    rating: 4.7,
    reviewCount: 156,
    baseRate: 200,
    description: 'Amplia experiencia en casos civiles y contratos.',
    workerType: 'independiente'
  },
  {
    id: '4',
    name: 'Lic. Luis Sánchez',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Ryker',
    specialization: 'Derecho Laboral',
    location: 'Ciudad de México',
    rating: 4.8,
    reviewCount: 127,
    baseRate: 250,
    description: 'Especialista en derecho laboral con más de 15 años de experiencia.',
    workerType: 'firma'
  },
  {
    id: '5',
    name: 'Lic. Ana López',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Mackenzie',
    specialization: 'Derecho Familiar',
    location: 'Querétaro',
    rating: 4.9,
    reviewCount: 89,
    baseRate: 300,
    description: 'Experta en derecho familiar.',
    workerType: 'independiente'
  },
  {
    id: '6',
    name: 'Lic. Roberto Méndez',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Nolan',
    specialization: 'Derecho Mercantil',
    location: 'Monterrey',
    rating: 4.7,
    reviewCount: 156,
    baseRate: 200,
    description: 'Amplia experiencia en casos civiles y contratos.',
    workerType: 'independiente'
  }  
])

const filteredLawyers = computed(() => {
  return lawyers.value.filter(lawyer => {
    const matchesSearch = lawyer.description.toLowerCase().includes(searchQuery.value.toLowerCase())
      
    const matchesSpecialization = !selectedSpecialization.value || 
      lawyer.specialization === selectedSpecialization.value
    
    const matchesLocation = !selectedLocation.value || 
      lawyer.location === selectedLocation.value
    
    const selectedPriceNum = selectedPrice.value ? Number(selectedPrice.value) : 0
    const matchesRate = !selectedPriceNum || lawyer.baseRate < selectedPriceNum

    return matchesSearch && matchesSpecialization && matchesLocation && matchesRate
  })
})

const getworkerTypeColor = (workerType: string) => {
  switch (workerType) {
    case 'available':
      return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
    case 'busy':
      return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400'
    case 'offline':
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
    default:
      return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
  }
}

// Lifecycle hooks
onMounted(() => {
  // No need to add event listeners for the old dropdown state and methods
})

onUnmounted(() => {
  // No need to remove event listeners for the old dropdown state and methods
})
</script>

<template>
  <div class="space-y-6">
    <!-- Header -->
    <div>
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
        Marketplace de Abogados
      </h1>
      <p class="mt-2 text-sm text-gray-700 dark:text-gray-300">
        Encuentra al abogado ideal para tu caso
      </p>
    </div>

    <!-- Filters -->
    <div class="bg-white dark:bg-gray-800 shadow sm:rounded-lg">
      <div class="p-6">
        <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
          <!-- Search -->
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Buscar abogados..."
              class="pl-10 w-full rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 py-2 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            />
          </div>

          <!-- Specialization Dropdown -->
          <CustomSelect
            v-model="selectedSpecialization"
            :options="specializationOptions"
            :clearable="true"
            placeholder="Todas las especializaciones"
            @change="option => selectedSpecialization = option?.value || ''"
          />

          <!-- Location -->
          <CustomSelect
            v-model="selectedLocation"
            :options="locationOptions"
            :clearable="true"
            placeholder="Todas las ubicaciones"
            @change="option => selectedLocation = option?.value || ''"
          />

          <!-- Price Range -->
          <CustomSelect
            v-model="selectedPrice"
            :options="priceOptions"
            :clearable="true"
            placeholder="Cualquier tarifa"
            @change="option => selectedPrice = option?.value || ''"
          />
        </div>
      </div>
    </div>

    <!-- Results -->
    <div v-if="filteredLawyers.length > 0" class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
      <div
        v-for="lawyer in filteredLawyers"
        :key="lawyer.id"
        class="grid grid-cols-1 gap-6"
      >
        <LawyerCard :lawyer="lawyer" />
      </div>
    </div>

    <!-- No Results -->
    <div 
      v-else 
      class="text-center bg-white dark:bg-gray-800 shadow sm:rounded-lg p-8"
    >
      <div class="mx-auto h-24 w-24 text-gray-400 dark:text-gray-500 mb-4">
        <ScaleIcon class="h-full w-full" />
      </div>
      <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-2">
        No encontramos abogados que coincidan con tus criterios
      </h3>
      <p class="text-sm text-gray-500 dark:text-gray-400 mb-6">
        No te preocupes, podemos ayudarte a encontrar un abogado especializado para tu caso. 
        Nuestro equipo se encargará de buscar al profesional ideal que se ajuste a tus necesidades.
      </p>
      <button
        type="button"
        class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
      >
        <UserGroupIcon class="h-5 w-5 mr-2" />
        Solicitar búsqueda personalizada
      </button>
    </div>
  </div>
</template>

<style scoped>
/* Remove old dropdown styles since we're using CustomSelect component */
</style> 