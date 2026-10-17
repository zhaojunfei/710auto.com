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
          :search-fields="[{ prop: 'workOrderId', label: '工单', lookup: orderSource }]"
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
          :search-fields="[{ prop: 'workOrderId', label: '工单', lookup: orderSource }]"
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
          :search-fields="[{ prop: 'refundNoLike', label: '退款单号' }, { prop: 'status', label: '状态', dict: 'refund_status' }]"
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
          <el-col :span="12"><el-form-item label="车牌号"><plate-input ref="manualPlateRef" v-model="manualForm.plateNo" required /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="品牌车型"><el-input v-model="manualForm.brandModel" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系人"><el-input v-model="manualForm.contactName" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="manualForm.contactPhone" placeholder="输入手机号自动匹配会员" @blur="onPhoneBlur" />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="memberTip">
            <el-alert :type="manualMember ? 'success' : 'info'" :closable="false" show-icon :title="memberTip" style="margin-bottom: 12px" />
          </el-col>
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
        <h4>状态操作 <el-tag size="small" type="info">{{ current?.status }}</el-tag></h4>
        <el-space wrap>
          <el-button v-if="canReceive" size="small" @click="doReceive">接车完成</el-button>
          <el-button v-if="canDispatch" size="small" type="warning" @click="dispatchVisible = true">派工</el-button>
          <el-button v-if="canStart" size="small" type="primary" @click="doStart">开工</el-button>
          <el-button v-if="canFinish" size="small" type="primary" @click="doFinish">完工提交</el-button>
          <el-button v-if="canCheck" size="small" @click="doCheck(true)">质检通过</el-button>
          <el-button v-if="canCheck" size="small" @click="doCheck(false)">质检不通过</el-button>
          <el-button v-if="canSettle" size="small" type="success" @click="settleVisible = true">结算</el-button>
          <el-button size="small" :type="current?.isUrgent ? 'info' : 'danger'" @click="toggleUrgent">
            {{ current?.isUrgent ? '取消加急' : '设为加急' }}
          </el-button>
          <span v-if="!canReceive && !canDispatch && !canStart && !canFinish && !canCheck && !canSettle" class="text-muted">当前状态无可用操作</span>
        </el-space>
      </div>

      <div class="card-block">
        <h4>检查项 / 报价 <el-tag v-if="!checksEditable" size="small" type="warning">已锁定</el-tag></h4>
        <el-alert
          v-if="!checksEditable"
          type="warning"
          :closable="false"
          show-icon
          title="工单已进入施工环节，检查项不可修改（仅「待接车 / 待派工」阶段可勾选维护）"
          style="margin-bottom: 8px"
        />
        <el-table :data="checks" size="small" stripe>
          <el-table-column v-if="checksEditable" label="操作" width="56">
            <template #default="{ $index }"><el-button link type="danger" @click="checks.splice($index, 1)">删除</el-button></template>
          </el-table-column>
          <el-table-column label="项目" min-width="160">
            <template #default="{ row }">
              <el-select v-if="checksEditable" v-model="row.checkName" placeholder="选择检查项" filterable allow-create style="width: 100%">
                <el-option v-for="o in checkItems" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
              <span v-else>{{ row.checkName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="结果" min-width="140">
            <template #default="{ row }">
              <el-input v-if="checksEditable" v-model="row.checkResult" placeholder="检查结果" />
              <span v-else>{{ row.checkResult }}</span>
            </template>
          </el-table-column>
          <el-table-column label="报价" width="130">
            <template #default="{ row }">
              <el-input-number v-if="checksEditable" v-model="row.quotePrice" :min="0" :controls="false" style="width: 100%" />
              <span v-else>¥{{ Number(row.quotePrice || 0).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="确认" width="80">
            <template #default="{ row }"><el-tag size="small" :type="row.customerConfirm ? 'success' : 'info'">{{ row.customerConfirm ? '已确认' : '未确认' }}</el-tag></template>
          </el-table-column>
        </el-table>
        <div class="toolbar" style="margin-top:8px" v-if="checksEditable">
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
            <el-option v-for="o in payTypes" :key="o.value" :label="o.label" :value="o.value" />
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
        <el-form-item label="门店">
          <el-select v-model="capacityForm.storeId" placeholder="请选择门店" clearable style="width:100%">
            <el-option v-for="s in stores" :key="s.id" :label="s.storeName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="capacityForm.date" type="date" value-format="yyyy-MM-dd" placeholder="留空=今天" style="width:100%" />
        </el-form-item>
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { appointmentApi, crud, memberLookupApi, refundApi, workOrderApi } from '@/api'
import CrudTable from '@/components/CrudTable.vue'
import PlateInput from '@/components/PlateInput.vue'

const tab = ref('kanban')
/** 工单看板列（来自数据字典 work_order_status，维护字典即生效；兜底内置顺序） */
const columns = ref<string[]>(['待接车', '待派工', '施工中', '待质检', '待结算', '已离店', '已评价'])
const board = ref<any>({})
const loading = ref(false)

/** 工单下拉（业务界面显示工单号，不暴露工单ID） */
const orderSource = { url: '/admin/work-order', label: 'orderNo', value: 'id' }

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

/* ---------- 手机号即会员 ---------- */
const manualMember = ref<any>(null)
const memberTip = ref('')
const manualPlateRef = ref<any>()

/* ---------- 字典下拉（统一由数据字典维护） ---------- */
const payTypes = ref<any[]>([])
const checkItems = ref<any[]>([])
const stores = ref<any[]>([])

/** 工单状态 → 可用操作（与后端状态机严格一致） */
const canReceive = computed(() => current.value?.status === '待接车')
const canDispatch = computed(() => ['待接车', '待派工'].includes(current.value?.status))
const canStart = computed(() => current.value?.status === '待派工')
const canFinish = computed(() => current.value?.status === '施工中')
const canCheck = computed(() => current.value?.status === '待质检')
const canSettle = computed(() => ['待结算', '待质检'].includes(current.value?.status))
/** 检查项仅在「待接车 / 待派工」阶段可维护，进入施工中后锁定 */
const checksEditable = computed(() => ['待接车', '待派工'].includes(current.value?.status))

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
async function doFinish() {
  await ElMessageBox.confirm('确认施工完成并提交质检？提交后工单将进入「待质检」。', '完工提交', { type: 'warning' })
  await workOrderApi.finish(current.value.id)
  afterAction('已提交质检')
}
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
  if (!manualPlateRef.value?.validate()) return
  if (manualForm.contactPhone) {
    // 提交前确保会员已建档（手机号即会员）
    await memberLookupApi.ensure({ phone: manualForm.contactPhone, realName: manualForm.contactName })
  }
  await workOrderApi.manual(manualForm)
  manualVisible.value = false
  ElMessage.success('开单成功')
  load()
}
/** 手机号失焦：自动匹配会员，不存在则提示将自动创建 */
async function onPhoneBlur() {
  const phone = (manualForm.contactPhone || '').trim()
  manualMember.value = null
  if (/^1\d{10}$/.test(phone)) {
    const m: any = await memberLookupApi.find(phone)
    manualMember.value = m || null
    memberTip.value = m
      ? `已匹配会员：${m.realName || m.nickname}（${m.phone}）　积分 ${m.points ?? 0}　储值 ¥${Number(m.balance || 0).toFixed(2)}`
      : `手机号 ${phone} 暂无会员，提交时将自动创建（用户名=手机号，初始密码=手机号后6位，首次登录需改密）`
  } else if (phone) {
    memberTip.value = ''
  } else {
    memberTip.value = ''
  }
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
  { prop: 'status', label: '状态', dict: 'appointment_status', width: 100, tag: true }
]
const apptForm = [
  { prop: 'plateNo', label: '车牌号', type: 'plate', required: true, span: 24  },
  { prop: 'brandModel', label: '品牌车型' },
  { prop: 'itemName', label: '服务项目' },
  { prop: 'contactName', label: '联系人' },
  { prop: 'contactPhone', label: '联系电话' },
  { prop: 'appointTime', label: '预约到店时间', type: 'datetime', span: 24 },
  { prop: 'remark', label: '客户备注', type: 'textarea', span: 24 },
  { prop: 'status', label: '状态', type: 'select', dict: 'appointment_status' }
]
const apptSearch = [
  { prop: 'appointmentNoLike', label: '预约单号' },
  { prop: 'plateNoLike', label: '车牌' },
  { prop: 'contactNameLike', label: '联系人' },
  { prop: 'status', label: '状态', dict: 'appointment_status' }
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
  { prop: 'workOrderId', label: '工单', lookup: orderSource, width: 150 },
  { prop: 'score', label: '综合', width: 70 },
  { prop: 'serviceScore', label: '服务', width: 70 },
  { prop: 'techScore', label: '技师', width: 70 },
  { prop: 'content', label: '评价内容', minWidth: 220 },
  { prop: 'reply', label: '门店回复', minWidth: 180 },
  { prop: 'status', label: '显示', dict: 'show_status', width: 80, tag: true }
]
const evalForm = [{ prop: 'reply', label: '门店回复', type: 'textarea', span: 24 }, { prop: 'status', label: '显示', type: 'switch', dict: 'show_status' }]

/* ---------- 派工记录 ---------- */
const dispatchColumns = [
  { prop: 'workOrderId', label: '工单', lookup: orderSource, width: 150 },
  { prop: 'technicianName', label: '技师', width: 120 },
  { prop: 'dispatchType', label: '类型', width: 100 },
  { prop: 'dispatchTime', label: '操作时间', width: 170 },
  { prop: 'status', label: '状态', dict: 'dispatch_status', width: 100, tag: true },
  { prop: 'remark', label: '备注', minWidth: 160 }
]

/* ---------- 退款单 ---------- */
const refundColumns = [
  { prop: 'refundNo', label: '退款单号', minWidth: 150 },
  { prop: 'orderNo', label: '原订单号', width: 150 },
  { prop: 'refundAmount', label: '退款金额', width: 110, money: true },
  { prop: 'reason', label: '原因', minWidth: 160 },
  { prop: 'status', label: '状态', dict: 'refund_status', width: 100, tag: true },
  { prop: 'auditTime', label: '审核时间', width: 170 },
  { prop: 'auditRemark', label: '审核备注', minWidth: 140 }
]
const refundForm = [
  { prop: 'status', label: '状态', type: 'select', dict: 'refund_status' },
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
  stores.value = await crud.list('/admin/store-info')
  payTypes.value = await dictOptions('pay_type')
  checkItems.value = await dictOptions('check_item')
  const ws = await dictOptions('work_order_status')
  if (ws.length) columns.value = ws.map((o: any) => o.value)
})

/** 读取数据字典（下拉统一字典化，维护后刷新页面即生效） */
async function dictOptions(type: string) {
  const rows: any = await crud.list('/admin/sys-dict-data', { dictType: type })
  const arr: any[] = Array.isArray(rows) ? rows : rows?.records || []
  return arr
    .filter((r) => r.status === undefined || r.status === 1)
    .sort((a, b) => (a.dictSort ?? 0) - (b.dictSort ?? 0))
    .map((r) => ({ label: r.dictLabel, value: r.dictValue }))
}
</script>

<style scoped>
.kanban { display: flex; gap: 12px; overflow-x: auto; padding-bottom: 8px; }
.kanban-col { flex: 0 0 220px; background: #f5f7fa; border-radius: 6px; padding: 8px; }
.kanban-col h4 { display: flex; justify-content: space-between; align-items: center; margin: 4px 4px 8px; }
.kanban-card { background: #fff; border-radius: 6px; padding: 10px; margin-bottom: 8px; cursor: pointer; box-shadow: 0 1px 3px rgba(0, 0, 0, .08); }
.kanban-card:hover { box-shadow: 0 2px 8px rgba(0, 0, 0, .16); }
.kanban-card.urgent { border-left: 3px solid #f56c6c; }
</style>
