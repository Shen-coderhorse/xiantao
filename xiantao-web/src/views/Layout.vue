<template>
  <div class="layout">
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="router.push('/')">
          <span class="logo-icon">🛒</span>
          <span class="logo-text">闲淘</span>
        </div>
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品..."
            @keyup.enter="handleSearch"
            clearable
          >
            <template #append>
              <el-button @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
        <div class="nav-actions">
          <template v-if="userStore.isLoggedIn">
            <el-button type="primary" @click="router.push('/publish')">发布商品</el-button>
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32">{{ userStore.user?.nickname?.charAt(0) || 'U' }}</el-avatar>
                <span class="username">{{ userStore.user?.nickname }}</span>
                <el-tag v-if="userStore.isAdmin" type="danger" size="small" style="margin-left: 4px">管理员</el-tag>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="user">个人中心</el-dropdown-item>
                  <el-dropdown-item command="products">我的商品</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" command="admin" divided>
                    <el-icon><Setting /></el-icon>
                    商品管理
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button @click="router.push('/login')">登录</el-button>
            <el-button type="primary" @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view v-slot="{ Component }">
        <keep-alive include="MyProducts">
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </main>
    <footer class="footer">
      <p>闲淘二手交易平台 © 2024</p>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Setting } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')

function handleSearch() {
  router.push({ path: '/', query: { keyword: searchKeyword.value } })
}

function handleCommand(command) {
  switch (command) {
    case 'user':
      router.push('/user')
      break
    case 'products':
      router.push('/my-products')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'admin':
      router.push('/admin/products')
      break
    case 'logout':
      userStore.logout()
      router.push('/')
      break
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ---------- 扁平深青吸顶导航 ---------- */
.header {
  background: var(--gradient-nav);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 16px rgba(15, 118, 110, 0.28);
  border-bottom: 1px solid rgba(20, 184, 166, 0.25);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  min-height: 64px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}

.logo-icon {
  font-size: 26px;
  filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.25));
}

.logo-text {
  font-size: 24px;
  font-weight: 800;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #7dd3fc 0%, #5eead4 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.search-box {
  flex: 1;
  max-width: 500px;
}

.search-box :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: none;
  border-radius: 10px;
  transition: var(--transition-base);
}

.search-box :deep(.el-input__wrapper:hover),
.search-box :deep(.el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(94, 234, 212, 0.6);
}

.search-box :deep(.el-input__inner) { color: #fff; }
.search-box :deep(.el-input__inner::placeholder) { color: rgba(255, 255, 255, 0.65); }

.search-box :deep(.el-input-group__append) {
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-left: none;
  box-shadow: none;
}
.search-box :deep(.el-input-group__append .el-button) { color: #fff; }

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

/* 未登录时的普通按钮在深色头上用描边白字 */
.nav-actions :deep(.el-button:not(.el-button--primary)) {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.25);
  color: #fff;
}
.nav-actions :deep(.el-button:not(.el-button--primary):hover) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.45);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #fff;
  padding: 4px 8px;
  border-radius: 10px;
  transition: background 0.25s ease;
}
.user-info:hover { background: rgba(255, 255, 255, 0.12); }

.username { color: #fff; font-size: 14px; }

.main {
  flex: 1;
  padding: 20px 0;
  background: var(--app-canvas);
  background-attachment: fixed;
}

.footer {
  background: var(--gradient-nav);
  padding: 24px;
  text-align: center;
  color: rgba(255, 255, 255, 0.75);
  border-top: 1px solid rgba(20, 184, 166, 0.2);
  font-size: 13px;
}
</style>
