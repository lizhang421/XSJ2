import './assets/main.scss'

import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'
import * as ElIcons from '@element-plus/icons-vue'

import i18n from '@/locales'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import router from '@/router'
import App from './App.vue'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

const app = createApp(App)

app.use(pinia)
app.use(i18n)
app.use(ElementPlus)

for (const [key, component] of Object.entries(ElIcons)) {
  app.component(key, component)
}

app.use(router)

// 路由守卫：未登录用户跳转到登录页面
router.beforeEach((to, from, next) => {
  // 检测管理员路径，重定向到管理员入口
  if (to.path.startsWith('/sys-x7k9m2')) {
    const adminPath = to.path.replace('/sys-x7k9m2', '')
    window.location.href = '/sys-x7k9m2-admin.html#' + adminPath
    return
  }
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - XSJ 新世界AI教育` : 'XSJ 新世界AI教育'
  
  // 不需要登录就能访问的页面
  const publicPaths = ['/', '/home', '/Home', '/login', '/forgot-password']
  if (publicPaths.includes(to.path)) {
    next()
    return
  }
  
  // 检查 token
  let hasToken = false
  try {
    const tokenData = localStorage.getItem('token')
    if (tokenData) {
      const parsed = JSON.parse(tokenData)
      if (parsed.token && typeof parsed.token === 'string' && parsed.token.length > 0) {
        hasToken = true
      }
    }
  } catch (e) {
    hasToken = false
  }
  
  if (hasToken) {
    next()
  } else {
    next('/login')
  }
})

app.mount('#app')
