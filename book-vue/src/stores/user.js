import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { me, logout as apiLogout } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => userInfo.value !== null)
  const username = computed(() => userInfo.value?.username || '')

  async function fetchUser() {
    try {
      loading.value = true
      const data = await me()
      userInfo.value = data
    } catch {
      userInfo.value = null
    } finally {
      loading.value = false
    }
  }

  function setUser(info) {
    userInfo.value = info
  }

  async function logout() {
    try {
      await apiLogout()
    } catch {
      // ignore
    }
    userInfo.value = null
  }

  return {
    userInfo,
    loading,
    isLoggedIn,
    username,
    fetchUser,
    setUser,
    logout
  }
})
