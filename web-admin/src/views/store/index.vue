<template>
  <div class="page-container">
    <el-tabs v-model="tab">
      <el-tab-pane label="门店信息" name="store">
        <crud-table url="/admin/store-info" title="门店" :columns="storeColumns" :form-fields="storeForm" :default-form="{ status: 1 }" />
      </el-tab-pane>
      <el-tab-pane label="工位" name="station">
        <crud-table url="/admin/work-station" title="工位" :columns="stationColumns" :form-fields="stationForm" :default-form="{ status: 1, capacity: 1 }" />
      </el-tab-pane>
      <el-tab-pane label="技师" name="technician">
        <crud-table url="/admin/technician" title="技师" :columns="techColumns" :form-fields="techForm" :default-form="{ status: 1 }" />
      </el-tab-pane>
      <el-tab-pane label="排班" name="schedule">
        <crud-table url="/admin/technician-schedule" title="排班" :columns="scheduleColumns" :form-fields="scheduleForm" :default-form="{ status: 1 }" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CrudTable from '@/components/CrudTable.vue'

const tab = ref('store')

const storeColumns = [{ prop: 'storeName', label: '门店名称' }, { prop: 'phone', label: '电话' }, { prop: 'address', label: '地址', minWidth: 200 }, { prop: 'status', label: '状态', width: 80 }]
const storeForm = [
  { prop: 'storeName', label: '门店名称' }, { prop: 'storeNameEn', label: '英文名称' },
  { prop: 'phone', label: '客服电话' }, { prop: 'businessHours', label: '营业时间' },
  { prop: 'address', label: '地址', span: 24 }, { prop: 'addressEn', label: '英文地址', span: 24 },
  { prop: 'longitude', label: '经度' }, { prop: 'latitude', label: '纬度' },
  { prop: 'logo', label: 'Logo', type: 'image', span: 24 },
  { prop: 'businessLicense', label: '营业执照', type: 'image', span: 24 },
  { prop: 'repairCert', label: '维修经营备案', type: 'image', span: 24 },
  { prop: 'intro', label: '门店简介', type: 'richtext', span: 24 },
  { prop: 'status', label: '启用', type: 'switch' }
]

const stationColumns = [{ prop: 'stationName', label: '工位' }, { prop: 'stationType', label: '类型' }, { prop: 'capacity', label: '同时开工上限' }, { prop: 'status', label: '状态', width: 80 }]
const stationForm = [{ prop: 'stationName', label: '工位名称' }, { prop: 'stationType', label: '类型', type: 'select', options: ['机修', '保养', '轮胎', '美容'].map((v) => ({ label: v, value: v })) }, { prop: 'capacity', label: '同时开工上限', type: 'number' }, { prop: 'storeId', label: '门店ID', type: 'number' }, { prop: 'sort', label: '排序', type: 'number' }, { prop: 'status', label: '启用', type: 'switch' }]

const techColumns = [{ prop: 'techName', label: '技师' }, { prop: 'techType', label: '工种' }, { prop: 'level', label: '等级' }, { prop: 'status', label: '状态', width: 80 }]
const techForm = [
  { prop: 'techName', label: '技师姓名' },
  { prop: 'techType', label: '工种', type: 'select', options: ['机修', '钣喷', '美容', '轮胎', '通用'].map((v) => ({ label: v, value: v })) },
  { prop: 'level', label: '等级', type: 'select', options: ['初级', '中级', '高级', '专家'].map((v) => ({ label: v, value: v })) },
  { prop: 'userId', label: '关联系统用户ID', type: 'number' },
  { prop: 'storeId', label: '门店ID', type: 'number' },
  { prop: 'phone', label: '手机号' }, { prop: 'certNo', label: '资质证书编号' },
  { prop: 'qualification', label: '资质证明', type: 'image', span: 24 },
  { prop: 'intro', label: '简介', type: 'textarea', span: 24 },
  { prop: 'status', label: '在职', type: 'switch' }
]

const scheduleColumns = [{ prop: 'technicianId', label: '技师ID' }, { prop: 'workDate', label: '日期' }, { prop: 'shift', label: '班次' }, { prop: 'startTime', label: '开始' }, { prop: 'endTime', label: '结束' }]
const scheduleForm = [{ prop: 'technicianId', label: '技师ID', type: 'number' }, { prop: 'workDate', label: '日期(yyyy-MM-dd)' }, { prop: 'shift', label: '班次', type: 'select', options: ['早班', '中班', '晚班', '休息'].map((v) => ({ label: v, value: v })) }, { prop: 'startTime', label: '开始时间' }, { prop: 'endTime', label: '结束时间' }, { prop: 'status', label: '有效', type: 'switch' }]
</script>
