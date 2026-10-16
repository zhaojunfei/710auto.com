<template>
  <div class="page-container">
    <div class="card-block">
      <div class="toolbar">
        <el-radio-group v-model="platform" @change="onPlatformChange">
          <el-radio-button v-for="p in platforms" :key="p.value" :value="p.value">{{ p.label }}</el-radio-button>
        </el-radio-group>
        <div style="flex:1"></div>
        <el-button @click="addNode(null)"><el-icon><Plus /></el-icon>&nbsp;新增一级菜单</el-button>
        <el-button type="success" plain @click="previewWechat" v-if="platform === 'mp'">预览到微信</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存并发布</el-button>
      </div>
      <p class="text-muted">
        拖拽节点可调整顺序 / 层级；编辑后点击「保存并发布」生效。公众号端发布将同步自定义菜单至微信；
        未保存离开页面会有确认提示。
      </p>
    </div>

    <el-row :gutter="16">
      <!-- 左：菜单树 -->
      <el-col :span="8">
        <div class="card-block" style="min-height:520px">
          <h3 style="margin-top:0">菜单结构</h3>
          <el-tree
            ref="treeRef"
            :data="tree"
            node-key="uid"
            draggable
            default-expand-all
            :expand-on-click-node="false"
            :props="{ label: 'menuName', children: 'children' }"
            @node-click="select"
            @node-drop="onDrop"
          >
            <template #default="{ node, data }">
              <span style="display:flex;align-items:center;width:100%">
                <el-icon v-if="data.icon" style="margin-right:4px"><component :is="data.icon" /></el-icon>
                <span :style="{ opacity: data.visible === 0 ? 0.4 : 1 }">{{ data.menuName || '(未命名)' }}</span>
                <span style="flex:1"></span>
                <el-button link type="primary" @click.stop="addNode(data)">+子</el-button>
                <el-button link type="danger" @click.stop="removeNode(data)">删除</el-button>
              </span>
            </template>
          </el-tree>
          <el-empty v-if="!tree.length" description="暂无菜单，点击「新增一级菜单」开始" />
        </div>
      </el-col>

      <!-- 中：属性面板 -->
      <el-col :span="9">
        <div class="card-block" style="min-height:520px">
          <h3 style="margin-top:0">属性面板</h3>
          <el-empty v-if="!current" description="请选择左侧菜单节点" />
          <el-form v-else :model="current" label-width="110px">
            <el-form-item label="菜单名称"><el-input v-model="current.menuName" /></el-form-item>
            <el-form-item label="英文名称"><el-input v-model="current.menuNameEn" /></el-form-item>
            <el-form-item label="菜单类型">
              <el-select v-model="current.menuType" style="width:100%">
                <el-option v-for="t in menuTypes" :key="t.value" :label="t.label" :value="t.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="跳转地址" v-if="current.menuType === 'view' || current.menuType === 'link'">
              <el-input v-model="current.url" placeholder="https://..." />
            </el-form-item>
            <el-form-item label="小程序AppID" v-if="current.menuType === 'miniprogram'">
              <el-input v-model="current.appId" />
            </el-form-item>
            <el-form-item label="页面路径" v-if="current.menuType === 'miniprogram'">
              <el-input v-model="current.pagePath" placeholder="pages/index/index" />
            </el-form-item>
            <el-form-item label="关键字" v-if="current.menuType === 'click' || current.menuType === 'keyword'">
              <el-input v-model="current.keyValue" />
            </el-form-item>
            <el-form-item label="图标"><el-input v-model="current.icon" placeholder="Element Plus 图标名，如 House" /></el-form-item>
            <el-form-item label="排序"><el-input-number v-model="current.sort" :min="0" /></el-form-item>
            <el-form-item label="显示">
              <el-switch v-model="current.visible" :active-value="1" :inactive-value="0" />
            </el-form-item>
            <el-form-item label="状态">
              <el-switch v-model="current.status" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-form>
        </div>
      </el-col>

      <!-- 右：实时预览 -->
      <el-col :span="7">
        <div class="card-block" style="min-height:520px">
          <h3 style="margin-top:0">实时预览</h3>
          <!-- PC 外壳 -->
          <div class="device pc" v-if="platform === 'public' || platform === 'h5'">
            <div class="device-bar"><i></i><i></i><i></i></div>
            <div class="device-body">
              <div class="preview-nav">
                <span v-for="n in tree" :key="n.uid" :style="{ opacity: n.visible === 0 ? 0.4 : 1 }">{{ n.menuName }}</span>
              </div>
            </div>
          </div>
          <!-- 手机外壳 -->
          <div class="device phone" v-else>
            <div class="device-notch"></div>
            <div class="device-body">
              <div class="preview-grid">
                <div class="preview-tab" v-for="n in tree" :key="n.uid" :style="{ opacity: n.visible === 0 ? 0.4 : 1 }">
                  <div class="preview-icon">{{ (n.menuName || '?').charAt(0) }}</div>
                  <div class="preview-label">{{ n.menuName }}</div>
                  <div class="preview-sub" v-if="n.children && n.children.length">{{ n.children.length }} 个子项</div>
                </div>
              </div>
            </div>
            <div class="device-home"></div>
          </div>
          <p class="text-muted" style="margin-top:12px">仅示意菜单结构，实际样式以各端页面为准。</p>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { onBeforeRouteLeave } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { menuApi } from '@/api'

