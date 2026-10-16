import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/login/index.vue'), meta: { public: true } },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/index.vue'), meta: { title: '数据看板' } },
      { path: 'workorder', name: 'WorkOrder', component: () => import('@/views/workorder/index.vue'), meta: { title: '预约工单' } },
      { path: 'service', name: 'Service', component: () => import('@/views/service/index.vue'), meta: { title: '服务项目' } },
      { path: 'tire', name: 'Tire', component: () => import('@/views/tire/index.vue'), meta: { title: '存胎寄存' } },
      { path: 'inventory', name: 'Inventory', component: () => import('@/views/inventory/index.vue'), meta: { title: '配件库存' } },
      { path: 'member', name: 'Member', component: () => import('@/views/member/index.vue'), meta: { title: '会员营销' } },
      { path: 'cms', name: 'Cms', component: () => import('@/views/cms/index.vue'), meta: { title: '官网CMS' } },
      { path: 'store', name: 'Store', component: () => import('@/views/store/index.vue'), meta: { title: '门店工位' } },
      { path: 'wechat', name: 'Wechat', component: () => import('@/views/wechat/index.vue'), meta: { title: '公众号' } },
      { path: 'system/user', name: 'SysUser', component: () => import('@/views/system/user.vue'), meta: { title: '用户角色' } },
      { path: 'system/config', name: 'SysConfig', component: () => import('@/views/system/config.vue'), meta: { title: '系统参数' } },
      { path: 'system/language', name: 'SysLanguage', component: () => import('@/views/system/language.vue'), meta: { title: '多语言配置' } },
      { path: 'system/menu', name: 'SysMenu', component: () => import('@/views/system/menu.vue'), meta: { title: '菜单可视化' } },
      { path: 'system/storage', name: 'SysStorage', component: () => import('@/views/system/storage.vue'), meta: { title: '对象存储' } },
      { path: 'system/audit', name: 'SysAudit', component: () => import('@/views/system/audit.vue'), meta: { title: '审计日志' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('auto710_token')
  if (to.meta.public) {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
