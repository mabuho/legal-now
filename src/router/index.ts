import { createRouter, createWebHistory } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Landing page routes
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/Home.vue'),
    },
    {
      path: '/services',
      name: 'services',
      component: () => import('@/views/header/menu/services/Services.vue'),
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/header/menu/about/About.vue'),
    },
    {
      path: '/contact',
      name: 'contact',
      component: () => import('@/views/header/menu/contact/Contact.vue'),
    },
    {
      path: '/services/consultation',
      name: 'services-consultation',
      component: () => import('../views/header/menu/services/ConsultationService.vue'),
    },
    {
      path: '/services/documents',
      name: 'services-documents',
      component: () => import('../views/header/menu/services/DocumentsService.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/auth/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/auth/RegisterView.vue'),
    },
    // Dashboard routes
    {
      path: '/dashboard',
      component: DashboardLayout,
      children: [
        {
          path: '/dashboard',
          name: 'dashboard',
          component: () => import('@/views/dashboard/DashboardHome.vue'),
        },
        {
          path: '/dashboard/users',
          name: 'dashboard-users',
          component: () => import('@/views/dashboard/DashboardUsers.vue'),
        },
        {
          path: '/dashboard/lawyers',
          name: 'dashboard-lawyers',
          component: () => import('@/views/dashboard/DashboardLawyers.vue'),
        },
        {
          path: 'marketplace',
          name: 'marketplace',
          component: () => import('@/views/dashboard/Marketplace.vue'),
        },
        {
          path: 'chat',
          name: 'chat',
          component: () => import('@/views/dashboard/Chat.vue'),
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

// Middleware de autenticación
/*router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const isAuthenticated = localStorage.getItem('user') // Esto deberá adaptarse a tu sistema de autenticación

  if (requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})*/

export default router
