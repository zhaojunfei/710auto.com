/**
 * 微信分享卡片配置（T44）
 * - 浏览器环境写入标准 OG meta，供社交平台抓取
 * - 公众号内配置 JS-SDK 签名（需后端 wx.config 接口下发 signature）
 */
export interface ShareConfig {
  title: string
  desc?: string
  link?: string
  imgUrl?: string
}

function setMeta(property: string, content: string) {
  let el = document.querySelector(`meta[property="${property}"]`) as HTMLMetaElement | null
  if (!el) {
    el = document.createElement('meta')
    el.setAttribute('property', property)
    document.head.appendChild(el)
  }
  el.setAttribute('content', content)
}

export function applyShareCard(config: ShareConfig) {
  const link = config.link || window.location.href
  document.title = config.title
  setMeta('og:title', config.title)
  if (config.desc) setMeta('og:description', config.desc)
  setMeta('og:url', link)
  if (config.imgUrl) setMeta('og:image', config.imgUrl)

  // 公众号网页需要调用 wx.updateAppMessageShareData，此处做存在性判断
  const wx = (window as any).wx
  if (wx && wx.ready) {
    wx.ready(() => {
      const shareData = {
        title: config.title,
        desc: config.desc || '',
        link,
        imgUrl: config.imgUrl || ''
      }
      wx.updateAppMessageShareData && wx.updateAppMessageShareData(shareData)
      wx.updateTimelineShareData && wx.updateTimelineShareData(shareData)
    })
  }
}
