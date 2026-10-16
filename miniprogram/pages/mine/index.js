const request = require('../../utils/request')
const auth = require('../../utils/auth')
const { t, getLocale, setLocale, languages } = require('../../utils/i18n')
const { formatMoney } = require('../../utils/util')

Page({
  data: {
    logged: false,
    profile: null,
    locale: 'zh-CN',
    languages: [],
    T: {}
  },

  onShow() {
    const locale = getLocale()
    this.setData({
      locale,
      languages: languages(),
      logged: auth.isLogin(),
      T: {
        mine: t('mine'), workorders: t('workorders'), myVehicles: t('myVehicles'),
        coupons: t('coupons'), tireStorage: t('tireStorage'), points: t('points'),
        balance: t('balance'), login: t('login'), logout: t('logout'), contact: t('contact')
      }
    })
    wx.setNavigationBarTitle({ title: locale === 'en-US' ? 'Mine' : '我的' })
    if (auth.isLogin()) this.loadProfile()
  },

  loadProfile() {
    request.get('/member/profile').then((profile) => {
      this.setData({ profile: { ...profile, balanceLabel: formatMoney(profile.balance), pointsLabel: profile.points || 0 } })
    }).catch(() => {
      this.setData({ logged: false })
      wx.removeStorageSync('auto710_token')
    })
  },

  login() {
    auth.login().then(() => {
      this.setData({ logged: true })
      this.loadProfile()
    }).catch(() => wx.showToast({ title: '登录失败', icon: 'none' }))
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确认退出登录？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('auto710_token')
          this.setData({ logged: false, profile: null })
        }
      }
    })
  },

  go(e) {
    wx.navigateTo({ url: e.currentTarget.dataset.url })
  },

  switchLang(e) {
    setLocale(e.currentTarget.dataset.code)
    this.setData({ locale: e.currentTarget.dataset.code })
    this.onShow()
  },

  contact() {
    wx.navigateTo({ url: '/pages/contact/index' })
  }
})
