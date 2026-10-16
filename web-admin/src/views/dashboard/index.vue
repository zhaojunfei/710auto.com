<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col :span="4" v-for="c in cards" :key="c.label">
        <div class="card-block stat-card">
          <div class="stat-value" :style="{ color: c.color }">{{ c.value }}</div>
          <div class="text-muted">{{ c.label }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12">
        <div class="card-block">
          <h3>近 30 天经营概况</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="营收">{{ money(dashboard.revenue) }}</el-descriptions-item>
            <el-descriptions-item label="工单量">{{ dashboard.orderCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="客单价">{{ money(dashboard.avgOrderAmount) }}</el-descriptions-item>
            <el-descriptions-item label="客户留存率">{{ dashboard.retention || 0 }}%</el-descriptions-item>
            <el-descriptions-item label="服务项目数">{{ dashboard.serviceItemCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="平均评分">{{ overview.avgScore || 5 }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card-block">
          <h3>技师产值排行</h3>
          <el-table :data="techRank" stripe>
            <el-table-column type="index" label="#" width="60" />
            <el-table-column prop="id" label="技师ID" />
            <el-table-column prop="amount" label="产值">
              <template #default="{ row }">{{ money(row.amount) }}</template>
            </el-table-column>
          </el-table>
          <div v-if="!techRank.length" class="empty-tip">暂无数据</div>
        </div>
      </el-col>
    </el-row>

    <div class="card-block">
      <div class="toolbar">
        <h3 style="margin:0">经营日报（最近 30 天）</h3>
        <div style="flex:1"></div>
        <el-button @click="genDaily">生成今日日报</el-button>
        <el-button @click="loadDaily">刷新</el-button>
      </div>
      <el-table :data="daily" stripe border>
        <el-table-column prop="statDate" label="日期" width="120" />
        <el-table-column prop="uv" label="UV" width="80" />
        <el-table-column prop="pv" label="PV" width="80" />
        <el-table-column prop="appointmentCount" label="预约" width="80" />
        <el-table-column prop="orderCount" label="工单" width="80" />
        <el-table-column prop="revenue" label="营收">
          <template #default="{ row }">{{ money(row.revenue) }}</template>
        </el-table-column>
        <el-table-column prop="grossProfit" label="毛利">
          <template #default="{ row }">{{ money(row.grossProfit) }}</template>
        </el-table-column>
        <el-table-column prop="tireCount" label="存胎" width="80" />
        <el-table-column prop="memberCount" label="新增会员" width="100" />
        <el-table-column prop="rechargeAmount" label="储值充值">
          <template #default="{ row }">{{ money(row.rechargeAmount) }}</template>
        </el-table-column>
      </el-table>
      <div v-if="!daily.length" class="empty-tip">暂无日报数据，可点击「生成今日日报」</div>
    </div>

    <div class="card-block">
      <h3>快捷入口</h3>
      <el-space wrap>
        <el-button @click="$router.push('/workorder')">工单/预约</el-button>
        <el-button @click="$router.push('/service')">服务项目</el-button>
        <el-button @click="$router.push('/tire')">存胎寄存</el-button>
        <el-button @click="$router.push('/system/dict')">数据字典</el-button>
        <el-button @click="$router.push('/system/file')">文件管理</el-button>
        <el-button @click="$router.push('/system/storage')">对象存储配置</el-button>
        <el-button @click="$router.push('/system/menu')">菜单可视化</el-button>
      </el-space>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { statApi } from '@/api'
import request from '@/api/request'

const overview = ref<any>({})
const dashboard = ref<any>({})
const daily = ref<any[]>([])

const cards = computed(() => [
  { label: '今日预约', value: overview.value.appointmentCount ?? 0, color: '#1f6feb' },
  { label: '今日工单', value: overview.value.orderCount ?? 0, color: '#e6a23c' },
  { label: '今日营收', value: money(overview.value.revenue), color: '#67c23a' },
  { label: '待处理工单', value: overview.value.pendingCount ?? 0, color: '#f56c6c' },
  { label: '今日存胎', value: overview.value.tireCount ?? 0, color: '#909399' },
  { label: '新增会员', value: overview.value.memberCount ?? 0, color: '#1f6feb' }
])

const techRank = computed(() => {
  const map = dashboard.value.techRevenue || {}
  return Object.keys(map).map((id) => ({ id, amount: map[id] })).sort((a, b) => b.amount - a.amount)
})

function money(v: any) {
  return v === null || v === undefined ? '¥0.00' : '¥' + Number(v).toFixed(2)
}

onMounted(async () => {
  overview.value = await statApi.overview()
  dashboard.value = await statApi.dashboard(30)
  loadDaily()
})

async function loadDaily() {
  const data: any = await request.get('/admin/stat-daily/page', { params: { page: 1, size: 30 } })
  daily.value = data.records || []
}

async function genDaily() {
  await request.post('/admin/stat/generate')
  ElMessage.success('日报已生成')
  loadDaily()
}
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-value { font-size: 24px; font-weight: 700; }
</style>
