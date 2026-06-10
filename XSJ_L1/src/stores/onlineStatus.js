import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useOnlineStatusStore = defineStore('onlineStatus', () => {
  const onlineUsers = ref({})

  const setOnline = (userEncode) => {
    onlineUsers.value[userEncode] = true
  }

  const setOffline = (userEncode) => {
    onlineUsers.value[userEncode] = false
  }

  const updateStatus = (userEncode, isOnline) => {
    onlineUsers.value[userEncode] = isOnline
  }

  const isOnline = (userEncode) => {
    return onlineUsers.value[userEncode] === true
  }

  const clearAll = () => {
    onlineUsers.value = {}
  }

  return {
    onlineUsers,
    setOnline,
    setOffline,
    updateStatus,
    isOnline,
    clearAll
  }
}, {
  persist: true
})
