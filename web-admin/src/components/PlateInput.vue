<template>
  <div class="plate-input">
    <el-input
      v-model="text"
      placeholder="请输入车牌号，如 京A12345 / 粤BD12345"
      maxlength="8"
      clearable
      @input="onInput"
      @blur="validate(false)"
    />
    <div v-if="error" class="pi-err">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

/**
 * 车牌号输入框（全局统一）
 * 直接输入完整车牌号，自动转大写、去空格；提交前做一次宽松格式校验
 * （省简称 + 字母 + 5~6 位，蓝牌 7 位 / 绿牌 8 位），v-model 输出完整车牌字符串。
 */
const props = withDefaults(
  defineProps<{ modelValue?: string; required?: boolean }>(),
  { modelValue: '', required: false }
)
const emit = defineEmits<{ (e: 'update:modelValue', v: string): void }>()

const text = ref('')
const error = ref('')

function norm(v: string) {
  return (v || '').toUpperCase().replace(/\s/g, '')
}

/** 校验：省简称 + 字母 + 5~6 位，非法格式在提交前拦截 */
function validate(showError = true): boolean {
  const v = norm(text.value)
  let msg = ''
  if (!v) {
    if (props.required) msg = '请输入车牌号'
  } else if (!/^[\u4e00-\u9fa5][A-Z][A-Z0-9]{4,6}$/.test(v)) {
    msg = '车牌号格式不正确，如 京A12345 / 粤BD12345'
  }
  if (showError) error.value = msg
  return !msg
}

function onInput(v: string) {
  const up = norm(v)
  if (up !== v) text.value = up
  if (error.value) error.value = ''
  emit('update:modelValue', up)
}

watch(
  () => props.modelValue,
  (v) => {
    const val = norm(v || '')
    if (val !== text.value) text.value = val
  },
  { immediate: true }
)

defineExpose({ validate })
</script>

<style scoped>
.plate-input { width: 100%; }
.pi-err { color: var(--el-color-danger); font-size: 12px; line-height: 1.4; margin-top: 2px; }
</style>
