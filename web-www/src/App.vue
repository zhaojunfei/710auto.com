<template>
  <SiteHeader />
  <main>
    <router-view />
  </main>
  <SiteFooter />
</template>

<script setup lang="ts">
import SiteHeader from './layout/Header.vue'
import SiteFooter from './layout/Footer.vue'
</script>
