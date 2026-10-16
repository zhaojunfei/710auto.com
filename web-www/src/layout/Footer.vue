<template>
  <footer class="site-footer">
    <div class="container footer-grid">
      <div>
        <h4>{{ t('brand') }}</h4>
        <p class="muted">{{ t('slogan') }}</p>
        <p class="muted">{{ site.icp || '' }}</p>
      </div>
      <div>
        <h4>{{ t('contact_info') }}</h4>
        <p class="muted">{{ t('address') }}：{{ pick(store, 'address', 'addressEn') || '-' }}</p>
        <p class="muted">{{ t('phone') }}：{{ store.phone || site.servicePhone || '-' }}</p>
        <p class="muted">{{ t('hours') }}：{{ store.businessHours || site.businessHours || '-' }}</p>
      </div>
      <div>
        <h4>{{ t('nav_contact') }}</h4>
        <p><router-link to="/services">{{ t('nav_services') }}</router-link></p>
        <p><router-link to="/cases">{{ t('nav_cases') }}</router-link></p>
        <p><router-link to="/articles">{{ t('nav_articles') }}</router-link></p>
      </div>
      <div>
        <h4>{{ t('book_mini') }}</h4>
        <div class="qr">小程序码</div>
        <div class="lang-switch">
          <button v-for="l in languages" :key="l.langCode" :class="{ active: state.locale === l.langCode }" @click="switchLang(l.langCode)">
            {{ l.flagIcon || '' }} {{ l.langName }}
          </button>
        </div>
      </div>
    </div>
    <div class="copyright container">
      © {{ year }} {{ site.siteName || t('brand') }} · {{ t('footer_rights') }} · {{ site.icp || '' }}
    </div>
  </footer>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { pick, state, t, loadLanguages, type LangCode } from '@/i18n'
import { publicApi } from '@/api'

const year = new Date().getFullYear()
const site = ref<any>({})
const store = ref<any>({})
const languages = ref<any[]>([
  { langCode: 'zh-CN', langName: '简体中文', flagIcon: '🇨🇳' },
  { langCode: 'en-US', langName: 'English', flagIcon: '🇺🇸' }
])

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
  try { site.value = (await publicApi.site()) || {} } catch { site.value = {} }
  try { store.value = (await publicApi.store()) || {} } catch { store.value = {} }
})
</script>

<style scoped>
.site-footer { background: #0f172a; color: #cbd5e1; margin-top: 64px; padding-top: 44px; }
.footer-grid { display: grid; grid-template-columns: 1.4fr 1.4fr 1fr 1fr; gap: 28px; padding-bottom: 28px; }
.site-footer h4 { color: #fff; margin: 0 0 12px; }
.site-footer .muted { color: #94a3b8; font-size: 14px; margin: 4px 0; }
.site-footer a { color: #cbd5e1; font-size: 14px; }
.site-footer a:hover { color: #fff; }
.qr { width: 92px; height: 92px; border: 1px dashed #475569; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 12px; color: #64748b; margin-bottom: 12px; }
.lang-switch { display: flex; gap: 8px; flex-wrap: wrap; }
.lang-switch button { border: 1px solid #334155; background: transparent; color: #cbd5e1; border-radius: 6px; padding: 4px 10px; font-size: 12px; cursor: pointer; }
.lang-switch button.active { background: var(--brand); border-color: var(--brand); color: #fff; }
.copyright { border-top: 1px solid #1e293b; padding: 16px 20px; font-size: 13px; color: #64748b; }
@media (max-width: 960px) { .footer-grid { grid-template-columns: 1fr 1fr; } }
@media (max-width: 640px) { .footer-grid { grid-template-columns: 1fr; } }
</style>
