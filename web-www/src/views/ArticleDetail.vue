<template>
  <div class="container">
    <div class="breadcrumb">
      <router-link to="/">{{ t('nav_home') }}</router-link> /
      <router-link to="/articles">{{ t('articles_title') }}</router-link> /
      {{ pick(article, 'title', 'titleEn') }}
    </div>
    <div class="section" v-if="article">
      <h1 class="title">{{ pick(article, 'title', 'titleEn') }}</h1>
      <div class="meta muted">
        <span>{{ article.publishTime }}</span>
        <span>{{ article.author || '710号汽车之家' }}</span>
        <span>阅读 {{ article.viewCount || 0 }}</span>
      </div>
      <img v-if="article.cover" :src="article.cover" class="cover" />
      <div class="rich" v-html="pick(article, 'content', 'contentEn')"></div>

      <h3 style="margin-top:40px">{{ t('articles_title') }}</h3>
      <div class="grid grid-3">
        <router-link v-for="a in related" :key="a.id" :to="`/articles/${a.id}`" class="card">
          <img class="card-cover" :src="a.cover" />
          <div class="card-body"><h3 class="card-title">{{ pick(a, 'title', 'titleEn') }}</h3></div>
        </router-link>
      </div>
    </div>
    <div v-else class="empty">{{ t('loading') }}</div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { pick, t } from '@/i18n'
import { publicApi } from '@/api'

const route = useRoute()
const article = ref<any>(null)
const related = ref<any[]>([])

async function load() {
  try {
    article.value = await publicApi.article(Number(route.params.id))
    const res: any = await publicApi.articles({ page: 1, size: 3 })
    related.value = (res.records || []).filter((a: any) => a.id !== Number(route.params.id))
  } catch { article.value = null }
}
watch(() => route.params.id, load)
onMounted(load)
</script>

<style scoped>
.title { font-size: 28px; margin: 0 0 12px; }
.meta { display: flex; gap: 18px; font-size: 13px; margin-bottom: 20px; }
.cover { width: 100%; max-height: 420px; object-fit: cover; border-radius: var(--radius); margin-bottom: 24px; }
.rich { line-height: 1.9; font-size: 16px; }
.rich :deep(img) { border-radius: 8px; margin: 16px 0; max-width: 100%; }
</style>
