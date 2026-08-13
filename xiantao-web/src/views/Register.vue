<template>
  <div class="register-page">
    <div class="register-box">
      <h2>注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号（选填）" size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleRegister" style="width: 100%">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  phone: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await register(form)
    userStore.setUser(res.data, res.data.token)
    ElMessage.success('注册成功')
    router.push('/')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #0f172a 0%, #134e4a 100%);
  position: relative;
  overflow: hidden;
}

.register-page::before {
  content: '';
  position: absolute;
  top: -120px;
  right: -120px;
  width: 360px;
  height: 360px;
  background: radial-gradient(circle, rgba(20, 184, 166, 0.3) 0%, transparent 70%);
}

.register-page::after {
  content: '';
  position: absolute;
  bottom: -120px;
  left: -120px;
  width: 320px;
  height: 320px;
  background: radial-gradient(circle, rgba(14, 165, 233, 0.24) 0%, transparent 70%);
}

.register-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.96);
  border-radius: var(--radius-lg);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.4);
  position: relative;
  z-index: 1;
  animation: fadeInUp 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.register-box h2 {
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
