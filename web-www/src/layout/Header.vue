<template>
  <header class="site-header">
    <div class="container header-inner">
      <router-link to="/" class="logo">
        <span class="logo-mark">710</span>
        <span class="logo-text">
          <strong>{{ t('brand') }}</strong>
          <small>{{ t('slogan') }}</small>
        </span>
      </router-link>

      <nav class="nav">
        <router-link v-for="m in navMenus" :key="m.key" :to="m.to">{{ m.label }}</router-link>
      </nav>

      <div class="header-right">
        <!-- 语言切换器 -->
        <div class="lang-switch">
          <button v-for="l in languages" :key="l.langCode" :class="{ active: state.locale === l.langCode }" @click="switchLang(l.langCode)">
            {{ l.flagIcon || '' }} {{ l.langCode }}
          </button>
        </div>
        <router-link to="/contact" class="btn btn-sm">{{ t('book_now') }}</router-link>
      </div>

      <button class="menu-toggle" @click="open = !open">☰</button>
    </div>

    <div class="mobile-nav" v-if="open">
      <router-link v-for="m in navMenus" :key="m.key" :to="m.to" @click="open = false">{{ m.label }}</router-link>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { state, t, loadLanguages, type LangCode } from '@/i18n'
import { publicApi } from '@/api'

const languages = ref<any[]>([
  { langCode: 'zh-CN', langName: '简体中文', flagIcon: '🇨🇳' },
  { langCode: 'en-US', langName: 'English', flagIcon: '🇺🇸' }
])
const backendMenus = ref<any[]>([])
const open = ref(false)

/** 后台未配置菜单时的默认导航 */
const defaultNav = computed(() => [
  { key: 'home', to: '/', label: t('nav_home'), zh: '首页' },
  { key: 'services', to: '/services', label: t('nav_services'), zh: '服务项目' },
  { key: 'about', to: '/about', label: t('nav_about'), zh: '门店介绍' },
  { key: 'cases', to: '/cases', label: t('nav_cases'), zh: '施工案例' },
  { key: 'articles', to: '/articles', label: t('nav_articles'), zh: '养护知识' },
  { key: 'contact', to: '/contact', label: t('nav_contact'), zh: '联系我们' }
])

/** 顶部导航优先从后台「PC 官网导航」菜单读取 */
const navMenus = computed(() => {
  const tops = backendMenus.value.filter((m) => !m.parentId || m.parentId === 0)
  if (!tops.length) return defaultNav.value
  return tops.map((m, i) => ({
    key: 'm' + (m.id || i),
    to: '/' + (m.url || '').replace(/^\//, ''),
    label: state.locale === 'en-US' ? m.menuNameEn || m.menuName : m.menuName
  }))
})

function switchLang(code: LangCode) {
  state.locale = code
  localStorage.setItem('auto710_www_lang', code)
  document.cookie = `auto710_lang=${code};path=/;max-age=31536000`
  document.documentElement.setAttribute('lang', code)
  window.location.reload()
}

onMounted(async () => {
  const langs = await loadLanguages()
  if (Array.isArray(langs) && langs.length) languages.value = langs
  try {
    const menus: any = await publicApi.menu('public')
    backendMenus.value = menus || []
  } catch {
    backendMenus.value = []
  }
})
</script>

<style scoped>
.site-header { position: sticky; top: 0; z-index: 50; background: #fff; border-bottom: 1px solid var(--line); }
.header-inner { display: flex; align-items: center; gap: 24px; height: 68px; }
.logo { display: flex; align-items: center; gap: 10px; }
.logo-mark { width: 40px; height: 40px; border-radius: 10px; background: var(--brand); color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 800; }
.logo-text { display: flex; flex-direction: column; line-height: 1.15; }
.logo-text small { color: var(--muted); font-size: 11px; }
.nav { display: flex; gap: 22px; font-size: 15px; }
.nav a { color: var(--ink); padding: 4px 0; border-bottom: 2px solid transparent; }
.nav a.router-link-active { color: var(--brand); border-bottom-color: var(--brand); font-weight: 600; }
.header-right { margin-left: auto; display: flex; align-items: center; gap: 14px; }
.lang-switch { display: flex; border: 1px solid var(--line); border-radius: 8px; overflow: hidden; }
.lang-switch button { border: none; background: #fff; padding: 6px 10px; cursor: pointer; font-size: 13px; color: var(--muted); }
.lang-switch button.active { background: var(--brand); color: #fff; }
.menu-toggle { display: none; border: 1px solid var(--line); background: #fff; border-radius: 8px; padding: 6px 10px; font-size: 18px; cursor: pointer; }
.mobile-nav { display: none; flex-direction: column; padding: 8px 20px 16px; border-top: 1px solid var(--line); }
@media (max-width: 960px) {
  .nav, .header-right { display: none; }
  .menu-toggle { display: block; margin-left: auto; }
  .mobile-nav { display: flex; }
}
</style>
