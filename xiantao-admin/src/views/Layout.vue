<template>
  <el-container class="admin-layout">
    <el-aside width="232px" class="sidebar">
      <div class="logo">
        <div class="logo-mark">🛒</div>
        <div class="logo-text">
          <h1>闲淘管理</h1>
          <span>校园二手交易平台</span>
        </div>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/credits">
          <el-icon><Star /></el-icon>
          <span>信用管理</span>
        </el-menu-item>
        <el-menu-item index="/transactions">
          <el-icon><Wallet /></el-icon>
          <span>交易流水</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <div class="version-badge">v3.0 · 担保交易平台</div>
      </div>
    </el-aside>

    <el-container class="content-wrapper">
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ $route.meta.title }}</span>
        </div>
        <div class="header-right">
          <span class="avatar-ring">
            <el-avatar :size="30" class="user-avatar">{{ userStore.user?.nickname?.charAt(0) || 'A' }}</el-avatar>
          </span>
          <span class="username">{{ userStore.user?.nickname || '管理员' }}</span>
          <el-button size="small" @click="handleLogout">退出</el-button>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { DataAnalysis, Goods, List, Menu, Star, User, Wallet } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const userStore = {
  user: JSON.parse(localStorage.getItem('user') || '{}')
}

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

/* ---------- 扁平深青侧边栏 ---------- */
.sidebar {
  position: relative;
  display: flex;
  flex-direction: column;
  background: var(--gradient-sidebar);
  overflow: hidden;
}

/* 单个收敛的青绿光晕（比 mall 更扁平，不堆多层炫彩） */
.sidebar::before {
  content: '';
  position: absolute;
  top: -70px;
  right: -70px;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(20, 184, 166, 0.28) 0%, transparent 70%);
  pointer-events: none;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 72px;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.logo-mark {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: linear-gradient(135deg, #0ea5e9 0%, #14b8a6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 4px 14px rgba(14, 165, 233, 0.4);
}

.logo-text h1 {
  font-size: 17px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
  line-height: 1.2;
}

.logo-text span {
  font-size: 11px;
  color: rgba(148, 163, 184, 0.9);
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 8px 12px;
  position: relative;
  z-index: 1;
  --el-menu-bg-color: transparent;
  --el-menu-text-color: #94a3b8;
  --el-menu-hover-bg-color: rgba(20, 184, 166, 0.14);
  --el-menu-active-color: #ffffff;
}

.sidebar-menu :deep(.el-menu-item) {
  border-radius: 10px;
  margin-bottom: 4px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar-menu :deep(.el-menu-item:hover) {
  color: #ccfbf1;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #0ea5e9 0%, #14b8a6 100%);
  color: #fff;
  box-shadow: 0 4px 14px rgba(14, 165, 233, 0.38);
}

.sidebar-footer {
  padding: 16px 20px;
  position: relative;
  z-index: 1;
}

.version-badge {
  font-size: 11px;
  color: rgba(148, 163, 184, 0.7);
  text-align: center;
  padding: 6px 0;
  border: 1px solid rgba(148, 163, 184, 0.15);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.03);
}

/* ---------- 玻璃顶栏 ---------- */
.content-wrapper { overflow: hidden; }

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(14px);
  border-bottom: 1px solid rgba(20, 184, 166, 0.18);
  padding: 0 24px;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #e2e8f0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-ring {
  display: inline-flex;
  padding: 2px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0ea5e9, #14b8a6);
}

.user-avatar {
  background: rgba(14, 165, 233, 0.15);
  color: var(--brand-primary-dark);
  font-weight: 600;
}

.username {
  color: #cbd5e1;
  font-size: 14px;
}

.main-content {
  background: var(--app-canvas);
  background-attachment: fixed;
  padding: 20px;
  overflow-y: auto;
}
</style>
