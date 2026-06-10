<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <div class="logo">
          <span class="logo-icon">🛡️</span>
          <span class="logo-text">XSJ管理员系统</span>
        </div>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#1e1e2d"
        text-color="#b0b0b0"
        active-text-color="#3699ff"
        router
      >
        <el-menu-item index="/sys-x7k9m2/home">
          <el-icon><HomeFilled /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        
        <el-menu-item index="/sys-x7k9m2/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        
        <el-menu-item index="/sys-x7k9m2/videos">
          <el-icon><VideoCamera /></el-icon>
          <span>视频管理</span>
        </el-menu-item>
        
        <el-menu-item index="/sys-x7k9m2/audit">
          <el-icon><CircleCheck /></el-icon>
          <span>视频审核</span>
        </el-menu-item>
        
        <el-menu-item index="/sys-x7k9m2/user-audit">
          <el-icon><UserFilled /></el-icon>
          <span>用户审核</span>
        </el-menu-item>
        
        <el-menu-item index="/sys-x7k9m2/comments">
          <el-icon><ChatDotRound /></el-icon>
          <span>评论管理</span>
        </el-menu-item>
        
        <el-menu-item index="/sys-x7k9m2/reports">
          <el-icon><Warning /></el-icon>
          <span>举报管理</span>
        </el-menu-item>
        
        <el-menu-item index="/sys-x7k9m2/ai-models">
          <el-icon><Cpu /></el-icon>
          <span>AI模型管理</span>
        </el-menu-item>
        
        <el-menu-item index="/sys-x7k9m2/resource-config">
          <el-icon><PictureFilled /></el-icon>
          <span>资源配置</span>
        </el-menu-item>
        
        <el-menu-item v-if="isAdmin" index="/sys-x7k9m2/settings">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </aside>
    
    <div class="admin-main">
      <header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/sys-x7k9m2/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="admin-avatar">
              <el-avatar :size="36" :src="cleanUrl(adminInfo.avatar)">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span class="admin-name">{{ adminInfo.nickName || adminInfo.realName || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  HomeFilled, User, VideoCamera, ChatDotRound, 
  Warning, Setting, UserFilled, ArrowDown, SwitchButton, CircleCheck, Cpu, Avatar, PictureFilled
} from '@element-plus/icons-vue'
import { useAdminStore } from '@/admin/stores/admin'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()

const activeMenu = computed(() => route.path)
const adminInfo = computed(() => adminStore.adminInfo)

const isAdmin = computed(() => {
  return adminInfo.value.roleBase === '2' || adminInfo.value.roleBase === 2
})

const cleanUrl = (url) => {
  if (!url) return ''
  return url.replace(/`/g, '').trim()
}

const handleProfile = () => {
  router.push('/sys-x7k9m2/profile')
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    adminStore.logout()
    ElMessage.success('已退出登录')
    router.push('/sys-x7k9m2/login')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}

.admin-sidebar {
  width: 260px;
  background: #1e1e2d;
  flex-shrink: 0;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-header {
  height: 70px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.sidebar-menu {
  border-right: none;
  padding: 10px 0;
}

.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 4px 12px;
  border-radius: 8px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: rgba(54, 153, 255, 0.1) !important;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: rgba(54, 153, 255, 0.2) !important;
}

.admin-main {
  flex: 1;
  margin-left: 260px;
  display: flex;
  flex-direction: column;
}

.admin-header {
  height: 70px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 99;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.admin-avatar {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background 0.3s;
}

.admin-avatar:hover {
  background: #f5f5f5;
}

.admin-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.admin-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>
