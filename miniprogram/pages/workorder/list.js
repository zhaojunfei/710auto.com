const request = require('../../utils/request')
const auth = require('../../utils/auth')
const { t, getLocale } = require('../../utils/i18n')
const { formatMoney, formatDate, statusText } = require('../../utils/util')

Page({
  data: { list: [], logged: false, loading: false, T: {} },

  onShow() {
    this.setData({ T: { workorders: t('workorders'), empty: t('empty'), evaluate: t('evaluate'), logout: t('logout') } })
    wx.setNavigationBarTitle({ title: getLocale() === 'en-US' ? 'My Orders' : '我的工单' })
    const logged = auth.isLogin()
    this.setData({ logged })
    if (logged) this.load()
  },

  load() {
    this.setData({ loading: true })
    request.get('/member/work-orders').then((list) => {
      this.setData({
        list: (list || []).map((o) => ({
          ...o,
          statusLabel: statusText(o.status),
          amountLabel: formatMoney(o.totalAmount),
          timeLabel: formatDate(o.createTime)
        })),
        loading: false
      })
    }).catch(() => this.setData({ loading: false }))
  },

  login() {
    auth.login().then(() => { this.setData({ logged: true }); this.load() })
      .catch(() => wx.showToast({ title: '登录失败', icon: 'none' }))
  },

  detail(e) {
    wx.navigateTo({ url: '/pages/workorder/detail?id=' + e.currentTarget.dataset.id })
  }
})
