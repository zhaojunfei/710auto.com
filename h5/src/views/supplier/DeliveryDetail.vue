<template>
  <div>
    <div class="topbar">
      <span class="back" @click="router.back()">‹</span>
      送货单详情
    </div>
    <div class="page">
      <div class="card">
        <div class="between">
          <b>{{ delivery.deliveryNo }}</b>
          <span class="tag">{{ delivery.status }}</span>
        </div>
        <div class="kv"><span class="muted">预计到货</span><span>{{ delivery.expectDate || '-' }}</span></div>
        <div class="kv"><span class="muted">提交时间</span><span>{{ delivery.submitTime || '-' }}</span></div>
        <div class="kv"><span class="muted">确认时间</span><span>{{ delivery.confirmTime || '-' }}</span></div>
        <div class="kv"><span class="muted">确认备注</span><span>{{ delivery.confirmRemark || '-' }}</span></div>
        <div class="kv"><span class="muted">备注</span><span>{{ delivery.remark || '-' }}</span></div>
        <div class="kv"><span class="muted">合计金额</span><span class="price">¥{{ delivery.totalAmount || 0 }}</span></div>
      </div>

      <div class="card">
        <div class="card-title">送货明细</div>
        <div v-for="it in items" :key="it.id" class="item">
          <div class="between">
            <b>{{ it.partName }}</b>
            <span class="price">¥{{ it.amount || 0 }}</span>
          </div>
          <div class="muted">{{ it.spec || '-' }} · {{ it.quantity }}{{ it.unit || '' }} × ¥{{ it.unitPrice || 0 }}</div>
          <div class="muted" v-if="it.remark">备注：{{ it.remark }}</div>
        </div>
        <div class="empty" v-if="!items.length">暂无明细</div>
      </div>

      <button v-if="delivery.status === '待确认'" class="btn" :disabled="loading" @click="cancel">撤销送货单</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { supplierApi } from '@/api'

const route = useRoute()
const router = useRouter()
const delivery = ref<any>({})
const items = ref<any[]>([])
const loading = ref(false)

async function load() {
  try {
    const data: any = await supplierApi.delivery(Number(route.params.id))
    delivery.value = data.delivery || {}
    items.value = data.items || []
  } catch (e: any) {
    alert(e.message || '加载失败')
  }
}

async function cancel() {
  if (!confirm('确认撤销该送货单？')) return
  loading.value = true
  try {
    await supplierApi.cancel(Number(route.params.id))
    alert('已撤销')
    router.replace('/supplier/deliveries')
  } catch (e: any) {
    alert(e.message || '操作失败')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.kv { display: flex; justify-content: space-between; padding: 6px 0; font-size: 14px; }
.item { border-top: 1px solid var(--line); padding: 10px 0; }
.item:first-of-type { border-top: none; }
</style>
