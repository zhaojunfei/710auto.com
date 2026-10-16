<template>
  <div>
    <!-- Banner -->
    <section class="hero">
      <div class="container">
        <h1>{{ pick(site, 'siteName', 'siteNameEn') || t('brand') }}</h1>
        <p>{{ t('slogan') }}</p>
        <router-link to="/contact" class="btn">{{ t('book_now') }}</router-link>
        <a class="btn btn-ghost" style="margin-left:12px" :href="miniProgramUrl">{{ t('book_mini') }}</a>
      </div>
    </section>

    <!-- Banner 轮播 -->
    <section class="section" v-if="banners.length">
      <div class="container">
        <div class="banner-track">
          <div v-for="(b, i) in banners" :key="b.id" class="banner-slide" :class="{ active: i === currentBanner }" @click="go(b)">
            <img :src="b.image" :alt="pick(b, 'title', 'titleEn')" />
            <div class="banner-caption">{{ pick(b, 'title', 'titleEn') }}</div>
          </div>
        </div>
        <div class="dots">
          <span v-for="(b, i) in banners" :key="b.id" :class="{ active: i === currentBanner }" @click="currentBanner = i"></span>
        </div>
      </div>
    </section>

    <!-- 品牌故事 -->
    <section class="section" v-if="story">
      <div class="container">
        <h2 class="section-title">{{ t('home_story') }}</h2>
        <p class="section-sub">{{ pick(story, 'title', 'titleEn') }}</p>
        <div class="story">
          <img class="story-cover" :src="story.cover" :alt="pick(story, 'title', 'titleEn')" />
          <div class="story-content" v-html="pick(story, 'content', 'contentEn')"></div>
        </div>
      </div>
    </section>

    <!-- 热门服务 -->
    <section class="section">
      <div class="container">
        <h2 class="section-title">{{ t('home_hot') }}</h2>
        <p class="section-sub">{{ t('services_sub') }}</p>
        <div class="grid grid-4">
          <router-link v-for="it in hotItems" :key="it.id" :to="`/services/${it.id}`" class="card">
            <img class="card-cover" :src="it.cover" :alt="pick(it, 'itemName', 'itemNameEn')" />
            <div class="card-body">
              <h3 class="card-title">{{ pick(it, 'itemName', 'itemNameEn') }}</h3>
              <p class="card-desc">{{ truncate(pick(it, 'description', 'descriptionEn')) }}</p>
              <span class="price">{{ fmtMoney(it.price) }}</span>
              <span class="tag" v-if="it.bookable === 1" style="margin-left:8px">{{ t('bookable') }}</span>
            </div>
          </router-link>
        </div>
        <div v-if="!hotItems.length" class="empty">{{ t('empty') }}</div>
        <div style="text-align:center;margin-top:24px">
          <router-link to="/services" class="btn btn-ghost">{{ t('more') }}</router-link>
        </div>
      </div>
    </section>

    <!-- 施工案例 -->
    <section class="section" style="background:var(--bg)">
      <div class="container">
        <h2 class="section-title">{{ t('home_cases') }}</h2>
        <p class="section-sub">{{ t('cases_title') }}</p>
        <div class="grid grid-3">
          <router-link v-for="c in cases" :key="c.id" to="/cases" class="card">
            <img class="card-cover" :src="c.cover" :alt="pick(c, 'caseName', 'caseNameEn')" />
            <div class="card-body">
              <h3 class="card-title">{{ pick(c, 'caseName', 'caseNameEn') }}</h3>
              <p class="card-desc">{{ truncate(pick(c, 'content', 'contentEn')) }}</p>
            </div>
          </router-link>
        </div>
        <div v-if="!cases.length" class="empty">{{ t('empty') }}</div>
      </div>
    </section>

    <!-- 养护知识 -->
    <section class="section">
      <div class="container">
        <h2 class="section-title">{{ t('home_articles') }}</h2>
        <p class="section-sub">{{ t('articles_title') }}</p>
        <div class="grid grid-3">
          <router-link v-for="a in articles" :key="a.id" :to="`/articles/${a.id}`" class="card">
            <img class="card-cover" :src="a.cover" :alt="pick(a, 'title', 'titleEn')" />
            <div class="card-body">
              <h3 class="card-title">{{ pick(a, 'title', 'titleEn') }}</h3>
              <p class="card-desc">{{ truncate(pick(a, 'summary', 'summaryEn')) }}</p>
              <span class="muted" style="font-size:12px">{{ a.publishTime }}</span>
            </div>
          </router-link>
        </div>
        <div v-if="!articles.length" class="empty">{{ t('empty') }}</div>
      </div>
    </section>

    <!-- 门店实力 -->
    <section class="section" style="background:var(--bg)" v-if="store">
      <div class="container">
        <h2 class="section-title">{{ t('home_store') }}</h2>
        <p class="section-sub">{{ pick(store, 'storeName', 'storeNameEn') }}</p>
        <div class="grid grid-3">
          <div class="stat"><strong>{{ store.businessHours || '-' }}</strong><span>{{ t('hours') }}</span></div>
          <div class="stat"><strong>{{ store.phone || '-' }}</strong><span>{{ t('phone') }}</span></div>
          <div class="stat"><strong>{{ pick(store, 'address', 'addressEn') || '-' }}</strong><span>{{ t('address') }}</span></div>
        </div>
        <div class="story-content" v-html="pick(store, 'intro', 'introEn')" style="margin-top:20px"></div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fmtMoney, pick, state, t } from '@/i18n'
