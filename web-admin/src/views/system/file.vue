<template>
  <div class="page-container">
    <crud-table
      url="/admin/sys-file"
      title="文件"
      :can-add="false"
      :can-edit="false"
      :columns="columns"
      :search-fields="[
        { prop: 'originalNameLike', label: '原始文件名' },
        { prop: 'bizType', label: '业务类型' },
        { prop: 'storageDriver', label: '存储驱动', options: ['local', 's3', 'oss', 'cos', 'minio'].map((v) => ({ label: v, value: v })) }
      ]"
    />
  </div>
</template>

<script setup lang="ts">
import CrudTable from '@/components/CrudTable.vue'

const columns = [
  { prop: 'originalName', label: '原始文件名', minWidth: 200 },
  { prop: 'fileType', label: '类型', width: 100 },
  { prop: 'fileSize', label: '大小(字节)', width: 110 },
  { prop: 'storageDriver', label: '驱动', width: 90 },
  { prop: 'bucket', label: '存储桶', width: 130 },
  { prop: 'bizType', label: '业务类型', width: 120 },
  { prop: 'memberId', label: '上传会员', width: 100 },
  { prop: 'fileUrl', label: '访问地址', minWidth: 240 },
  { prop: 'createTime', label: '上传时间', width: 170 }
]
</script>
