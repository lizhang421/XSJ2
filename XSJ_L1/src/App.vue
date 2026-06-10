<template>
	<router-view></router-view>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUserInfoStore } from '@/stores/userinfo'
import { userTokenStore } from '@/stores/token'
import { GetUserDate } from '@/api/Users'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '@/router'

const { locale } = useI18n()
const userInfoStore = useUserInfoStore()
const tokenStore = userTokenStore()

let statusCheckTimer = null

const checkUserStatus = async () => {
  if (!tokenStore.token) {
    return
  }
  
  try {
    const result = await GetUserDate()
    if (result && result.code === 0 && result.data && result.data.length > 0) {
      const userInfo = result.data[0]
      
      if (userInfo.status === '2' || userInfo.status === 2) {
        tokenStore.removeToken()
        userInfoStore.removeInfo()
        
        const banReason = userInfo.reason || '未知原因'
        const endTime = userInfo.endTime ? new Date(userInfo.endTime).toLocaleString('zh-CN') : '未知'
        
        ElMessageBox.alert(
          `您的账号已被紧急封禁！\n原因：${banReason}\n封禁至：${endTime}\n如有疑问请联系管理员`,
          '账号被封禁',
          {
            confirmButtonText: '确定',
            type: 'error',
            callback: () => {
              router.push('/Home')
            }
          }
        )
        
        if (statusCheckTimer) {
          clearInterval(statusCheckTimer)
          statusCheckTimer = null
        }
        return
      }
      
      if (userInfo.status === '3' || userInfo.status === 3) {
        const banReason = userInfo.reason || '未知原因'
        const endTime = userInfo.endTime ? new Date(userInfo.endTime).toLocaleString('zh-CN') : '未知'
        ElMessage.warning(`账号存在异常状态！\n原因：${banReason}\n限制至：${endTime}\n请尽快联系管理员处理`)
      }
      
      userInfoStore.setInfo(userInfo)
    }
  } catch (error) {
    console.error('检查用户状态失败:', error)
  }
}

const startStatusCheck = () => {
  if (statusCheckTimer) {
    clearInterval(statusCheckTimer)
  }
  
  if (tokenStore.token) {
    checkUserStatus()
  }
  
  statusCheckTimer = setInterval(() => {
    if (tokenStore.token) {
      checkUserStatus()
    }
  }, 5 * 60 * 1000)
}

onMounted(() => {
  if (userInfoStore.info && userInfoStore.info.language) {
    const langMap = {
      '中文': 'zh',
      '英文': 'en',
      '日文': 'ja'
    }
    const userLang = langMap[userInfoStore.info.language]
    if (userLang) {
      locale.value = userLang
      localStorage.setItem('locale', userLang)
    }
  } else {
    const savedLocale = localStorage.getItem('locale')
    if (savedLocale) {
      locale.value = savedLocale
    }
  }
  
  startStatusCheck()
})

onUnmounted(() => {
  if (statusCheckTimer) {
    clearInterval(statusCheckTimer)
    statusCheckTimer = null
  }
})
</script>

<style>
</style>
