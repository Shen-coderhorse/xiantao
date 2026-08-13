<template>
  <div class="login-page">
    <div class="login-box">
      <h2>登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(form)
    userStore.setUser(res.data, res.data.token)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #0f172a 0%, #134e4a 100%);
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: '';
  position: absolute;
  top: -120px;
  right: -120px;
  width: 360px;
  height: 360px;
  background: radial-gradient(circle, rgba(20, 184, 166, 0.3) 0%, transparent 70%);
}

.login-page::after {
  content: '';
  position: absolute;
  bottom: -120px;
  left: -120px;
  width: 320px;
  height: 320px;
  background: radial-gradient(circle, rgba(14, 165, 233, 0.24) 0%, transparent 70%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.96);
  border-radius: var(--radius-lg);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.4);
  position: relative;
  z-index: 1;
  animation: fadeInUp 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: var(--text-primary);
  font-weight: 700;
  letter-spacing: 1px;
}

.footer {
  text-align: center;
  margin-top: 20px;
  color: var(--text-secondary);
}

.footer a {
  color: var(--brand-primary);
  text-decoration: none;
  font-weight: 500;
}
</style>
