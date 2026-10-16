<template>
  <div class="container">
    <div class="breadcrumb">{{ t('nav_home') }} / {{ t('about_title') }}</div>
    <div class="section">
      <h2 class="section-title">{{ t('about_title') }}</h2>
      <p class="section-sub">{{ pick(store, 'storeName', 'storeNameEn') }}</p>

      <div class="about-grid" v-if="store">
        <img :src="store.logo" class="logo-img" v-if="store.logo" />
        <div class="about-info">
          <p><b>{{ t('address') }}：</b>{{ pick(store, 'address', 'addressEn') || '-' }}</p>
          <p><b>{{ t('phone') }}：</b>{{ store.phone || '-' }}</p>
          <p><b>{{ t('hours') }}：</b>{{ store.businessHours || '-' }}</p>
        </div>
      </div>
      <div class="rich" v-html="pick(store, 'intro', 'introEn')"></div>

      <h2 class="section-title" style="font-size:22px;margin-top:40px">{{ t('home_story') }}</h2>
      <div v-for="s in stories" :key="s.id" class="story-item">
        <h3>{{ pick(s, 'title', 'titleEn') }}</h3>
        <img v-if="s.cover" :src="s.cover" class="story-cover" />
        <div class="rich" v-html="pick(s, 'content', 'contentEn')"></div>
      </div>
      <div v-if="!stories.length" class="empty">{{ t('empty') }}</div>

      <h2 class="section-title" style="font-size:22px;margin-top:40px">{{ t('home_store') }}</h2>
      <div class="certs">
        <img v-if="store.businessLicense" :src="store.businessLicense" alt="营业执照" />
        <img v-if="store.repairCert" :src="store.repairCert" alt="维修资质" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { pick, t } from '@/i18n'
import { publicApi } from '@/api'

const store = ref<any>({})
const stories = ref<any[]>([])

onMounted(async () => {
  try { store.value = (await publicApi.store()) || {} } catch { store.value = {} }
  try {
    const s: any = await publicApi.brandStory()
    stories.value = Array.isArray(s) ? s : s ? [s] : []
  } catch { stories.value = [] }
})
</script>

<style scoped>
.about-grid { display: flex; gap: 28px; align-items: center; margin-bottom: 24px; flex-wrap: wrap; }
.logo-img { width: 120px; height: 120px; object-fit: contain; border: 1px solid var(--line); border-radius: var(--radius); }
.about-info p { margin: 6px 0; }
.rich { line-height: 1.9; }
.rich :deep(img) { border-radius: 8px; margin: 12px 0; }
.story-item { margin-bottom: 32px; }
.story-item h3 { margin: 0 0 12px; }
.story-cover { width: 100%; max-height: 420px; object-fit: cover; border-radius: var(--radius); margin-bottom: 16px; }
.certs { display: flex; gap: 16px; flex-wrap: wrap; }
.certs img { width: 240px; border: 1px solid var(--line); border-radius: 8px; }
</style>
