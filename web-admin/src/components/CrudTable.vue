<template>
  <div class="page-container">
    <!-- 筛选区（可折叠） -->
    <div class="search-area" v-if="searchFields.length">
      <el-form :inline="true" :model="query">
        <el-form-item v-for="f in visibleSearch" :key="f.prop" :label="fieldLabel(f)">
          <el-date-picker
            v-if="f.type === 'date'"
            v-model="query[f.prop]"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="选择日期"
            clearable
            style="width: 180px"
          />
          <el-date-picker
            v-else-if="f.type === 'daterange'"
            v-model="query[f.prop]"
            type="daterange"
            value-format="yyyy-MM-dd"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 260px"
          />
          <el-select
            v-else-if="f.options || f.source || f.lookup || dictOf(f)"
            v-model="query[f.prop]"
            :placeholder="'请选择' + f.label"
            clearable
            style="width: 180px"
          >
            <el-option v-for="o in optionsOf(f)" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
          <el-input v-else v-model="query[f.prop]" :placeholder="'请输入' + f.label" clearable style="width: 180px" @keyup.enter="load" />
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
            <el-tag v-if="c.tag" :type="tagClassOf(c, row[c.prop])" :effect="c.effect || 'light'">{{ displayValue(row, c) }}</el-tag>
            <el-image v-else-if="c.image" :src="row[c.prop]" style="width: 42px; height: 42px; border-radius: 4px" fit="cover" :preview-src-list="row[c.prop] ? [row[c.prop]] : []" />
            <span v-else>{{ displayValue(row, c) }}</span>
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
      <el-form :model="form" label-width="140px">
        <el-row :gutter="16">
          <el-col v-for="f in formFields" :key="f.prop" :span="f.span || 12">
            <el-form-item :label="fieldLabel(f)">
              <el-input v-if="!f.type || f.type === 'input'" v-model="form[f.prop]" :placeholder="'请输入' + f.label" />
              <el-input v-else-if="f.type === 'textarea'" type="textarea" :rows="3" v-model="form[f.prop]" />
              <el-input-number v-else-if="f.type === 'number'" v-model="form[f.prop]" :min="0" style="width: 100%" />
              <el-date-picker
                v-else-if="f.type === 'date'"
                v-model="form[f.prop]"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择日期"
                style="width: 100%"
              />
              <el-date-picker
                v-else-if="f.type === 'datetime'"
                v-model="form[f.prop]"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择日期时间"
                style="width: 100%"
              />
              <el-select v-else-if="f.type === 'select'" v-model="form[f.prop]" :placeholder="'请选择' + f.label" clearable :multiple="!!f.multiple" style="width: 100%">
                <el-option v-for="o in optionsOf(f)" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
              <el-switch
                v-else-if="f.type === 'switch'"
                v-model="form[f.prop]"
                :active-value="f.activeValue ?? 1"
                :inactive-value="f.inactiveValue ?? 0"
                :active-text="switchText(f, f.activeValue ?? 1)"
                :inactive-text="switchText(f, f.inactiveValue ?? 0)"
                :inline-prompt="!!dictOf(f)"
              />
              <image-upload v-else-if="f.type === 'image'" v-model="form[f.prop]" :dir="f.dir || 'images'" :multiple="!!f.multiple" />
              <plate-input v-else-if="f.type === 'plate'" v-model="form[f.prop]" :required="!!f.required" />
              <rich-editor v-else-if="f.type === 'richtext'" v-model="form[f.prop]" />
              <div v-if="f.tip" class="field-tip">{{ f.tip }}</div>
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
import { hasPerm } from '@/utils/perm'
import ImageUpload from './ImageUpload.vue'
import RichEditor from './RichEditor.vue'
import PlateInput from './PlateInput.vue'

// 注意：Vue 会把「未显式传入」的 Boolean 类型 prop 强制转换为 false，
// 因此这里必须用 withDefaults 指定默认为 true，否则任何没写 :can-add="true"
// 的页面都会丢失「新增 / 编辑 / 删除」按钮。
const props = withDefaults(
  defineProps<{
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
    /** 权限前缀，默认由 url 推导（如 /admin/store-info → store-info）；传空串表示不做权限控制 */
    perm?: string
  }>(),
  {
    canAdd: true,
    canEdit: true,
    canDelete: true
  }
)

const list = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const selection = ref<any[]>([])
const expand = ref(false)

const query = reactive<any>({ page: 1, size: 10 })
const form = ref<any>({})

const canAdd = computed(() => props.canAdd !== false && hasPerm(permOf('add')))
const canEdit = computed(() => props.canEdit !== false && hasPerm(permOf('edit')))
const canDelete = computed(() => props.canDelete !== false && hasPerm(permOf('delete')))
const operationWidth = computed(() => (props as any).operationWidth || 170)

