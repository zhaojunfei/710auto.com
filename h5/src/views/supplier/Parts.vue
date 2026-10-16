<template>
  <div>
    <div class="topbar">
      <span class="back" @click="router.back()">‹</span>
      配件与报价
    </div>
    <div class="page">
      <div class="row" style="gap:8px;margin-bottom:10px">
        <input v-model="keyword" placeholder="搜索配件名称" @keyup.enter="load" />
        <button class="btn sm" @click="load">搜索</button>
      </div>
      <button class="btn" style="margin-bottom:10px" @click="openForm()">＋ 新增配件报价</button>

      <div class="card" v-for="p in parts" :key="p.id">
        <div class="between">
          <b>{{ p.partName }}</b>
          <span class="muted" @click="openForm(p)">编辑</span>
        </div>
        <div class="muted">{{ p.spec || '-' }} · 单位 {{ p.unit || '-' }} · 编码 {{ p.partCode || '-' }}</div>
        <div class="muted" style="margin-top:6px">
          供货价 <span class="price">¥{{ p.purchasePrice || 0 }}</span>
          <span style="margin-left:10px">当前库存 {{ p.stock || 0 }}</span>
        </div>
      </div>
      <div class="empty" v-if="!parts.length">暂无配件，可点击上方新增</div>

      <div class="mask" v-if="formVisible" @click.self="formVisible = false">
        <div class="sheet">
          <div class="card-title">{{ form.id ? '编辑配件报价' : '新增配件报价' }}</div>
          <label>配件名称</label>
          <input v-model="form.partName" placeholder="配件名称" />
          <label>配件编码</label>
          <input v-model="form.partCode" placeholder="选填" />
          <label>规格型号</label>
          <input v-model="form.spec" placeholder="规格型号" />
          <label>品牌</label>
          <input v-model="form.brand" placeholder="品牌" />
          <label>单位</label>
          <input v-model="form.unit" placeholder="件" />
          <label>供货价（成本价）</label>
          <input v-model.number="form.purchasePrice" type="number" min="0" step="0.01" />
          <div class="row" style="gap:10px;margin-top:16px">
            <button class="btn ghost" @click="formVisible = false">取消</button>
            <button class="btn" :disabled="saving" @click="save">{{ saving ? '保存中...' : '保存' }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { supplierApi } from '@/api'

const router = useRouter()
const parts = ref<any[]>([])
const keyword = ref('')
const formVisible = ref(false)
const saving = ref(false)
const form = reactive<any>({ id: undefined, partName: '', partCode: '', spec: '', brand: '', unit: '', purchasePrice: undefined })

async function load() {
  try { parts.value = (await supplierApi.parts(keyword.value || undefined)) as any } catch { parts.value = [] }
}

function openForm(p?: any) {
  Object.assign(form, {
    id: p?.id,
    partName: p?.partName || '',
    partCode: p?.partCode || '',
    spec: p?.spec || '',
    brand: p?.brand || '',
    unit: p?.unit || '',
    purchasePrice: p?.purchasePrice
  })
  formVisible.value = true
}

async function save() {
  if (!form.partName) { alert('请填写配件名称'); return }
  saving.value = true
  try {
    await supplierApi.savePart({ ...form })
    formVisible.value = false
    alert('已保存')
    load()
  } catch (e: any) {
    alert(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.mask { position: fixed; inset: 0; background: rgba(0,0,0,.4); display: flex; align-items: flex-end; z-index: 50; }
.sheet { background: #fff; width: 100%; border-radius: 16px 16px 0 0; padding: 16px; max-height: 88vh; overflow-y: auto; }
</style>