import { publicApi } from '@/api'

const router = useRouter()
const site = ref<any>({})
const store = ref<any>({})
const banners = ref<any[]>([])
const hotItems = ref<any[]>([])
const cases = ref<any[]>([])
const articles = ref<any[]>([])
const story = ref<any>(null)
const currentBanner = ref(0)

const miniProgramUrl = computed(() => '/miniprogram')

function truncate(s: string, n = 60) {
  if (!s) return ''
  const text = String(s).replace(/<[^>]+>/g, '')
  return text.length > n ? text.slice(0, n) + '...' : text
}
function go(b: any) {
  if (b.link) window.open(b.link, '_blank')
}

onMounted(async () => {
  try {
    const home: any = await publicApi.home()
    site.value = home.site || {}
    store.value = home.store || {}
    banners.value = home.banners || []
    hotItems.value = home.hotItems || []
    cases.value = home.cases || []
    articles.value = home.articles || []
  } catch {
    // 后端未启动时保持空数据，页面仍可访问
  }
  try {
    const s: any = await publicApi.brandStory()
    story.value = Array.isArray(s) ? s[0] : s
  } catch { story.value = null }
})
</script>

<style scoped>
.banner-track { position: relative; height: 320px; border-radius: var(--radius); overflow: hidden; background: #eef2f7; }
.banner-slide { position: absolute; inset: 0; opacity: 0; transition: opacity .5s; cursor: pointer; }
.banner-slide.active { opacity: 1; }
.banner-slide img { width: 100%; height: 100%; object-fit: cover; }
.banner-caption { position: absolute; left: 24px; bottom: 20px; color: #fff; font-size: 20px; font-weight: 600; text-shadow: 0 2px 8px rgba(0,0,0,.5); }
.dots { display: flex; gap: 8px; justify-content: center; margin-top: 12px; }
.dots span { width: 9px; height: 9px; border-radius: 50%; background: #cbd5e1; cursor: pointer; }
.dots span.active { background: var(--brand); }
.story { display: grid; grid-template-columns: 1fr 1.2fr; gap: 24px; align-items: start; }
.story-cover { border-radius: var(--radius); height: 320px; object-fit: cover; width: 100%; }
.story-content { color: #333; line-height: 1.9; }
.stat { background: #fff; border: 1px solid var(--line); border-radius: var(--radius); padding: 22px; text-align: center; }
.stat strong { display: block; font-size: 20px; margin-bottom: 6px; }
.stat span { color: var(--muted); font-size: 13px; }
@media (max-width: 960px) { .story { grid-template-columns: 1fr; } }
</style>
