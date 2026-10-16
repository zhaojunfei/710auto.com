/**
 * 小程序多语言：静态字典 + 内容字段按语言选择
 */
const STORAGE_KEY = 'auto710_locale'

const messages = {
  'zh-CN': {
    home: '首页', services: '服务项目', booking: '预约', mine: '我的',
    hotService: '热门服务', brandIntro: '品牌简介', cases: '施工案例',
    knowledge: '养护知识', bookNow: '立即预约', viewDetail: '查看详情',
    appointment: '在线预约', myAppointments: '我的预约', workorders: '我的工单',
    tireStorage: '存胎寄存', myVehicles: '车辆管理', coupons: '优惠券',
    points: '积分', balance: '储值余额', login: '微信登录', logout: '退出登录',
    contact: '联系我们', navigation: '门店导航', message: '在线留言',
    submit: '提交', cancel: '取消', confirm: '确认', empty: '暂无数据',
    loading: '加载中', price: '价格', laborFee: '工时费', duration: '时长(分钟)',
    selectService: '选择服务', selectTime: '选择到店时间', vehicleInfo: '车辆信息',
    plateNo: '车牌号', brand: '品牌', model: '车型', mileage: '里程',
    submitAppointment: '提交预约', status: '状态', createTime: '创建时间',
    tireNote: '存胎寄存须知', checkIn: '寄存入库', pickup: '取胎',
    wechatPay: '微信支付', useBalance: '储值抵扣', evaluate: '评价'
  },
  'en-US': {
    home: 'Home', services: 'Services', booking: 'Booking', mine: 'Mine',
    hotService: 'Popular Services', brandIntro: 'About Us', cases: 'Cases',
    knowledge: 'Knowledge', bookNow: 'Book Now', viewDetail: 'Detail',
    appointment: 'Booking', myAppointments: 'My Bookings', workorders: 'My Orders',
    tireStorage: 'Tire Storage', myVehicles: 'My Vehicles', coupons: 'Coupons',
    points: 'Points', balance: 'Balance', login: 'WeChat Login', logout: 'Logout',
    contact: 'Contact', navigation: 'Navigate', message: 'Message',
    submit: 'Submit', cancel: 'Cancel', confirm: 'Confirm', empty: 'No data',
    loading: 'Loading', price: 'Price', laborFee: 'Labor Fee', duration: 'Duration(min)',
    selectService: 'Select Service', selectTime: 'Select Time', vehicleInfo: 'Vehicle',
    plateNo: 'Plate No.', brand: 'Brand', model: 'Model', mileage: 'Mileage',
    submitAppointment: 'Submit', status: 'Status', createTime: 'Created',
    tireNote: 'Tire Storage Notice', checkIn: 'Check In', pickup: 'Pick Up',
    wechatPay: 'WeChat Pay', useBalance: 'Balance', evaluate: 'Evaluate'
  }
}

function getLocale() {
  return wx.getStorageSync(STORAGE_KEY) || 'zh-CN'
}

function setLocale(locale) {
  wx.setStorageSync(STORAGE_KEY, locale)
  const a = getApp()
  if (a) a.globalData.locale = locale
  if (a && a.applyTabBar) a.applyTabBar(locale)
}

function t(key) {
  const locale = getLocale()
  return (messages[locale] && messages[locale][key]) || (messages['zh-CN'] && messages['zh-CN'][key]) || key
}

/** 按当前语言在 中文字段/英文字段 间选择 */
function pick(obj, zhField, enField) {
  if (!obj) return ''
  const en = obj[enField]
  const zh = obj[zhField]
  return getLocale() === 'en-US' ? en || zh || '' : zh || en || ''
}

/** 语言切换器数据源 */
function languages() {
  return [
    { langCode: 'zh-CN', langName: '简体中文' },
    { langCode: 'en-US', langName: 'English' }
  ]
}

module.exports = { t, pick, getLocale, setLocale, languages, messages }
