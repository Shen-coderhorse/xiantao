<template>
  <div class="profile-page">
    <div class="header">
      <div class="avatar">👤</div>
      <span class="nickname">{{ userInfo.nickname || userInfo.username || '未登录' }}</span>
      <button class="btn-login" @click="goLogin" v-if="!userInfo.username">登录</button>
    </div>

    <div class="stats">
      <div class="stat-item">
        <span class="num">¥{{ userInfo.balance || 0 }}</span>
        <span class="label">余额</span>
      </div>
      <div class="stat-item">
        <span class="num">{{ credit.creditScore || 500 }}</span>
        <span class="label">信用分</span>
      </div>
      <div class="stat-item">
        <span class="num">{{ credit.totalTransactions || 0 }}</span>
        <span class="label">交易数</span>
      </div>
    </div>

    <div class="menu-list">
      <div class="menu-item" @click="$router.push('/orders')">
        <span>📦 我的订单</span><span>›</span>
      </div>
      <div class="menu-item" @click="$router.push('/address')">
        <span>📍 收货地址</span><span>›</span>
      </div>
      <div class="menu-item" @click="$router.push('/publish')">
        <span>📝 发布商品</span><span>›</span>
      </div>
      <div class="menu-item" @click="handleLogout" v-if="userInfo.username">
        <span>🚪 退出登录</span><span>›</span>
      </div>
    </div>
    
    <TabBar />
  </div>
</template>

<script>
import { getUserInfo, getUserCredit } from '@/api/index'
import TabBar from '@/components/TabBar.vue'
import uni from '@/uni-api'

export default {
  name: 'ProfilePage',
  components: { TabBar },
  data() {
    return {
      userInfo: {},
      credit: {}
    }
  },
  mounted() {
    const token = uni.getStorageSync('token')
    if (!token) return
    this.loadData()
  },
  methods: {
    async loadData() {
      const token = uni.getStorageSync('token')
      if (!token) return
      
      try {
        const [userRes, creditRes] = await Promise.all([
          getUserInfo(),
          getUserCredit()
        ])
        this.userInfo = userRes.data || {}
        this.credit = creditRes.data || {}
      } catch (e) {
        if (e.code !== 401) console.error(e)
      }
    },
    goLogin() {
      this.$router.push('/login')
    },
    handleLogout() {
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      this.userInfo = {}
      this.credit = {}
      uni.showToast({ title: '已退出登录', icon: 'success' })
    }
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 70px;
}

.header {
  background: linear-gradient(135deg, #409eff, #67c23a);
  padding: 40px 24px;
  display: flex;
  align-items: center;
  color: #fff;
}

.avatar {
  width: 60px;
  height: 60px;
  background: rgba(255,255,255,0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin-right: 16px;
}

.nickname {
  font-size: 18px;
  flex: 1;
}

.btn-login {
  background: rgba(255,255,255,0.3);
  color: #fff;
  border: none;
  border-radius: 16px;
  padding: 6px 16px;
  cursor: pointer;
}

.stats {
  display: flex;
  background: #fff;
  padding: 20px 0;
  margin: -20px 12px 12px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-item .num {
  display: block;
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-item .label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.menu-list {
  background: #fff;
  margin: 0 12px;
  border-radius: 8px;
  overflow: hidden;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}

.menu-item:last-child {
  border-bottom: none;
}
</style>