const platforms = [
  { value: 'mp', label: '公众号菜单' },
  { value: 'public', label: 'PC 官网导航' },
  { value: 'miniapp', label: '小程序 tabBar' },
  { value: 'h5', label: 'H5 底部导航' }
]
const menuTypes = [
  { value: 'view', label: '网页链接(view)' },
  { value: 'miniprogram', label: '跳转小程序' },
  { value: 'click', label: '点击事件(click)' },
  { value: 'keyword', label: '关键词回复' },
  { value: 'article', label: '图文素材' },
  { value: 'link', label: '内部路由' }
]

const platform = ref('mp')
const tree = ref<any[]>([])
const current = ref<any>(null)
const saving = ref(false)
const dirty = ref(false)
const treeRef = ref()

let uid = 1
function decorate(nodes: any[]): any[] {
  return (nodes || []).map((n) => {
    n.uid = uid++
    n.children = decorate(n.children || [])
    return n
  })
}
function strip(nodes: any[]): any[] {
  return (nodes || []).map((n) => ({
    id: n.id,
    menuName: n.menuName,
    menuNameEn: n.menuNameEn,
    menuType: n.menuType,
    url: n.url,
    appId: n.appId,
    pagePath: n.pagePath,
    keyValue: n.keyValue,
    icon: n.icon,
    sort: n.sort,
    visible: n.visible,
    status: n.status,
    children: strip(n.children || [])
  }))
}

async function load() {
  uid = 1
  const data: any = await menuApi.tree(platform.value)
  tree.value = decorate(data || [])
  current.value = null
  dirty.value = false
}
function onPlatformChange() {
  if (dirty.value) {
    ElMessageBox.confirm('当前端菜单有未保存修改，切换将丢失，是否继续？', '提示', { type: 'warning' })
      .then(load)
      .catch(() => {})
  } else {
    load()
  }
}
function select(data: any) {
  current.value = data
}
function addNode(parent: any) {
  const node = { uid: uid++, menuName: '', menuNameEn: '', menuType: 'view', url: '', icon: '', sort: 0, visible: 1, status: 1, children: [] }
  if (parent) {
    parent.children = parent.children || []
    parent.children.push(node)
  } else {
    tree.value.push(node)
  }
  current.value = node
  dirty.value = true
}
function removeNode(node: any) {
  ElMessageBox.confirm(`确认删除菜单「${node.menuName || '未命名'}」及其子菜单？`, '提示', { type: 'warning' }).then(() => {
    const remove = (list: any[]): boolean => {
      const idx = list.indexOf(node)
      if (idx > -1) { list.splice(idx, 1); return true }
      return list.some((n) => remove(n.children || []))
    }
    remove(tree.value)
    if (current.value === node) current.value = null
    dirty.value = true
  }).catch(() => {})
}
function onDrop() {
  dirty.value = true
}
async function previewWechat() {
  const json: any = await menuApi.publish(platform.value)
  ElMessageBox.alert(JSON.stringify(json.wechat?.menu || {}, null, 2), '微信公众号菜单结构预览', { customClass: 'wechat-preview' })
}
async function save() {
  if (dirty.value === false) {
    ElMessage.info('没有需要保存的修改')
    return
  }
  saving.value = true
  try {
    await menuApi.saveTree(platform.value, strip(tree.value))
    const res: any = await menuApi.publish(platform.value)
    dirty.value = false
    ElMessage.success('已保存并发布' + (res?.wechat ? '（公众号菜单同步：' + (res.wechat.synced ? '成功' : '空跑') + '）' : ''))
    load()
  } finally {
    saving.value = false
  }
}

const hasTree = computed(() => tree.value.length > 0)
onBeforeRouteLeave((_to, _from, next) => {
  if (dirty.value) {
    ElMessageBox.confirm('菜单有未保存的修改，确认离开？', '提示', { type: 'warning' })
      .then(() => next())
      .catch(() => next(false))
  } else {
    next()
  }
})
onMounted(load)
</script>

<style scoped>
.device { border: 1px solid #dcdfe6; border-radius: 10px; overflow: hidden; background: #f7f8fa; }
.device.pc { height: 300px; }
.device.pc .device-bar { height: 28px; background: #e4e7ed; display: flex; align-items: center; gap: 6px; padding: 0 10px; }
.device.pc .device-bar i { width: 10px; height: 10px; border-radius: 50%; background: #c0c4cc; display: inline-block; }
.device-body { padding: 12px; }
.preview-nav { display: flex; gap: 18px; background: #fff; padding: 12px; border-radius: 6px; font-size: 13px; color: #303133; }
.device.phone { width: 240px; margin: 0 auto; border-radius: 22px; padding: 8px; }
.device-notch { width: 80px; height: 6px; background: #dcdfe6; border-radius: 3px; margin: 4px auto 8px; }
.device.phone .device-body { background: #fff; border-radius: 14px; min-height: 340px; padding: 24px 12px; }
.device-home { width: 90px; height: 4px; background: #dcdfe6; border-radius: 2px; margin: 8px auto; }
.preview-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.preview-tab { text-align: center; }
.preview-icon { width: 38px; height: 38px; border-radius: 10px; background: var(--brand-color); color: #fff; display: flex; align-items: center; justify-content: center; margin: 0 auto 4px; font-weight: 700; }
.preview-label { font-size: 12px; color: #303133; }
.preview-sub { font-size: 10px; color: #909399; }
</style>
