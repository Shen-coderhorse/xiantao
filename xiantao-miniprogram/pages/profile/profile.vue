<template>
  <view class="profile-page">
    <view class="header">
      <view class="avatar">👤</view>
      <text class="nickname">{{ userInfo.nickname || userInfo.username || '未登录' }}</text>
      <button class="btn-login" size="mini" @click="goLogin" v-if="!userInfo.username">登录</button>
    </view>

    <view class="stats">
      <view class="stat-item">
        <text class="num">¥{{ userInfo.balance || 0 }}</text>
        <text class="label">余额</text>
      </view>
      <view class="stat-item">
        <text class="num">{{ credit.creditScore || 500 }}</text>
        <text class="label">信用分</text>
      </view>
      <view class="stat-item">
        <text class="num">{{ credit.totalTransactions || 0 }}</text>
        <text class="label">交易数</text>
      </view>
    </view>

    <view class="menu-list">
      <view class="menu-item" @click="navTo('/pages/orders/orders')">
        <text>📦 我的订单</text><text>›</text>
      </view>
      <view class="menu-item" @click="navTo('/pages/address/address')">
        <text>📍 收货地址</text><text>›</text>
      </view>
      <view class="menu-item" @click="navTo('/pages/publish/publish')">
        <text>📝 发布商品</text><text>›</text>
      </view>
      <view class="menu-item" @click="handleLogout" v-if="userInfo.username">
        <text>🚪 退出登录</text><text>›</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserInfo, getUserCredit } from '@/api/index'

export default {
  data() {
    return {
      userInfo: {},
      credit: {}
    }
  },
  onShow() {
    this.loadData()
  },
  methods: {
    async loadData() {
      const token = uni.getStorageSync('token')
      if (!token) return
      try {
        const [userRes, creditRes] = await Promise.all([getUserInfo(), getUserCredit()])
        this.userInfo = userRes.data || {}
        this.credit = creditRes.data || {}
      } catch (e) {
        console.error(e)
      }
    },
    goLogin() {
      uni.navigateTo({ url: '/pages/login/login' })
    },
    navTo(url) {
      const token = uni.getStorageSync('token')
      if (!token && url !== '/pages/login/login') {
        uni.showToast({ title: '请先登录', icon: 'none' })
        setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1500)
        return
      }
      uni.navigateTo({ url })
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
.profile-page { min-height: 100vh; background: #f5f7fa; }
.header { background: linear-gradient(135deg, #409eff, #67c23a); padding: 40px 24px 30px; text-align: center; color: #fff; }
.avatar { font-size: 64px; margin-bottom: 12px; }
.nickname { font-size: 18px; display: block; margin-bottom: 8px; }
.btn-login { background: rgba(255,255,255,0.3); color: #fff; border: none; border-radius: 16px; font-size: 12px; }
.stats { display: flex; background: #fff; margin: -16px 16px 16px; border-radius: 8px; padding: 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.stat-item { flex: 1; text-align: center; }
.num { font-size: 20px; font-weight: bold; color: #333; display: block; }
.label { font-size: 12px; color: #999; }
.menu-list { background: #fff; margin: 0 16px; border-radius: 8px; }
.menu-item { display: flex; justify-content: space-between; padding: 16px; border-bottom: 1px solid #f5f5f5; font-size: 15px; }
.menu-item:last-child { border-bottom: none; }
</style>
