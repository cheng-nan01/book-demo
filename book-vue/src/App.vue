<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppSidebar from '@/components/layout/AppSidebar.vue'

const route = useRoute()

const isLoginPage = computed(() => route.path === '/login')
</script>

<template>
  <div :class="['app-container', { 'login-page': isLoginPage }]">
    <template v-if="isLoginPage">
      <router-view />
    </template>

    <template v-else>
      <div class="app-bg"></div>
      <AppSidebar />
      <main class="main-content">
        <router-view />
      </main>
    </template>
  </div>
</template>

<style>
@import './assets/base.css';

.app-container {
  height: 100%;
  display: flex;
  position: relative;
}

.app-container.login-page {
  display: block;
}

/* Background */
.app-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    radial-gradient(ellipse at 20% 0%, rgba(59, 130, 246, 0.04) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 80%, rgba(99, 102, 241, 0.03) 0%, transparent 50%),
    linear-gradient(180deg, #f8fafc 0%, #f1f5f9 50%, #f8fafc 100%);
}

.main-content {
  flex: 1;
  margin-left: 220px;
  min-height: 100vh;
  overflow-y: auto;
  padding: 24px;
  position: relative;
  z-index: 1;
}
</style>
