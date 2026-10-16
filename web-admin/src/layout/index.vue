<template>
  <el-container style="height: 100%">
    <el-aside :width="collapsed ? '64px' : '220px'" style="transition: width .2s; background: #001529">
      <div class="logo">
        <span v-if="!collapsed">710号汽车之家</span>
        <span v-else>710</span>
      </div>
      <el-menu
        :default-active="$route.path"
        :collapse="collapsed"
        background-color="#001529"
        text-color="#c9d1d9"
        active-text-color="#ffffff"
        router
      >
        <template v-for="menu in sidebarMenus" :key="menu.path || menu.id">
          <el-sub-menu v-if="menu.children && menu.children.length" :index="String(menu.id)">
            <template #title><el-icon><component :is="menu.icon || 'Menu'" /></el-icon><span>{{ menu.name }}</span></template>
            <el-menu-item v-for="child in menu.children" :key="child.path" :index="child.path">
              <el-icon><component :is="child.icon || 'Document'" /></el-icon><span>{{ child.name }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.path">
            <el-icon><component :is="menu.icon || 'Document'" /></el-icon><span>{{ menu.name }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="collapsed = !collapsed"><Fold /></el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title || '' }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-tag size="small" type="success" v-if="userStore.userInfo?.storeId">门店 #{{ userStore.userInfo.storeId }}</el-tag>
          <el-dropdown @command="onCommand">
            <span class="user-info">
              <el-avatar :size="28">{{ (userStore.userInfo?.realName || 'U').charAt(0) }}</el-avatar>
              <span>{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main style="background: #f5f7fa">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { authApi } from '@/api'

const collapsed = ref(false)
const router = useRouter()
const userStore = useUserStore()

const sidebarMenus = computed(() => {
  if (userStore.menus && userStore.menus.length) {
    return buildPath(userStore.menus)
  }
  return defaultMenus
})

/** 递归补全子菜单 path（后端菜单树） */
function buildPath(menus: any[]): any[] {
  return menus.map((m) => ({ ...m, children: m.children ? buildPath(m.children) : [] }))
}

const defaultMenus = [
  { id: 1, name: '数据看板', path: '/dashboard', icon: 'DataLine' },
  { id: 4, name: '预约工单', path: '/workorder', icon: 'Tickets' },
  { id: 3, name: '服务项目', path: '/service', icon: 'Tools' },
  { id: 5, name: '存胎寄存', path: '/tire', icon: 'Van' },
  { id: 6, name: '配件库存', path: '/inventory', icon: 'Box' },
  { id: 7, name: '会员营销', path: '/member', icon: 'User' },
  { id: 8, name: '官网CMS', path: '/cms', icon: 'Document' },
  { id: 9, name: '公众号', path: '/wechat', icon: 'ChatDotRound' },
  {
    id: 10, name: '系统管理', icon: 'Setting', children: [
      { id: 11, name: '用户角色', path: '/system/user' },
      { id: 12, name: '系统参数', path: '/system/config' },
      { id: 13, name: '多语言配置', path: '/system/language' },
      { id: 14, name: '菜单可视化', path: '/system/menu' },
      { id: 15, name: '对象存储', path: '/system/storage' },
      { id: 17, name: '数据字典', path: '/system/dict' },
      { id: 18, name: '文件管理', path: '/system/file' },
      { id: 16, name: '审计日志', path: '/system/audit' }
    ]
  }
]

onMounted(async () => {
  if (!userStore.userInfo?.username) {
    try { await userStore.loadInfo() } catch { /* ignore */ }
  }
})

async function onCommand(command: string) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'password') {
    const { value } = await ElMessageBox.prompt('请输入新密码', '修改密码', { inputType: 'password' })
    if (value) {
      await authApi.changePassword({ oldPassword: prompt('请输入原密码') || '', newPassword: value })
      ElMessage.success('修改成功')
    }
  }
}
</script>

<style scoped>
.logo { height: 56px; line-height: 56px; text-align: center; color: #fff; font-weight: 600; letter-spacing: 1px; }
.header { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #ebeef5; height: 56px; }
.header-left { display: flex; align-items: center; gap: 12px; }
.collapse-btn { cursor: pointer; font-size: 18px; }
.header-right { display: flex; align-items: center; gap: 12px; }
.user-info { display: flex; align-items: center; gap: 6px; cursor: pointer; }
:deep(.el-menu) { border-right: none; }
</style>
