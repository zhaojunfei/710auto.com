const request = require('../../utils/request')
const { pick } = require('../../utils/i18n')

Page({
  data: { store: {}, site: {}, form: { name: '', phone: '', content: '' }, sending: false },

  onLoad() {
    request.get('/miniapp/store', {}, false).then((store) => this.setData({ store: store || {} })).catch(() => {})
    request.get('/miniapp/site', {}, false).then((site) => this.setData({ site: site || {} })).catch(() => {})
  },

  onInput(e) {
    this.setData({ ['form.' + e.currentTarget.dataset.field]: e.detail.value })
  },

  callPhone() {
    const phone = this.data.store.phone || this.data.site.servicePhone
    if (phone) wx.makePhoneCall({ phoneNumber: String(phone) })
    else wx.showToast({ title: '未配置电话', icon: 'none' })
  },

  openLocation() {
    const s = this.data.store
    if (!s.latitude || !s.longitude) {
      wx.showToast({ title: '未配置门店坐标', icon: 'none' })
      return
    }
    wx.openLocation({
      latitude: Number(s.latitude),
      longitude: Number(s.longitude),
      name: pick(s, 'storeName', 'storeNameEn'),
      address: pick(s, 'address', 'addressEn')
    })
  },

  submit() {
    if (!this.data.form.name || !this.data.form.phone) {
      wx.showToast({ title: '请填写姓名与电话', icon: 'none' })
      return
    }
    this.setData({ sending: true })
    request.post('/miniapp/message', { ...this.data.form, source: 'miniapp', status: 0 }, false)
      .then(() => {
        wx.showToast({ title: '提交成功', icon: 'success' })
        this.setData({ form: { name: '', phone: '', content: '' } })
      })
      .catch(() => {})
      .then(() => this.setData({ sending: false }))
  }
})
