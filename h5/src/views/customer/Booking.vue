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
        <div class="row" style="gap:8px">
          <select v-model="plateProvince" style="max-width:90px">
            <option value="">简称</option>
            <option v-for="p in PROVINCES" :key="p" :value="p">{{ p }}</option>
          </select>
          <input
            v-model="plateRest"
            :placeholder="plateType === 'green' ? '如 D123456（7位）' : '如 A12345（6位）'"
            :maxlength="plateType === 'green' ? 7 : 6"
            style="text-transform:uppercase"
          />
        </div>
        <div class="row" style="gap:16px;margin-top:6px">
          <label class="muted" style="margin:0"><input type="radio" value="blue" v-model="plateType" /> 蓝牌（油车）</label>
          <label class="muted" style="margin:0"><input type="radio" value="green" v-model="plateType" /> 绿牌（新能源）</label>
        </div>
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
import { computed, onMounted, ref } from 'vue'
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

/* ---------- 车牌号（行政区简称 + 油/电类型 + 号码，按类型校验） ---------- */
const PROVINCES = [
  '京', '津', '冀', '晋', '蒙', '辽', '吉', '黑', '沪', '苏', '浙', '皖', '闽', '赣', '鲁',
  '豫', '鄂', '湘', '粤', '桂', '琼', '渝', '川', '贵', '云', '藏', '陕', '甘', '青', '宁', '新'
]
const plateProvince = ref('')
const plateRest = ref('')
const plateType = ref<'blue' | 'green'>('blue')
const plateNo = computed(() => {
  const r = (plateRest.value || '').toUpperCase().replace(/\s/g, '')
  return plateProvince.value && r ? plateProvince.value + r : ''
})
/** 蓝牌 7 位（简称+字母+5位）/ 绿牌 8 位（简称+字母+6位），非法格式拦截 */
function plateError(): string {
  const p = plateProvince.value
  const r = (plateRest.value || '').toUpperCase().replace(/\s/g, '')
  if (!p || !r) return '请完整填写车牌号（省份简称 + 号码）'
  const len = plateType.value === 'green' ? 7 : 6
  if (!/^[A-Z][A-Z0-9]*$/.test(r)) return '车牌号码须以字母开头，仅含字母与数字'
  if (r.length !== len) return plateType.value === 'green' ? '新能源绿牌共 8 位（简称+字母+6位）' : '蓝牌共 7 位（简称+字母+5位）'
  if (/[IO]/.test(r)) return '车牌号码不含字母 I、O'
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
  submitting.value = true
  try {
    await ensureLogin()
    await memberApi.createAppointment({
      itemId: serviceId.value,
      itemName: services.value.find((s) => s.id === serviceId.value)?.itemName,
      appointTime: date.value + ' ' + time.value + ':00',
      contactPhone: contactPhone.value,
      plateNo: plateNo.value,
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
