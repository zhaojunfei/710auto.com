import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import('@/views/Home.vue'), meta: { title: '首页' } },
  { path: '/services', name: 'Services', component: () => import('@/views/Services.vue'), meta: { title: '服务项目' } },
  { path: '/services/:id', name: 'ServiceDetail', component: () => import('@/views/ServiceDetail.vue'), meta: { title: '服务详情' } },
  { path: '/about', name: 'About', component: () => import('@/views/About.vue'), meta: { title: '门店介绍' } },
  { path: '/cases', name: 'Cases', component: () => import('@/views/Cases.vue'), meta: { title: '施工案例' } },
  { path: '/articles', name: 'Articles', component: () => import('@/views/Articles.vue'), meta: { title: '养护知识' } },
  { path: '/articles/:id', name: 'ArticleDetail', component: () => import('@/views/ArticleDetail.vue'), meta: { title: '文章详情' } },
  { path: '/contact', name: 'Contact', component: () => import('@/views/Contact.vue'), meta: { title: '联系我们' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

export default router
