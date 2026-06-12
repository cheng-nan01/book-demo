import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '仪表盘', requiresAuth: true }
  },
  {
    path: '/books',
    name: 'Books',
    component: () => import('@/views/BookManage.vue'),
    meta: { title: '图书管理', requiresAuth: true }
  },
  {
    path: '/customers',
    name: 'Customers',
    component: () => import('@/views/CustomerManage.vue'),
    meta: { title: '客户管理', requiresAuth: true }
  },
  {
    path: '/sales',
    name: 'Sales',
    component: () => import('@/views/SaleManage.vue'),
    meta: { title: '销售管理', requiresAuth: true }
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('@/views/Statistics.vue'),
    meta: { title: '统计报表', requiresAuth: true }
  },
  {
    path: '/salespeople',
    name: 'Salespeople',
    component: () => import('@/views/SalespeopleManage.vue'),
    meta: { title: '销售员管理', requiresAuth: true }
  },
  {
    path: '/account',
    name: 'AccountSettings',
    component: () => import('@/views/AccountSettings.vue'),
    meta: { title: '账户设置', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 书店管理系统` : '书店管理系统'

  const userStore = useUserStore()

  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      await userStore.fetchUser()
    }
    if (!userStore.isLoggedIn) {
      next('/login')
      return
    }
  }

  if (to.meta.guest && userStore.isLoggedIn) {
    next('/')
    return
  }

  next()
})

export default router
