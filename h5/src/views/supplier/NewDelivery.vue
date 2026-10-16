<template>
  <div>
    <div class="topbar">
      <span class="back" @click="router.back()">‹</span>
      提交送货单
    </div>
    <div class="page">
      <div class="card">
        <label>收货门店ID</label>
        <input v-model.number="form.storeId" type="number" placeholder="例如 1" />
        <label>预计到货日期</label>
        <input v-model="form.expectDate" type="date" />
        <label>备注</label>
        <input v-model="form.remark" placeholder="选填" />
      </div>

      <div class="card" v-for="(it, i) in form.items" :key="i">
        <div class="between" style="margin-bottom:6px">
          <b>明细 {{ i + 1 }}</b>
          <span class="muted" @click="removeItem(i)">删除</span>
        </div>
        <label>选择在供配件（可选）</label>
        <select v-model.number="it.partId" @change="onPickPart(i)">
          <option :value="undefined">— 手填新配件 —</option>
          <option v-for="p in parts" :key="p.id" :value="p.id">{{ p.partName }}{{ p.spec ? ' / ' + p.spec : '' }}</option>
        </select>
        <label>配件名称</label>
        <input v-model="it.partName" placeholder="配件名称" />
        <label>规格型号</label>
        <input v-model="it.spec" placeholder="规格型号" />
        <div class="row" style="gap:10px">
          <div style="flex:1">
            <label>单位</label>
            <input v-model="it.unit" placeholder="件" />
          </div>
          <div style="flex:1">
            <label>数量</label>
            <input v-model.number="it.quantity" type="number" min="1" />
          </div>
          <div style="flex:1">
            <label>供货单价</label>
            <input v-model.number="it.unitPrice" type="number" min="0" step="0.01" />
          </div>
        </div>
        <label>备注</label>
        <input v-model="it.remark" placeholder="选填" />
        <div class="muted" style="margin-top:8px">小计：<span class="price">¥{{ lineAmount(it) }}</span></div>
      </div>

      <button class="btn ghost" style="margin-bottom:10px" @click="addItem">＋ 添加明细</button>
      <div class="card between">
        <b>合计金额</b>
        <span class="price">¥{{ totalAmount }}</span>
      </div>
      <button class="btn" :disabled="loading" @click="submit">{{ loading ? '提交中...' : '提交送货单' }}</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { supplierApi } from '@/api'

const router = useRouter()
const parts = ref<any[]>([])
const loading = ref(false)
const form = reactive<any>({
  storeId: 1,
  expectDate: '',
  remark: '',
  items: [newItem()]
})

function newItem() {
  return { partId: undefined, partName: '', spec: '', unit: '', quantity: 1, unitPrice: undefined, remark: '' }
}
function addItem() { form.items.push(newItem()) }
function removeItem(i: number) {
  form.items.splice(i, 1)
  if (!form.items.length) addItem()
}
function lineAmount(it: any) {
  const q = Number(it.quantity) || 0
  const p = Number(it.unitPrice) || 0
  return (q * p).toFixed(2)
}
const totalAmount = computed(() =>
  form.items.reduce((sum: number, it: any) => sum + (Number(it.quantity) || 0) * (Number(it.unitPrice) || 0), 0).toFixed(2)
)

function onPickPart(i: number) {
  const it = form.items[i]
  const part = parts.value.find((p) => p.id === it.partId)
  if (part) {
    it.partName = part.partName
    it.spec = part.spec
    it.unit = part.unit
    it.unitPrice = part.purchasePrice
  }
}

async function submit() {
  const items = form.items.filter((it: any) => it.partName)
  if (!items.length) { alert('请至少填写一条配件明细'); return }
  loading.value = true
  try {
    await supplierApi.submit({ storeId: form.storeId, expectDate: form.expectDate, remark: form.remark, items })
    alert('提交成功，等待门店确认')
    router.replace('/supplier/deliveries')
  } catch (e: any) {
    alert(e.message || '提交失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try { parts.value = (await supplierApi.parts()) as any } catch {}
})
</script>
