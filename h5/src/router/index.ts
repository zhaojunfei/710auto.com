import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/services' },
  { path: '/services', name: 'H5Services', component: () => import('@/views/customer/ServiceList.vue') },
  { path: '/service/:id', name: 'H5ServiceDetail', component: () => import('@/views/customer/ServiceDetail.vue') },
  { path: '/booking', name: 'H5Booking', component: () => import('@/views/customer/Booking.vue') },
  { path: '/orders', name: 'H5MyOrders', component: () => import('@/views/customer/MyOrders.vue') },
  { path: '/tech/login', name: 'H5TechLogin', component: () => import('@/views/tech/Login.vue') },
  { path: '/tech/orders', name: 'H5TechOrders', component: () => import('@/views/tech/Orders.vue') },
  { path: '/tech/orders/:id', name: 'H5TechOrderDetail', component: () => import('@/views/tech/OrderDetail.vue') }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to) => {
  if (to.path.startsWith('/tech/orders')) {
    const token = localStorage.getItem('auto710_tech_token')
    if (!token) return '/tech/login'
  }
})

export default router
