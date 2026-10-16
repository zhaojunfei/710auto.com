import { defineStore } from 'pinia'
import { authApi } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('auto710_token') || '',
    userInfo: {} as any,
    menus: [] as any[],
    permissions: [] as string[]
  }),
  getters: {
    isLogin: (state) => !!state.token,
    isSuperAdmin: (state) => !!state.userInfo?.superAdmin
  },
  actions: {
    async login(form: any) {
      const data = await authApi.login(form)
      this.token = data.token
      localStorage.setItem('auto710_token', data.token)
      this.userInfo = data
      this.permissions = data.permissions || []
      const menus = await authApi.menus()
      this.menus = menus || []
      return data
    },
    async loadInfo() {
      const data = await authApi.info()
      this.userInfo = data
      this.permissions = data.permissions || []
      this.menus = data.menus || []
      return data
    },
    logout() {
      this.token = ''
      this.userInfo = {}
      this.menus = []
      localStorage.removeItem('auto710_token')
    }
  }
})
