<template>
  <div class="app">
    <router-view />
    <nav class="tabbar" v-if="showTab">
      <router-link to="/services" :class="{ active: $route.path === '/services' }">服务</router-link>
      <router-link to="/booking" :class="{ active: $route.path === '/booking' }">预约</router-link>
      <router-link to="/orders" :class="{ active: $route.path === '/orders' }">我的工单</router-link>
      <router-link to="/tech/login" :class="{ active: $route.path.startsWith('/tech') }">技师端</router-link>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const showTab = computed(() => !route.path.startsWith('/tech/orders/'))
</script>
