<template>
  <div class="admin-login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="login-brand">
          <span class="brand-icon">🛡️</span>
          <h1 class="brand-title">系统管理</h1>
          <p class="brand-desc">安全 · 高效 · 智能</p>
        </div>
        <div class="login-illustration">
          <div class="illustration-circle"></div>
          <div class="illustration-circle"></div>
          <div class="illustration-circle"></div>
        </div>
      </div>
      
      <div class="login-right">
        <div class="login-form-container">
          <h2 class="login-title">管理员登录</h2>
          <p class="login-subtitle">请输入您的账号和密码</p>
          
          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
            <el-form-item prop="phone">
              <el-input
                v-model="loginForm.phone"
                placeholder="请输入管理员手机号"
                size="large"
                prefix-icon="User"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            
            <el-form-item>
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-btn"
                :loading="loading"
                @click="handleLogin"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="login-footer">
            <p>© 2024 XSJ 平台 · 系统管理</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { AdminLogin, GetManagerMessage } from '@/admin/api/admin'
import { useAdminStore } from '@/admin/stores/admin'

const router = useRouter()
const adminStore = useAdminStore()

const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  phone: '',
  password: ''
})

const loginRules = {
  phone: [
    { required: true, message: '请输入管理员手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    const result = await AdminLogin(loginForm.phone, loginForm.password)
    
    if (result.code === 0) {
      adminStore.setToken(result.data)
      
      try {
        const managerInfo = await GetManagerMessage()
        if (managerInfo.code === 0 && managerInfo.data) {
          adminStore.setAdminInfo(managerInfo.data.data || managerInfo.data)
        }
      } catch (err) {
        console.error('获取管理员信息失败:', err)
      }
      
      if (rememberMe.value) {
        localStorage.setItem('adminRememberMe', 'true')
      }
      
      ElMessage.success('登录成功')
      router.push('/sys-x7k9m2/home')
    }
  } catch (err) {
    console.error('登录失败:', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e1e2d 0%, #2d2d44 100%);
}

.login-container {
  display: flex;
  width: 1000px;
  min-height: 600px;
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #3699ff 0%, #1e1e2d 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  overflow: hidden;
}

.login-brand {
  text-align: center;
  z-index: 1;
}

.brand-icon {
  font-size: 80px;
  display: block;
  margin-bottom: 20px;
}

.brand-title {
  font-size: 32px;
  color: #fff;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.brand-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.login-illustration {
  position: absolute;
  inset: 0;
}

.illustration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.illustration-circle:nth-child(1) {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
}

.illustration-circle:nth-child(2) {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
}

.illustration-circle:nth-child(3) {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-form-container {
  width: 100%;
  max-width: 360px;
}

.login-title {
  font-size: 28px;
  color: #1e1e2d;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.login-subtitle {
  font-size: 14px;
  color: #999;
  margin: 0 0 40px 0;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #3699ff 0%, #1e1e2d 100%);
  border: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, #2589ef 0%, #0e0e1d 100%);
}

.login-footer {
  text-align: center;
  margin-top: 40px;
}

.login-footer p {
  font-size: 12px;
  color: #999;
  margin: 0;
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    width: 90%;
    max-width: 400px;
  }
  
  .login-left {
    display: none;
  }
}
</style>
