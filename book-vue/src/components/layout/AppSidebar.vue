<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  DataAnalysis,
  Notebook,
  User,
  ShoppingCart,
  TrendCharts,
  SwitchButton,
  Setting
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const menuItems = [
  { path: '/', icon: DataAnalysis, title: '仪表盘' },
  { path: '/books', icon: Notebook, title: '图书管理' },
  { path: '/customers', icon: User, title: '客户管理' },
  { path: '/sales', icon: ShoppingCart, title: '销售管理' },
  { path: '/statistics', icon: TrendCharts, title: '统计报表' }
]

async function handleLogout() {
  await userStore.logout()
  router.push('/login')
}
</script>

<template>
  <aside class="app-sidebar">
    <div class="sidebar-header">
      <div class="logo-wrapper">
        <el-icon class="logo-icon"><Notebook /></el-icon>
      </div>
      <span class="logo-text">书店管理</span>
    </div>

    <nav class="sidebar-nav">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        class="nav-item"
        :class="{ active: route.path === item.path }"
      >
        <el-icon class="nav-icon"><component :is="item.icon" /></el-icon>
        <span class="nav-title">{{ item.title }}</span>
      </router-link>
    </nav>

    <div class="sidebar-footer">
      <router-link to="/account" class="user-info account-link">
        <el-icon class="user-avatar"><User /></el-icon>
        <span class="user-name">{{ userStore.username || '用户' }}</span>
        <el-icon class="settings-icon"><Setting /></el-icon>
      </router-link>
      <button class="logout-btn" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        <span>退出登录</span>
      </button>
    </div>
  </aside>
</template>

<style scoped>
.app-sidebar {
  width: 220px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 100;

  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-right: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 1px 0 20px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.sidebar-header {
  height: 72px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.logo-wrapper {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12) 0%, rgba(99, 102, 241, 0.1) 100%);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon {
  font-size: 22px;
  color: #3b82f6;
}

.logo-text {
  font-size: 17px;
  font-weight: 600;
  color: #0f172a;
  letter-spacing: 0.3px;
}

.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  margin-bottom: 4px;
  border-radius: 10px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.nav-item:hover {
  background: rgba(0, 0, 0, 0.04);
  color: #334155;
}

.nav-item.active {
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.1) 0%, rgba(99, 102, 241, 0.08) 100%);
  color: #2563eb;
  border: 1px solid rgba(59, 130, 246, 0.12);
  font-weight: 600;
}

.nav-icon {
  font-size: 18px;
}

.nav-title {
  font-size: 14px;
  font-weight: 500;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  margin-bottom: 10px;
  background: rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 10px;
}

.account-link {
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.account-link:hover {
  background: rgba(59, 130, 246, 0.06);
  border-color: rgba(59, 130, 246, 0.15);
}

.user-avatar {
  font-size: 18px;
  color: #3b82f6;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #334155;
  flex: 1;
}

.settings-icon {
  font-size: 14px;
  color: #94a3b8;
  transition: color 0.2s;
}

.account-link:hover .settings-icon {
  color: #3b82f6;
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: rgba(239, 68, 68, 0.06);
  border: 1px solid rgba(239, 68, 68, 0.12);
  border-radius: 10px;
  color: #ef4444;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.logout-btn:hover {
  background: rgba(239, 68, 68, 0.12);
  border-color: rgba(239, 68, 68, 0.25);
}
</style>
