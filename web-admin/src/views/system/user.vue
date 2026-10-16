<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" class="card-block">
      <!-- 用户管理 -->
      <el-tab-pane label="用户管理" name="user">
        <crud-table
          url="/admin/sys-user"
          title="用户"
          :columns="userColumns"
          :form-fields="userFormFields"
          :search-fields="userSearchFields"
          :default-form="{ status: 1 }"
        >
          <template #row-actions="{ row }">
            <el-button link type="primary" @click="openRoleDialog(row)">分配角色</el-button>
          </template>
        </crud-table>
      </el-tab-pane>

      <!-- 角色管理 -->
      <el-tab-pane label="角色管理" name="role">
        <crud-table
          url="/admin/sys-role"
          title="角色"
          :columns="roleColumns"
          :form-fields="roleFormFields"
          :default-form="{ status: 1, dataScope: 1, sort: 1 }"
        >
          <template #row-actions="{ row }">
            <el-button link type="primary" @click="openPermDialog(row)">分配权限</el-button>
          </template>
        </crud-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 分配角色 -->
    <el-dialog v-model="roleDialogVisible" :title="`分配角色 - ${roleUser?.realName || roleUser?.username || ''}`" width="420px">
      <el-checkbox-group v-model="checkedRoleIds">
        <el-checkbox v-for="r in roles" :key="r.id" :value="r.id" style="display:block">{{ r.roleName }}（{{ r.roleCode }}）</el-checkbox>
      </el-checkbox-group>
      <el-empty v-if="!roles.length" description="暂无角色" :image-size="80" />
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveUserRoles">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限（菜单 + 按钮） -->
    <el-dialog v-model="permDialogVisible" :title="`分配权限 - ${permRole?.roleName || ''}`" width="560px">
      <p class="text-muted" style="margin-top:0">
        勾选「目录/菜单」控制可见页面；勾选「按钮」控制新增/编辑/删除等操作权限。
      </p>
      <el-tree
        ref="permTreeRef"
        :data="menuTree"
        node-key="id"
        show-checkbox
        default-expand-all
        :props="{ label: 'menuName', children: 'children' }"
        style="max-height: 460px; overflow: auto"
      >
        <template #default="{ data }">
          <span style="display: inline-flex; align-items: center; gap: 6px">
            <el-tag size="small" effect="plain" :type="menuTypeTag(data.menuType)">{{ menuTypeLabel(data.menuType) }}</el-tag>
            <span>{{ data.menuName }}</span>
            <span v-if="data.perms" class="text-muted" style="font-size: 12px">{{ data.perms }}</span>
          </span>
        </template>
      </el-tree>
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveRoleMenus">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { nextTick, ref } from 'vue'
import { ElMessage } from 'element-plus'
import CrudTable from '@/components/CrudTable.vue'
import request from '@/api/request'
import { crud } from '@/api'

const activeTab = ref('user')
const saving = ref(false)

/* ---------- 用户 ---------- */
const userColumns = [
  { prop: 'username', label: '账号', width: 130 },
  { prop: 'realName', label: '姓名', width: 120 },
  { prop: 'phone', label: '手机号', width: 140 },
  { prop: 'email', label: '邮箱', minWidth: 160 },
  { prop: 'status', label: '状态', width: 80, tag: true },
  { prop: 'lastLoginTime', label: '最后登录', width: 170 },
  { prop: 'createTime', label: '创建时间', width: 170 }
]
const userFormFields = [
  { prop: 'username', label: '登录账号' },
  { prop: 'password', label: '密码', span: 12 },
  { prop: 'realName', label: '真实姓名' },
  { prop: 'phone', label: '手机号' },
  { prop: 'email', label: '邮箱' },
  { prop: 'storeId', label: '所属门店ID', type: 'number' },
  { prop: 'avatar', label: '头像', type: 'image', span: 24, dir: 'avatar' },
  { prop: 'status', label: '状态', type: 'switch' },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24 }
]
const userSearchFields = [
  { prop: 'usernameLike', label: '账号' },
  { prop: 'realNameLike', label: '姓名' },
  { prop: 'phoneLike', label: '手机号' }
]

const roles = ref<any[]>([])
const roleDialogVisible = ref(false)
const roleUser = ref<any>(null)
const checkedRoleIds = ref<number[]>([])

