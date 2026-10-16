import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/services' },
  { path: '/services', name: 'H5Services', component: () => import('@/views/customer/ServiceList.vue') },
  { path: '/service/:id', name: 'H5ServiceDetail', component: () => import('@/views/customer/ServiceDetail.vue') },
  { path: '/booking', name: 'H5Booking', component: () => import('@/views/customer/Booking.vue') },
  { path: '/orders', name: 'H5MyOrders', component: () => import('@/views/customer/MyOrders.vue') },
  { path: '/tech/login', name: 'H5TechLogin', component: () => import('@/views/tech/Login.vue') },
  { path: '/tech/orders', name: 'H5TechOrders', component: () => import('@/views/tech/Orders.vue') },
  { path: '/tech/orders/:id', name: 'H5TechOrderDetail', component: () => import('@/views/tech/OrderDetail.vue') },
  { path: '/supplier/login', name: 'H5SupplierLogin', component: () => import('@/views/supplier/Login.vue') },
  { path: '/supplier/home', name: 'H5SupplierHome', component: () => import('@/views/supplier/Home.vue') },
  { path: '/supplier/deliveries', name: 'H5SupplierDeliveries', component: () => import('@/views/supplier/Deliveries.vue') },
  { path: '/supplier/deliveries/new', name: 'H5SupplierNewDelivery', component: () => import('@/views/supplier/NewDelivery.vue') },
  { path: '/supplier/deliveries/:id', name: 'H5SupplierDeliveryDetail', component: () => import('@/views/supplier/DeliveryDetail.vue') },
  { path: '/supplier/parts', name: 'H5SupplierParts', component: () => import('@/views/supplier/Parts.vue') },
  { path: '/supplier/statements', name: 'H5SupplierStatements', component: () => import('@/views/supplier/Statements.vue') }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to) => {
  if (to.path.startsWith('/tech/orders')) {
    const token = localStorage.getItem('auto710_tech_token')
    if (!token) return '/tech/login'
  }
  if (to.path.startsWith('/supplier') && to.path !== '/supplier/login') {
    const token = localStorage.getItem('auto710_supplier_token')
    if (!token) return '/supplier/login'
  }
})

export default router