/** 权限前缀：优先取 perm 属性，其次由 url 推导（/admin/store-info → store-info） */
const permPrefix = computed<string | undefined>(() => {
  if (props.perm === '') return undefined
  if (props.perm) return props.perm
  return props.url.replace(/^\/admin\//, '').replace(/\//g, '-')
})
function permOf(action: string) {
  return permPrefix.value ? `${permPrefix.value}:${action}` : undefined
}
const searchFields = computed(() => props.searchFields || [])
const formFields = computed(() => props.formFields || [])
const visibleSearch = computed(() => (expand.value ? searchFields.value : searchFields.value.slice(0, 4)))

/* ==================== 下拉数据源（字典 / 接口）==================== */
// 统一缓存：字典按 dictType，接口按 url+params
const sourceCache = reactive<Record<string, any[]>>({})

function dictKey(type: string) {
  return 'dict:' + type
}
function srcKey(s: any) {
  return 'src:' + JSON.stringify({ u: s.url, p: s.params || {} })
}

/** 字段可选值：options 静态 / dict 字典 / source 或 lookup 接口 */
function optionsOf(f: any): any[] {
  if (!f) return []
  if (f.options) return f.options
  const dt = dictOf(f)
  if (dt) return sourceCache[dictKey(dt)] || []
  const src = f.source || f.lookup
  if (src) return sourceCache[srcKey(src)] || []
  return []
}

/**
 * 字段字典类型：显式 f.dict 优先；裸 status 字段兜底走通用启停字典，
 * 保证「全系统无裸 0/1」——即使页面漏配 dict，也不会显示 0/1。
 */
function dictOf(f: any): string | undefined {
  if (!f) return undefined
  if (f.dict) return f.dict
  if ((f.prop === 'status' || f.prop === 'enabled') && !f.options && !f.source && !f.lookup) return 'enable_status'
  return undefined
}

/** 匹配字段当前值对应的选项（含 cssClass 标签颜色） */
function optionOf(f: any, v: any): any {
  if (!f) return undefined
  return optionsOf(f).find((o: any) => String(o.value) === String(v))
}

/** 标签颜色：优先取字典 css_class，其次回退内置状态色 */
function tagClassOf(c: any, v: any) {
  const hit = optionOf(c, v)
  if (hit && hit.cssClass) return hit.cssClass
  return tagType(v)
}

/** 开关文案：来自字典业务名称 */
function switchText(f: any, v: any) {
  const hit = optionOf(f, v)
  return hit ? hit.label : ''
}

/** 加载数据字典（实时生效：字典维护后调用 reloadOptions 即刷新） */
async function ensureDict(type: string) {
  if (sourceCache[dictKey(type)]) return
  const rows: any = await crud.list('/admin/sys-dict-data', { dictType: type, size: 500 })
  const arr: any[] = Array.isArray(rows) ? rows : rows?.records || []
  arr.sort((a, b) => (a.dictSort ?? 0) - (b.dictSort ?? 0))
  sourceCache[dictKey(type)] = arr
    .filter((r) => r.status === undefined || r.status === 1)
    .map((r) => ({ label: r.dictLabel, value: r.dictValue, cssClass: r.cssClass }))
}

/** 加载业务下拉（门店 / 技师 / 会员 等），把 ID 换成名称 */
async function ensureSource(s: any) {
  const key = srcKey(s)
  if (sourceCache[key]) return
  const rows: any = await crud.list(s.url, { ...(s.params || {}), size: 500 })
  const arr: any[] = Array.isArray(rows) ? rows : rows?.records || []
  const label = s.label || 'name'
  const value = s.value || 'id'
  sourceCache[key] = arr.map((r) => ({ label: r[label], value: r[value] }))
}

async function preloadOptions() {
  const fields: any[] = [...(props.columns || []), ...(props.formFields || []), ...(searchFields.value || [])]
  const dicts = new Set<string>()
  const srcs: any[] = []
  fields.forEach((f: any) => {
    const dt = dictOf(f)
    if (dt) dicts.add(dt)
    else if ((f.source || f.lookup) && !f.options) srcs.push(f.source || f.lookup)
  })
  await Promise.allSettled([
    ...[...dicts].map((t) => ensureDict(t)),
    ...srcs.map((s) => ensureSource(s))
  ])
}

/** 清空并重新加载所有下拉（字典维护后调用，保证实时生效） */
async function reloadOptions() {
  Object.keys(sourceCache).forEach((k) => delete sourceCache[k])
  await preloadOptions()
}

/* ==================== 展示 / Label ==================== */
function tagType(value: any) {
  const map: any = { 待接车: 'info', 待派工: 'warning', 施工中: 'primary', 待质检: 'warning', 待结算: 'danger', 已离店: 'success', 已评价: 'success', 已取消: 'info', 待确认: '', 已确认: 'success' }
  return map[value] || ''
}
function format(value: any, col: any) {
  if (value === null || value === undefined || value === '') return '-'
  if (col.money) return '¥' + Number(value).toFixed(2)
  return value
}
/** 列展示：lookup/dict 优先把 ID / 编码换成名称 */
function displayValue(row: any, c: any) {
  const v = row[c.prop]
  if (c.lookup || dictOf(c)) {
    const hit = optionOf(c, v)
    if (hit) return hit.label
  }
  return format(v, c)
}
/** Label 规范：日期类字段统一为「名称(yyyy-MM-dd)」 */
function fieldLabel(f: any) {
  const label = f.label || ''
  if (f.type === 'date' && !label.includes('(')) return label
  if (f.type === 'datetime' && !label.includes('(')) return label 
  return label
}
/** 删除确认文案中的日期统一 (yyyy-MM-dd) */
function fmtDate(v: any) {
  if (!v) return ''
  return '(' + String(v).slice(0, 10) + ')'
}

/* ==================== CRUD ==================== */
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
  preloadOptions()
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
  const dateCol = (props.columns || []).find((c: any) => c.type === 'date' || /date|日期|时间/.test(c.label || ''))
  const extra = dateCol ? ' ' + fmtDate(row[dateCol.prop]) : ''
  await ElMessageBox.confirm(`确认删除该记录？${extra}`, '提示', { type: 'warning' })
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

defineExpose({ load, list, reloadOptions })
onMounted(() => {
  preloadOptions()
  load()
})
</script>
