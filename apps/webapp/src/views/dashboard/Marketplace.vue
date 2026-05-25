<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
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
import LawyerCard from '@/components/marketplace/LawyerCard.vue'
import { apiGet } from '@/services/apiClient'

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

interface ApiLawyer {
  user_id: string
  name: string
  avatar_url: string | null
  bar_id: string | null
  bio: string | null
  languages: string[]
  specializations: Array<{ code: string; name: string }>
  verified_at: string | null
  created_at: string
  updated_at: string
}

function mapLawyer(a: ApiLawyer): Lawyer {
  return {
    id: a.user_id,
    name: a.name ?? 'Sin nombre',
    avatar: a.avatar_url ?? `https://api.dicebear.com/7.x/avataaars/svg?seed=${a.user_id}`,
    specialization: a.specializations[0]?.name ?? 'Sin especialización',
    location: '',
    rating: 0,
    reviewCount: 0,
    baseRate: 0,
    description: a.bio ?? '',
    workerType: 'independiente',
  }
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
  { value: '200', label: 'Hasta $200/hr' },
  { value: '300', label: 'Hasta $300/hr' },
  { value: '500', label: 'Hasta $500/hr' },
  { value: '1000', label: 'Hasta $1000/hr' },
]

const locations = [
  'Ciudad de México',
  'Guadalajara',
  'Monterrey',
  'Puebla',
  'Querétaro'
]

const lawyers = ref<Lawyer[]>([])
const loading = ref(false)
const fetchError = ref<string | null>(null)

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

onMounted(async () => {
  loading.value = true
  const { data, error } = await apiGet<ApiLawyer[]>('/api/v1/lawyers')
  if (error) {
    fetchError.value = error.message
  } else {
    lawyers.value = (data ?? []).map(mapLawyer)
  }
  loading.value = false
})
</script>

<template>
  <div>
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

    <!-- Loading / Error states -->
    <div v-if="loading" class="text-center py-12 text-text-muted font-body">Cargando abogados...</div>
    <div v-else-if="fetchError" class="text-center py-12 text-status-error font-body text-sm">{{ fetchError }}</div>

    <!-- Results -->
    <div v-else-if="filteredLawyers.length > 0" class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
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
      v-else-if="!loading && !fetchError"
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