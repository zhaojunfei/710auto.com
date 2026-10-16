<template>
  <div class="page-container">
    <el-alert v-if="warnings.length" type="warning" :closable="false" style="margin-bottom:12px">
      <template #title>低库存预警：{{ warnings.map((w: any) => w.partName + '(' + w.stock + ')').join('、') }}</template>
    </el-alert>
    <el-tabs v-model="tab">
      <el-tab-pane label="配件耗材" name="part">
        <crud-table url="/admin/part-info" title="配件" :columns="columns" :form-fields="formFields" :search-fields="searchFields" :default-form="{ status: 1, stock: 0, warnStock: 5 }">
          <template #actions>
            <el-button @click="stockDialog = true">出入库</el-button>
          </template>
        </crud-table>
      </el-tab-pane>
      <el-tab-pane label="出入库流水" name="record">
        <crud-table url="/admin/part-stock-record" title="流水" :columns="recordColumns" :can-add="false" :can-edit="false" :search-fields="[{ prop: 'partNameLike', label: '配件名称' }]" />
      </el-tab-pane>
      <el-tab-pane label="配件分类" name="category">
        <crud-table url="/admin/part-category" title="分类" :columns="[{ prop: 'categoryName', label: '分类名称' }, { prop: 'sort', label: '排序', width: 80 }, { prop: 'status', label: '状态', width: 80 }]" :form-fields="[{ prop: 'categoryName', label: '分类名称' }, { prop: 'parentId', label: '父级ID', type: 'number' }, { prop: 'sort', label: '排序', type: 'number' }, { prop: 'status', label: '启用', type: 'switch' }]" />
      </el-tab-pane>
      <el-tab-pane label="供应商" name="supplier">
        <crud-table url="/admin/part-supplier" title="供应商" :columns="[{ prop: 'supplierName', label: '供应商' }, { prop: 'contactName', label: '联系人' }, { prop: 'phone', label: '电话' }, { prop: 'status', label: '状态', width: 80 }]" :form-fields="[{ prop: 'supplierName', label: '供应商' }, { prop: 'contactName', label: '联系人' }, { prop: 'phone', label: '电话' }, { prop: 'address', label: '地址', span: 24 }, { prop: 'remark', label: '备注', span: 24 }, { prop: 'status', label: '启用', type: 'switch' }]" />
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="stockDialog" title="出入库" width="480px">
      <el-form label-width="90px">
        <el-form-item label="配件ID"><el-input-number v-model="stockForm.partId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="stockForm.type">
            <el-radio value="in">入库</el-radio>
            <el-radio value="out">出库</el-radio>
            <el-radio value="check">盘点</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="stockForm.type === 'check' ? '实际库存' : '数量'"><el-input-number v-model="stockForm.quantity" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="stockForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialog = false">取消</el-button>
        <el-button type="primary" @click="submitStock">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import CrudTable from '@/components/CrudTable.vue'

const tab = ref('part')
const warnings = ref<any[]>([])
const stockDialog = ref(false)
const stockForm = reactive<any>({ type: 'in', quantity: 1 })

const columns = [
  { prop: 'partName', label: '配件名称', minWidth: 160 },
  { prop: 'partCode', label: '编码', width: 110 },
  { prop: 'spec', label: '规格', width: 110 },
  { prop: 'purchasePrice', label: '成本', width: 90, money: true },
  { prop: 'salePrice', label: '售价', width: 90, money: true },
  { prop: 'stock', label: '库存', width: 80 },
  { prop: 'warnStock', label: '预警', width: 70 },
  { prop: 'unit', label: '单位', width: 60 },
  { prop: 'status', label: '状态', width: 70 }
]
const formFields = [
  { prop: 'partName', label: '配件名称' },
  { prop: 'partCode', label: '配件编码' },
  { prop: 'categoryId', label: '分类ID', type: 'number' },
  { prop: 'brand', label: '品牌' },
  { prop: 'spec', label: '规格型号' },
  { prop: 'unit', label: '单位' },
  { prop: 'purchasePrice', label: '成本价', type: 'number' },
  { prop: 'salePrice', label: '售价', type: 'number' },
  { prop: 'stock', label: '当前库存', type: 'number' },
  { prop: 'warnStock', label: '预警库存', type: 'number' },
  { prop: 'supplier', label: '供应商' },
  { prop: 'cover', label: '图片', type: 'image', span: 24 },
  { prop: 'status', label: '启用', type: 'switch' }
]
const searchFields = [{ prop: 'partNameLike', label: '配件名称' }]
const recordColumns = [
  { prop: 'partName', label: '配件' },
  { prop: 'recordType', label: '类型', width: 80 },
  { prop: 'quantity', label: '数量', width: 80 },
  { prop: 'beforeStock', label: '变动前', width: 80 },
  { prop: 'afterStock', label: '变动后', width: 80 },
  { prop: 'orderNo', label: '关联单号', width: 150 },
  { prop: 'operatorName', label: '操作人', width: 100 },
  { prop: 'createTime', label: '时间', width: 170 }
]

async function submitStock() {
  const { type, partId, quantity, remark } = stockForm
  const url = type === 'in' ? '/admin/inventory/stock-in' : type === 'out' ? '/admin/inventory/stock-out' : '/admin/inventory/stock-check'
  const params: any = type === 'check' ? { partId, realStock: quantity, remark } : { partId, quantity, remark }
  await request.post(url, null, { params })
  ElMessage.success('操作成功')
  stockDialog.value = false
  location.reload()
}

onMounted(async () => { warnings.value = await request.get('/admin/inventory/warnings') })
</script>
