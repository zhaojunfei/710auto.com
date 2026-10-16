<template>
  <div>
    <div class="topbar">接车技师端</div>
    <div class="page">
      <div class="card">
        <div class="card-title">技师登录</div>
        <label>账号</label>
        <input v-model="username" placeholder="请输入技师账号" />
        <label>密码</label>
        <input v-model="password" type="password" placeholder="请输入密码" />
        <button class="btn" :disabled="loading" @click="submit" style="margin-top:16px">
          {{ loading ? '登录中...' : '登录' }}
        </button>
        <div class="muted" style="margin-top:10px">仅可查看本人被派工单；改价需店长权限。</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { techApi } from '@/api'

const router = useRouter()
const username = ref('')
const password = ref('')
const loading = ref(false)

async function submit() {
  if (!username.value || !password.value) { alert('请输入账号与密码'); return }
  loading.value = true
  try {
    const data: any = await techApi.login({ username: username.value, password: password.value })
    localStorage.setItem('auto710_tech_token', data.token)
    localStorage.setItem('auto710_tech_name', data.technician?.techName || username.value)
    router.push('/tech/orders')
  } catch (e: any) {
    alert(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>
