<template>
  <div>
    <div class="topbar">在线预约</div>
    <div class="page">
      <div class="card">
        <label>服务项目</label>
        <select v-model="serviceId">
          <option :value="null">请选择</option>
          <option v-for="s in services" :key="s.id" :value="s.id">{{ s.itemName }}（¥{{ s.price }}）</option>
        </select>

        <label>到店时间</label>
        <div class="row" style="gap:8px">
          <input type="date" v-model="date" />
          <input type="time" v-model="time" />
        </div>

        <label>手机号</label>
        <input v-model="contactPhone" placeholder="用于接收预约与施工通知" />
        <label>车牌号</label>
        <input v-model="plateNo" placeholder="请输入车牌号，如 京A12345" maxlength="8" style="text-transform:uppercase" />
        <label>品牌 / 车型</label>
        <input v-model="brandModel" placeholder="如 大众 迈腾" />
        <label>备注</label>
        <textarea v-model="remark" rows="3" placeholder="选填"></textarea>
      </div>
      <button class="btn" :disabled="submitting" @click="submit">{{ submitting ? '提交中...' : '提交预约' }}</button>
      <div class="muted" style="text-align:center;margin-top:10px">
        与小程序共用会员与车辆数据（UnionID 打通）
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { memberApi, publicApi } from '@/api'

const route = useRoute()
const router = useRouter()
const services = ref<any[]>([])
const serviceId = ref<number | null>(null)
const today = new Date().toISOString().slice(0, 10)
const date = ref(today)
const time = ref('09:00')
const contactPhone = ref('')
const brandModel = ref('')
const remark = ref('')
const submitting = ref(false)

/* ---------- 车牌号（直接输入完整车牌，提交前宽松校验） ---------- */
const plateNo = ref('')
function plateError(): string {
  const v = (plateNo.value || '').toUpperCase().replace(/\s/g, '')
  if (!v) return '请填写车牌号'
  if (!/^[\u4e00-\u9fa5][A-Z][A-Z0-9]{4,6}$/.test(v)) return '车牌号格式不正确，如 京A12345 / 粤BD12345'
  return ''
}

function ensureLogin(): Promise<void> {
  if (localStorage.getItem('auto710_h5_token')) return Promise.resolve()
  // H5 在微信内可经公众号网页授权拿 code；此处联调用 unionid 占位登录
  const unionid = localStorage.getItem('auto710_unionid') || 'h5-' + Date.now()
  localStorage.setItem('auto710_unionid', unionid)
  return memberApi.wxLogin({ unionid, source: 'h5', nickname: 'H5用户' }).then((data: any) => {
    localStorage.setItem('auto710_h5_token', data.token)
  })
}

async function submit() {
  if (!serviceId.value) { alert('请选择服务项目'); return }
  if (!contactPhone.value) { alert('请填写手机号'); return }
  if (!/^1\d{10}$/.test(contactPhone.value)) { alert('请填写正确的手机号'); return }
  const pe = plateError()
  if (pe) { alert(pe); return }
  const plate = (plateNo.value || '').toUpperCase().replace(/\s/g, '')
  submitting.value = true
  try {
    await ensureLogin()
    await memberApi.createAppointment({
      itemId: serviceId.value,
      itemName: services.value.find((s) => s.id === serviceId.value)?.itemName,
      appointTime: date.value + ' ' + time.value + ':00',
      contactPhone: contactPhone.value,
      plateNo: plate,
      brandModel: brandModel.value,
      remark: remark.value,
      source: 'h5'
    })
    alert('预约提交成功')
    router.push('/orders')
  } catch (e: any) {
    alert(e.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try {
    const res: any = await publicApi.items({ page: 1, size: 100 })
    services.value = res.records || []
    if (route.query.serviceId) serviceId.value = Number(route.query.serviceId)
  } catch {}
})
</script>
