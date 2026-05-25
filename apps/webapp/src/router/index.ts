import { createRouter, createWebHistory } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Landing page routes
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/Home.vue'),
      meta: { public: true }
    },
    {
      path: '/services',
      name: 'services',
      component: () => import('@/views/header/menu/services/Services.vue'),
      meta: { public: true }
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/header/menu/about/About.vue'),
      meta: { public: true }
    },
    {
      path: '/contact',
      name: 'contact',
      component: () => import('@/views/header/menu/contact/Contact.vue'),
      meta: { public: true }
    },
    {
      path: '/services/consultation',
      name: 'services-consultation',
      component: () => import('../views/header/menu/services/ConsultationService.vue'),
      meta: { public: true }
    },
    {
      path: '/services/documents',
      name: 'services-documents',
      component: () => import('../views/header/menu/services/DocumentsService.vue'),
      meta: { public: true }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/auth/LoginView.vue'),
      meta: { public: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/auth/RegisterView.vue'),
      meta: { public: true }
    },
    // Dashboard routes
    {
      path: '/dashboard',
      component: DashboardLayout,
      meta: { roles: ['client', 'lawyer'] }, // acceso permitido para ambos,
      children: [
        {
          path: '',
          redirect: (to) => {
            const authStore = useAuthStore()
            if (authStore.user?.role === 'lawyer') {
              return { name: 'dashboard-lawyer', params: { id: to.params.id } }
            } else {
              return { name: 'dashboard-client', params: { id: to.params.id } }
            }
          }
        },
        {
          path: 'client',
          name: 'dashboard-client',
          component: () => import('@/views/dashboard/DashboardUsers.vue'),
          meta: { roles: ['client'] }
        },
        {
          path: 'lawyer',
          name: 'dashboard-lawyer',
          component: () => import('@/views/dashboard/DashboardLawyers.vue'),
          meta: { roles: ['lawyer'] }
        },
        {
          path: 'chat',
          name: 'chat',
          component: () => import('@/views/dashboard/ChatPanel.vue'),
        },
        {
          path: 'history',
          name: 'history',
          component: () => import('@/views/dashboard/DashboardHistory.vue'),
        },
        {
          path: 'marketplace',
          name: 'marketplace',
          component: () => import('@/views/dashboard/Marketplace.vue'),
        },
        {
          path: '/consult-payment/:id',
          name: 'ConsultPayment',
          component: () => import('@/views/ConsultPayment.vue')
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/dashboard/Profile.vue'),
        },
        {
          path: 'payment-methods',
          name: 'payment-methods',
          component: () => import('@/views/dashboard/PaymentMethods.vue'),
        },
        {
          path: 'documents',
          name: 'documents',
          component: () => import('@/views/dashboard/Documents.vue'),
        }
      ]
    },
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  if (!auth.user) auth.init()

  // Partial state: token present but user missing — hydrate from /auth/me.
  if (!auth.user && auth.accessToken) {
    try {
      await auth.fetchMe()
    } catch {
      await auth.logout()
      return { name: 'login', query: { redirect: to.fullPath } }
    }
  }

  const isLogin = to.name === 'login'
  const allowedRoles = to.meta.roles as string[] | undefined

  if (!auth.user && !to.meta.public) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (auth.user && isLogin) {
    return auth.user?.role === 'lawyer' ? "/dashboard/lawyer" : "/dashboard/client"
  }

  if (allowedRoles && !allowedRoles.includes(auth.user?.role!)) {
    return auth.user?.role === 'lawyer' ? "/dashboard/lawyer" : "/dashboard/client"
  }

  return true
})

export default router
