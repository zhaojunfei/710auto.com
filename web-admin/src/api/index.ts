import request from './request'

/** 通用 CRUD 请求封装 */
export const crud = {
  page: (url: string, params: any) => request.get(`${url}/page`, { params }),
  list: (url: string, params?: any) => request.get(`${url}/list`, { params }),
  detail: (url: string, id: number) => request.get(`${url}/${id}`),
  create: (url: string, data: any) => request.post(url, data),
  update: (url: string, data: any) => request.put(url, data),
  remove: (url: string, id: number) => request.delete(`${url}/${id}`),
  batchRemove: (url: string, ids: number[]) => request.delete(`${url}/batch`, { data: ids })
}

export const authApi = {
  login: (data: any) => request.post('/auth/login', data),
  info: () => request.get('/auth/info'),
  menus: () => request.get('/auth/menus'),
  changePassword: (data: any) => request.post('/auth/password', data)
}

export const configApi = {
  groups: () => request.get('/admin/config/groups'),
  group: (group: string) => request.get(`/admin/config/group/${group}`),
  save: (data: any) => request.post('/admin/config/save', data),
  saveBatch: (data: any[]) => request.post('/admin/config/save-batch', data),
  saveGroup: (group: string, data: any) => request.post(`/admin/config/save-group/${group}`, data)
}

export const storageApi = {
  drivers: () => request.get('/admin/storage/drivers'),
  config: () => request.get('/admin/storage/config'),
  test: (data: any) => request.post('/admin/storage/test', data),
  save: (data: any) => request.post('/admin/storage/save', data)
}

export const languageApi = {
  all: () => request.get('/admin/language/list-all'),
  status: (id: number, status: number) => request.put(`/admin/language/${id}/status`, null, { params: { status } }),
  setDefault: (id: number) => request.put(`/admin/language/${id}/default`),
  fields: (params: any) => request.get('/admin/i18n/fields', { params }),
  saveFields: (params: any, data: any) => request.post('/admin/i18n/fields', data, { params })
}

export const menuApi = {
  tree: (platform: string) => request.get('/admin/cms-menu/tree', { params: { platform } }),
  save: (data: any) => request.post('/admin/cms-menu/save', data),
  saveTree: (platform: string, data: any[]) => request.post('/admin/cms-menu/save-tree', data, { params: { platform } }),
  remove: (id: number) => request.delete(`/admin/cms-menu/${id}`),
  publish: (platform: string) => request.post('/admin/cms-menu/publish', null, { params: { platform } })
}

export const statApi = {
  overview: () => request.get('/admin/stat/overview'),
  dashboard: (days = 30) => request.get('/admin/stat/dashboard', { params: { days } })
}

export const workOrderApi = {
  kanban: () => request.get('/admin/work-order/kanban'),
  manual: (data: any) => request.post('/admin/work-order/manual', data),
  checks: (id: number) => request.get(`/admin/work-order/${id}/checks`),
  addChecks: (id: number, data: any[]) => request.post(`/admin/work-order/${id}/checks`, data),
  receive: (id: number) => request.post(`/admin/work-order/${id}/receive`),
  start: (id: number) => request.post(`/admin/work-order/${id}/start`),
  dispatch: (id: number, technicianId: number) => request.post(`/admin/work-order/${id}/dispatch`, null, { params: { technicianId } }),
  finish: (id: number) => request.post(`/admin/work-order/${id}/finish`),
  qualityCheck: (id: number, pass: boolean, remark?: string) => request.post(`/admin/work-order/${id}/check`, null, { params: { pass, remark } }),
  settle: (id: number, payType: string, useBalance?: number, usePoints?: number) => request.post(`/admin/work-order/${id}/settle`, null, { params: { payType, useBalance, usePoints } }),
  items: (id: number) => request.get(`/admin/work-order/${id}/items`),
  addItem: (id: number, data: any, outStock = true) => request.post(`/admin/work-order/${id}/items`, data, { params: { outStock } }),
  logs: (id: number) => request.get(`/admin/work-order/${id}/logs`),
  urgent: (id: number, urgent: boolean) => request.post(`/admin/work-order/${id}/urgent`, null, { params: { urgent } })
}

export const fileApi = {
  uploadUrl: '/api/admin/file/upload',
  remove: (key: string) => request.delete('/admin/file', { params: { key } })
}

/** 预约管理（确认 / 取消 / 到店接车 / 剩余产能） */
export const appointmentApi = {
  confirm: (id: number) => request.post(`/admin/appointments/${id}/confirm`),
  cancel: (id: number, reason?: string) => request.post(`/admin/appointments/${id}/cancel`, null, { params: { reason } }),
  receive: (id: number, data: any) => request.post(`/admin/appointments/${id}/receive`, data),
  remainCapacity: (params: { storeId?: number; date?: string }) => request.get('/admin/appointments/remain-capacity', { params })
}

/** 退款审核 */
export const refundApi = {
  audit: (refundId: number, pass: boolean, remark?: string) =>
    request.post(`/admin/work-order/refund/${refundId}/audit`, null, { params: { pass, remark } })
}

/** 手机号即会员：按手机号查询 / 自动建档 */
export const memberLookupApi = {
  find: (phone: string) => request.get('/admin/member-lookup/find', { params: { phone } }),
  ensure: (data: { phone: string; realName?: string }) => request.post('/admin/member-lookup/ensure', data)
}

/** 后台会员充值（#10） */
export const memberRechargeApi = {
  recharge: (params: { memberId?: number; phone?: string; amount: number; payType?: string; ruleId?: number; remark?: string }) =>
    request.post('/admin/member-recharge/recharge', null, { params })
}
