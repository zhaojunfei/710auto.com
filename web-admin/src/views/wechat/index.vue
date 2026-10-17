<template>
  <div class="page-container">
    <el-alert type="info" :closable="false" style="margin-bottom:12px">
      <template #title>公众号自定义菜单由「菜单可视化」模块配置；此处可预览微信菜单结构并一键同步。</template>
    </el-alert>
    <div class="card-block">
      <div class="toolbar">
        <el-button type="primary" @click="syncMenu">同步菜单到微信</el-button>
        <el-button @click="loadMenuJson">查看菜单结构</el-button>
      </div>
      <el-input v-if="menuJson" type="textarea" :rows="10" :model-value="JSON.stringify(menuJson, null, 2)" readonly />
    </div>
    <el-tabs v-model="tab">
      <el-tab-pane label="自动回复规则" name="reply">
        <crud-table url="/admin/wechat-reply-rule" title="回复规则" :columns="replyColumns" :form-fields="replyForm" :default-form="{ status: 1, ruleType: '关键词' }" />
      </el-tab-pane>
      <el-tab-pane label="模板消息" name="template">
        <crud-table url="/admin/wechat-template-msg" title="模板消息" :columns="tplColumns" :form-fields="tplForm" :default-form="{ status: 1 }" />
      </el-tab-pane>
      <el-tab-pane label="粉丝" name="fans">
        <crud-table url="/admin/wechat-fans" title="粉丝" :can-add="false" :columns="fansColumns" :form-fields="[{ prop: 'remark', label: '备注' }]" />
      </el-tab-pane>
      <el-tab-pane label="素材" name="material">
        <crud-table url="/admin/wechat-material" title="素材" :columns="materialColumns" :form-fields="materialForm" :default-form="{ status: 1, materialType: '图文' }" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import CrudTable from '@/components/CrudTable.vue'

const tab = ref('reply')
const menuJson = ref<any>(null)

async function syncMenu() {
  const res: any = await request.post('/admin/wechat/sync-menu')
  ElMessage.success(res.synced ? '菜单已同步微信' : '已生成菜单结构（未配置公众号，空跑）')
  menuJson.value = res.menu
}
async function loadMenuJson() {
  menuJson.value = await request.get('/admin/wechat/menu-json')
}

const replyColumns = [{ prop: 'ruleType', label: '类型', width: 90 }, { prop: 'keyword', label: '关键词', width: 120 }, { prop: 'matchType', label: '匹配', width: 90 }, { prop: 'content', label: '回复内容', minWidth: 220 }, { prop: 'status', label: '状态', dict: 'enable_status', width: 70, tag: true }]
const replyForm = [
  { prop: 'ruleType', label: '类型', type: 'select', options: ['关注', '关键词', '默认'].map((v) => ({ label: v, value: v })) },
  { prop: 'keyword', label: '关键词' },
  { prop: 'matchType', label: '匹配方式', type: 'select', options: ['全匹配', '半匹配'].map((v) => ({ label: v, value: v })) },
  { prop: 'replyType', label: '回复类型', type: 'select', options: ['文本', '图文', '图片'].map((v) => ({ label: v, value: v })) },
  { prop: 'content', label: '回复内容', type: 'textarea', span: 24 },
  { prop: 'sort', label: '排序', type: 'number' },
  { prop: 'status', label: '启用', type: 'switch', dict: 'enable_status' }
]

const tplColumns = [{ prop: 'msgType', label: '消息类型', width: 140 }, { prop: 'templateId', label: '模板ID', minWidth: 180 }, { prop: 'status', label: '状态', dict: 'enable_status', width: 70, tag: true }]
const tplForm = [
  { prop: 'msgType', label: '消息类型', type: 'select', options: ['预约确认', '接车', '施工进度', '完工待取车', '结算完成', '存胎到期提醒', '退款'].map((v) => ({ label: v, value: v })) },
  { prop: 'templateId', label: '微信模板ID' },
  { prop: 'title', label: '模板标题' },
  { prop: 'contentTemplate', label: '内容模板', type: 'textarea', span: 24 },
  { prop: 'status', label: '启用', type: 'switch', dict: 'enable_status' }
]

const fansColumns = [{ prop: 'nickname', label: '昵称' }, { prop: 'openid', label: 'OpenID', minWidth: 200 }, { prop: 'subscribe', label: '关注', width: 70 }, { prop: 'subscribeTime', label: '关注时间', width: 170 }]

const materialColumns = [{ prop: 'materialType', label: '类型', width: 90 }, { prop: 'title', label: '标题', minWidth: 180 }, { prop: 'mediaId', label: '媒体ID', minWidth: 160 }, { prop: 'status', label: '状态', dict: 'enable_status', width: 70, tag: true }]
const materialForm = [{ prop: 'materialType', label: '类型', type: 'select', options: ['图文', '图片', '视频'].map((v) => ({ label: v, value: v })) }, { prop: 'title', label: '标题' }, { prop: 'mediaId', label: '微信媒体ID' }, { prop: 'thumbUrl', label: '缩略图', type: 'image', span: 24 }, { prop: 'content', label: '图文内容', type: 'richtext', span: 24 }, { prop: 'status', label: '有效', type: 'switch', dict: 'enable_status' }]
</script>
