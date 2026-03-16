<template>
  <div class="user-center">
    <div class="container">
      <div class="user-card">
        <div class="avatar-section">
          <el-avatar :size="80">{{ userStore.user?.nickname?.charAt(0) || 'U' }}</el-avatar>
          <h2>{{ userStore.user?.nickname }}</h2>
          <p>@{{ userStore.user?.username }}</p>
        </div>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleUpdate">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { updateUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  nickname: '',
  phone: ''
})

const rules = {
  nickname: [{ max: 50, message: '昵称不能超过50个字符', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

onMounted(() => {
  form.nickname = userStore.user?.nickname || ''
  form.phone = userStore.user?.phone || ''
})

async function handleUpdate() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await updateUserInfo(form)
    userStore.setUser(res.data, userStore.token)
    ElMessage.success('修改成功')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.user-center {
  padding: 20px 0;
}

.user-card {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  max-width: 500px;
  margin: 0 auto;
}

.avatar-section {
  text-align: center;
  margin-bottom: 30px;
}

.avatar-section h2 {
  margin-top: 16px;
  font-size: 20px;
}

.avatar-section p {
  color: #999;
  margin-top: 4px;
}
</style>
