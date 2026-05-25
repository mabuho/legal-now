import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export interface NavItem {
  label: string
  to?: string
  sectionId?: string
  icon?: string
  visible: boolean
  highlight?: boolean
}

export function useNavMenu() {
  const route = useRoute()
  const authStore = useAuthStore()

  const navItems = computed<NavItem[]>(() => {
    const user = authStore.user

    if (!user) {
      if (route.name === 'home') {
        return [
          { label: '¿Cómo funciona?', sectionId: '#how-it-works', visible: true },
          { label: 'Especialidades', sectionId: '#specialties', visible: true },
          { label: 'Preguntas Frecuentes', sectionId: '#faq', visible: true },
        ]
      }
      return []
    }

    if (user.role === 'client') {
      return [
        { label: 'Consultas', to: '/dashboard/client', visible: true },
        { label: 'Historial', to: '/dashboard/history', visible: true },
        { label: 'Nueva consulta', to: '/dashboard/client', highlight: true, visible: true },
      ]
    }

    if (user.role === 'lawyer') {
      return [
        { label: 'Mis consultas', to: '/dashboard/lawyer', visible: true },
        { label: 'Historial', to: '/dashboard/history', visible: true },
        { label: 'Completar onboarding', to: '/onboarding', visible: !user.onboardingCompletedAt },
        { label: 'Verificación pendiente', visible: !!user.onboardingCompletedAt && !user.verifiedAt },
      ]
    }

    if (user.role === 'admin') {
      return [
        { label: 'Abogados', to: '/dashboard/admin/lawyers', visible: true },
      ]
    }

    return [] // auth branches added in next tasks
  })

  const bottomItems = computed<NavItem[]>(() => {
    const user = authStore.user
    if (!user) return []

    if (user.role === 'client') {
      return [
        { label: 'Consultas', to: '/dashboard/client', icon: 'home', visible: true },
        { label: 'Historial', to: '/dashboard/history', icon: 'history', visible: true },
        { label: 'Cuenta', to: '/dashboard/profile', icon: 'profile', visible: true },
        { label: 'Asistente', icon: 'ai', visible: true },
      ]
    }

    if (user.role === 'lawyer') {
      return [
        { label: 'Consultas', to: '/dashboard/lawyer', icon: 'home', visible: true },
        { label: 'Historial', to: '/dashboard/history', icon: 'history', visible: true },
        { label: 'Cuenta', to: '/dashboard/profile', icon: 'profile', visible: true },
      ]
    }

    return []
  })

  return { navItems, bottomItems }
}
