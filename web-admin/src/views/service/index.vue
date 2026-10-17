<template>
  <div class="page-container">
    <el-tabs v-model="tab">
      <el-tab-pane label="服务项目" name="item">
        <crud-table
          url="/admin/service-item"
          title="服务项目"
          :columns="columns"
          :form-fields="formFields"
          :search-fields="searchFields"
          :default-form="{ status: 1, auditStatus: 1, bookable: 1, supportHome: 0 }"
        />
      </el-tab-pane>
      <el-tab-pane label="服务分类" name="category">
        <crud-table
          url="/admin/service-category"
          title="服务分类"
          :columns="catColumns"
          :form-fields="catFormFields"
          :default-form="{ status: 1, parentId: 0 }"
        />
      </el-tab-pane>
      <el-tab-pane label="价格配置" name="price">
        <crud-table url="/admin/service-price" title="价格配置" :columns="priceColumns" :form-fields="priceFormFields" :default-form="{ status: 1 }" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CrudTable from '@/components/CrudTable.vue'

const tab = ref('item')

/** 服务分类 / 服务项目 下拉（统一显示名称，不暴露 ID） */
const categorySource = { url: '/admin/service-category', label: 'categoryName', value: 'id' }
const itemSource = { url: '/admin/service-item', label: 'itemName', value: 'id' }

const columns = [
  { prop: 'itemName', label: '项目名称', minWidth: 160 },
  { prop: 'categoryId', label: '分类', lookup: categorySource, width: 130 },
  { prop: 'itemCode', label: '编码', width: 110 },
  { prop: 'laborFee', label: '工时费', width: 100, money: true },
  { prop: 'price', label: '参考价', width: 100, money: true },
  { prop: 'duration', label: '时长(分)', width: 90 },
  { prop: 'stationType', label: '所需工位', dict: 'station_type', width: 100 },
  { prop: 'bookable', label: '可预约', dict: 'yes_no', width: 80, tag: true },
  { prop: 'status', label: '状态', dict: 'shelf_status', width: 80, tag: true },
  { prop: 'viewCount', label: '浏览', width: 80 }
]
const formFields = [
  { prop: 'itemName', label: '项目名称' },
  { prop: 'itemNameEn', label: '英文名称' },
  { prop: 'categoryId', label: '所属分类', type: 'select', source: categorySource },
  { prop: 'itemCode', label: '项目编码' },
  { prop: 'laborFee', label: '工时费', type: 'number' },
  { prop: 'price', label: '参考价格', type: 'number' },
  { prop: 'duration', label: '时长(分钟)', type: 'number' },
  { prop: 'stationType', label: '所需工位', type: 'select', dict: 'station_type' },
  { prop: 'supportHome', label: '支持上门', type: 'switch', dict: 'yes_no' },
  { prop: 'bookable', label: '可预约', type: 'switch', dict: 'yes_no' },
  { prop: 'status', label: '上架', type: 'switch', dict: 'shelf_status' },
  { prop: 'cover', label: '封面图', type: 'image', span: 24 },
  { prop: 'images', label: '项目图集', type: 'image', span: 24, dir: 'service', multiple: true },
  { prop: 'description', label: '项目详情', type: 'richtext', span: 24 }
]
const searchFields = [
  { prop: 'itemNameLike', label: '项目名称' },
  { prop: 'categoryId', label: '所属分类', lookup: categorySource },
  { prop: 'status', label: '状态', dict: 'shelf_status' }
]
const catColumns = [
  { prop: 'categoryName', label: '分类名称' },
  { prop: 'categoryNameEn', label: '英文名称' },
  { prop: 'parentId', label: '上级分类', lookup: categorySource },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'status', label: '状态', dict: 'shelf_status', width: 80, tag: true }
]
const catFormFields = [
  { prop: 'categoryName', label: '分类名称' },
  { prop: 'categoryNameEn', label: '英文名称' },
  { prop: 'parentId', label: '上级分类', type: 'select', source: categorySource },
  { prop: 'sort', label: '排序', type: 'number' },
  { prop: 'cover', label: '封面', type: 'image', span: 24 },
  { prop: 'description', label: '描述', type: 'textarea', span: 24 },
  { prop: 'status', label: '上架', type: 'switch', dict: 'shelf_status' }
]
const priceColumns = [
  { prop: 'itemId', label: '服务项目', lookup: itemSource, minWidth: 140 },
  { prop: 'priceName', label: '价格名称' },
  { prop: 'priceType', label: '类型' },
  { prop: 'price', label: '价格', money: true },
  { prop: 'status', label: '状态', dict: 'enable_status', width: 80, tag: true }
]
const priceFormFields = [
  { prop: 'itemId', label: '服务项目', type: 'select', source: itemSource },
  { prop: 'priceName', label: '价格名称' },
  { prop: 'priceType', label: '类型' },
  { prop: 'price', label: '价格', type: 'number' },
  { prop: 'description', label: '说明' },
  { prop: 'status', label: '启用', type: 'switch', dict: 'enable_status' }
]
</script>
