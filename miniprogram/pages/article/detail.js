const request = require('../../utils/request')
const { pick } = require('../../utils/i18n')

Page({
  data: { article: null, content: '' },

  onLoad(options) {
    request.get('/miniapp/articles/' + options.id, {}, false).then((a) => {
      this.setData({ article: a, content: pick(a, 'content', 'contentEn') })
      wx.setNavigationBarTitle({ title: pick(a, 'title', 'titleEn') })
    }).catch(() => {})
  },

  onShareAppMessage() {
    return { title: (this.data.article && pick(this.data.article, 'title', 'titleEn')) || '养护知识', path: '/pages/article/detail?id=' + (this.data.article ? this.data.article.id : '') }
  }
})
