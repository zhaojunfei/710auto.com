<template>
  <div>
    <div class="topbar">
      <span class="back" @click="router.back()">‹</span>
      对账单
    </div>
    <div class="page">
      <div class="card">
        <label>对账月份</label>
        <div class="row" style="gap:10px">
          <input v-model="month" type="month" @change="load" />
        </div>
      </div>

      <div class="card">
        <div class="between">
          <b>{{ month }} 已入库合计</b>
          <span class="price">¥{{ statement.total || 0 }}</span>
        </div>
        <div class="muted" style="margin-top:6px">共 {{ statement.count || 0 }} 张送货单</div>
      </div>

      <div class="card" v-for="d in statement.list || []" :key="d.id">
        <div class="between">
          <b>{{ d.deliveryNo }}</b>
          <span class="price">¥{{ d.totalAmount || 0 }}</span>
        </div>
        <div class="muted">{{ d.itemCount || 0 }} 项 · 入库时间 {{ d.confirmTime || '-' }}</div>
      </div>
      <div class="empty" v-if="!(statement.list || []).length">该月暂无已入库送货单</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { supplierApi } from '@/api'

const router = useRouter()
const statement = ref<any>({})
const month = ref(new Date().toISOString().slice(0, 7))

async function load() {
  try { statement.value = (await supplierApi.statements(month.value)) as any } catch { statement.value = {} }
}

onMounted(load)
</script>
