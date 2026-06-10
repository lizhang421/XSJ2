import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('adminToken') || '')
  const adminInfo = ref(JSON.parse(localStorage.getItem('adminInfo') || '{}'))

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('adminToken', newToken)
  }

  const setAdminInfo = (info) => {
    adminInfo.value = info
    localStorage.setItem('adminInfo', JSON.stringify(info))
  }

  const logout = () => {
    token.value = ''
    adminInfo.value = {}
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminInfo')
  }

  return {
    token,
    adminInfo,
    setToken,
    setAdminInfo,
    logout
  }
}, {
  persist: true
})