async function loadRoles() {
  roles.value = await crud.list('/admin/sys-role', { status: 1 })
}
async function openRoleDialog(row: any) {
  roleUser.value = row
  await loadRoles()
  const mappings: any[] = await crud.list('/admin/sys-user-role', { userId: row.id })
  checkedRoleIds.value = mappings.map((m) => m.roleId)
  roleDialogVisible.value = true
}
async function saveUserRoles() {
  saving.value = true
  try {
    const existing: any[] = await crud.list('/admin/sys-user-role', { userId: roleUser.value.id })
    const existMap = new Map(existing.map((m) => [m.roleId, m.id]))
    const target = new Set(checkedRoleIds.value)
    // 删除取消勾选的
    for (const m of existing) {
      if (!target.has(m.roleId)) await crud.remove('/admin/sys-user-role', m.id)
    }
    // 新增勾选的
    for (const rid of checkedRoleIds.value) {
      if (!existMap.has(rid)) await crud.create('/admin/sys-user-role', { userId: roleUser.value.id, roleId: rid })
    }
    ElMessage.success('角色已更新')
    roleDialogVisible.value = false
  } finally {
    saving.value = false
  }
}

/* ---------- 角色 ---------- */
const roleColumns = [
  { prop: 'roleName', label: '角色名称', width: 160 },
  { prop: 'roleCode', label: '角色编码', width: 160 },
  { prop: 'dataScope', label: '数据范围', width: 100 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'status', label: '状态', width: 80, tag: true },
  { prop: 'remark', label: '备注', minWidth: 160 }
]
const roleFormFields = [
  { prop: 'roleName', label: '角色名称' },
  { prop: 'roleCode', label: '角色编码' },
  { prop: 'dataScope', label: '数据范围', type: 'select', options: [
    { label: '全部数据', value: 1 }, { label: '本店数据', value: 2 }, { label: '仅本人', value: 3 }
  ] },
  { prop: 'sort', label: '排序', type: 'number' },
  { prop: 'status', label: '状态', type: 'switch' },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24 }
]

const menuTree = ref<any[]>([])
const permTreeRef = ref()
const permDialogVisible = ref(false)
const permRole = ref<any>(null)

function menuTypeLabel(t: string) {
  return ({ DIR: '目录', MENU: '菜单', BUTTON: '按钮' } as any)[t] || t
}
function menuTypeTag(t: string) {
  return ({ DIR: 'warning', MENU: 'primary', BUTTON: 'success' } as any)[t] || 'info'
}

function buildTree(list: any[]): any[] {
  const map: any = {}
  list.forEach((m) => { map[m.id] = { ...m, children: [] } })
  const roots: any[] = []
  list.forEach((m) => {
    if (m.parentId && map[m.parentId]) map[m.parentId].children.push(map[m.id])
    else roots.push(map[m.id])
  })
  return roots
}
async function openPermDialog(row: any) {
  permRole.value = row
  const menus: any[] = await crud.list('/admin/sys-menu')
  menuTree.value = buildTree(menus)
  const mappings: any[] = await crud.list('/admin/sys-role-menu', { roleId: row.id })
  permDialogVisible.value = true
  await nextTick()
  // 仅勾选叶子节点，父节点由级联自动计算
  const checked = mappings.map((m) => m.menuId)
  permTreeRef.value?.setCheckedKeys(checked, false)
}
async function saveRoleMenus() {
  saving.value = true
  try {
    const checked: number[] = permTreeRef.value.getCheckedKeys()
    const half: number[] = permTreeRef.value.getHalfCheckedKeys()
    const all = Array.from(new Set([...checked, ...half]))
    const existing: any[] = await crud.list('/admin/sys-role-menu', { roleId: permRole.value.id })
    const existMap = new Map<number, any>(existing.map((m) => [m.menuId, m.id]))
    const target = new Set(all)
    for (const m of existing) {
      if (!target.has(m.menuId)) await crud.remove('/admin/sys-role-menu', m.id)
    }
    for (const mid of all) {
      if (!existMap.has(mid)) await crud.create('/admin/sys-role-menu', { roleId: permRole.value.id, menuId: mid })
    }
    ElMessage.success('权限已更新')
    permDialogVisible.value = false
  } finally {
    saving.value = false
  }
}
</script>
