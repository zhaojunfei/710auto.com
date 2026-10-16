<template>
  <div>
    <div class="topbar"><span class="back" @click="$router.back()">‹</span>服务详情</div>
    <div class="page" v-if="item">
      <img class="cover" style="height:200px" :src="item.cover" />
      <div class="card">
        <div style="font-size:17px;font-weight:700;margin-bottom:8px">{{ item.itemName }}</div>
        <div class="between">
          <span class="price" style="font-size:20px">¥{{ item.price }}</span>
          <span class="muted">工时费 ¥{{ item.laborFee || 0 }}</span>
        </div>
        <div class="muted" style="margin-top:8px">预计时长：{{ item.duration || '-' }} 分钟</div>
      </div>
      <div class="card">
        <div class="card-title">服务介绍</div>
        <div v-html="item.description || '暂无详情'"></div>
      </div>
      <button class="btn" @click="book">立即预约</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { publicApi } from '@/api'
import { applyShareCard } from '@/utils/share'

const route = useRoute()
const router = useRouter()
const item = ref<any>(null)
function book() { router.push({ path: '/booking', query: { serviceId: route.params.id } }) }
onMounted(async () => {
  try {
    item.value = await publicApi.item(Number(route.params.id))
    applyShareCard({ title: item.value?.itemName || '710号汽车之家', desc: '专业技师 · 透明报价', imgUrl: item.value?.cover })
  } catch {}
})
</script>
