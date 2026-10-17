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
        <crud-table url="/admin/part-category" title="分类" :columns="[{ prop: 'categoryName', label: '分类名称' }, { prop: 'parentId', label: '上级分类', lookup: partCategorySource }, { prop: 'sort', label: '排序', width: 80 }, { prop: 'status', label: '状态', width: 80 }]" :form-fields="[{ prop: 'categoryName', label: '分类名称' }, { prop: 'parentId', label: '上级分类', type: 'select', source: partCategorySource }, { prop: 'sort', label: '排序', type: 'number' }, { prop: 'status', label: '启用', type: 'switch' }]" />
      </el-tab-pane>
      <el-tab-pane label="供应商" name="supplier">
        <crud-table url="/admin/part-supplier" title="供应商" :columns="[{ prop: 'supplierName', label: '供应商' }, { prop: 'contactName', label: '联系人' }, { prop: 'phone', label: '电话' }, { prop: 'userId', label: '绑定登录账号', lookup: userSource, width: 130 }, { prop: 'status', label: '状态', width: 80 }]" :form-fields="[{ prop: 'supplierName', label: '供应商' }, { prop: 'contactName', label: '联系人' }, { prop: 'phone', label: '电话' }, { prop: 'userId', label: '绑定登录账号', type: 'select', source: userSource }, { prop: 'address', label: '地址', span: 24 }, { prop: 'remark', label: '备注', span: 24 }, { prop: 'status', label: '启用', type: 'switch' }]" />
      </el-tab-pane>
      <el-tab-pane label="供应商送货单" name="delivery">
        <crud-table
          ref="deliveryTableRef"
          url="/admin/supplier-delivery"
          title="送货单"
          :can-add="false"
          :columns="deliveryColumns"
          :form-fields="deliveryForm"
          :search-fields="[{ prop: 'deliveryNoLike', label: '送货单号' }, { prop: 'supplierNameLike', label: '供应商' }, { prop: 'storeId', label: '收货门店', lookup: storeSource }, { prop: 'status', label: '状态', options: ['待确认', '已入库', '已拒绝'].map((v) => ({ label: v, value: v })) }]"
        >
          <template #row-actions="{ row }">
            <el-button v-if="row.status === '待确认'" link type="success" @click="confirmDelivery(row, true)">确认入库</el-button>
            <el-button v-if="row.status === '待确认'" link type="danger" @click="confirmDelivery(row, false)">拒绝</el-button>
            <el-button link type="primary" @click="viewDelivery(row)">明细</el-button>
          </template>
        </crud-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="deliveryDialog" :title="'送货明细 ' + (currentDelivery?.deliveryNo || '')" width="720px">
      <el-descriptions :column="2" border style="margin-bottom:12px">
        <el-descriptions-item label="供应商">{{ currentDelivery?.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentDelivery?.status }}</el-descriptions-item>
        <el-descriptions-item label="预计到货">{{ currentDelivery?.expectDate }}</el-descriptions-item>
        <el-descriptions-item label="合计金额">¥{{ currentDelivery?.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentDelivery?.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="确认时间">{{ currentDelivery?.confirmTime }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="deliveryItems" size="small" stripe border>
        <el-table-column prop="partName" label="配件" min-width="140" />
        <el-table-column prop="spec" label="规格" width="120" />
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="100" />
        <el-table-column prop="amount" label="金额" width="110" />
        <el-table-column prop="remark" label="备注" min-width="120" />
      </el-table>
      <template #footer>
        <el-button @click="deliveryDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="stockDialog" title="出入库" width="480px">
      <el-form label-width="90px">
        <el-form-item label="配件">
          <el-select v-model="stockForm.partId" filterable placeholder="请选择配件" style="width:100%">
            <el-option v-for="p in parts" :key="p.id" :label="p.partName + (p.spec ? '（' + p.spec + '）' : '')" :value="p.id" />
          </el-select>
        </el-form-item>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import CrudTable from '@/components/CrudTable.vue'

const tab = ref('part')
const warnings = ref<any[]>([])
const stockDialog = ref(false)
const stockForm = reactive<any>({ type: 'in', quantity: 1 })

/** 下拉数据源（业务界面显示名称，不暴露 ID） */
const storeSource = { url: '/admin/store-info', label: 'storeName', value: 'id' }
const partCategorySource = { url: '/admin/part-category', label: 'categoryName', value: 'id' }
const userSource = { url: '/admin/sys-user', label: 'realName', value: 'id' }

const columns = [
  { prop: 'partName', label: '配件名称', minWidth: 160 },
  { prop: 'partCode', label: '编码', width: 110 },
  { prop: 'categoryId', label: '分类', lookup: partCategorySource, width: 120 },
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
  { prop: 'categoryId', label: '所属分类', type: 'select', source: partCategorySource },
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
const searchFields = [
  { prop: 'partNameLike', label: '配件名称' },
  { prop: 'categoryId', label: '所属分类', lookup: partCategorySource }
]
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

/* ---------- 供应商送货单 ---------- */
const deliveryTableRef = ref<any>()
const deliveryDialog = ref(false)
const currentDelivery = ref<any>(null)
const deliveryItems = ref<any[]>([])

const deliveryColumns = [
  { prop: 'deliveryNo', label: '送货单号', minWidth: 150 },
  { prop: 'supplierName', label: '供应商', minWidth: 140 },
  { prop: 'storeId', label: '收货门店', lookup: storeSource, width: 130 },
  { prop: 'itemCount', label: '条数', width: 70 },
  { prop: 'totalAmount', label: '金额', width: 110, money: true },
  { prop: 'expectDate', label: '预计到货', width: 120 },
  { prop: 'submitTime', label: '提交时间', width: 170 },
  { prop: 'status', label: '状态', width: 100, tag: true }
]
const deliveryForm = [
  { prop: 'storeId', label: '收货门店', type: 'select', source: storeSource },
  { prop: 'expectDate', label: '预计到货日期', type: 'date' },
  { prop: 'status', label: '状态', type: 'select', options: ['待确认', '已入库', '已拒绝'].map((v) => ({ label: v, value: v })) },
  { prop: 'confirmRemark', label: '确认备注', type: 'textarea', span: 24 },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24 }
]

async function confirmDelivery(row: any, pass: boolean) {
  const { value } = await ElMessageBox.prompt(pass ? '入库备注（可选）' : '拒绝原因', '送货单确认', { inputType: 'textarea' })
  await request.post(`/admin/supplier-delivery/${row.id}/confirm`, null, { params: { pass, remark: value || undefined } })
  ElMessage.success(pass ? '已确认入库' : '已拒绝')
  deliveryTableRef.value?.load()
}

async function viewDelivery(row: any) {
  currentDelivery.value = row
  const data: any = await request.get('/admin/supplier-delivery-item/page', { params: { page: 1, size: 100, deliveryId: row.id } })
  deliveryItems.value = data.records || []
  deliveryDialog.value = true
}

const parts = ref<any[]>([])
onMounted(async () => {
  warnings.value = await request.get('/admin/inventory/warnings')
  parts.value = await request.get('/admin/part-info/list')
})
</script>
