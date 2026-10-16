const request = require('../../utils/request')
const auth = require('../../utils/auth')
const { t, getLocale } = require('../../utils/i18n')
const { formatDate, formatMoney } = require('../../utils/util')

Page({
  data: {
    readonly: false,
    order: null,
    items: [],
    proofs: [],
    form: {
      plateNo: '',
      tireType: '四季胎',
      positionNo: '',
      startDate: '',
      endDate: '',
      remindPreference: '提前7天',
      remark: ''
    },
    tireItems: [],
    submitting: false,
    T: {}
  },

  onLoad(options) {
    const today = new Date()
    const end = new Date()
    end.setMonth(end.getMonth() + 6)
    this.setData({
      readonly: options.readonly === '1',
      form: {
        ...this.data.form,
        startDate: today.toISOString().slice(0, 10),
        endDate: end.toISOString().slice(0, 10)
      },
      T: { tireStorage: t('tireStorage'), checkIn: t('checkIn'), submit: t('submit'), pickup: t('pickup') }
    })
    wx.setNavigationBarTitle({ title: options.readonly ? '寄存单详情' : (getLocale() === 'en-US' ? 'Tire Check-in' : '在线存胎') })
    if (options.id) {
      this.loadDetail(options.id)
    } else {
      this.addTire()
    }
  },

  loadDetail(id) {
    request.get('/member/tire-storage').then((list) => {
      const order = (list || []).find((o) => String(o.id) === String(id))
      if (order) {
        this.setData({
          order: { ...order, feeLabel: formatMoney(order.storageFee), timeLabel: formatDate(order.startDate) }
        })
      }
    }).catch(() => {})
    request.get('/member/tire-storage/' + id + '/items').then((items) => this.setData({ items: items || [] })).catch(() => {})
    request.get('/member/tire-storage/' + id + '/proofs').then((proofs) => {
      this.setData({ proofs: (proofs || []).map((p) => ({ ...p, images: p.images ? [].concat(p.images) : [] })) })
    }).catch(() => {})
  },

  addTire() {
    const list = this.data.tireItems.concat({ tirePosition: '', tireBrand: '', tireSize: '', treadDepth: '', remark: '' })
    this.setData({ tireItems: list })
  },

  removeTire(e) {
    const index = e.currentTarget.dataset.index
    const list = this.data.tireItems.slice()
    list.splice(index, 1)
    this.setData({ tireItems: list })
  },

  onTireInput(e) {
    const { index, field } = e.currentTarget.dataset
    const list = this.data.tireItems.slice()
    list[index][field] = e.detail.value
    this.setData({ tireItems: list })
  },

  onFormInput(e) {
    this.setData({ ['form.' + e.currentTarget.dataset.field]: e.detail.value })
  },

  onDateChange(e) {
    this.setData({ ['form.' + e.currentTarget.dataset.field]: e.detail.value })
  },

  async submit() {
    if (!this.data.form.plateNo) {
      wx.showToast({ title: '请填写车牌号', icon: 'none' })
      return
    }
    if (!auth.isLogin()) {
      try { await auth.login() } catch (e) { wx.showToast({ title: '登录失败', icon: 'none' }); return }
    }
    const items = this.data.tireItems.filter((i) => i.tireBrand || i.tireSize)
    const payload = {
      plateNo: this.data.form.plateNo,
      tireCount: items.length || this.data.tireItems.length,
      tireType: this.data.form.tireType,
      positionNo: this.data.form.positionNo,
      startDate: this.data.form.startDate + ' 00:00:00',
      endDate: this.data.form.endDate + ' 00:00:00',
      remindPreference: this.data.form.remindPreference,
      remark: this.data.form.remark,
      status: 0
    }
    this.setData({ submitting: true })
    request.post('/member/tire-storage?itemsJson=' + encodeURIComponent(JSON.stringify(items)), payload)
      .then(() => {
        wx.showToast({ title: '下单成功', icon: 'success' })
        setTimeout(() => wx.redirectTo({ url: '/pages/tire/index' }), 800)
      })
      .catch(() => {})
      .then(() => this.setData({ submitting: false }))
  },

  previewProof(e) {
    wx.previewImage({ urls: e.currentTarget.dataset.images, current: e.currentTarget.dataset.src })
  }
})
