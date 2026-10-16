<template>
  <div class="container">
    <div class="breadcrumb">{{ t('nav_home') }} / {{ t('services_title') }}</div>
    <div class="section">
      <h2 class="section-title">{{ t('services_title') }}</h2>
      <p class="section-sub">{{ t('services_sub') }}</p>

      <div class="filters">
        <button :class="{ active: categoryId === null }" @click="selectCategory(null)">{{ t('all') }}</button>
        <button v-for="c in categories" :key="c.id" :class="{ active: categoryId === c.id }" @click="selectCategory(c.id)">
          {{ pick(c, 'categoryName', 'categoryNameEn') }}
        </button>
      </div>

      <div class="grid grid-3">
        <router-link v-for="it in items" :key="it.id" :to="`/services/${it.id}`" class="card">
          <img class="card-cover" :src="it.cover" :alt="pick(it, 'itemName', 'itemNameEn')" />
          <div class="card-body">
            <h3 class="card-title">{{ pick(it, 'itemName', 'itemNameEn') }}</h3>
            <p class="card-desc">{{ truncate(pick(it, 'description', 'descriptionEn')) }}</p>
            <div class="meta">
              <span class="price">{{ fmtMoney(it.price) }}</span>
              <span class="muted" v-if="it.duration">{{ t('duration') }} {{ it.duration }}min</span>
            </div>
            <span class="tag" v-if="it.bookable === 1">{{ t('bookable') }}</span>
          </div>
        </router-link>
      </div>
      <div v-if="!items.length && !loading" class="empty">{{ t('empty') }}</div>

      <div class="pager" v-if="total > size">
        <button :disabled="page <= 1" @click="page--; load()">上一页</button>
        <span>{{ page }} / {{ Math.ceil(total / size) }}</span>
        <button :disabled="page >= Math.ceil(total / size)" @click="page++; load()">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fmtMoney, pick, t } from '@/i18n'
import { publicApi } from '@/api'

const loading = ref(false)
const categories = ref<any[]>([])
const items = ref<any[]>([])
const categoryId = ref<number | null>(null)
const page = ref(1)
const size = ref(9)
const total = ref(0)

function truncate(s: string, n = 56) {
  if (!s) return ''
  const text = String(s).replace(/<[^>]+>/g, '')
  return text.length > n ? text.slice(0, n) + '...' : text
}
function selectCategory(id: number | null) {
  categoryId.value = id
  page.value = 1
  load()
}
async function load() {
  loading.value = true
  try {
    const res: any = await publicApi.items({ page: page.value, size: size.value, categoryId: categoryId.value || undefined })
    items.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}
onMounted(async () => {
  try { categories.value = (await publicApi.categories()) || [] } catch { categories.value = [] }
  load()
})
</script>

<style scoped>
.filters { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 24px; }
.filters button { border: 1px solid var(--line); background: #fff; border-radius: 20px; padding: 6px 18px; cursor: pointer; font-size: 14px; color: var(--muted); }
.filters button.active { background: var(--brand); border-color: var(--brand); color: #fff; }
.meta { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.pager { display: flex; gap: 16px; justify-content: center; align-items: center; margin-top: 28px; }
.pager button { border: 1px solid var(--line); background: #fff; border-radius: 8px; padding: 6px 16px; cursor: pointer; }
.pager button:disabled { opacity: .5; cursor: not-allowed; }
</style>
