const request = require('../../utils/request')
const auth = require('../../utils/auth')
const { t, getLocale } = require('../../utils/i18n')

Page({
  data: { list: [], form: { plateNo: '', brand: '', model: '', mileage: '', isDefault: 0 }, showForm: false, T: {} },

  onShow() {
    this.setData({ T: { myVehicles: t('myVehicles'), plateNo: t('plateNo'), brand: t('brand'), model: t('model'), mileage: t('mileage'), submit: t('submit'), cancel: t('cancel') } })
    wx.setNavigationBarTitle({ title: getLocale() === 'en-US' ? 'My Vehicles' : '车辆管理' })
    this.load()
  },

  load() {
    if (!auth.isLogin()) {
      auth.login().then(() => this.load()).catch(() => {})
      return
    }
    request.get('/member/vehicles').then((list) => this.setData({ list: list || [] })).catch(() => {})
  },

  toggleForm() {
    this.setData({ showForm: !this.data.showForm })
  },

  onInput(e) {
    this.setData({ ['form.' + e.currentTarget.dataset.field]: e.detail.value })
  },

  bindDefault(e) {
    this.setData({ 'form.isDefault': e.detail.value ? 1 : 0 })
  },

  onPlateChange(e) {
    this.setData({ 'form.plateNo': e.detail.value })
  },

  submit() {
    const pv = this.selectComponent('#plate').validate()
    if (!pv.ok) {
      wx.showToast({ title: pv.msg || '请填写车牌号', icon: 'none' })
      return
    }
    request.post('/member/vehicles', this.data.form).then(() => {
      wx.showToast({ title: '绑定成功', icon: 'success' })
      this.setData({ showForm: false, form: { plateNo: '', brand: '', model: '', mileage: '', isDefault: 0 } })
      this.load()
    }).catch(() => {})
  },

  remove(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确认解绑该车辆？',
      success: (res) => {
        if (res.confirm) {
          request.del('/member/vehicles/' + id).then(() => { wx.showToast({ title: '已解绑', icon: 'success' }); this.load() })
        }
      }
    })
  }
})
