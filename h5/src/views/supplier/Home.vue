<template>
  <div>
    <div class="topbar">
      供应商工作台
      <span class="quit" @click="logout">退出</span>
    </div>
    <div class="page">
      <div class="card">
        <div class="between" style="margin-bottom:10px">
          <b>{{ profile.supplierName || '供应商' }}</b>
          <span class="tag">{{ profile.contactName || '-' }}</span>
        </div>
        <div class="row" style="gap:10px">
          <div class="stat">
            <div class="num">{{ stats.pending || 0 }}</div>
            <div class="muted">待确认</div>
          </div>
          <div class="stat">
            <div class="num green">{{ stats.stored || 0 }}</div>
            <div class="muted">已入库</div>
          </div>
          <div class="stat">
            <div class="num blue">{{ stats.partCount || 0 }}</div>
            <div class="muted">在供配件</div>
          </div>
        </div>
        <div class="muted" style="margin-top:10px">累计已入库金额：<span class="price">¥{{ stats.storedAmount || 0 }}</span></div>
      </div>

      <div class="grid">
        <div class="card entry" @click="router.push('/supplier/deliveries/new')">
          <div class="entry-t">提交送货单</div>
          <div class="muted">填写本次到货明细</div>
        </div>
        <div class="card entry" @click="router.push('/supplier/deliveries')">
          <div class="entry-t">我的送货单</div>
          <div class="muted">进度与确认结果</div>
        </div>
        <div class="card entry" @click="router.push('/supplier/parts')">
          <div class="entry-t">配件与报价</div>
          <div class="muted">维护供货目录</div>
        </div>
        <div class="card entry" @click="router.push('/supplier/statements')">
          <div class="entry-t">对账单</div>
          <div class="muted">按月汇总已入库</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { supplierApi } from '@/api'

const router = useRouter()
const profile = ref<any>({})
const stats = ref<any>({})

function logout() {
  localStorage.removeItem('auto710_supplier_token')
  router.push('/supplier/login')
}

onMounted(async () => {
  try { profile.value = (await supplierApi.profile()) as any } catch {}
  try { stats.value = (await supplierApi.stats()) as any } catch {}
})
</script>

<style scoped>
.quit { margin-left: auto; font-size: 13px; font-weight: 400; cursor: pointer; }
.stat { flex: 1; text-align: center; }
.num { font-size: 20px; font-weight: 700; }
.num.green { color: #16a34a; }
.num.blue { color: var(--brand); }
.entry { cursor: pointer; }
.entry-t { font-size: 15px; font-weight: 600; margin-bottom: 4px; }
</style>
