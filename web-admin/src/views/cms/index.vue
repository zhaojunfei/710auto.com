<template>
  <div class="page-container">
    <el-tabs v-model="tab">
      <el-tab-pane label="养护知识文章" name="article">
        <crud-table url="/admin/cms-article" title="文章" :columns="articleColumns" :form-fields="articleForm" :search-fields="[{ prop: 'titleLike', label: '标题' }]" :default-form="{ status: 1, isTop: 0 }" />
      </el-tab-pane>
      <el-tab-pane label="文章分类" name="category">
        <crud-table url="/admin/cms-article-category" title="文章分类" :columns="catColumns" :form-fields="catForm" :default-form="{ status: 1, parentId: 0 }" />
      </el-tab-pane>
      <el-tab-pane label="施工案例" name="case">
        <crud-table url="/admin/cms-case" title="案例" :columns="caseColumns" :form-fields="caseForm" :default-form="{ status: 1 }" />
      </el-tab-pane>
      <el-tab-pane label="品牌故事" name="brand">
        <crud-table url="/admin/cms-brand-story" title="品牌故事" :columns="brandColumns" :form-fields="brandForm" :default-form="{ status: 1 }" />
      </el-tab-pane>
      <el-tab-pane label="轮播图" name="banner">
        <crud-table url="/admin/cms-banner" title="轮播图" :columns="bannerColumns" :form-fields="bannerForm" :default-form="{ status: 1, platform: 'home' }" />
      </el-tab-pane>
      <el-tab-pane label="在线留言" name="message">
        <crud-table url="/admin/cms-message" title="留言" :can-add="false" :columns="messageColumns" :form-fields="messageForm" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CrudTable from '@/components/CrudTable.vue'

const tab = ref('article')

const articleColumns = [
  { prop: 'title', label: '标题', minWidth: 200 },
  { prop: 'author', label: '作者', width: 100 },
  { prop: 'viewCount', label: '浏览', width: 80 },
  { prop: 'isTop', label: '置顶', width: 70 },
  { prop: 'status', label: '状态', width: 80 }
]
const articleForm = [
  { prop: 'title', label: '标题' }, { prop: 'titleEn', label: '英文标题' },
  { prop: 'categoryId', label: '分类ID', type: 'number' }, { prop: 'author', label: '作者' },
  { prop: 'isTop', label: '置顶', type: 'switch' },
  { prop: 'cover', label: '封面图', type: 'image', span: 24 },
  { prop: 'summary', label: '摘要', type: 'textarea', span: 24 },
  { prop: 'content', label: '正文(中文)', type: 'richtext', span: 24 },
  { prop: 'contentEn', label: '正文(English)', type: 'richtext', span: 24 },
  { prop: 'seoTitle', label: 'SEO标题' }, { prop: 'seoKeywords', label: 'SEO关键词' },
  { prop: 'seoDescription', label: 'SEO描述', span: 24 },
  { prop: 'status', label: '发布', type: 'switch' }
]

const catColumns = [
  { prop: 'categoryName', label: '分类名称' },
  { prop: 'categoryNameEn', label: '英文名称' },
  { prop: 'parentId', label: '父级ID', width: 90 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'status', label: '状态', width: 80 }
]
const catForm = [
  { prop: 'categoryName', label: '分类名称' },
  { prop: 'categoryNameEn', label: '英文名称' },
  { prop: 'parentId', label: '父级ID', type: 'number' },
  { prop: 'sort', label: '排序', type: 'number' },
  { prop: 'status', label: '启用', type: 'switch' }
]

const caseColumns = [{ prop: 'caseName', label: '案例' }, { prop: 'caseType', label: '类型', width: 90 }, { prop: 'viewCount', label: '浏览', width: 80 }, { prop: 'status', label: '状态', width: 80 }]
const caseForm = [
  { prop: 'caseName', label: '案例名称' }, { prop: 'caseNameEn', label: '英文名称' },
  { prop: 'caseType', label: '类型', type: 'select', options: ['维修', '保养', '美容', '存胎'].map((v) => ({ label: v, value: v })) },
  { prop: 'cover', label: '封面', type: 'image', span: 24 },
  { prop: 'beforeImages', label: '施工前图集', type: 'image', span: 24, dir: 'case' },
  { prop: 'afterImages', label: '施工后图集', type: 'image', span: 24, dir: 'case' },
  { prop: 'content', label: '案例描述', type: 'richtext', span: 24 },
  { prop: 'status', label: '上架', type: 'switch' }
]

const brandColumns = [{ prop: 'title', label: '标题' }, { prop: 'sort', label: '排序', width: 80 }, { prop: 'status', label: '状态', width: 80 }]
const brandForm = [
  { prop: 'title', label: '标题' }, { prop: 'titleEn', label: '英文标题' },
  { prop: 'cover', label: '封面', type: 'image', span: 24 },
  { prop: 'images', label: '门店实拍图集', type: 'image', span: 24, dir: 'store' },
  { prop: 'content', label: '内容(中文)', type: 'richtext', span: 24 },
  { prop: 'contentEn', label: '内容(English)', type: 'richtext', span: 24 },
  { prop: 'status', label: '启用', type: 'switch' }
]

const bannerColumns = [{ prop: 'title', label: '标题' }, { prop: 'platform', label: '端', width: 90 }, { prop: 'sort', label: '排序', width: 80 }, { prop: 'status', label: '状态', width: 80 }]
const bannerForm = [
  { prop: 'title', label: '标题' }, { prop: 'titleEn', label: '英文标题' },
  { prop: 'platform', label: '端', type: 'select', options: [{ label: '首页', value: 'home' }, { label: '服务页', value: 'service' }, { label: '官网', value: 'www' }] },
  { prop: 'image', label: '图片', type: 'image', span: 24 },
  { prop: 'link', label: '跳转链接', span: 24 },
  { prop: 'sort', label: '排序', type: 'number' }, { prop: 'status', label: '启用', type: 'switch' }
]

const messageColumns = [{ prop: 'name', label: '姓名', width: 100 }, { prop: 'phone', label: '电话', width: 140 }, { prop: 'content', label: '留言', minWidth: 200 }, { prop: 'source', label: '来源', width: 90 }, { prop: 'status', label: '状态', width: 90 }]
const messageForm = [{ prop: 'name', label: '姓名' }, { prop: 'phone', label: '电话' }, { prop: 'reply', label: '回复', type: 'textarea', span: 24 }, { prop: 'status', label: '已处理', type: 'switch' }]
</script>
