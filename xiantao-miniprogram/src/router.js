import { createRouter, createWebHistory } from 'vue-router'

// Import uni for storage access
import { getStorageSync } from './uni-api'

const routes = [
  {
    path: '/',
    redirect: '/index'
  },
  {
    path: '/index',
    name: 'Index',
    component: () => import('./pages/index/index.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('./pages/login/login.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('./pages/profile/profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/product-detail',
    name: 'ProductDetail',
    component: () => import('./pages/product-detail/product-detail.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('./pages/orders/orders.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/publish',
    name: 'Publish',
    component: () => import('./pages/publish/publish.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/address',
    name: 'Address',
    component: () => import('./pages/address/address.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard for authentication
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const token = getStorageSync('token')
    if (!token) {
      next('/login')
      return
    }
  }
  next()
})

export default router
