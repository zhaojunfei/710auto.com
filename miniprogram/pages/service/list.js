const request = require('../../utils/request')
const { t, pick, getLocale } = require('../../utils/i18n')
const { formatMoney } = require('../../utils/util')

Page({
  data: {
    categories: [],
    items: [],
    categoryId: null,
    keyword: '',
    page: 1,
    size: 10,
    total: 0,
    loading: false,
    T: {}
  },

  onLoad(options) {
    if (options.categoryId) {
      this.setData({ categoryId: Number(options.categoryId) })
    }
    this.setData({ T: { services: t('services'), bookNow: t('bookNow'), viewDetail: t('viewDetail'), price: t('price'), laborFee: t('laborFee'), duration: t('duration') } })
    wx.setNavigationBarTitle({ title: getLocale() === 'en-US' ? 'Services' : '服务项目' })
    this.loadCategories()
    this.load()
  },

  loadCategories() {
    request.get('/miniapp/service-categories', {}, false).then((list) => {
      this.setData({ categories: list || [] })
    }).catch(() => {})
  },

  load() {
    if (this.data.loading) return
    this.setData({ loading: true })
    request.get('/miniapp/service-items', {
      page: this.data.page,
      size: this.data.size,
      categoryId: this.data.categoryId || '',
      keyword: this.data.keyword || ''
    }, false).then((res) => {
      const records = (res.records || []).map((it) => ({
        ...it,
        displayName: pick(it, 'itemName', 'itemNameEn'),
        displayPrice: formatMoney(it.price)
      }))
      this.setData({
        items: this.data.page === 1 ? records : this.data.items.concat(records),
        total: res.total || 0,
        loading: false
      })
    }).catch(() => this.setData({ loading: false }))
  },

  selectCategory(e) {
    const id = e.currentTarget.dataset.id
    this.setData({ categoryId: id === 'all' ? null : Number(id), page: 1, items: [] })
    this.load()
  },

  onSearch(e) {
    this.setData({ keyword: e.detail.value, page: 1, items: [] })
    this.load()
  },

  goDetail(e) {
    wx.navigateTo({ url: '/pages/service/detail?id=' + e.currentTarget.dataset.id })
  },

  onReachBottom() {
    if (this.data.items.length < this.data.total) {
      this.setData({ page: this.data.page + 1 })
      this.load()
    }
  },

  onShareAppMessage() {
    return { title: '710号汽车之家 · 服务项目', path: '/pages/service/list' }
  }
})
