import { reactive, watchEffect } from 'vue'
import request from '@/api/request'

export type LangCode = 'zh-CN' | 'en-US'

/** 站点静态文案字典（接口类动态内容中的 *_en 字段由 pick() 处理） */
const messages: Record<LangCode, Record<string, string>> = {
  'zh-CN': {
    brand: '710号汽车之家',
    slogan: '专业技师 · 透明报价 · 连锁门店',
    nav_home: '首页',
    nav_services: '服务项目',
    nav_about: '门店介绍',
    nav_cases: '施工案例',
    nav_articles: '养护知识',
    nav_contact: '联系我们',
    book_now: '立即预约',
    book_mini: '小程序预约',
    view_detail: '查看详情',
    more: '了解更多',
    home_hot: '热门服务',
    home_cases: '施工案例',
    home_articles: '养护知识',
    home_story: '品牌故事',
    home_store: '门店实力',
    services_title: '服务项目',
    services_sub: '覆盖保养 / 机修 / 轮胎 / 美容 / 存胎寄存全场景',
    cases_title: '施工案例',
    articles_title: '养护知识',
    about_title: '门店介绍',
    contact_title: '联系我们',
    contact_name: '您的称呼',
    contact_phone: '联系电话',
    contact_content: '留言内容',
    contact_submit: '提交留言',
    contact_success: '提交成功，我们会尽快与您联系！',
    contact_info: '联系方式',
    address: '门店地址',
    phone: '联系电话',
    hours: '营业时间',
    labor_fee: '工时费',
    price: '参考价格',
    duration: '预计时长',
    bookable: '支持在线预约',
    tire_storage: '存胎寄存',
    empty: '暂无内容',
    loading: '加载中...',
    all: '全部',
    footer_rights: '版权所有',
    related_cases: '相关案例'
  },
  'en-US': {
    brand: '710 Auto House',
    slogan: 'Expert Technicians · Transparent Pricing · Chain Stores',
    nav_home: 'Home',
    nav_services: 'Services',
    nav_about: 'About',
    nav_cases: 'Cases',
    nav_articles: 'Knowledge',
    nav_contact: 'Contact',
    book_now: 'Book Now',
    book_mini: 'Book via Mini Program',
    view_detail: 'View Detail',
    more: 'Learn More',
    home_hot: 'Popular Services',
    home_cases: 'Case Studies',
    home_articles: 'Maintenance Knowledge',
    home_story: 'Brand Story',
    home_store: 'Our Store',
    services_title: 'Our Services',
    services_sub: 'Maintenance / Repair / Tire / Detailing / Tire Storage',
    cases_title: 'Case Studies',
    articles_title: 'Knowledge',
    about_title: 'About Us',
    contact_title: 'Contact Us',
    contact_name: 'Your Name',
    contact_phone: 'Phone',
    contact_content: 'Message',
    contact_submit: 'Submit',
    contact_success: 'Submitted. We will contact you soon!',
    contact_info: 'Contact Info',
    address: 'Address',
    phone: 'Phone',
    hours: 'Business Hours',
    labor_fee: 'Labor Fee',
    price: 'Price',
    duration: 'Duration',
    bookable: 'Online booking available',
    tire_storage: 'Tire Storage',
    empty: 'No content',
    loading: 'Loading...',
    all: 'All',
    footer_rights: 'All rights reserved',
    related_cases: 'Related Cases'
  }
}

const saved = (localStorage.getItem('auto710_www_lang') as LangCode) || 'zh-CN'
export const state = reactive<{ locale: LangCode }>({ locale: saved })

function persistLocale(lang: LangCode) {
  state.locale = lang
  localStorage.setItem('auto710_www_lang', lang)
  document.cookie = `auto710_lang=${lang};path=/;max-age=31536000`
  document.documentElement.setAttribute('lang', lang)
}

export function t(key: string): string {
  return messages[state.locale]?.[key] ?? messages['zh-CN'][key] ?? key
}

/** 按当前语言在实体的 中文字段 / 英文字段 间选择 */
export function pick(obj: any, zhField: string, enField: string): string {
  if (!obj) return ''
  const zh = obj[zhField]
  const en = obj[enField]
  if (state.locale === 'en-US') return en || zh || ''
  return zh || en || ''
}

export function fmtMoney(v: any): string {
  if (v === null || v === undefined || v === '') return '-'
  return '¥' + Number(v).toFixed(2)
}

export const i18n = {
  install() {
    persistLocale(state.locale)
  }
}

/** 读取后台启用的语言列表（前台语言切换器数据源） */
export async function loadLanguages() {
  try {
    return await request.get('/public/languages')
  } catch {
    return [
      { langCode: 'zh-CN', langName: '简体中文', flagIcon: '🇨🇳' },
      { langCode: 'en-US', langName: 'English', flagIcon: '🇺🇸' }
    ]
  }
}

watchEffect(() => {
  document.documentElement.setAttribute('lang', state.locale)
})
