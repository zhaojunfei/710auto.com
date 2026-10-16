<template>
  <crud-table
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
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import CrudTable from '@/components/CrudTable.vue'

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
  location.reload()
}
</script>
