<template>
  <div class="page-container">
    <el-tabs v-model="tab">
      <el-tab-pane label="字典类型" name="type">
        <crud-table
          url="/admin/sys-dict-type"
          title="字典类型"
          :columns="typeColumns"
          :form-fields="typeForm"
          :search-fields="[{ prop: 'dictNameLike', label: '字典名称' }, { prop: 'dictTypeLike', label: '类型编码' }]"
          :default-form="{ status: 1 }"
        />
      </el-tab-pane>
      <el-tab-pane label="字典数据" name="data">
        <crud-table
          url="/admin/sys-dict-data"
          title="字典数据"
          :columns="dataColumns"
          :form-fields="dataForm"
          :search-fields="[{ prop: 'dictType', label: '字典类型' }, { prop: 'dictLabelLike', label: '字典标签' }]"
          :default-form="{ status: 1, dictSort: 1 }"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CrudTable from '@/components/CrudTable.vue'

const tab = ref('type')

const typeColumns = [
  { prop: 'dictName', label: '字典名称', minWidth: 140 },
  { prop: 'dictType', label: '类型编码', minWidth: 160 },
  { prop: 'status', label: '状态', width: 80, tag: true },
  { prop: 'remark', label: '备注', minWidth: 160 },
  { prop: 'createTime', label: '创建时间', width: 170 }
]
const typeForm = [
  { prop: 'dictName', label: '字典名称' },
  { prop: 'dictType', label: '类型编码' },
  { prop: 'status', label: '启用', type: 'switch' },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24 }
]

const dataColumns = [
  { prop: 'dictType', label: '字典类型', width: 150 },
  { prop: 'dictLabel', label: '标签', width: 140 },
  { prop: 'dictValue', label: '键值', width: 120 },
  { prop: 'dictSort', label: '排序', width: 80 },
  { prop: 'cssClass', label: '样式', width: 110 },
  { prop: 'status', label: '状态', width: 80, tag: true },
  { prop: 'remark', label: '备注', minWidth: 140 }
]
const dataForm = [
  { prop: 'dictType', label: '字典类型' },
  { prop: 'dictLabel', label: '字典标签' },
  { prop: 'dictValue', label: '字典键值' },
  { prop: 'dictSort', label: '排序', type: 'number' },
  { prop: 'cssClass', label: '样式类' },
  { prop: 'status', label: '启用', type: 'switch' },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24 }
]
</script>
