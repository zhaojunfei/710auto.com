<template>
  <div class="upload-block">
    <el-upload
      :action="action"
      :headers="headers"
      name="file"
      :data="{ dir, bizType }"
      list-type="picture-card"
      :file-list="fileList"
      :on-success="onSuccess"
      :on-remove="onRemove"
      :on-preview="onPreview"
      multiple
      v-model:file-list="fileList"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>
    <div class="text-muted">支持拖拽/点击上传、多图并行；建议尺寸 1080×1440，单张 ≤5MB</div>
    <el-dialog v-model="previewVisible" width="620px">
      <img :src="previewUrl" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'

const props = defineProps<{
  modelValue?: string | string[]
  dir?: string
  bizType?: string
  multiple?: boolean
}>()
const emit = defineEmits(['update:modelValue'])

const action = '/api/admin/file/upload'
const headers = { Authorization: 'Bearer ' + (localStorage.getItem('auto710_token') || '') }
const dir = props.dir || 'images'
const bizType = props.bizType || 'common'

const previewVisible = ref(false)
const previewUrl = ref('')

function parse(v: any): any[] {
  if (!v) return []
  if (Array.isArray(v)) return v.map((url) => ({ name: url, url }))
  if (typeof v === 'string') {
    try {
      const arr = JSON.parse(v)
      if (Array.isArray(arr)) return arr.map((url: string) => ({ name: url, url }))
    } catch { /* 单个 URL */ }
    return v ? [{ name: v, url: v }] : []
  }
  return []
}

const fileList = ref<any[]>(parse(props.modelValue))

watch(() => props.modelValue, (v) => { fileList.value = parse(v) })

const urls = computed(() => fileList.value.map((f) => f.url).filter(Boolean))

function sync() {
  emit('update:modelValue', props.multiple ? JSON.stringify(urls.value) : urls.value[0] || '')
}
function onSuccess(res: any, file: any) {
  if (res?.code === 200 && res.data?.url) {
    file.url = res.data.url
    sync()
  }
}
function onRemove() { sync() }
function onPreview(file: any) {
  previewUrl.value = file.url
  previewVisible.value = true
}
</script>

<style scoped>
.upload-block { width: 100%; }
</style>
