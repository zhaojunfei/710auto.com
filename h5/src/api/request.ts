import axios from 'axios'

const request = axios.create({ baseURL: '/api', timeout: 30000 })

request.interceptors.request.use((config) => {
  const url = config.url || ''
  const token = url.startsWith('/tech')
    ? localStorage.getItem('auto710_tech_token')
    : url.startsWith('/supplier')
      ? localStorage.getItem('auto710_supplier_token')
      : localStorage.getItem('auto710_h5_token')
  if (token) config.headers.Authorization = 'Bearer ' + token
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
