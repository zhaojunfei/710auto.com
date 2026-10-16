<template>
  <div>
    <div class="topbar">710号汽车之家 · 服务</div>
    <div class="page">
      <div class="card" v-if="store.storeName">
        <div class="between">
          <b>{{ store.storeName }}</b>
          <span class="muted">{{ store.phone || '' }}</span>
        </div>
        <div class="muted" style="margin-top:6px">{{ store.address || '' }}</div>
      </div>

      <div class="grid">
        <div v-for="it in items" :key="it.id" class="card" @click="go(it.id)" style="cursor:pointer">
          <img class="cover" :src="it.cover" />
          <div style="font-weight:600;margin:8px 0 4px">{{ it.itemName }}</div>
          <div class="between">
            <span class="price">¥{{ it.price }}</span>
            <span class="tag" v-if="it.bookable === 1">可预约</span>
          </div>
        </div>
      </div>
      <div class="empty" v-if="!items.length">暂无服务</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { publicApi } from '@/api'

const router = useRouter()
const items = ref<any[]>([])
const store = ref<any>({})
function go(id: number) { router.push('/service/' + id) }
onMounted(async () => {
  try { store.value = (await publicApi.store()) || {} } catch {}
  try {
    const res: any = await publicApi.items({ page: 1, size: 20 })
    items.value = res.records || []
  } catch {}
})
</script>
