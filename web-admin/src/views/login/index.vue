<template>
  <div class="login-bg">
    <div class="login-box">
      <div class="brand">
        <div class="brand-logo">710</div>
        <h2>710号汽车之家</h2>
        <p class="text-muted">数字化多端平台 · 统一后台管理</p>
      </div>
      <el-form :model="form" @keyup.enter="onSubmit">
        <el-form-item>
          <el-input v-model="form.username" size="large" placeholder="请输入账号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" size="large" type="password" show-password placeholder="请输入密码" :prefix-icon="Lock" />
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="onSubmit">登 录</el-button>
      </el-form>
      <div class="tips text-muted">默认账号 admin / admin123（首次登录后请修改密码）</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123' })

async function onSubmit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-bg { height: 100%; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #1f6feb 0%, #0b2a5b 100%); }
.login-box { width: 380px; background: #fff; border-radius: 12px; padding: 40px 32px 24px; box-shadow: 0 12px 40px rgba(0,0,0,.2); }
.brand { text-align: center; margin-bottom: 24px; }
.brand-logo { width: 64px; height: 64px; line-height: 64px; border-radius: 50%; background: var(--brand-color); color: #fff; font-size: 24px; font-weight: 700; margin: 0 auto 12px; }
.brand h2 { margin: 0 0 4px; }
.tips { margin-top: 12px; text-align: center; }
</style>
