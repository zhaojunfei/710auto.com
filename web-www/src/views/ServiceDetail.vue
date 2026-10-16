<template>
  <div class="container">
    <div class="breadcrumb">
      <router-link to="/">{{ t('nav_home') }}</router-link> /
      <router-link to="/services">{{ t('services_title') }}</router-link> /
      {{ pick(item, 'itemName', 'itemNameEn') }}
    </div>
    <div class="section" v-if="item">
      <div class="detail">
        <div class="gallery">
          <img class="main-img" :src="item.cover" :alt="pick(item, 'itemName', 'itemNameEn')" />
          <div class="thumbs" v-if="gallery.length">
            <img v-for="(g, i) in gallery" :key="i" :src="g" @click="item.cover = g" />
          </div>
        </div>
        <div class="info">
          <h1 class="title">{{ pick(item, 'itemName', 'itemNameEn') }}</h1>
          <div class="price-row">
            <span class="price">{{ fmtMoney(item.price) }}</span>
            <span class="muted" v-if="item.laborFee">{{ t('labor_fee') }} {{ fmtMoney(item.laborFee) }}</span>
          </div>
          <ul class="specs">
            <li><span>{{ t('duration') }}</span><b>{{ item.duration || '-' }} 分钟</b></li>
            <li><span>{{ t('price') }}</span><b>{{ item.priceDesc || '-' }}</b></li>
            <li><span>{{ t('bookable') }}</span><b>{{ item.bookable === 1 ? '✔' : '✘' }}</b></li>
          </ul>
          <div class="actions">
            <router-link to="/contact" class="btn">{{ t('book_now') }}</router-link>
            <a class="btn btn-ghost" href="/miniprogram">{{ t('book_mini') }}</a>
          </div>
        </div>
      </div>

      <div class="section-title" style="font-size:22px;margin-top:40px">{{ t('view_detail') }}</div>
      <div class="rich" v-html="pick(item, 'description', 'descriptionEn')"></div>
      <div v-if="!pick(item, 'description', 'descriptionEn')" class="empty">{{ t('empty') }}</div>
    </div>
    <div v-else class="empty">{{ t('loading') }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fmtMoney, pick, t } from '@/i18n'
import { publicApi } from '@/api'

const route = useRoute()
const item = ref<any>(null)

const gallery = computed(() => {
  const raw = item.value?.images
  if (!raw) return []
  try {
    const arr = Array.isArray(raw) ? raw : JSON.parse(raw)
    return Array.isArray(arr) ? arr : []
  } catch { return [] }
})

async function load() {
  try { item.value = await publicApi.item(Number(route.params.id)) } catch { item.value = null }
}
watch(() => route.params.id, load)
onMounted(load)
</script>

<style scoped>
.detail { display: grid; grid-template-columns: 1fr 1fr; gap: 36px; align-items: start; }
.main-img { width: 100%; height: 380px; object-fit: cover; border-radius: var(--radius); }
.thumbs { display: flex; gap: 10px; margin-top: 12px; }
.thumbs img { width: 72px; height: 72px; object-fit: cover; border-radius: 8px; cursor: pointer; border: 1px solid var(--line); }
.title { font-size: 28px; margin: 0 0 16px; }
.price-row { display: flex; align-items: baseline; gap: 16px; margin-bottom: 18px; }
.price-row .price { font-size: 30px; }
.specs { list-style: none; padding: 0; margin: 0 0 22px; border-top: 1px solid var(--line); }
.specs li { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid var(--line); }
.specs span { color: var(--muted); }
.actions { display: flex; gap: 12px; }
.rich { line-height: 1.9; }
.rich :deep(img) { border-radius: 8px; margin: 12px 0; }
@media (max-width: 960px) { .detail { grid-template-columns: 1fr; } }
</style>
