import { useUserStore } from '@/stores/user'
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/Home.vue') },
      { path: 'product/:id', name: 'ProductDetail', component: () => import('@/views/ProductDetail.vue') },
      { path: 'publish', name: 'Publish', component: () => import('@/views/Publish.vue'), meta: { requiresAuth: true } },
      { path: 'user', name: 'UserCenter', component: () => import('@/views/UserCenter.vue'), meta: { requiresAuth: true } },
      { path: 'my-products', name: 'MyProducts', component: () => import('@/views/MyProducts.vue'), meta: { requiresAuth: true } },
      { path: 'orders', name: 'Orders', component: () => import('@/views/Orders.vue'), meta: { requiresAuth: true } },
      { path: 'order/:id', name: 'OrderDetail', component: () => import('@/views/OrderDetail.vue'), meta: { requiresAuth: true } },
      { path: 'address', name: 'AddressManagement', component: () => import('@/views/AddressManagement.vue'), meta: { requiresAuth: true } },
      { path: 'credit', name: 'CreditDetail', component: () => import('@/views/CreditDetail.vue'), meta: { requiresAuth: true } },
      { path: 'transactions', name: 'Transactions', component: () => import('@/views/Transactions.vue'), meta: { requiresAuth: true } },
      { path: 'admin/products', name: 'AdminProducts', component: () => import('@/views/AdminProducts.vue'), meta: { requiresAuth: true, requiresAdmin: true } }
    ]
  },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && userStore.user?.role !== 'admin') {
    next('/')
  } else {
    next()
  }
})

export default router
