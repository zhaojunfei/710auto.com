<template>
  <div class="page-container">
    <div class="card-block">
      <el-alert type="success" :closable="false" style="margin-bottom:12px">
        <template #title>系统全局参数可视化配置：站点、公众号、小程序、微信支付、短信、SEO 等；保存后前台实时生效，无需改代码、无需重启。密钥类字段页面只返回掩码，不明文展示。</template>
      </el-alert>
      <el-tabs v-model="activeGroup" @tab-change="loadGroup">
        <el-tab-pane v-for="g in groups" :key="g" :label="groupLabel(g)" :name="g" />
      </el-tabs>

      <el-form :model="formData" label-width="200px" style="max-width:820px" v-loading="loading">
        <el-form-item v-for="c in configs" :key="c.configKey" :label="c.configName || c.configKey">
          <el-input v-if="c.encrypted === 1" v-model="formData[c.configKey]" type="password" show-password :placeholder="'当前：' + c.configValue" />
          <el-input v-else-if="c.configType === 'bool'" v-model="formData[c.configKey]" placeholder="true / false" />
          <el-input v-else v-model="formData[c.configKey]" />
          <div class="text-muted">键名：{{ c.configKey }}</div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="save">保存并实时生效</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const groups = ref<string[]>([])
const activeGroup = ref('')
const configs = ref<any[]>([])
const formData = ref<any>({})
const loading = ref(false)
const saving = ref(false)

const labels: any = { site: '网站基础', storage: '对象存储', wechat: '公众号/小程序', pay: '微信支付', sms: '短信通道' }
const groupLabel = (g: string) => labels[g] || g

async function loadGroup() {
  if (!activeGroup.value) return
  loading.value = true
  try {
    configs.value = await request.get(`/admin/config/group/${activeGroup.value}`)
    const data: any = {}
    configs.value.forEach((c) => { data[c.configKey] = c.encrypted === 1 ? '' : c.configValue })
    formData.value = data
  } finally {
    loading.value = false
  }
}

async function save() {
  saving.value = true
  try {
    const list = configs.value.map((c) => ({
      configKey: c.configKey,
      configValue: formData.value[c.configKey],
      configName: c.configName,
      configGroup: c.configGroup,
      configType: c.configType,
      encrypted: c.encrypted
    }))
    await request.post('/admin/config/save-batch', list)
    ElMessage.success('保存成功，已实时生效')
    loadGroup()
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  groups.value = await request.get('/admin/config/groups')
  if (groups.value.length) {
    activeGroup.value = groups.value[0]
    loadGroup()
  }
})
</script>
