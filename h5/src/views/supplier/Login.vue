<template>
  <div>
    <div class="topbar">供应商端</div>
    <div class="page">
      <div class="card">
        <div class="card-title">供应商登录</div>
        <label>账号</label>
        <input v-model="username" placeholder="请输入供应商账号" />
        <label>密码</label>
        <input v-model="password" type="password" placeholder="请输入密码" />
        <button class="btn" :disabled="loading" @click="submit" style="margin-top:16px">
          {{ loading ? '登录中...' : '登录' }}
        </button>
        <div class="muted" style="margin-top:10px">可维护自己的配件报价、提交送货单、查看对账单。</div>
        <div class="muted back-link" @click="router.push('/services')">返回客户版 ›</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { supplierApi } from '@/api'

const router = useRouter()
const username = ref('')
const password = ref('')
const loading = ref(false)

async function submit() {
  if (!username.value || !password.value) { alert('请输入账号与密码'); return }
  loading.value = true
  try {
    const data: any = await supplierApi.login({ username: username.value, password: password.value })
    localStorage.setItem('auto710_supplier_token', data.token)
    localStorage.setItem('auto710_supplier_name', data.supplier?.supplierName || username.value)
    router.push('/supplier/home')
  } catch (e: any) {
    alert(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.back-link { margin-top: 16px; text-align: center; cursor: pointer; }
</style>
