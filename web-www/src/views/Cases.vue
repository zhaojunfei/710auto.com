<template>
  <div class="container">
    <div class="breadcrumb">{{ t('nav_home') }} / {{ t('cases_title') }}</div>
    <div class="section">
      <h2 class="section-title">{{ t('cases_title') }}</h2>
      <p class="section-sub">{{ t('home_cases') }}</p>

      <div class="filters">
        <button :class="{ active: caseType === '' }" @click="selectType('')">{{ t('all') }}</button>
        <button v-for="c in caseTypes" :key="c" :class="{ active: caseType === c }" @click="selectType(c)">{{ c }}</button>
      </div>

      <div class="grid grid-3">
        <div v-for="c in cases" :key="c.id" class="card">
          <img class="card-cover" :src="c.cover" :alt="pick(c, 'caseName', 'caseNameEn')" />
          <div class="card-body">
            <h3 class="card-title">{{ pick(c, 'caseName', 'caseNameEn') }}</h3>
            <p class="card-desc">{{ truncate(pick(c, 'content', 'contentEn')) }}</p>
            <div class="compare">
              <div>
                <span class="muted" style="font-size:12px">施工前</span>
                <img v-for="(b, i) in parseImages(c.beforeImages)" :key="'b' + i" :src="b" />
              </div>
              <div>
                <span class="muted" style="font-size:12px">施工后</span>
                <img v-for="(a, i) in parseImages(c.afterImages)" :key="'a' + i" :src="a" />
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!cases.length" class="empty">{{ t('empty') }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { pick, t } from '@/i18n'
import { publicApi } from '@/api'

const cases = ref<any[]>([])
const caseType = ref('')
const caseTypes = ['保养', '机修', '轮胎', '美容']

function truncate(s: string, n = 70) {
  if (!s) return ''
  const text = String(s).replace(/<[^>]+>/g, '')
  return text.length > n ? text.slice(0, n) + '...' : text
}
function parseImages(raw: any): string[] {
  if (!raw) return []
  try {
    const arr = Array.isArray(raw) ? raw : JSON.parse(raw)
    return Array.isArray(arr) ? arr : []
  } catch { return [] }
}
function selectType(type: string) {
  caseType.value = type
  load()
}
async function load() {
  try {
    cases.value = (await publicApi.cases(caseType.value ? { caseType: caseType.value } : undefined)) || []
  } catch { cases.value = [] }
}
onMounted(load)
</script>

<style scoped>
.filters { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 24px; }
.filters button { border: 1px solid var(--line); background: #fff; border-radius: 20px; padding: 6px 18px; cursor: pointer; font-size: 14px; color: var(--muted); }
.filters button.active { background: var(--brand); border-color: var(--brand); color: #fff; }
.compare { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-top: 10px; }
.compare img { width: 100%; height: 80px; object-fit: cover; border-radius: 6px; margin-top: 4px; }
</style>
