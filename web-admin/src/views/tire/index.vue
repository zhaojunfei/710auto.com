<template>
  <div class="page-container">
    <el-tabs v-model="tab">
      <el-tab-pane label="寄存单" name="order">
        <crud-table
          ref="orderTableRef"
          url="/admin/tire-storage"
          title="存胎寄存单"
          :columns="columns"
          :form-fields="formFields"
          :search-fields="searchFields"
          :default-form="{ status: '已寄存' }"
        >
          <template #actions>
            <el-button type="warning" @click="remind">换季批量提醒</el-button>
            <el-button type="danger" plain @click="scanOverdue">超期扫描结算</el-button>
          </template>
          <template #row-actions="{ row }">
            <el-button link type="success" @click="pickup(row)">取胎核销</el-button>
          </template>
        </crud-table>
      </el-tab-pane>

      <el-tab-pane label="货位库存" name="stock">
        <crud-table
          url="/admin/tire-storage-stock"
          title="寄存货位库存"
          :columns="stockColumns"
          :form-fields="stockForm"
          :search-fields="[{ prop: 'positionNoLike', label: '货位号' }, { prop: 'plateNoLike', label: '车牌' }, { prop: 'status', label: '状态', options: ['在库', '已出库'].map((v) => ({ label: v, value: v })) }]"
          :default-form="{ status: '在库' }"
        />
      </el-tab-pane>

      <el-tab-pane label="寄存明细" name="item">
        <crud-table
          url="/admin/tire-storage-item"
          title="寄存轮胎明细"
          :columns="itemColumns"
          :form-fields="itemForm"
          :search-fields="[{ prop: 'storageOrderId', label: '寄存单ID' }]"
        />
      </el-tab-pane>

      <el-tab-pane label="取胎凭证" name="proof">
        <crud-table
          url="/admin/tire-storage-proof"
          title="寄存凭证"
          :can-add="false"
          :can-edit="false"
          :can-delete="false"
          :columns="proofColumns"
          :search-fields="[{ prop: 'storageOrderId', label: '寄存单ID' }]"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import CrudTable from '@/components/CrudTable.vue'

const tab = ref('order')
const orderTableRef = ref<any>()

const columns = [
  { prop: 'storageNo', label: '寄存单号', minWidth: 150 },
  { prop: 'plateNo', label: '车牌', width: 120 },
  { prop: 'tireCount', label: '数量', width: 70 },
  { prop: 'tireType', label: '类型', width: 90 },
  { prop: 'positionNo', label: '货位', width: 90 },
  { prop: 'storageFee', label: '寄存费', width: 100, money: true },
  { prop: 'overdueFee', label: '超期费', width: 100, money: true },
  { prop: 'startDate', label: '开始', width: 110 },
  { prop: 'endDate', label: '到期', width: 110 },
  { prop: 'status', label: '状态', width: 100, tag: true }
]
const formFields = [
  { prop: 'plateNo', label: '车牌号' },
  { prop: 'tireCount', label: '轮胎数量', type: 'number' },
  { prop: 'tireType', label: '轮胎类型' },
  { prop: 'tireBrand', label: '品牌' },
  { prop: 'tireSize', label: '规格' },
  { prop: 'positionNo', label: '货位号' },
  { prop: 'storageFee', label: '寄存费', type: 'number' },
  { prop: 'endDate', label: '预计取胎日期' },
  { prop: 'status', label: '状态' }
]
const searchFields = [
  { prop: 'storageNoLike', label: '寄存单号' },
  { prop: 'plateNoLike', label: '车牌' },
  { prop: 'status', label: '状态', options: ['已寄存', '寄存中', '待取胎', '已取胎', '超期'].map((v) => ({ label: v, value: v })) }
]

const stockColumns = [
  { prop: 'positionNo', label: '货位号', width: 100 },
  { prop: 'plateNo', label: '车牌', width: 120 },
  { prop: 'storageOrderId', label: '寄存单ID', width: 110 },
  { prop: 'tireCount', label: '数量', width: 80 },
  { prop: 'status', label: '状态', width: 90 },
  { prop: 'inTime', label: '入库时间', width: 170 },
  { prop: 'outTime', label: '出库时间', width: 170 }
]
const stockForm = [
  { prop: 'storeId', label: '门店ID', type: 'number' },
  { prop: 'positionNo', label: '货位号' },
  { prop: 'storageOrderId', label: '寄存单ID', type: 'number' },
  { prop: 'plateNo', label: '车牌号' },
  { prop: 'tireCount', label: '轮胎数量', type: 'number' },
  { prop: 'status', label: '状态', type: 'select', options: [{ label: '在库', value: '在库' }, { label: '已出库', value: '已出库' }] }
]

const itemColumns = [
  { prop: 'storageOrderId', label: '寄存单ID', width: 110 },
  { prop: 'tirePosition', label: '位置', width: 100 },
  { prop: 'tireBrand', label: '品牌', width: 120 },
  { prop: 'tireSize', label: '规格', width: 140 },
  { prop: 'treadDepth', label: '花纹深度', width: 110 },
  { prop: 'remark', label: '备注', minWidth: 140 }
]
const itemForm = [
  { prop: 'storageOrderId', label: '寄存单ID', type: 'number' },
  { prop: 'tirePosition', label: '位置', type: 'select', options: ['左前', '右前', '左后', '右后', '备胎'].map((v) => ({ label: v, value: v })) },
  { prop: 'tireBrand', label: '品牌' },
  { prop: 'tireSize', label: '规格' },
  { prop: 'treadDepth', label: '花纹深度' },
  { prop: 'images', label: '照片', type: 'image', span: 24, dir: 'tire', multiple: true },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24 }
]

const proofColumns = [
  { prop: 'storageOrderId', label: '寄存单ID', width: 110 },
  { prop: 'proofType', label: '类型', width: 100 },
  { prop: 'operatorName', label: '操作人', width: 120 },
  { prop: 'proofTime', label: '凭证时间', width: 170 },
  { prop: 'remark', label: '备注', minWidth: 160 }
]

async function remind() {
  const n = await request.post('/admin/tire-storage/remind', null, { params: { daysBefore: 7 } })
  ElMessage.success(`已提醒 ${n} 条换季存胎`)
}
async function scanOverdue() {
  const n = await request.post('/admin/tire-storage/scan-overdue')
  ElMessage.success(`超期扫描完成，标记 ${n} 条`)
}
async function pickup(row: any) {
  await ElMessageBox.confirm('确认该车已取胎并核销库存？', '取胎核销', { type: 'warning' })
  await request.post(`/admin/tire-storage/${row.id}/pickup`)
  ElMessage.success('取胎核销成功')
  orderTableRef.value?.load()
}
</script>
