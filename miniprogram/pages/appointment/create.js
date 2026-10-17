const request = require('../../utils/request')
const auth = require('../../utils/auth')
const { t, pick, getLocale } = require('../../utils/i18n')
const { formatMoney } = require('../../utils/util')

Page({
  data: {
    serviceId: null,
    services: [],
    serviceIndex: -1,
    date: '',
    time: '09:00',
    plateNo: '',
    brand: '',
    model: '',
    mileage: '',
    remark: '',
    submitting: false,
    T: {}
  },

  onLoad(options) {
    const today = new Date()
    this.setData({
      T: {
        appointment: t('appointment'),
        selectService: t('selectService'),
        selectTime: t('selectTime'),
        vehicleInfo: t('vehicleInfo'),
        plateNo: t('plateNo'),
        brand: t('brand'),
        model: t('model'),
        mileage: t('mileage'),
        submitAppointment: t('submitAppointment')
      },
      date: today.toISOString().slice(0, 10),
      serviceId: options.serviceId ? Number(options.serviceId) : null
    })
    wx.setNavigationBarTitle({ title: getLocale() === 'en-US' ? 'Book Now' : '在线预约' })
    this.loadServices()
  },

  loadServices() {
    request.get('/miniapp/service-items', { page: 1, size: 100 }, false).then((res) => {
      const services = (res.records || []).map((it) => ({
        id: it.id,
        name: pick(it, 'itemName', 'itemNameEn'),
        price: formatMoney(it.price),
        duration: it.duration
      }))
      let serviceIndex = -1
      if (this.data.serviceId) {
        serviceIndex = services.findIndex((s) => s.id === this.data.serviceId)
      }
      this.setData({ services, serviceIndex })
    }).catch(() => {})
  },

  onServiceChange(e) {
    this.setData({ serviceIndex: Number(e.detail.value) })
  },
  onDateChange(e) {
    this.setData({ date: e.detail.value })
  },
  onTimeChange(e) {
    this.setData({ time: e.detail.value })
  },
  onInput(e) {
    this.setData({ [e.currentTarget.dataset.field]: e.detail.value })
  },

  async submit() {
    if (this.data.serviceIndex < 0) {
      wx.showToast({ title: '请选择服务项目', icon: 'none' })
      return
    }
    if (!this.data.plateNo) {
      wx.showToast({ title: '请填写车牌号', icon: 'none' })
      return
    }
    if (!auth.isLogin()) {
      try {
        await auth.login()
      } catch (e) {
        wx.showToast({ title: '登录失败', icon: 'none' })
        return
      }
    }
    this.setData({ submitting: true })
    const service = this.data.services[this.data.serviceIndex]
    request.post('/member/appointments', {
      itemId: service.id,
      itemName: service.name,
      appointTime: this.data.date + ' ' + this.data.time + ':00',
      plateNo: this.data.plateNo,
      brandModel: [this.data.brand, this.data.model].filter(Boolean).join(' '),
      remark: this.data.remark,
      source: 'miniapp'
    }).then(() => {
      wx.showToast({ title: '预约提交成功', icon: 'success' })
      setTimeout(() => wx.redirectTo({ url: '/pages/appointment/list' }), 800)
    }).catch(() => {}).then(() => this.setData({ submitting: false }))
  }
})
