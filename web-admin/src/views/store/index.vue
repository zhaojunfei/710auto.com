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

/** 门店下拉（统一显示门店名称，不暴露门店ID） */
const storeSource = { url: '/admin/store-info', label: 'storeName', value: 'id' }

const storeColumns = [{ prop: 'storeName', label: '门店名称' }, { prop: 'phone', label: '电话' }, { prop: 'address', label: '地址', minWidth: 200 }, { prop: 'status', label: '状态', dict: 'enable_status', width: 80, tag: true }]
const storeForm = [
  { prop: 'storeName', label: '门店名称' }, { prop: 'storeNameEn', label: '英文名称' },
  { prop: 'phone', label: '客服电话' }, { prop: 'businessHours', label: '营业时间' },
  { prop: 'address', label: '地址', span: 24 }, { prop: 'addressEn', label: '英文地址', span: 24 },
  { prop: 'longitude', label: '经度' }, { prop: 'latitude', label: '纬度' },
  { prop: 'logo', label: 'Logo', type: 'image', span: 24 },
  { prop: 'businessLicense', label: '营业执照', type: 'image', span: 24 },
  { prop: 'repairCert', label: '维修经营备案', type: 'image', span: 24 },
  { prop: 'intro', label: '门店简介', type: 'richtext', span: 24 },
  { prop: 'status', label: '启用', type: 'switch', dict: 'enable_status' }
]

const stationColumns = [
  { prop: 'stationName', label: '工位' },
  { prop: 'stationType', label: '类型', dict: 'station_type', width: 100 },
  { prop: 'storeId', label: '所属门店', lookup: storeSource },
  { prop: 'capacity', label: '同时开工上限', width: 120 },
  { prop: 'status', label: '状态', dict: 'enable_status', width: 80, tag: true }
]
const stationForm = [
  { prop: 'stationName', label: '工位名称' },
  { prop: 'stationType', label: '类型', type: 'select', dict: 'station_type' },
  { prop: 'capacity', label: '同时开工上限', type: 'number' },
  { prop: 'storeId', label: '所属门店', type: 'select', source: storeSource },
  { prop: 'sort', label: '排序', type: 'number' },
  { prop: 'status', label: '启用', type: 'switch', dict: 'enable_status' }
]

const techColumns = [
  { prop: 'techName', label: '技师' },
  { prop: 'techType', label: '工种', dict: 'tech_type', width: 100 },
  { prop: 'level', label: '等级', dict: 'tech_level', width: 100 },
  { prop: 'storeId', label: '所属门店', lookup: storeSource },
  { prop: 'userId', label: '登录账号', lookup: { url: '/admin/sys-user', label: 'realName', value: 'id' } },
  { prop: 'status', label: '状态', dict: 'technician_status', width: 80, tag: true }
]
const techForm = [
  { prop: 'techName', label: '技师姓名' },
  { prop: 'techType', label: '工种', type: 'select', dict: 'tech_type' },
  { prop: 'level', label: '等级', type: 'select', dict: 'tech_level' },
  { prop: 'storeId', label: '所属门店', type: 'select', source: storeSource },
  { prop: 'phone', label: '手机号' },
  { prop: 'userId', label: '关联系统用户', type: 'select', source: { url: '/admin/sys-user', label: 'realName', value: 'id' }},
  { prop: 'certNo', label: '资质证书编号' },
  { prop: 'qualification', label: '资质证明', type: 'image', span: 24 },
  { prop: 'intro', label: '简介', type: 'textarea', span: 24 },
  { prop: 'status', label: '在职', type: 'switch', dict: 'technician_status' }
]

const scheduleColumns = [
  { prop: 'technicianId', label: '技师', lookup: { url: '/admin/technician', label: 'techName', value: 'id' } },
  { prop: 'workDate', label: '日期', width: 130 },
  { prop: 'shift', label: '班次', dict: 'shift', width: 100 },
  { prop: 'startTime', label: '开始', width: 110 },
  { prop: 'endTime', label: '结束', width: 110 },
  { prop: 'status', label: '状态', dict: 'enable_status', width: 80, tag: true }
]
const scheduleForm = [
  { prop: 'technicianId', label: '技师', type: 'select', source: { url: '/admin/technician', label: 'techName', value: 'id' } },
  { prop: 'workDate', label: '日期', type: 'date' },
  { prop: 'shift', label: '班次', type: 'select', dict: 'shift' },
  { prop: 'startTime', label: '开始时间' },
  { prop: 'endTime', label: '结束时间' },
  { prop: 'status', label: '有效', type: 'switch', dict: 'enable_status' }
]
</script>
