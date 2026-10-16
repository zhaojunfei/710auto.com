<template>
  <div>
    <div class="topbar">我的工单</div>
    <div class="page">
      <div class="card" v-for="o in orders" :key="o.id">
        <div class="between">
          <b>{{ o.orderNo || ('工单 #' + o.id) }}</b>
          <span class="tag">{{ o.status }}</span>
        </div>
        <div class="muted" style="margin-top:6px">车牌：{{ o.plateNo || '-' }}</div>
        <div class="muted">进店：{{ o.createTime }}</div>
        <div class="between" style="margin-top:8px">
          <span class="price">¥{{ o.totalAmount || 0 }}</span>
          <button class="btn ghost sm" @click="detail(o.id)">查看明细</button>
        </div>
        <div v-if="details[o.id]" style="margin-top:10px;border-top:1px solid var(--line);padding-top:8px">
          <div v-for="it in details[o.id]" :key="it.id" class="between muted">
            <span>{{ it.itemName }} × {{ it.quantity || 1 }}</span>
            <span>¥{{ it.amount || 0 }}</span>
          </div>
        </div>
      </div>
      <div class="empty" v-if="!logged">请先登录查看工单</div>
      <button class="btn" v-if="!logged" @click="login" style="margin-top:10px">微信登录</button>
      <div class="empty" v-else-if="!orders.length">暂无工单</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { memberApi } from '@/api'

const orders = ref<any[]>([])
const details = reactive<Record<number, any[]>>({})
const logged = ref(!!localStorage.getItem('auto710_h5_token'))

async function load() {
  try {
    orders.value = (await memberApi.workOrders()) as any
  } catch { orders.value = [] }
}
async function detail(id: number) {
  if (details[id]) { delete details[id]; return }
  try { details[id] = (await memberApi.workOrderItems(id)) as any } catch { details[id] = [] }
}
function login() {
  const unionid = localStorage.getItem('auto710_unionid') || 'h5-' + Date.now()
  localStorage.setItem('auto710_unionid', unionid)
  memberApi.wxLogin({ unionid, source: 'h5', nickname: 'H5用户' }).then((data: any) => {
    localStorage.setItem('auto710_h5_token', data.token)
    logged.value = true
    load()
  })
}
onMounted(() => { if (logged.value) load() })
</script>
