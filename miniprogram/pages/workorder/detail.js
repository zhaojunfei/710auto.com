const request = require('../../utils/request')
const { t, getLocale } = require('../../utils/i18n')
const { formatMoney, formatDate, statusText } = require('../../utils/util')

Page({
  data: { id: null, order: null, items: [], logs: [], T: {} },

  onLoad(options) {
    this.setData({ id: options.id, T: { status: t('status'), wechatPay: t('wechatPay'), useBalance: t('useBalance'), evaluate: t('evaluate'), confirm: t('confirm') } })
    wx.setNavigationBarTitle({ title: '工单详情' })
    this.load()
  },

  load() {
    request.get('/member/work-orders/' + this.data.id).then((order) => {
      this.setData({
        order: {
          ...order,
          statusLabel: statusText(order.status),
          amountLabel: formatMoney(order.totalAmount),
          timeLabel: formatDate(order.createTime)
        }
      })
    }).catch(() => {})
    request.get('/member/work-orders/' + this.data.id + '/items').then((items) => {
      this.setData({ items: (items || []).map((i) => ({ ...i, amountLabel: formatMoney(i.amount) })) })
    }).catch(() => {})
  },

  confirmQuote() {
    request.post('/member/work-orders/' + this.data.id + '/confirm-quote', {}).then(() => {
      wx.showToast({ title: '已确认报价', icon: 'success' })
      this.load()
    })
  },

  pay(e) {
    const payType = e.currentTarget.dataset.type
    request.post('/member/work-orders/' + this.data.id + '/settle?payType=' + payType, {}).then(() => {
      wx.showToast({ title: '结算成功', icon: 'success' })
      this.load()
    }).catch(() => {
      wx.showToast({ title: '结算失败', icon: 'none' })
    })
  },

  evaluate() {
    wx.showModal({
      title: '服务评价',
      editable: true,
      placeholderText: '请输入您的评价',
      success: (res) => {
        if (res.confirm) {
          request.post('/member/work-orders/' + this.data.id + '/evaluate', { score: 5, content: res.content }).then(() => {
            wx.showToast({ title: '感谢评价', icon: 'success' })
          })
        }
      }
    })
  }
})
