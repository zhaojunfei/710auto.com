import axios from 'axios'
import { state } from '@/i18n'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use((config) => {
  config.headers['Accept-Language'] = state.locale
  return config
})

request.interceptors.response.use(
  (res) => {
    const body = res.data
    if (body && body.code === 200) return body.data
    return Promise.reject(new Error(body?.message || 'request failed'))
  },
  (err) => Promise.reject(err)
)

export default request
