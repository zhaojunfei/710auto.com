<template>
  <div class="page-container">
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
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { crud, workOrderApi } from '@/api'

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

onMounted(async () => {
  load()
  technicians.value = await crud.list('/admin/technician')
})
</script>
