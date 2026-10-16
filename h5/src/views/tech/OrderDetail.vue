<template>
  <div>
    <div class="topbar"><span class="back" @click="$router.back()">‹</span>工单详情</div>
    <div class="page" v-if="order">
      <div class="card">
        <div class="between">
          <b>{{ order.orderNo || ('工单 #' + order.id) }}</b>
          <span class="tag">{{ order.status }}</span>
        </div>
        <div class="muted" style="margin-top:6px">车牌：{{ order.plateNo || '-' }}</div>
        <div class="muted">车型：{{ order.brandModel || '-' }} · 里程 {{ order.mileage || '-' }}</div>
        <div class="muted">故障描述：{{ order.faultDesc || '-' }}</div>
      </div>

      <!-- 接车开单：检查项 -->
      <div class="card">
        <div class="card-title">接车检查项（{{ checks.length }}）</div>
        <div v-for="(c, i) in checks" :key="i" class="between muted" style="padding:6px 0">
          <span>{{ c.checkItem || c.itemName }}</span>
          <span>{{ c.result || '' }}</span>
        </div>
        <div class="empty" v-if="!checks.length" style="padding:12px 0">暂无检查项</div>
        <div v-if="showCheckForm" style="margin-top:8px">
          <input v-model="checkForm.checkItem" placeholder="检查项名称，如 刹车片厚度" />
          <input v-model="checkForm.result" placeholder="检查结果 / 建议" style="margin-top:8px" />
          <button class="btn sm" style="margin-top:8px" @click="addCheck">添加检查项</button>
        </div>
        <button v-else class="btn ghost sm" style="margin-top:8px" @click="showCheckForm = true">+ 录入检查项</button>
      </div>

      <!-- 用料登记 -->
      <div class="card">
        <div class="card-title">用料明细（{{ items.length }}）</div>
        <div v-for="it in items" :key="it.id" class="between muted" style="padding:6px 0">
          <span>{{ it.itemName }} × {{ it.quantity || 1 }}</span>
          <span>¥{{ it.amount || 0 }}</span>
        </div>
        <div class="empty" v-if="!items.length" style="padding:12px 0">暂无用料</div>
        <div v-if="showItemForm" style="margin-top:8px">
          <input v-model="itemForm.itemName" placeholder="物料 / 项目名称" />
          <div class="row" style="gap:8px;margin-top:8px">
            <input v-model.number="itemForm.quantity" type="number" placeholder="数量" />
            <input v-model.number="itemForm.price" type="number" placeholder="单价" />
          </div>
          <button class="btn sm" style="margin-top:8px" @click="addItem">登记用料（自动出库）</button>
        </div>
        <button v-else class="btn ghost sm" style="margin-top:8px" @click="showItemForm = true">+ 用料登记</button>
      </div>

      <!-- 施工日志 -->
      <div class="card">
        <div class="card-title">施工进度日志</div>
        <div v-for="l in logs" :key="l.id" class="muted" style="padding:4px 0">
          {{ l.createTime }} · {{ l.remark || (l.fromStatus + ' → ' + l.toStatus) }}
        </div>
        <div class="empty" v-if="!logs.length" style="padding:12px 0">暂无日志</div>
      </div>

      <div class="row" style="gap:8px;flex-wrap:wrap">
        <button class="btn" style="flex:1;min-width:120px" @click="start">一键开工</button>
        <button class="btn" style="flex:1;min-width:120px" @click="finish">完工提交</button>
        <button class="btn ghost" style="flex:1;min-width:120px" @click="abnormal">异常上报</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { techApi } from '@/api'

const route = useRoute()
const id = Number(route.params.id)
const order = ref<any>(null)
const checks = ref<any[]>([])
const items = ref<any[]>([])
const logs = ref<any[]>([])
const showCheckForm = ref(false)
const showItemForm = ref(false)
const checkForm = ref<any>({ checkItem: '', result: '' })
const itemForm = ref<any>({ itemName: '', quantity: 1, price: 0 })

async function load() {
  try {
    const data: any = await techApi.order(id)
    order.value = data.order
    checks.value = data.checks || []
    items.value = data.items || []
    logs.value = data.logs || []
  } catch (e: any) { alert(e.message || '加载失败') }
}

async function addCheck() {
  if (!checkForm.value.checkItem) { alert('请填写检查项'); return }
  await techApi.addChecks(id, [checkForm.value])
  checkForm.value = { checkItem: '', result: '' }
  showCheckForm.value = false
  load()
}
async function addItem() {
  if (!itemForm.value.itemName) { alert('请填写物料名称'); return }
  const amount = Number(itemForm.value.quantity) * Number(itemForm.value.price)
  await techApi.addItem(id, { ...itemForm.value, amount })
  itemForm.value = { itemName: '', quantity: 1, price: 0 }
  showItemForm.value = false
  load()
}
async function start() { await techApi.start(id); alert('已开工'); load() }
async function finish() { await techApi.finish(id); alert('已完工，流转质检/结算'); load() }
async function abnormal() {
  const reason = prompt('请输入异常原因（如缺件、客户要求暂停）')
  if (reason) { await techApi.abnormal(id, reason); alert('已上报'); load() }
}

onMounted(load)
</script>
