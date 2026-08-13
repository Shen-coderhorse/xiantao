<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2>闲淘管理后台</h2>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" @click="handleLogin" :loading="loading">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import request from '@/utils/request'
import { Lock, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: '123456'
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
    const res = await request.post('/auth/login', form)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data))
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
  height: 100vh;
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

.login-card {
  width: 400px;
  padding: 40px;
  position: relative;
  z-index: 1;
  border: none;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.4);
  animation: fadeInUp 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: var(--text-primary);
  font-weight: 700;
  letter-spacing: 1px;
}
</style>
