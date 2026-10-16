const request = require('../../utils/request')
const auth = require('../../utils/auth')
const { t, getLocale } = require('../../utils/i18n')
const { formatDate, statusText } = require('../../utils/util')

Page({
  data: {
    list: [],
    page: 1,
    size: 10,
    total: 0,
    loading: false,
    logged: false,
    T: {}
  },

  onShow() {
    this.setData({ T: { myAppointments: t('myAppointments'), appointment: t('appointment'), cancel: t('cancel'), empty: t('empty') } })
    wx.setNavigationBarTitle({ title: getLocale() === 'en-US' ? 'My Bookings' : '我的预约' })
    this.setData({ logged: auth.isLogin() })
    if (auth.isLogin()) {
      this.setData({ page: 1, list: [] })
      this.load()
    }
  },

  load() {
    if (this.data.loading) return
    this.setData({ loading: true })
    request.get('/member/appointments', { page: this.data.page, size: this.data.size }).then((res) => {
      const records = (res.records || []).map((a) => ({
        ...a,
        statusLabel: statusText(a.status),
        timeLabel: formatDate(a.appointTime || a.createTime)
      }))
      this.setData({
        list: this.data.page === 1 ? records : this.data.list.concat(records),
        total: res.total || 0,
        loading: false
      })
    }).catch(() => this.setData({ loading: false }))
  },

  login() {
    auth.login().then(() => {
      this.setData({ logged: true, page: 1, list: [] })
      this.load()
    }).catch(() => wx.showToast({ title: '登录失败', icon: 'none' }))
  },

  create() {
    wx.navigateTo({ url: '/pages/appointment/create' })
  },

  cancel(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确认取消该预约？',
      success: (res) => {
        if (res.confirm) {
          request.post('/member/appointments/' + id + '/cancel', { reason: '用户取消' }).then(() => {
            wx.showToast({ title: '已取消', icon: 'success' })
            this.setData({ page: 1, list: [] })
            this.load()
          })
        }
      }
    })
  },

  onReachBottom() {
    if (this.data.list.length < this.data.total) {
      this.setData({ page: this.data.page + 1 })
      this.load()
    }
  }
})
