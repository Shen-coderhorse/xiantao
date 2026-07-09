import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue')
    },
    {
        path: '/',
        component: () => import('@/views/Layout.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/Dashboard.vue'),
                meta: { title: '数据统计' }
            },
            {
                path: 'users',
                name: 'Users',
                component: () => import('@/views/Users.vue'),
                meta: { title: '用户管理' }
            },
            {
                path: 'products',
                name: 'AdminProducts',
                component: () => import('@/views/Products.vue'),
                meta: { title: '商品管理' }
            },
            {
                path: 'orders',
                name: 'AdminOrders',
                component: () => import('@/views/Orders.vue'),
                meta: { title: '订单管理' }
            },
            {
                path: 'categories',
                name: 'Categories',
                component: () => import('@/views/Categories.vue'),
                meta: { title: '分类管理' }
            },
            {
                path: 'credits',
                name: 'Credits',
                component: () => import('@/views/Credits.vue'),
                meta: { title: '信用管理' }
            },
            {
                path: 'transactions',
                name: 'Transactions',
                component: () => import('@/views/Transactions.vue'),
                meta: { title: '交易流水' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    const user = JSON.parse(localStorage.getItem('user') || '{}')

    if (to.path !== '/login' && !token) {
        next('/login')
    } else if (to.path === '/login' && token) {
        next('/')
    } else if (token && user.role !== 'admin' && to.path !== '/login') {
        // 非管理员用户不能访问管理后台
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        next('/login')
    } else {
        next()
    }
})

export default router