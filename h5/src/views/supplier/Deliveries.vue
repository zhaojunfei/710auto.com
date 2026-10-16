<template>
  <div>
    <div class="topbar">
      <span class="back" @click="router.back()">‹</span>
      我的送货单
    </div>
    <div class="page">
      <button class="btn" style="margin-bottom:10px" @click="router.push('/supplier/deliveries/new')">＋ 提交送货单</button>

      <div class="row" style="gap:8px;margin-bottom:10px;flex-wrap:wrap">
        <button v-for="s in statuses" :key="s.value" class="btn sm" :class="{ ghost: status !== s.value }" @click="changeStatus(s.value)">
          {{ s.label }}
        </button>
      </div>

      <div class="card" v-for="d in deliveries" :key="d.id" style="cursor:pointer" @click="router.push('/supplier/deliveries/' + d.id)">
        <div class="between">
          <b>{{ d.deliveryNo }}</b>
          <span class="tag">{{ d.status }}</span>
        </div>
        <div class="muted" style="margin-top:6px">
          {{ d.itemCount || 0 }} 项 · 金额 <span class="price">¥{{ d.totalAmount || 0 }}</span>
        </div>
        <div class="muted">预计到货：{{ d.expectDate || '-' }} · 提交：{{ d.submitTime || '-' }}</div>
      </div>
      <div class="empty" v-if="!deliveries.length">暂无送货单</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { supplierApi } from '@/api'

const router = useRouter()
const deliveries = ref<any[]>([])
const status = ref('')
const statuses = [
  { label: '全部', value: '' },
  { label: '待确认', value: '待确认' },
  { label: '已入库', value: '已入库' },
  { label: '已拒绝', value: '已拒绝' }
]

function changeStatus(s: string) { status.value = s; load() }

async function load() {
  try { deliveries.value = (await supplierApi.deliveries(status.value || undefined)) as any } catch { deliveries.value = [] }
}
onMounted(load)
</script>
