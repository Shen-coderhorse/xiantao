<template>
  <view class="login-page">
    <view class="login-header">
      <text class="logo">🛒 闲淘</text>
      <text class="slogan">让闲置流动起来</text>
    </view>

    <view class="form-container">
      <view class="tab-bar">
        <view
          :class="['tab', { active: isLogin }]"
          @click="switchTab(true)"
        >登录</view>
        <view
          :class="['tab', { active: !isLogin }]"
          @click="switchTab(false)"
        >注册</view>
      </view>

      <view v-if="isLogin" class="form">
        <input v-model="loginForm.username" placeholder="用户名" class="input" />
        <input v-model="loginForm.password" type="password" placeholder="密码" class="input" />
        <button class="btn" @click="handleLogin">登录</button>
      </view>

      <view v-else class="form">
        <input v-model="registerForm.username" placeholder="用户名" class="input" />
        <input v-model="registerForm.password" type="password" placeholder="密码" class="input" />
        <input v-model="registerForm.phone" placeholder="手机号" class="input" />
        <button class="btn" @click="handleRegister">注册</button>
      </view>
    </view>
  </view>
</template>

<script>
import { login, register } from '@/api/index'

export default {
  data() {
    return {
      isLogin: true,
      loginForm: { username: '', password: '' },
      registerForm: { username: '', password: '', phone: '' }
    }
  },
  methods: {
    switchTab(isLogin) {
      this.isLogin = isLogin
    },
    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
        return
      }
      try {
        const res = await login(this.loginForm)
        uni.setStorageSync('token', res.data.token)
        uni.setStorageSync('userInfo', res.data)
        uni.switchTab({ url: '/pages/index/index' })
        uni.showToast({ title: '登录成功', icon: 'success' })
      } catch (e) {
        console.error(e)
      }
    },
    async handleRegister() {
      if (!this.registerForm.username || !this.registerForm.password || !this.registerForm.phone) {
        uni.showToast({ title: '请填写完整信息', icon: 'none' })
        return
      }
      try {
        const res = await register(this.registerForm)
        uni.setStorageSync('token', res.data.token)
        uni.switchTab({ url: '/pages/index/index' })
        uni.showToast({ title: '注册成功', icon: 'success' })
      } catch (e) {
        console.error(e)
      }
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #409eff, #67c23a);
  padding: 40px 24px;
}
.login-header {
  text-align: center;
  margin-bottom: 60px;
}
.logo {
  font-size: 48px;
  color: #fff;
  display: block;
}
.slogan {
  font-size: 16px;
  color: rgba(255,255,255,0.8);
  margin-top: 8px;
}
.form-container {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
}
.tab-bar {
  display: flex;
  margin-bottom: 24px;
  border-bottom: 1px solid #eee;
}
.tab {
  flex: 1;
  text-align: center;
  padding: 12px;
  font-size: 16px;
  color: #999;
}
.tab.active {
  color: #409eff;
  border-bottom: 2px solid #409eff;
}
.input {
  width: 100%;
  height: 44px;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 0 12px;
  margin-bottom: 12px;
  font-size: 14px;
  box-sizing: border-box;
}
.btn {
  width: 100%;
  height: 44px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  margin-top: 12px;
}
</style>
