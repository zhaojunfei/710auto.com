<template>
  <crud-table
    url="/admin/sys-audit-log"
    title="审计日志"
    :can-add="false"
    :can-edit="false"
    :columns="columns"
    :search-fields="searchFields"
  >
    <template #row-actions="{ row }">
      <el-popover width="500" trigger="click">
        <template #reference><el-button link type="primary">详情</el-button></template>
        <div style="max-height:400px;overflow:auto">
          <p><b>模块：</b>{{ row.module }} / {{ row.operation }}</p>
          <p><b>地址：</b>{{ row.requestMethod }} {{ row.requestUrl }}</p>
          <p><b>IP：</b>{{ row.ip }}</p>
          <p><b>耗时：</b>{{ row.costTime }}ms</p>
          <p><b>参数：</b></p>
          <pre style="white-space:pre-wrap;word-break:break-all">{{ row.requestParam }}</pre>
          <p v-if="row.errorMsg"><b>异常：</b>{{ row.errorMsg }}</p>
        </div>
      </el-popover>
    </template>
  </crud-table>
</template>

<script setup lang="ts">
import CrudTable from '@/components/CrudTable.vue'

const columns = [
  { prop: 'username', label: '操作人', width: 110 },
  { prop: 'module', label: '模块', width: 110 },
  { prop: 'operation', label: '操作', width: 120 },
  { prop: 'requestMethod', label: '方式', width: 70 },
  { prop: 'requestUrl', label: '地址', minWidth: 220 },
  { prop: 'ip', label: 'IP', width: 130 },
  { prop: 'costTime', label: '耗时(ms)', width: 90 },
  { prop: 'status', label: '状态', width: 70 },
  { prop: 'createTime', label: '时间', width: 170 }
]
const searchFields = [
  { prop: 'usernameLike', label: '操作人' },
  { prop: 'moduleLike', label: '模块' },
  { prop: 'operationLike', label: '操作' }
]
</script>
