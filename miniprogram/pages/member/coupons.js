const request = require('../../utils/request')
const auth = require('../../utils/auth')
const { t, getLocale } = require('../../utils/i18n')
const { formatMoney, formatDate } = require('../../utils/util')

Page({
  data: { tab: 'mine', mine: [], available: [], T: {} },

  onShow() {
    this.setData({ T: { coupons: t('coupons'), submit: t('submit'), empty: t('empty') } })
    wx.setNavigationBarTitle({ title: getLocale() === 'en-US' ? 'Coupons' : '优惠券' })
    if (!auth.isLogin()) {
      auth.login().then(() => this.load()).catch(() => {})
      return
    }
    this.load()
  },

  load() {
    request.get('/member/coupons').then((list) => {
      this.setData({
        mine: (list || []).map((c) => ({
          ...c,
          endLabel: formatDate(c.expireTime),
          valueLabel: c.faceValue ? formatMoney(c.faceValue) : (c.discount ? c.discount + '折' : '')
        }))
      })
    }).catch(() => {})
    request.get('/member/coupons/available').then((list) => {
      this.setData({
        available: (list || []).map((c) => ({
          ...c,
          valueLabel: c.faceValue ? formatMoney(c.faceValue) : (c.discount ? c.discount + '折' : ''),
          conditionLabel: c.minAmount ? ('满' + c.minAmount + '元可用') : '无门槛'
        }))
      })
    }).catch(() => {})
  },

  switchTab(e) {
    this.setData({ tab: e.currentTarget.dataset.tab })
  },

  receive(e) {
    const id = e.currentTarget.dataset.id
    request.post('/member/coupons/' + id + '/receive', {}).then(() => {
      wx.showToast({ title: '领取成功', icon: 'success' })
      this.load()
    }).catch(() => {})
  }
})
