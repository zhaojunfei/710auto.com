const request = require('../../utils/request')
const auth = require('../../utils/auth')
const { t, getLocale } = require('../../utils/i18n')
const { formatMoney, formatDate, statusText } = require('../../utils/util')

Page({
  data: { list: [], logged: false, loading: false, T: {} },

  onShow() {
    this.setData({ T: { tireStorage: t('tireStorage'), checkIn: t('checkIn'), empty: t('empty'), logout: t('logout') } })
    wx.setNavigationBarTitle({ title: getLocale() === 'en-US' ? 'Tire Storage' : '存胎寄存' })
    const logged = auth.isLogin()
    this.setData({ logged })
    if (logged) this.load()
  },

  load() {
    this.setData({ loading: true })
    request.get('/member/tire-storage').then((list) => {
      this.setData({
        list: (list || []).map((o) => ({
          ...o,
          statusLabel: statusText(o.status),
          timeLabel: formatDate(o.createTime),
          feeLabel: formatMoney(o.storageFee)
        })),
        loading: false
      })
    }).catch(() => this.setData({ loading: false }))
  },

  login() {
    auth.login().then(() => { this.setData({ logged: true }); this.load() })
      .catch(() => wx.showToast({ title: '登录失败', icon: 'none' }))
  },

  create() {
    wx.navigateTo({ url: '/pages/tire/create' })
  },

  detail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/tire/create?id=' + id + '&readonly=1' })
  }
})
