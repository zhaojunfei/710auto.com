<template>
  <div class="page-container">
    <div class="card-block">
      <div class="toolbar">
        <el-button type="primary" v-if="canManage" @click="openForm(null, null)">
          <el-icon><Plus /></el-icon>&nbsp;新增一级菜单
        </el-button>
        <div style="flex: 1"></div>
        <el-input v-model="keyword" placeholder="搜索菜单名称 / 权限标识" clearable style="width: 220px" />
        <el-button @click="load"><el-icon><Refresh /></el-icon>&nbsp;刷新</el-button>
      </div>
      <p class="text-muted">
        维护后台侧边栏菜单与权限点，三种类型：<b>目录(DIR)</b> / <b>菜单(MENU)</b> / <b>按钮(BUTTON)</b>。
        「权限标识」就是按钮级权限：例如 <code>store-info:add</code>，在「角色管理 → 分配权限」里勾选授权后，
        对应角色才会看到该按钮（超级管理员默认拥有全部权限）。
      </p>

      <el-table
        :data="filteredTree"
        row-key="id"
        default-expand-all
        border
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <el-icon v-if="row.icon && row.menuType !== 'BUTTON'" style="vertical-align: -2px; margin-right: 4px">
              <component :is="row.icon" />
            </el-icon>
            <span>{{ row.menuName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.menuType)" size="small">{{ typeLabel(row.menuType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由地址" min-width="160" show-overflow-tooltip />
        <el-table-column prop="perms" label="权限标识" min-width="170" show-overflow-tooltip />
        <el-table-column prop="icon" label="图标" width="130" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column label="显示" width="70">
          <template #default="{ row }">{{ dictLabel('yes_no', row.visible) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="dictClass('enable_status', row.status)" size="small">{{ dictLabel('enable_status', row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button v-if="canManage && row.menuType !== 'BUTTON'" link type="primary" @click="openForm(null, row)">新增子级</el-button>
            <el-button v-if="canManage" link type="primary" @click="openForm(row, null)">编辑</el-button>
            <el-button v-if="canManage" link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑菜单' : '新增菜单'" width="640px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            node-key="id"
            :props="{ label: 'menuName', children: 'children' }"
            check-strictly
            :render-after-expand="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型">
          <el-radio-group v-model="form.menuType">
            <el-radio-button value="DIR">目录</el-radio-button>
            <el-radio-button value="MENU">菜单</el-radio-button>
            <el-radio-button value="BUTTON">按钮</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称"><el-input v-model="form.menuName" placeholder="如 门店工位 / 新增" /></el-form-item>
        <el-form-item label="路由地址" v-if="form.menuType !== 'BUTTON'">
          <el-input v-model="form.path" placeholder="/system/xxx" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="form.menuType === 'MENU'">
          <el-input v-model="form.component" placeholder="system/xxx" />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="form.perms" placeholder="store-info:add" />
        </el-form-item>
        <el-form-item label="图标" v-if="form.menuType !== 'BUTTON'">
          <el-input v-model="form.icon" placeholder="Element Plus 图标名，如 Setting" />
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="是否显示"><el-switch v-model="form.visible" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import { crud } from '@/api'
import { hasPerm } from '@/utils/perm'

const MANAGE_PERM = 'system:menu:manage'
const canManage = computed(() => hasPerm(MANAGE_PERM))

/* 状态名称与标签颜色统一来自数据字典（#8） */
const dicts = ref<Record<string, any[]>>({})
async function loadDict(type: string) {
  const rows: any = await crud.list('/admin/sys-dict-data', { dictType: type, size: 500 })
  const arr: any[] = Array.isArray(rows) ? rows : rows?.records || []
  dicts.value[type] = arr
    .filter((r) => r.status === undefined || r.status === 1)
    .sort((a, b) => (a.dictSort ?? 0) - (b.dictSort ?? 0))
}
function dictLabel(type: string, v: any) {
  const hit = (dicts.value[type] || []).find((d) => String(d.dictValue) === String(v))
  return hit ? hit.dictLabel : v
}
function dictClass(type: string, v: any) {
  const hit = (dicts.value[type] || []).find((d) => String(d.dictValue) === String(v))
  return (hit && hit.cssClass) || 'info'
}

const tree = ref<any[]>([])
const keyword = ref('')
const dialogVisible = ref(false)
const saving = ref(false)
const form = ref<any>({})

function typeLabel(t: string) {
  return ({ DIR: '目录', MENU: '菜单', BUTTON: '按钮' } as any)[t] || t
}
function typeTag(t: string) {
  return ({ DIR: 'warning', MENU: 'primary', BUTTON: 'success' } as any)[t] || 'info'
}

async function load() {
  tree.value = (await request.get('/admin/menu/tree')) as any
}

const filteredTree = computed(() => {
  if (!keyword.value) return tree.value
  const kw = keyword.value.toLowerCase()
  const filter = (nodes: any[]): any[] =>
    nodes
      .map((n) => ({ ...n, children: n.children ? filter(n.children) : [] }))
      .filter(
        (n) =>
          (n.menuName || '').toLowerCase().includes(kw) ||
          (n.perms || '').toLowerCase().includes(kw) ||
          (n.children && n.children.length)
      )
  return filter(tree.value)
})

const parentOptions = computed(() => {
  const strip = (nodes: any[]): any[] =>
    nodes.filter((n) => n.menuType !== 'BUTTON').map((n) => ({ ...n, children: n.children ? strip(n.children) : [] }))
  return [{ id: 0, menuName: '顶级菜单', children: strip(tree.value) }]
})

function openForm(row: any, parent: any) {
  if (row) {
    form.value = { ...row }
  } else {
    form.value = {
      parentId: parent ? parent.id : 0,
      menuType: parent && parent.menuType === 'MENU' ? 'BUTTON' : 'MENU',
      path: '',
      component: '',
      perms: '',
      icon: '',
      sort: 0,
      visible: 1,
      status: 1
    }
  }
  dialogVisible.value = true
}

async function submit() {
  if (!form.value.menuName) {
    ElMessage.warning('请填写菜单名称')
    return
  }
  saving.value = true
  try {
    const payload: any = { ...form.value }
    if (payload.menuType === 'BUTTON') {
      payload.path = null
      payload.component = null
      payload.icon = null
    }
    if (payload.id) await request.put('/admin/menu', payload)
    else await request.post('/admin/menu', payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function remove(row: any) {
  await ElMessageBox.confirm(`确认删除「${row.menuName}」及其子菜单（并解除角色授权）？`, '提示', { type: 'warning' })
  await request.delete('/admin/menu/' + row.id)
  ElMessage.success('删除成功')
  await load()
}

onMounted(() => {
  load()
  loadDict('enable_status')
  loadDict('yes_no')
})
</script>
