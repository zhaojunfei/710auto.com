const request = require('../../utils/request')
const { t, pick, getLocale } = require('../../utils/i18n')

Page({
  data: { list: [], page: 1, size: 10, total: 0, loading: false, T: {} },

  onLoad() {
    this.setData({ T: { knowledge: t('knowledge'), empty: t('empty') } })
    wx.setNavigationBarTitle({ title: getLocale() === 'en-US' ? 'Knowledge' : '养护知识' })
    this.load()
  },

  load() {
    if (this.data.loading) return
    this.setData({ loading: true })
    request.get('/miniapp/articles', { page: this.data.page, size: this.data.size }, false).then((res) => {
      const records = (res.records || []).map((a) => ({ ...a, displayTitle: pick(a, 'title', 'titleEn'), displaySummary: pick(a, 'summary', 'summaryEn') }))
      this.setData({
        list: this.data.page === 1 ? records : this.data.list.concat(records),
        total: res.total || 0,
        loading: false
      })
    }).catch(() => this.setData({ loading: false }))
  },

  detail(e) {
    wx.navigateTo({ url: '/pages/article/detail?id=' + e.currentTarget.dataset.id })
  },

  onReachBottom() {
    if (this.data.list.length < this.data.total) {
      this.setData({ page: this.data.page + 1 })
      this.load()
    }
  }
})
