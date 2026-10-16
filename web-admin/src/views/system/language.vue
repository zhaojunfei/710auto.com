<template>
  <div class="page-container">
    <div class="card-block">
      <div class="toolbar">
        <el-button type="primary" @click="openAdd">新增语言</el-button>
      </div>
      <el-table :data="languages" stripe border v-loading="loading">
        <el-table-column prop="langCode" label="语言编码" width="140" />
        <el-table-column prop="langName" label="名称" width="140" />
        <el-table-column prop="langNameEn" label="英文名称" width="140" />
        <el-table-column prop="flagIcon" label="图标" width="80" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="默认" width="90">
          <template #default="{ row }"><el-tag v-if="row.isDefault === 1" type="success">默认</el-tag></template>
        </el-table-column>
        <el-table-column label="启用" width="90">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="(v:boolean) => toggle(row, v)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="setDefault(row)">设为默认</el-button>
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card-block">
      <h3>内容多语言字段</h3>
      <p class="text-muted">按业务类型 + 业务ID + 语言，录入对应语言的字段内容（供服务项目 / 文章 / 菜单 / Banner 使用）。</p>
      <el-form :inline="true">
        <el-form-item label="业务类型">
          <el-select v-model="i18n.bizType" style="width:160px">
            <el-option v-for="t in ['service', 'article', 'menu', 'banner', 'brand']" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务ID"><el-input-number v-model="i18n.bizId" :min="1" /></el-form-item>
        <el-form-item label="语言">
          <el-select v-model="i18n.langCode" style="width:140px">
            <el-option v-for="l in languages" :key="l.langCode" :label="l.langName" :value="l.langCode" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadFields">加载</el-button></el-form-item>
      </el-form>
      <el-table :data="fieldRows" stripe border>
        <el-table-column prop="key" label="字段名" width="200" />
        <el-table-column label="内容">
          <template #default="{ row }"><el-input v-model="row.value" /></template>
        </el-table-column>
      </el-table>
      <div class="toolbar" style="margin-top:8px">
        <el-button @click="fieldRows.push({ key: '', value: '' })">新增字段</el-button>
        <el-button type="primary" @click="saveFields">保存字段</el-button>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editRow ? '编辑语言' : '新增语言'" width="480px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="语言编码"><el-input v-model="form.langCode" placeholder="如 ja-JP" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.langName" /></el-form-item>
        <el-form-item label="英文名称"><el-input v-model="form.langNameEn" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.flagIcon" placeholder="emoji 或图片地址" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import { languageApi } from '@/api'

const languages = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editRow = ref<any>(null)
const form = reactive<any>({ sort: 1 })
const i18n = reactive<any>({ bizType: 'service', bizId: 1, langCode: 'en-US' })
const fieldRows = ref<{ key: string; value: string }[]>([])

async function load() {
  loading.value = true
  try { languages.value = await languageApi.all() } finally { loading.value = false }
}
async function toggle(row: any, v: boolean) {
  await languageApi.status(row.id, v ? 1 : 0)
  ElMessage.success('已更新')
  load()
}
async function setDefault(row: any) {
  await languageApi.setDefault(row.id)
  ElMessage.success('已设为默认语言')
  load()
}
function openAdd() { editRow.value = null; Object.assign(form, { langCode: '', langName: '', langNameEn: '', flagIcon: '', sort: languages.value.length + 1 }); dialogVisible.value = true }
function openEdit(row: any) { editRow.value = row; Object.assign(form, row); dialogVisible.value = true }
async function submit() {
  await request.post('/admin/sys-language', form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}
async function loadFields() {
  const map: any = await languageApi.fields({ ...i18n })
  fieldRows.value = Object.keys(map).map((k) => ({ key: k, value: map[k] }))
  if (!fieldRows.value.length) fieldRows.value = [{ key: 'name', value: '' }, { name: '', value: '' } as any]
}
async function saveFields() {
  const data: any = {}
  fieldRows.value.forEach((r) => { if (r.key) data[r.key] = r.value })
  await languageApi.saveFields({ ...i18n }, data)
  ElMessage.success('字段已保存')
}

onMounted(load)
</script>
