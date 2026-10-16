const request = require('../../utils/request')
const { t, pick, getLocale } = require('../../utils/i18n')
const { formatMoney, parseImages } = require('../../utils/util')

Page({
  data: {
    id: null,
    item: null,
    gallery: [],
    displayName: '',
    displayDesc: '',
    displayPrice: '',
    T: {}
  },

  onLoad(options) {
    this.setData({
      id: options.id,
      T: { bookNow: t('bookNow'), laborFee: t('laborFee'), price: t('price'), duration: t('duration'), viewDetail: t('viewDetail'), tireStorage: t('tireStorage') }
    })
    this.load()
  },

  load() {
    request.get('/miniapp/service-items/' + this.data.id, {}, false).then((item) => {
      const gallery = [item.cover].concat(parseImages(item.images)).filter(Boolean)
      this.setData({
        item,
        gallery,
        displayName: pick(item, 'itemName', 'itemNameEn'),
        displayDesc: pick(item, 'description', 'descriptionEn'),
        displayPrice: formatMoney(item.price)
      })
      wx.setNavigationBarTitle({ title: pick(item, 'itemName', 'itemNameEn') })
    }).catch(() => {})
  },

  previewImage(e) {
    wx.previewImage({ current: e.currentTarget.dataset.src, urls: this.data.gallery })
  },

  bookNow() {
    wx.navigateTo({ url: '/pages/appointment/create?serviceId=' + this.data.id })
  },

  onShareAppMessage() {
    return { title: this.data.displayName, path: '/pages/service/detail?id=' + this.data.id }
  }
})
