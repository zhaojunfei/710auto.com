<template>
  <div class="page-container">
    <!-- 筛选区（可折叠） -->
    <div class="search-area" v-if="searchFields.length">
      <el-form :inline="true" :model="query">
        <el-form-item v-for="f in visibleSearch" :key="f.prop" :label="f.label">
          <el-input v-if="!f.options" v-model="query[f.prop]" :placeholder="'请输入' + f.label" clearable style="width: 180px" @keyup.enter="load" />
          <el-select v-else v-model="query[f.prop]" :placeholder="'请选择' + f.label" clearable style="width: 180px">
            <el-option v-for="o in f.options" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <el-button link type="primary" @click="expand = !expand">{{ expand ? '收起' : '展开' }}</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="card-block">
      <div class="toolbar">
        <el-button v-if="canAdd" type="primary" @click="openForm()">
          <el-icon><Plus /></el-icon>&nbsp;新增
        </el-button>
        <el-button v-if="canDelete" type="danger" plain :disabled="!selection.length" @click="batchDelete">批量删除</el-button>
        <slot name="actions" />
        <div style="flex: 1"></div>
        <el-button @click="load"><el-icon><Refresh /></el-icon>&nbsp;刷新</el-button>
      </div>

      <el-table :data="list" v-loading="loading" stripe border @selection-change="onSelect" height="auto">
        <el-table-column type="selection" width="46" v-if="canDelete" />
        <el-table-column v-for="c in columns" :key="c.prop" :prop="c.prop" :label="c.label" :width="c.width" :min-width="c.minWidth || 120" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tag v-if="c.tag" :type="tagType(row[c.prop])">{{ row[c.prop] }}</el-tag>
            <el-image v-else-if="c.image" :src="row[c.prop]" style="width: 42px; height: 42px; border-radius: 4px" fit="cover" :preview-src-list="row[c.prop] ? [row[c.prop]] : []" />
            <span v-else>{{ format(row[c.prop], c) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" :width="operationWidth">
          <template #default="{ row }">
            <slot name="row-actions" :row="row" />
            <el-button link type="primary" @click="openForm(row)" v-if="canEdit">编辑</el-button>
            <el-button link type="danger" @click="remove(row)" v-if="canDelete">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-tip">
            <el-empty :description="emptyText">
              <el-button v-if="canAdd" type="primary" @click="openForm()">立即新增{{ title }}</el-button>
            </el-empty>
          </div>
        </template>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="load"
          @current-change="load"
        />
      </div>
    </div>

    <!-- 表单弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑' + title : '新增' + title" width="760px" destroy-on-close>
      <el-form :model="form" label-width="120px">
        <el-row :gutter="16">
          <el-col v-for="f in formFields" :key="f.prop" :span="f.span || 12">
            <el-form-item :label="f.label">
              <el-input v-if="!f.type || f.type === 'input'" v-model="form[f.prop]" :placeholder="'请输入' + f.label" />
              <el-input v-else-if="f.type === 'textarea'" type="textarea" :rows="3" v-model="form[f.prop]" />
              <el-input-number v-else-if="f.type === 'number'" v-model="form[f.prop]" :min="0" style="width: 100%" />
              <el-select v-else-if="f.type === 'select'" v-model="form[f.prop]" style="width: 100%">
                <el-option v-for="o in f.options" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
              <el-switch v-else-if="f.type === 'switch'" v-model="form[f.prop]" :active-value="1" :inactive-value="0" />
              <image-upload v-else-if="f.type === 'image'" v-model="form[f.prop]" :dir="f.dir || 'images'" />
              <rich-editor v-else-if="f.type === 'richtext'" v-model="form[f.prop]" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { crud } from '@/api'
import ImageUpload from './ImageUpload.vue'
import RichEditor from './RichEditor.vue'

const props = defineProps<{
  url: string
  title?: string
  columns: any[]
  formFields?: any[]
  searchFields?: any[]
  canAdd?: boolean
  canEdit?: boolean
  canDelete?: boolean
  emptyText?: string
  defaultForm?: any
}>()

const list = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const selection = ref<any[]>([])
const expand = ref(false)

const query = reactive<any>({ page: 1, size: 10 })
const form = ref<any>({})

const canAdd = computed(() => props.canAdd !== false)
const canEdit = computed(() => props.canEdit !== false)
const canDelete = computed(() => props.canDelete !== false)
const operationWidth = computed(() => (props as any).operationWidth || 170)
const searchFields = computed(() => props.searchFields || [])
const formFields = computed(() => props.formFields || [])
const visibleSearch = computed(() => (expand.value ? searchFields.value : searchFields.value.slice(0, 4)))

function tagType(value: any) {
  const map: any = { 待接车: 'info', 待派工: 'warning', 施工中: 'primary', 待质检: 'warning', 待结算: 'danger', 已离店: 'success', 已评价: 'success', 已取消: 'info', 待确认: '', 已确认: 'success' }
  return map[value] || ''
}
function format(value: any, col: any) {
  if (value === null || value === undefined) return '-'
  if (col.money) return '¥' + Number(value).toFixed(2)
  return value
}

async function load() {
  loading.value = true
  try {
    const params: any = { ...query }
    Object.keys(params).forEach((k) => { if (params[k] === '' || params[k] === null) delete params[k] })
    const data: any = await crud.page(props.url, params)
    list.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}
function reset() {
  Object.keys(query).forEach((k) => { if (k !== 'page' && k !== 'size') query[k] = '' })
  load()
}
function onSelect(rows: any[]) { selection.value = rows }
function openForm(row?: any) {
  form.value = row ? { ...row } : { ...(props.defaultForm || {}) }
  dialogVisible.value = true
}
async function submit() {
  saving.value = true
  try {
    if (form.value.id) await crud.update(props.url, form.value)
    else await crud.create(props.url, form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}
async function remove(row: any) {
  await ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' })
  await crud.remove(props.url, row.id)
  ElMessage.success('删除成功')
  load()
}
async function batchDelete() {
  await ElMessageBox.confirm(`确认删除选中的 ${selection.value.length} 条记录？`, '提示', { type: 'warning' })
  await crud.batchRemove(props.url, selection.value.map((r) => r.id))
  ElMessage.success('删除成功')
  load()
}

defineExpose({ load, list })
onMounted(load)
</script>
