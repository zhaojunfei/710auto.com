<template>
  <div>
    <div class="topbar">
      我的工单
      <span style="margin-left:auto;font-size:13px;font-weight:400" @click="logout">退出</span>
    </div>
    <div class="page">
      <div class="card">
        <div class="row" style="gap:10px">
          <div style="flex:1;text-align:center">
            <div style="font-size:20px;font-weight:700">{{ stats.totalOrders || 0 }}</div>
            <div class="muted">累计工单</div>
          </div>
          <div style="flex:1;text-align:center">
            <div style="font-size:20px;font-weight:700;color:#1f6feb">{{ stats.workingOrders || 0 }}</div>
            <div class="muted">施工中</div>
          </div>
          <div style="flex:1;text-align:center">
            <div style="font-size:20px;font-weight:700;color:#16a34a">{{ stats.finishedOrders || 0 }}</div>
            <div class="muted">已完工</div>
          </div>
        </div>
      </div>

      <div class="row" style="gap:8px;margin-bottom:10px;flex-wrap:wrap">
        <button v-for="s in statuses" :key="s" class="btn sm"
          :class="{ ghost: status !== s }" @click="changeStatus(s)">{{ s }}</button>
      </div>

      <div class="card" v-for="o in orders" :key="o.id" @click="go(o.id)" style="cursor:pointer">
        <div class="between">
          <b>{{ o.orderNo || ('工单 #' + o.id) }}</b>
          <span class="tag">{{ o.status }}</span>
        </div>
        <div class="muted" style="margin-top:6px">{{ o.plateNo || '-' }} · {{ o.brandModel || '' }}</div>
        <div class="muted">进店：{{ o.createTime }}</div>
      </div>
      <div class="empty" v-if="!orders.length">暂无工单</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { techApi } from '@/api'

const router = useRouter()
const orders = ref<any[]>([])
const stats = ref<any>({})
const status = ref('')
const statuses = ['', '待派工', '施工中', '待质检', '待结算']

function go(id: number) { router.push('/tech/orders/' + id) }
function changeStatus(s: string) { status.value = s; load() }
function logout() { localStorage.removeItem('auto710_tech_token'); router.push('/tech/login') }

async function load() {
  try { orders.value = (await techApi.orders(status.value || undefined)) as any } catch { orders.value = [] }
}
onMounted(async () => {
  try { stats.value = (await techApi.stats()) as any } catch {}
  load()
})
</script>
