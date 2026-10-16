const request = require('../../utils/request')
const { t, pick, getLocale, setLocale, languages } = require('../../utils/i18n')
const { formatMoney } = require('../../utils/util')

Page({
  data: {
    locale: 'zh-CN',
    languages: [],
    site: {},
    store: {},
    banners: [],
    categories: [],
    hotItems: [],
    cases: [],
    articles: [],
    T: {}
  },

  onLoad() {
    this.refreshText()
    this.load()
  },

  onShow() {
    this.refreshText()
  },

  refreshText() {
    const locale = getLocale()
    this.setData({
      locale,
      languages: languages(),
      T: {
        hotService: t('hotService'),
        brandIntro: t('brandIntro'),
        cases: t('cases'),
        knowledge: t('knowledge'),
        bookNow: t('bookNow'),
        viewDetail: t('viewDetail'),
        tireStorage: t('tireStorage'),
        contact: t('contact'),
        navigation: t('navigation')
      }
    })
    wx.setNavigationBarTitle({ title: getLocale() === 'en-US' ? '710 Auto House' : '710号汽车之家' })
  },

  load() {
    request.get('/miniapp/home', {}, false).then((data) => {
      this.setData({
        site: data.site || {},
        store: data.store || {},
        banners: data.banners || [],
        categories: data.categories || [],
        hotItems: data.hotItems || [],
        cases: data.cases || [],
        articles: data.articles || []
      })
    }).catch(() => {})
  },

  name(item, zh, en) {
    return pick(item, zh, en)
  },

  money(v) {
    return formatMoney(v)
  },

  goService(e) {
    wx.navigateTo({ url: '/pages/service/detail?id=' + e.currentTarget.dataset.id })
  },

  goCategory(e) {
    wx.navigateTo({ url: '/pages/service/list?categoryId=' + e.currentTarget.dataset.id })
  },

  goTire() {
    wx.navigateTo({ url: '/pages/tire/index' })
  },

  goBook() {
    wx.navigateTo({ url: '/pages/appointment/create' })
  },

  goArticles() {
    wx.navigateTo({ url: '/pages/article/list' })
  },

  goContact() {
    wx.navigateTo({ url: '/pages/contact/index' })
  },

  openLocation() {
    const s = this.data.store || {}
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

  switchLang(e) {
    setLocale(e.currentTarget.dataset.code)
    this.refreshText()
    this.load()
  },

  onShareAppMessage() {
    return { title: pick(this.data.site, 'siteName', 'siteNameEn') || '710号汽车之家', path: '/pages/index/index' }
  }
})
