const { getLocale } = require('./utils/i18n')

App({
  globalData: {
    baseUrl: 'http://127.0.0.1:8080/api',
    token: '',
    userInfo: null,
    locale: 'zh-CN',
    store: null,
    site: null
  },

  onLaunch() {
    const token = wx.getStorageSync('auto710_token') || ''
    this.globalData.token = token
    this.globalData.locale = getLocale()
    this.loadSite()
  },

  /** 读取站点配置，供全局使用（logo、客服、营业时间等） */
  loadSite() {
    const request = require('./utils/request')
    request.get('/miniapp/site', {}, false)
      .then((site) => { this.globalData.site = site })
      .catch(() => {})
    request.get('/miniapp/store', {}, false)
      .then((store) => { this.globalData.store = store })
      .catch(() => {})
  },

  /** 语言切换后刷新 tabBar 文案 */
  applyTabBar(locale) {
    const zh = ['首页', '服务', '预约', '我的']
    const en = ['Home', 'Services', 'Booking', 'Mine']
    const texts = locale === 'en-US' ? en : zh
    texts.forEach((text, index) => {
      wx.setTabBarItem({ index, text, fail: () => {} })
    })
  }
})
