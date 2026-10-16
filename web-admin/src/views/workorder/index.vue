<template>
  <div class="page-container">
    <el-tabs v-model="tab">
      <!-- ============ 工单看板 ============ -->
      <el-tab-pane label="工单看板" name="kanban">
        <div class="card-block">
          <div class="toolbar">
            <el-button type="primary" @click="manualVisible = true"><el-icon><Plus /></el-icon>&nbsp;手动开单</el-button>
            <el-button @click="load"><el-icon><Refresh /></el-icon>&nbsp;刷新看板</el-button>
            <div style="flex:1"></div>
            <span class="text-muted">拖拽卡片换列由后端状态机校验；超时/加急卡片红色标记</span>
          </div>
          <div class="kanban" v-loading="loading">
            <div class="kanban-col" v-for="col in columns" :key="col">
              <h4><span>{{ col }}</span><el-tag size="small">{{ (board[col] || []).length }}</el-tag></h4>
              <div
                v-for="o in board[col]"
                :key="o.id"
                class="kanban-card"
                :class="{ urgent: o.isUrgent === 1 }"
                @click="openDetail(o)"
              >
                <div><b>{{ o.orderNo }}</b> <el-tag size="small" :type="o.isUrgent ? 'danger' : 'info'">{{ o.plateNo }}</el-tag></div>
                <div class="text-muted">{{ o.brandModel || '未填写车型' }} · {{ o.contactName || '' }}</div>
                <div class="text-muted">预计/创建：{{ o.createTime }}</div>
              </div>
              <div v-if="!(board[col] || []).length" class="text-muted" style="text-align:center;padding:12px">暂无</div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- ============ 预约管理 ============ -->
      <el-tab-pane label="预约管理" name="appointment">
        <crud-table
          ref="apptTableRef"
          url="/admin/appointments"
          title="预约单"
          :columns="apptColumns"
          :form-fields="apptForm"
          :search-fields="apptSearch"
          :default-form="{ status: '待确认', source: 'admin' }"
        >
          <template #actions>
            <el-button @click="capacityVisible = true">查询剩余产能</el-button>
          </template>
          <template #row-actions="{ row }">
            <el-button v-if="row.status === '待确认'" link type="primary" @click="confirmAppt(row)">确认</el-button>
            <el-button v-if="row.status !== '已取消' && row.status !== '已完成'" link type="warning" @click="receiveAppt(row)">到店接车</el-button>
            <el-button v-if="row.status !== '已取消' && row.status !== '已完成'" link type="danger" @click="cancelAppt(row)">取消</el-button>
          </template>
        </crud-table>
      </el-tab-pane>

      <!-- ============ 工单评价 ============ -->
      <el-tab-pane label="工单评价" name="evaluate">
        <crud-table
          url="/admin/work-order-evaluate"
          title="评价"
          :can-add="false"
          :columns="evalColumns"
          :form-fields="evalForm"
          :search-fields="[{ prop: 'workOrderId', label: '工单ID' }]"
        />
      </el-tab-pane>

      <!-- ============ 派工记录 ============ -->
      <el-tab-pane label="派工记录" name="dispatch">
        <crud-table
          url="/admin/dispatch-record"
          title="派工记录"
          :can-add="false"
          :can-edit="false"
          :can-delete="false"
          :columns="dispatchColumns"
          :search-fields="[{ prop: 'workOrderId', label: '工单ID' }]"
        />
      </el-tab-pane>

      <!-- ============ 退款单 ============ -->
      <el-tab-pane label="退款单" name="refund">
        <crud-table
          ref="refundTableRef"
          url="/admin/refund-record"
          title="退款单"
          :can-add="false"
          :columns="refundColumns"
          :form-fields="refundForm"
          :search-fields="[{ prop: 'refundNoLike', label: '退款单号' }, { prop: 'status', label: '状态', options: ['待审核', '已通过', '已拒绝', '已退款'].map((v) => ({ label: v, value: v })) }]"
        >
          <template #row-actions="{ row }">
            <el-button v-if="row.status === '待审核'" link type="success" @click="auditRefund(row, true)">通过</el-button>
            <el-button v-if="row.status === '待审核'" link type="danger" @click="auditRefund(row, false)">拒绝</el-button>
          </template>
        </crud-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 手动开单 -->
    <el-dialog v-model="manualVisible" title="手动录入线下工单" width="640px">
      <el-form :model="manualForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="车牌号"><el-input v-model="manualForm.plateNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="品牌车型"><el-input v-model="manualForm.brandModel" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系人"><el-input v-model="manualForm.contactName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话"><el-input v-model="manualForm.contactPhone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="进厂里程"><el-input-number v-model="manualForm.mileage" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="加急"><el-switch v-model="manualForm.isUrgent" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="故障描述"><el-input v-model="manualForm.faultDesc" type="textarea" :rows="3" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="manualVisible = false">取消</el-button>
        <el-button type="primary" @click="submitManual">提交</el-button>
      </template>
    </el-dialog>

    <!-- 工单详情 -->
    <el-drawer v-model="detailVisible" :title="'工单 ' + (current?.orderNo || '')" size="640px">
      <el-descriptions :column="2" border v-if="current">
        <el-descriptions-item label="状态"><el-tag>{{ current.status }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="车牌">{{ current.plateNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ current.contactName }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ current.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="工时费">¥{{ current.laborAmount }}</el-descriptions-item>
        <el-descriptions-item label="配件费">¥{{ current.partsAmount }}</el-descriptions-item>
        <el-descriptions-item label="总额">¥{{ current.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="实收">¥{{ current.paidAmount }}</el-descriptions-item>
      </el-descriptions>

      <div class="card-block" style="margin-top:16px">
        <h4>状态操作</h4>
        <el-space wrap>
          <el-button size="small" @click="doReceive">接车完成</el-button>
          <el-button size="small" type="warning" @click="dispatchVisible = true">派工</el-button>
          <el-button size="small" type="primary" @click="doStart">开工</el-button>
          <el-button size="small" @click="doCheck(true)">质检通过</el-button>
          <el-button size="small" @click="doCheck(false)">质检不通过</el-button>
          <el-button size="small" type="success" @click="settleVisible = true">结算</el-button>
          <el-button size="small" :type="current?.isUrgent ? 'info' : 'danger'" @click="toggleUrgent">
            {{ current?.isUrgent ? '取消加急' : '设为加急' }}
          </el-button>
        </el-space>
      </div>

      <div class="card-block">
        <h4>检查项 / 报价</h4>
        <el-table :data="checks" size="small" stripe>
          <el-table-column prop="checkName" label="项目" />
          <el-table-column prop="checkResult" label="结果" />
          <el-table-column prop="quotePrice" label="报价" />
          <el-table-column label="确认" width="80">
            <template #default="{ row }"><el-tag size="small" :type="row.customerConfirm ? 'success' : 'info'">{{ row.customerConfirm ? '已确认' : '未确认' }}</el-tag></template>
          </el-table-column>
        </el-table>
        <div class="toolbar" style="margin-top:8px">
          <el-button size="small" @click="addCheckRow">新增检查项</el-button>
          <el-button size="small" type="primary" @click="saveChecks">保存检查项</el-button>
        </div>
      </div>

      <div class="card-block">
        <h4>用料明细</h4>
        <el-table :data="items" size="small" stripe>
          <el-table-column prop="itemType" label="类型" width="70" />
          <el-table-column prop="itemName" label="名称" />
          <el-table-column prop="quantity" label="数量" width="70" />
          <el-table-column prop="amount" label="金额" width="90" />
        </el-table>
      </div>

      <div class="card-block">
        <h4>流转日志</h4>
        <el-timeline>
          <el-timeline-item v-for="l in logs" :key="l.id" :timestamp="l.createTime">
            {{ l.fromStatus ? l.fromStatus + ' → ' : '' }}{{ l.toStatus }} {{ l.remark }}
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-drawer>

    <!-- 派工 -->
    <el-dialog v-model="dispatchVisible" title="派工" width="420px">
      <el-select v-model="dispatchTechId" placeholder="选择技师" style="width:100%">
        <el-option v-for="t in technicians" :key="t.id" :label="t.techName + '（' + t.techType + '）'" :value="t.id" />
      </el-select>
      <template #footer>
        <el-button @click="dispatchVisible = false">取消</el-button>
        <el-button type="primary" @click="doDispatch">确定派工</el-button>
      </template>
    </el-dialog>

    <!-- 结算 -->
    <el-dialog v-model="settleVisible" title="结算离店" width="460px">
      <el-form label-width="100px">
        <el-form-item label="支付方式">
          <el-select v-model="settleForm.payType" style="width:100%">
            <el-option label="微信支付" value="微信" />
            <el-option label="储值余额" value="储值" />
            <el-option label="现金" value="现金" />
            <el-option label="混合支付" value="混合" />
          </el-select>
        </el-form-item>
        <el-form-item label="储值抵扣"><el-input-number v-model="settleForm.useBalance" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="积分抵工时"><el-input-number v-model="settleForm.usePoints" :min="0" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="settleVisible = false">取消</el-button>
        <el-button type="primary" @click="doSettle">确认结算</el-button>
      </template>
    </el-dialog>

    <!-- 剩余产能 -->
    <el-dialog v-model="capacityVisible" title="查询各门店剩余产能" width="420px">
      <el-form label-width="110px">
        <el-form-item label="门店ID"><el-input-number v-model="capacityForm.storeId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="日期"><el-input v-model="capacityForm.date" placeholder="yyyy-MM-dd，留空=今天" /></el-form-item>
      </el-form>
      <el-alert v-if="capacityResult !== null" type="success" :closable="false" :title="`剩余可预约产能：${capacityResult} 个`" />
      <template #footer>
        <el-button @click="capacityVisible = false">关闭</el-button>
        <el-button type="primary" @click="queryCapacity">查询</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { appointmentApi, crud, refundApi, workOrderApi } from '@/api'
import CrudTable from '@/components/CrudTable.vue'

const tab = ref('kanban')
const columns = ['待接车', '待派工', '施工中', '待质检', '待结算', '已离店', '已评价']
const board = ref<any>({})
const loading = ref(false)

const manualVisible = ref(false)
const detailVisible = ref(false)
const dispatchVisible = ref(false)
const settleVisible = ref(false)
const current = ref<any>(null)
const checks = ref<any[]>([])
const items = ref<any[]>([])
const logs = ref<any[]>([])
const technicians = ref<any[]>([])
const dispatchTechId = ref<number>()
const manualForm = reactive<any>({ isUrgent: 0 })
const settleForm = reactive<any>({ payType: '微信', useBalance: 0, usePoints: 0 })

const apptTableRef = ref<any>()
const refundTableRef = ref<any>()
const capacityVisible = ref(false)
const capacityResult = ref<number | null>(null)
const capacityForm = reactive<any>({ storeId: undefined, date: '' })

/* ---------- 看板 ---------- */
async function load() {
  loading.value = true
  try { board.value = await workOrderApi.kanban() } finally { loading.value = false }
}

async function openDetail(o: any) {
  current.value = o
  detailVisible.value = true
  checks.value = await workOrderApi.checks(o.id)
  items.value = await workOrderApi.items(o.id)
  logs.value = await workOrderApi.logs(o.id)
}

function addCheckRow() {
  checks.value.push({ checkName: '', checkResult: '', quotePrice: 0, customerConfirm: 0 })
}
async function saveChecks() {
  await workOrderApi.addChecks(current.value.id, checks.value)
  ElMessage.success('检查项已保存')
}
async function doReceive() { await workOrderApi.receive(current.value.id); afterAction('接车完成') }
async function doStart() { await workOrderApi.start(current.value.id); afterAction('已开工') }
async function doDispatch() {
  if (!dispatchTechId.value) { ElMessage.warning('请选择技师'); return }
  await workOrderApi.dispatch(current.value.id, dispatchTechId.value)
  dispatchVisible.value = false
  afterAction('派工成功')
}
async function doCheck(pass: boolean) { await workOrderApi.qualityCheck(current.value.id, pass); afterAction(pass ? '质检通过' : '已退回施工') }
async function doSettle() {
  await workOrderApi.settle(current.value.id, settleForm.payType, settleForm.useBalance, settleForm.usePoints)
  settleVisible.value = false
  afterAction('结算完成')
}
async function toggleUrgent() {
  await workOrderApi.urgent(current.value.id, current.value.isUrgent !== 1)
  afterAction('已更新')
}
async function submitManual() {
  await workOrderApi.manual(manualForm)
  manualVisible.value = false
  ElMessage.success('开单成功')
  load()
}
async function afterAction(msg: string) {
  ElMessage.success(msg)
  detailVisible.value = false
  load()
}

/* ---------- 预约管理 ---------- */
const apptColumns = [
  { prop: 'appointmentNo', label: '预约单号', minWidth: 150 },
  { prop: 'plateNo', label: '车牌', width: 120 },
  { prop: 'itemName', label: '服务项目', minWidth: 130 },
  { prop: 'contactName', label: '联系人', width: 100 },
  { prop: 'contactPhone', label: '电话', width: 130 },
  { prop: 'appointTime', label: '到店时间', width: 170 },
  { prop: 'source', label: '来源', width: 90 },
  { prop: 'status', label: '状态', width: 100, tag: true }
]
const apptForm = [
  { prop: 'plateNo', label: '车牌号' },
  { prop: 'brandModel', label: '品牌车型' },
  { prop: 'itemName', label: '服务项目' },
  { prop: 'contactName', label: '联系人' },
  { prop: 'contactPhone', label: '联系电话' },
  { prop: 'appointTime', label: '预约到店时间', span: 24 },
  { prop: 'remark', label: '客户备注', type: 'textarea', span: 24 },
  { prop: 'status', label: '状态', type: 'select', options: ['待确认', '已确认', '待接车', '已完成', '已取消'].map((v) => ({ label: v, value: v })) }
]
const apptSearch = [
  { prop: 'appointmentNoLike', label: '预约单号' },
  { prop: 'plateNoLike', label: '车牌' },
  { prop: 'contactNameLike', label: '联系人' },
  { prop: 'status', label: '状态', options: ['待确认', '已确认', '待接车', '已完成', '已取消'].map((v) => ({ label: v, value: v })) }
]

async function confirmAppt(row: any) {
  await appointmentApi.confirm(row.id)
  ElMessage.success('预约已确认')
  apptTableRef.value?.load()
}
async function cancelAppt(row: any) {
  const { value } = await ElMessageBox.prompt('请输入取消原因', '取消预约', { inputValue: '客户取消', inputType: 'textarea' })
  await appointmentApi.cancel(row.id, value || undefined)
  ElMessage.success('预约已取消')
  apptTableRef.value?.load()
}
async function receiveAppt(row: any) {
  await ElMessageBox.confirm('确认已到店接车并生成工单？', '到店接车', { type: 'warning' })
  await appointmentApi.receive(row.id, {
    plateNo: row.plateNo,
    brandModel: row.brandModel,
    contactName: row.contactName,
    contactPhone: row.contactPhone
  })
  ElMessage.success('已接车并生成工单')
  apptTableRef.value?.load()
  load()
}
async function queryCapacity() {
  capacityResult.value = await appointmentApi.remainCapacity({ storeId: capacityForm.storeId, date: capacityForm.date || undefined })
}

/* ---------- 工单评价 ---------- */
const evalColumns = [
  { prop: 'workOrderId', label: '工单ID', width: 100 },
  { prop: 'score', label: '综合', width: 70 },
  { prop: 'serviceScore', label: '服务', width: 70 },
  { prop: 'techScore', label: '技师', width: 70 },
  { prop: 'content', label: '评价内容', minWidth: 220 },
  { prop: 'reply', label: '门店回复', minWidth: 180 },
  { prop: 'status', label: '显示', width: 80, tag: true }
]
const evalForm = [{ prop: 'reply', label: '门店回复', type: 'textarea', span: 24 }, { prop: 'status', label: '显示', type: 'switch' }]

/* ---------- 派工记录 ---------- */
const dispatchColumns = [
  { prop: 'workOrderId', label: '工单ID', width: 100 },
  { prop: 'technicianName', label: '技师', width: 120 },
  { prop: 'dispatchType', label: '类型', width: 100 },
  { prop: 'dispatchTime', label: '操作时间', width: 170 },
  { prop: 'status', label: '状态', width: 100 },
  { prop: 'remark', label: '备注', minWidth: 160 }
]

/* ---------- 退款单 ---------- */
const refundColumns = [
  { prop: 'refundNo', label: '退款单号', minWidth: 150 },
  { prop: 'orderNo', label: '原订单号', width: 150 },
  { prop: 'refundAmount', label: '退款金额', width: 110, money: true },
  { prop: 'reason', label: '原因', minWidth: 160 },
  { prop: 'status', label: '状态', width: 100 },
  { prop: 'auditTime', label: '审核时间', width: 170 },
  { prop: 'auditRemark', label: '审核备注', minWidth: 140 }
]
const refundForm = [
  { prop: 'status', label: '状态', type: 'select', options: ['待审核', '已通过', '已拒绝', '已退款'].map((v) => ({ label: v, value: v })) },
  { prop: 'auditRemark', label: '审核备注', type: 'textarea', span: 24 }
]

async function auditRefund(row: any, pass: boolean) {
  const { value } = await ElMessageBox.prompt(pass ? '通过备注（可选）' : '拒绝原因', '退款审核', { inputType: 'textarea' })
  await refundApi.audit(row.id, pass, value || undefined)
  ElMessage.success(pass ? '已通过' : '已拒绝')
  refundTableRef.value?.load()
}

onMounted(async () => {
  load()
  technicians.value = await crud.list('/admin/technician')
})
</script>

<style scoped>
.kanban { display: flex; gap: 12px; overflow-x: auto; padding-bottom: 8px; }
.kanban-col { flex: 0 0 220px; background: #f5f7fa; border-radius: 6px; padding: 8px; }
.kanban-col h4 { display: flex; justify-content: space-between; align-items: center; margin: 4px 4px 8px; }
.kanban-card { background: #fff; border-radius: 6px; padding: 10px; margin-bottom: 8px; cursor: pointer; box-shadow: 0 1px 3px rgba(0, 0, 0, .08); }
.kanban-card:hover { box-shadow: 0 2px 8px rgba(0, 0, 0, .16); }
.kanban-card.urgent { border-left: 3px solid #f56c6c; }
</style>
