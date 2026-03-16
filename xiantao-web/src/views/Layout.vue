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

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.search-box {
  flex: 1;
  max-width: 500px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  color: #333;
}

.main {
  flex: 1;
  padding: 20px 0;
}

.footer {
  background: #fff;
  padding: 20px;
  text-align: center;
  color: #666;
  border-top: 1px solid #eee;
}
</style>
