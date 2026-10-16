<template>
  <div class="container">
    <div class="breadcrumb">{{ t('nav_home') }} / {{ t('contact_title') }}</div>
    <div class="section">
      <h2 class="section-title">{{ t('contact_title') }}</h2>
      <p class="section-sub">{{ t('slogan') }}</p>

      <div class="contact-grid">
        <div class="card" style="padding:24px">
          <h3 style="margin-top:0">{{ t('contact_info') }}</h3>
          <p><b>{{ t('address') }}：</b>{{ pick(store, 'address', 'addressEn') || '-' }}</p>
          <p><b>{{ t('phone') }}：</b>{{ store.phone || site.servicePhone || '-' }}</p>
          <p><b>{{ t('hours') }}：</b>{{ store.businessHours || site.businessHours || '-' }}</p>
          <div class="mini-entry">
            <div class="qr">小程序码</div>
            <div>
              <p style="margin:0 0 6px"><b>{{ t('book_mini') }}</b></p>
              <p class="muted" style="margin:0;font-size:13px">扫码进入小程序，随时预约、查看工单、管理存胎。</p>
            </div>
          </div>
        </div>

        <div class="card" style="padding:24px">
          <h3 style="margin-top:0">{{ t('contact_title') }}</h3>
          <form @submit.prevent="submit">
            <label>{{ t('contact_name') }}</label>
            <input v-model="form.name" required />
            <label>{{ t('contact_phone') }}</label>
            <input v-model="form.phone" required />
            <label>{{ t('contact_content') }}</label>
            <textarea v-model="form.content" rows="5"></textarea>
            <button class="btn" type="submit" :disabled="sending" style="margin-top:14px">
              {{ sending ? '...' : t('contact_submit') }}
            </button>
          </form>
          <p v-if="success" style="color:#16a34a;margin-top:12px">{{ t('contact_success') }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { pick, t } from '@/i18n'
import { publicApi } from '@/api'

const store = ref<any>({})
const site = ref<any>({})
const sending = ref(false)
const success = ref(false)
const form = reactive({ name: '', phone: '', content: '', source: 'www' })

async function submit() {
  sending.value = true
  try {
    await publicApi.message({ ...form, contactName: form.name, contactPhone: form.phone, content: form.content })
    success.value = true
    form.name = ''
    form.phone = ''
    form.content = ''
  } finally {
    sending.value = false
  }
}

onMounted(async () => {
  try { store.value = (await publicApi.store()) || {} } catch { store.value = {} }
  try { site.value = (await publicApi.site()) || {} } catch { site.value = {} }
})
</script>

<style scoped>
.contact-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
form { display: flex; flex-direction: column; }
label { font-size: 13px; color: var(--muted); margin: 10px 0 4px; }
input, textarea { border: 1px solid var(--line); border-radius: 8px; padding: 10px 12px; font-size: 14px; font-family: inherit; }
input:focus, textarea:focus { outline: none; border-color: var(--brand); }
.mini-entry { display: flex; gap: 16px; align-items: center; margin-top: 20px; }
.qr { width: 88px; height: 88px; border: 1px dashed var(--line); border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 12px; color: var(--muted); }
@media (max-width: 960px) { .contact-grid { grid-template-columns: 1fr; } }
</style>
