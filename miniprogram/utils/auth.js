/**
 * 会员登录：wx.login 换取 code → 后端 wx-login 换取 token
 * 生产环境后端用 code 调用 code2Session 得到 openid/unionid；
 * 未配置公众号/小程序密钥时后端会以 code 作为临时 openid 建档（便于联调）。
 */
const request = require('./request')

function getToken() {
  return wx.getStorageSync('auto710_token') || ''
}

function isLogin() {
  return !!getToken()
}

function login() {
  return new Promise((resolve, reject) => {
    wx.login({
      success(res) {
        if (!res.code) {
          reject(new Error('wx.login 未返回 code'))
          return
        }
        request.post('/member/wx-login', {
          code: res.code,
          openid: res.code,
          source: 'miniapp',
          nickname: '微信用户'
        }, false).then((data) => {
          wx.setStorageSync('auto710_token', data.token)
          const app = getApp()
          if (app) app.globalData.token = data.token
          resolve(data)
        }).catch(reject)
      },
      fail: reject
    })
  })
}

/** 将用户授权信息补全到会员档案 */
function updateProfile(payload) {
  return request.put('/member/profile', payload)
}

module.exports = { getToken, isLogin, login, updateProfile }
