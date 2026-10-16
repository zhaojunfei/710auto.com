import request from './request'

/** 公开接口（客户侧，无需登录） */
export const publicApi = {
  site: () => request.get('/h5/site'),
  store: () => request.get('/h5/store'),
  items: (params: any) => request.get('/h5/service-items', { params }),
  item: (id: number) => request.get(`/h5/service-items/${id}`),
  banners: (platform = 'h5') => request.get('/h5/banners', { params: { platform } }),
  message: (data: any) => request.post('/h5/message', data)
}

/** 会员接口（UnionID 与小程序/公众号互通） */
export const memberApi = {
  wxLogin: (data: any) => request.post('/member/wx-login', data),
  profile: () => request.get('/member/profile'),
  vehicles: () => request.get('/member/vehicles'),
  addVehicle: (data: any) => request.post('/member/vehicles', data),
  appointments: (params: any) => request.get('/member/appointments', { params }),
  createAppointment: (data: any) => request.post('/member/appointments', data),
  cancelAppointment: (id: number) => request.post(`/member/appointments/${id}/cancel`),
  workOrders: () => request.get('/member/work-orders'),
  workOrder: (id: number) => request.get(`/member/work-orders/${id}`),
  workOrderItems: (id: number) => request.get(`/member/work-orders/${id}/items`),
  coupons: () => request.get('/member/coupons'),
  availableCoupons: () => request.get('/member/coupons/available'),
  receiveCoupon: (id: number) => request.post(`/member/coupons/${id}/receive`)
}

/** 接车技师端 */
export const techApi = {
  login: (data: any) => request.post('/tech/login', data),
  profile: () => request.get('/tech/profile'),
  orders: (status?: string) => request.get('/tech/orders', { params: { status } }),
  order: (id: number) => request.get(`/tech/orders/${id}`),
  addChecks: (id: number, data: any[]) => request.post(`/tech/orders/${id}/checks`, data),
  start: (id: number) => request.post(`/tech/orders/${id}/start`),
  addItem: (id: number, data: any, outStock = true) => request.post(`/tech/orders/${id}/items`, data, { params: { outStock } }),
  finish: (id: number) => request.post(`/tech/orders/${id}/finish`),
  abnormal: (id: number, reason: string) => request.post(`/tech/orders/${id}/abnormal`, null, { params: { reason } }),
  stats: () => request.get('/tech/stats')
}

/** 供应商端 */
export const supplierApi = {
  login: (data: any) => request.post('/supplier/login', data),
  profile: () => request.get('/supplier/profile'),
  stats: () => request.get('/supplier/stats'),
  parts: (keyword?: string) => request.get('/supplier/parts', { params: { keyword } }),
  savePart: (data: any) => request.post('/supplier/parts', data),
  deliveries: (status?: string) => request.get('/supplier/deliveries', { params: { status } }),
  delivery: (id: number) => request.get(`/supplier/deliveries/${id}`),
  submit: (data: any) => request.post('/supplier/deliveries', data),
  cancel: (id: number) => request.post(`/supplier/deliveries/${id}/cancel`),
  statements: (month?: string) => request.get('/supplier/statements', { params: { month } })
}
