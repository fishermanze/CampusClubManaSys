import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'
  },
  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  // 检查路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 检查accessToken是否存在
    const accessToken = localStorage.getItem('accessToken')
    
    // 简单的token有效性检查（完整的JWT验证在axiosInstance中处理）
    if (!accessToken) {
      // 如果没有token，重定向到登录页
      next({ name: 'Login' })
    } else {
      // 有token，允许访问
      next()
    }
  } else {
    // 如果不需要认证，直接放行
    next()
  }
})

export default router