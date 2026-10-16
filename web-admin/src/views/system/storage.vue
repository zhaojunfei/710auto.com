<template>
  <div class="page-container">
    <div class="card-block">
      <h3>第一步：选择存储驱动</h3>
      <p class="text-muted">未选中的驱动不渲染参数表单；切换驱动时历史图片仍由原驱动读取，新上传走新驱动。</p>
      <el-row :gutter="12">
        <el-col :span="4" v-for="(name, type) in drivers" :key="type">
          <div class="driver-card" :class="{ active: form.type === type }" @click="selectDriver(String(type))">
            <div class="logo">{{ logo(String(type)) }}</div>
            <div>{{ name }}</div>
            <div class="text-muted">{{ desc(String(type)) }}</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="card-block" v-if="form.type">
      <h3>第二步：填写参数</h3>
      <el-form :model="form" label-width="140px" style="max-width:720px">
        <template v-if="form.type === 'local'">
          <el-form-item label="存储路径">
            <el-input v-model="form.basePath" placeholder="./data/upload" />
          </el-form-item>
          <el-form-item label="外网访问前缀">
            <el-input v-model="form.urlPrefix" placeholder="http://127.0.0.1:8080/upload/local" />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="Endpoint">
            <el-input v-model="form.endpoint" :placeholder="endpointPlaceholder" />
          </el-form-item>
          <el-form-item label="Region">
            <el-input v-model="form.region" :placeholder="regionPlaceholder" />
          </el-form-item>
          <el-form-item label="AccessKey ID">
            <el-input v-model="form.accessKey" />
          </el-form-item>
          <el-form-item label="AccessKey Secret">
            <el-input v-model="form.secretKey" type="password" show-password placeholder="留空或掩码表示不修改" />
          </el-form-item>
          <el-form-item label="Bucket 名称">
            <el-input v-model="form.bucket" />
          </el-form-item>
          <el-form-item label="外网访问域名">
            <el-input v-model="form.domain" placeholder="可选，如 https://cdn.example.com" />
          </el-form-item>
          <el-form-item label="Path Style" v-if="form.type === 'minio'">
            <el-switch v-model="form.pathStyle" />
          </el-form-item>
        </template>
      </el-form>

      <div class="toolbar">
        <el-button :loading="testing" @click="testConnection">连接测试</el-button>
        <el-button type="primary" :disabled="!testPassed" :loading="saving" @click="save">保存并应用</el-button>
        <span v-if="testPassed" style="color:#67c23a">✓ 连接测试通过</span>
        <span v-else class="text-muted">需先通过「连接测试」才能保存</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { storageApi } from '@/api'

const drivers = ref<any>({})
const form = reactive<any>({ type: '' })
const originalType = ref('')
const testPassed = ref(false)
const testing = ref(false)
const saving = ref(false)

const endpointPlaceholder = computed(() => ({
  s3: 'https://s3.amazonaws.com', oss: 'https://oss-cn-hangzhou.aliyuncs.com',
  cos: 'https://cos.ap-guangzhou.myqcloud.com', minio: 'http://127.0.0.1:9000'
} as any)[form.type] || '')

const regionPlaceholder = computed(() => ({
  s3: 'us-east-1', oss: 'oss-cn-hangzhou', cos: 'ap-guangzhou', minio: 'us-east-1'
} as any)[form.type] || '')

function logo(type: string) {
  return { local: '💾', s3: 'S3', oss: 'OSS', cos: 'COS', minio: 'M' }[type] || type.toUpperCase()
}
function desc(type: string) {
  return {
    local: '服务器本地磁盘', s3: '亚马逊云对象存储', oss: '阿里云对象存储',
    cos: '腾讯云对象存储', minio: '自建 S3 兼容存储'
  }[type] || ''
}

function selectDriver(type: string) {
  if (form.type !== type) {
    testPassed.value = false
  }
  form.type = type
}

async function testConnection() {
  testing.value = true
  try {
    await storageApi.test({ ...form })
    testPassed.value = true
    ElMessage.success('连接测试通过（已实际上传并读取 1KB 测试文件）')
  } catch {
    testPassed.value = false
  } finally {
    testing.value = false
  }
}

async function save() {
  if (originalType.value && originalType.value !== form.type) {
    await ElMessageBox.confirm(
      `切换后历史图片仍由原驱动读取，新上传走新驱动，确认从「${originalType.value}」切换到「${form.type}」？`,
      '切换存储驱动', { type: 'warning' }
    )
  }
  saving.value = true
  try {
    await storageApi.save({ ...form })
    originalType.value = form.type
    ElMessage.success('保存并应用成功，新上传将使用该驱动')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  drivers.value = await storageApi.drivers()
  const config = await storageApi.config()
  Object.assign(form, config)
  originalType.value = form.type
})
</script>
