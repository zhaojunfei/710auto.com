/**
 * 统一请求封装：自动携带 token、处理 401、按语言附加 Accept-Language
 */
const app = () => getApp()

function baseUrl() {
  const a = app()
  return (a && a.globalData && a.globalData.baseUrl) || 'http://127.0.0.1:8080/api'
}

function locale() {
  const a = app()
  return (a && a.globalData && a.globalData.locale) || 'zh-CN'
}

function request(url, method, data, auth) {
  const token = wx.getStorageSync('auto710_token') || ''
  return new Promise((resolve, reject) => {
    wx.request({
      url: baseUrl() + url,
      method: method || 'GET',
      data: data || {},
      header: {
        'content-type': 'application/json',
        'Accept-Language': locale(),
        Authorization: auth === false ? '' : token ? 'Bearer ' + token : ''
      },
      success(res) {
        const body = res.data || {}
        if (body.code === 200) {
          resolve(body.data)
        } else if (body.code === 401) {
          wx.removeStorageSync('auto710_token')
          reject(body)
        } else {
          wx.showToast({ title: body.message || '请求失败', icon: 'none' })
          reject(body)
        }
      },
      fail(err) {
        wx.showToast({ title: '网络异常', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = {
  baseUrl,
  get: (url, data, auth) => request(url, 'GET', data, auth),
  post: (url, data, auth) => request(url, 'POST', data, auth),
  put: (url, data, auth) => request(url, 'PUT', data, auth),
  del: (url, data, auth) => request(url, 'DELETE', data, auth),
  upload(filePath, dir) {
    const token = wx.getStorageSync('auto710_token') || ''
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: baseUrl() + '/miniapp/file/upload?dir=' + (dir || 'images'),
        filePath,
        name: 'file',
        header: { Authorization: token ? 'Bearer ' + token : '' },
        success(res) {
          try {
            const body = JSON.parse(res.data)
            if (body.code === 200) resolve(body.data)
            else reject(body)
          } catch (e) {
            reject(e)
          }
        },
        fail: reject
      })
    })
  }
}
