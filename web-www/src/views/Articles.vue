<template>
  <div class="container">
    <div class="breadcrumb">{{ t('nav_home') }} / {{ t('articles_title') }}</div>
    <div class="section">
      <h2 class="section-title">{{ t('articles_title') }}</h2>
      <p class="section-sub">{{ t('home_articles') }}</p>

      <div class="article-list">
        <router-link v-for="a in articles" :key="a.id" :to="`/articles/${a.id}`" class="article-item">
          <img :src="a.cover" :alt="pick(a, 'title', 'titleEn')" />
          <div class="article-body">
            <h3>{{ pick(a, 'title', 'titleEn') }}</h3>
            <p class="muted">{{ truncate(pick(a, 'summary', 'summaryEn'), 110) }}</p>
            <span class="muted" style="font-size:12px">{{ a.publishTime }} · {{ a.author || '710号汽车之家' }}</span>
          </div>
        </router-link>
      </div>
      <div v-if="!articles.length" class="empty">{{ t('empty') }}</div>

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
import { pick, t } from '@/i18n'
import { publicApi } from '@/api'

const articles = ref<any[]>([])
const page = ref(1)
const size = ref(8)
const total = ref(0)

function truncate(s: string, n: number) {
  if (!s) return ''
  const text = String(s).replace(/<[^>]+>/g, '')
  return text.length > n ? text.slice(0, n) + '...' : text
}
async function load() {
  try {
    const res: any = await publicApi.articles({ page: page.value, size: size.value })
    articles.value = res.records || []
    total.value = res.total || 0
  } catch { articles.value = [] }
}
onMounted(load)
</script>

<style scoped>
.article-list { display: flex; flex-direction: column; gap: 18px; }
.article-item { display: grid; grid-template-columns: 260px 1fr; gap: 20px; border: 1px solid var(--line); border-radius: var(--radius); overflow: hidden; transition: box-shadow .2s; }
.article-item:hover { box-shadow: 0 8px 24px rgba(20,30,60,.08); }
.article-item img { width: 100%; height: 170px; object-fit: cover; }
.article-body { padding: 16px 16px 16px 0; }
.article-body h3 { margin: 0 0 10px; }
.pager { display: flex; gap: 16px; justify-content: center; align-items: center; margin-top: 28px; }
.pager button { border: 1px solid var(--line); background: #fff; border-radius: 8px; padding: 6px 16px; cursor: pointer; }
.pager button:disabled { opacity: .5; cursor: not-allowed; }
@media (max-width: 640px) { .article-item { grid-template-columns: 1fr; } .article-body { padding: 0 16px 16px; } }
</style>
