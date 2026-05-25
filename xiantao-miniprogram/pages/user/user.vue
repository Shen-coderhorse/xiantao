<template>
  <view class="user-page">
    <view v-if="!isLoggedIn" class="login-prompt">
      <view class="login-info">
        <text class="title">请先登录</text>
        <text class="desc">登录后享受更多功能</text>
      </view>
      <button type="primary" @click="goLogin">去登录</button>
    </view>

    <view v-else class="user-content">
      <view class="user-header">
        <view class="user-info">
          <view class="avatar">{{ userInfo.nickname?.charAt(0) || 'U' }}</view>
          <view class="info">
            <text class="nickname">{{ userInfo.nickname }}</text>
            <text class="username">@{{ userInfo.username }}</text>
          </view>
        </view>
        <view class="credit-badge" :style="{ background: creditInfo?.creditLevelColor }">
          <text class="score">{{ creditInfo?.creditScore || 500 }}</text>
          <text class="level">{{ creditInfo?.creditLevel }}</text>
        </view>
      </view>

      <view class="balance-section">
        <view class="balance-item">
          <text class="value">¥{{ userInfo.balance || '0.00' }}</text>
          <text class="label">余额</text>
        </view>
      </view>

      <view class="menu-section">
        <view class="menu-item" @click="navigateTo('/pages/orders/orders')">
          <text class="menu-icon">📦</text>
          <text class="menu-text">我的订单</text>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/address/address')">
          <text class="menu-icon">📍</text>
          <text class="menu-text">地址管理</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserCredit } from '@/api/rating';
import { getUserInfo } from '@/api/user';

export default {
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      creditInfo: null
    }
  },
  onShow() {
    this.checkLogin()
  },
  methods: {
    checkLogin() {
      const token = uni.getStorageSync('token')
      if (token) {
        this.isLoggedIn = true
        this.loadUserInfo()
        this.loadCreditInfo()
      } else {
        this.isLoggedIn = false
      }
    },
    async loadUserInfo() {
      try {
        const res = await getUserInfo()
        this.userInfo = res.data
      } catch (e) {
        console.error(e)
      }
    },
    async loadCreditInfo() {
      try {
        const res = await getUserCredit()
        this.creditInfo = res.data
      } catch (e) {
        console.error(e)
      }
    },
    goLogin() {
      uni.navigateTo({ url: '/pages/login/login' })
    },
    navigateTo(url) {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        return
      }
      uni.navigateTo({ url })
    }
  }
}
</script>

<style scoped>
.user-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.login-prompt {
  background: #fff;
  margin: 12px;
  padding: 40px 20px;
  border-radius: 8px;
  text-align: center;
}

.login-info .title {
  display: block;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.login-info .desc {
  display: block;
  color: #999;
  font-size: 14px;
  margin-bottom: 20px;
}

.user-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-header {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  padding: 30px 20px;
  color: #fff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255,255,255,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
}

.info .nickname {
  display: block;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 4px;
}

.info .username {
  display: block;
  font-size: 12px;
  opacity: 0.8;
}

.credit-badge {
  margin-top: 16px;
  padding: 8px 16px;
  background: rgba(255,255,255,0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.credit-badge .score {
  font-size: 20px;
  font-weight: bold;
}

.credit-badge .level {
  font-size: 12px;
  opacity: 0.9;
}

.balance-section {
  background: #fff;
  padding: 20px;
  display: flex;
  justify-content: center;
}

.balance-item {
  text-align: center;
}

.balance-item .value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.balance-item .label {
  display: block;
  font-size: 12px;
  color: #999;
}

.menu-section {
  background: #fff;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid #f5f7fa;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 20px;
}

.menu-text {
  font-size: 14px;
  color: #333;
}
</style>
