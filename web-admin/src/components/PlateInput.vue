<template>
  <div class="plate-input">
    <div class="plate-row">
      <el-select v-model="province" class="pi-prov" placeholder="简称" @change="sync" filterable>
        <el-option v-for="p in PROVINCES" :key="p" :label="p" :value="p" />
      </el-select>
      <el-input
        v-model="rest"
        class="pi-no"
        :placeholder="type === 'green' ? '如 D123456（7位）' : '如 A12345（6位）'"
        :maxlength="type === 'green' ? 7 : 6"
        @input="onInput"
      />
      <el-radio-group v-model="type" class="pi-type" @change="onTypeChange">
        <el-radio-button value="blue">蓝牌(油)</el-radio-button>
        <el-radio-button value="green">绿牌(电)</el-radio-button>
      </el-radio-group>
    </div>
    <div v-if="error" class="pi-err">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

/**
 * 车牌号专用输入组件（全局统一）
 * 行政区简称下拉 + 车牌号码 + 油车(蓝牌 7 位) / 新能源(绿牌 8 位) 切换，按类型校验格式。
 * v-model 输出完整车牌字符串，如「黑A12345」/「粤BD12345」。
 */
const PROVINCES = [
  '京', '津', '冀', '晋', '蒙', '辽', '吉', '黑', '沪', '苏', '浙', '皖', '闽', '赣', '鲁',
  '豫', '鄂', '湘', '粤', '桂', '琼', '渝', '川', '贵', '云', '藏', '陕', '甘', '青', '宁', '新'
]

const props = withDefaults(
  defineProps<{ modelValue?: string; required?: boolean }>(),
  { modelValue: '', required: false }
)
const emit = defineEmits<{ (e: 'update:modelValue', v: string): void }>()

const province = ref('')
const rest = ref('')
const type = ref<'blue' | 'green'>('blue')
const error = ref('')

/** 校验：蓝牌 = 简称 + 字母 + 5 位（共 7 位）；绿牌 = 简称 + 字母 + 6 位（共 8 位） */
function validate(showError = true): boolean {
  const p = province.value
  const r = (rest.value || '').toUpperCase().replace(/\s/g, '')
  let msg = ''
  if (!p || !r) {
    if (props.required && (!p || !r)) msg = '请完整填写车牌号（省份简称 + 号码）'
  } else {
    const len = type.value === 'green' ? 7 : 6
    if (!/^[A-Z][A-Z0-9]*$/.test(r)) msg = '号码须以字母开头，仅含字母与数字（不含 I、O）'
    else if (r.length !== len) msg = type.value === 'green' ? '新能源绿牌为「简称+字母+6位」共 8 位' : '蓝牌为「简称+字母+5位」共 7 位'
    else if (/[IO]/.test(r)) msg = '车牌号码不含字母 I、O'
  }
  if (showError) error.value = msg
  return !msg
}

function compose() {
  const r = (rest.value || '').toUpperCase().replace(/\s/g, '')
  return province.value && r ? province.value + r : ''
}

function sync() {
  validate(false)
  error.value = ''
  emit('update:modelValue', compose())
}

function onInput(v: string) {
  const up = (v || '').toUpperCase().replace(/\s/g, '')
  if (up !== v) rest.value = up
  sync()
}

function onTypeChange() {
  rest.value = rest.value.slice(0, type.value === 'green' ? 7 : 6)
  sync()
}

/** 外部值 → 内部状态（编辑回显 / 程序赋值） */
function parse(v: string) {
  const val = (v || '').toUpperCase()
  if (!val) {
    province.value = ''
    rest.value = ''
    return
  }
  if (PROVINCES.includes(val.charAt(0))) {
    province.value = val.charAt(0)
    rest.value = val.slice(1)
  } else {
    province.value = ''
    rest.value = val
  }
  type.value = rest.value.length > 6 ? 'green' : 'blue'
}

watch(
  () => props.modelValue,
  (v) => {
    if ((v || '').toUpperCase() !== compose()) parse(v || '')
  },
  { immediate: true }
)

defineExpose({ validate, plate: compose })
</script>

<style scoped>
.plate-input { width: 100%; }
.plate-row { display: flex; gap: 6px; align-items: center; }
.pi-prov { width: 84px; flex: 0 0 84px; }
.pi-no { flex: 1 1 auto; }
.pi-type { flex: 0 0 auto; }
.pi-err { color: var(--el-color-danger); font-size: 12px; line-height: 1.4; margin-top: 2px; }
</style>
