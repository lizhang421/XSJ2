import { createRouter, createWebHashHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { GetManagerMessage } from '@/admin/api/admin'
import { useAdminStore } from '@/admin/stores/admin'

import AdminLayout from '@/admin/layouts/AdminLayout.vue'
import AdminLogin from '@/admin/views/AdminLogin.vue'
import AdminHome from '@/admin/views/AdminHome.vue'
import UserManage from '@/admin/views/UserManage.vue'
import VideoManage from '@/admin/views/VideoManage.vue'
import VideoAudit from '@/admin/views/VideoAudit.vue'
import UserAudit from '@/admin/views/UserAudit.vue'
import CommentManage from '@/admin/views/CommentManage.vue'
import ReportManage from '@/admin/views/ReportManage.vue'
import SystemSettings from '@/admin/views/SystemSettings.vue'
import AIModelManage from '@/admin/views/AIModelManage.vue'
import AdminProfile from '@/admin/views/AdminProfile.vue'
import SystemResourceConfig from '@/admin/views/SystemResourceConfig.vue'

const routes = [
  { 
    path: '/sys-x7k9m2-admin.html',
    redirect: '/sys-x7k9m2/home'
  },
  { 
    path: '/', 
    redirect: '/sys-x7k9m2/home' 
  },
  { path: '/sys-x7k9m2/login', component: AdminLogin, meta: { title: '管理员登录' } },
  { 
    path: '/sys-x7k9m2', 
    component: AdminLayout, 
    redirect: '/sys-x7k9m2/home',
    children: [
      { path: 'home', component: AdminHome, meta: { title: '控制台' } },
      { path: 'users', component: UserManage, meta: { title: '用户管理' } },
      { path: 'videos', component: VideoManage, meta: { title: '视频管理' } },
      { path: 'audit', component: VideoAudit, meta: { title: '视频审核' } },
      { path: 'user-audit', component: UserAudit, meta: { title: '用户审核' } },
      { path: 'comments', component: CommentManage, meta: { title: '评论管理' } },
      { path: 'reports', component: ReportManage, meta: { title: '举报管理' } },
      { path: 'ai-models', component: AIModelManage, meta: { title: 'AI模型管理' } },
      { path: 'profile', component: AdminProfile, meta: { title: '个人中心' } },
      { path: 'settings', component: SystemSettings, meta: { title: '系统设置' } },
      { path: 'resource-config', component: SystemResourceConfig, meta: { title: '资源配置' } }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/sys-x7k9m2/home'
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes: routes
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 系统管理` : '系统管理'
  
  const adminToken = localStorage.getItem('adminToken')
  
  if (!adminToken && to.path !== '/sys-x7k9m2/login') {
    next('/sys-x7k9m2/login')
    return
  }
  
  if (adminToken && to.path === '/sys-x7k9m2/login') {
    next('/sys-x7k9m2/home')
    return
  }
  
  if (adminToken && to.path !== '/sys-x7k9m2/login') {
    const adminStore = useAdminStore()
    
    if (!adminStore.adminInfo || Object.keys(adminStore.adminInfo).length === 0) {
      try {
        const res = await GetManagerMessage()
        if (res.code === 0 && res.data) {
          adminStore.setAdminInfo(res.data.data || res.data)
        }
      } catch (error) {
        console.error('获取管理员信息失败:', error)
      }
    }
    
    if (to.path === '/sys-x7k9m2/settings') {
      const roleBase = adminStore.adminInfo.roleBase
      if (roleBase !== '2' && roleBase !== 2) {
        ElMessage.warning('权限不足，只有超级管理员才能访问此页面')
        next('/sys-x7k9m2/home')
        return
      }
    }
  }
  
  next()
})

export default router
