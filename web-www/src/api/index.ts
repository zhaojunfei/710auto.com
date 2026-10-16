import request from './request'

export const publicApi = {
  site: () => request.get('/public/site'),
  menu: (platform = 'public') => request.get('/public/menu', { params: { platform } }),
  home: () => request.get('/public/home'),
  categories: () => request.get('/public/service-categories'),
  items: (params: any) => request.get('/public/service-items', { params }),
  item: (id: number) => request.get(`/public/service-items/${id}`),
  articles: (params: any) => request.get('/public/articles', { params }),
  article: (id: number) => request.get(`/public/articles/${id}`),
  cases: (params?: any) => request.get('/public/cases', { params }),
  brandStory: () => request.get('/public/brand-story'),
  store: () => request.get('/public/store'),
  packages: () => request.get('/public/packages'),
  message: (data: any) => request.post('/public/message', data)
}
