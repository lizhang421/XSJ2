import { defineStore } from 'pinia'
import { ref } from 'vue'

/*
第一个参数：名字
第二个参数函数，函数内部可以定义状态的所有的内容

*/

export const userTokenStore = defineStore(
  'token', // 第一个参数：仓库名称（唯一）
  () => { // 第二个参数：回调函数（状态、方法）
    const token = ref('')
    const roleBase = ref('0') // 0:普通用户, 1:管理员, 2:超级管理员
    const enCode = ref('') // 用户编码
    const setToken = (newToken) => {
      token.value = newToken
      
      try {
        const base64Url = newToken.split('.')[1]
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
        const jsonPayload = decodeURIComponent(escape(atob(base64)))
        const decoded = JSON.parse(jsonPayload)
        
        if (decoded && decoded.claims) {
          roleBase.value = String(decoded.claims.RoleBase || '0')
          enCode.value = decoded.claims.EnCode || ''
        }
      } catch (error) {
        console.error('解析token失败:', error)
        roleBase.value = '0'
        enCode.value = ''
      }
    }
    const removeToken = () => {
      token.value = ''
      roleBase.value = '0'
      enCode.value = ''
    }
    return { token, setToken, removeToken, roleBase, enCode }
  },
  { // 第三个参数：选项配置（这里放 persist）
    persist: true // 开启持久化（默认存储到 localStorage）
  }
)
