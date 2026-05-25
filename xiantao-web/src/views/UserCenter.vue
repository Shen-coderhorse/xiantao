<template>
  <div class="user-center">
    <div class="container">
      <el-row :gutter="20">
        <el-col :xs="24" :md="16">
          <div class="user-card">
            <div class="avatar-section">
              <el-avatar :size="80">{{ userStore.user?.nickname?.charAt(0) || 'U' }}</el-avatar>
              <h2>{{ userStore.user?.nickname }}</h2>
              <p>@{{ userStore.user?.username }}</p>
              <div class="balance-info" v-if="creditInfo">
                <div class="balance-item">
                  <span class="label">余额</span>
                  <span class="value">¥{{ userStore.user?.balance || '0.00' }}</span>
                </div>
                <div class="balance-item" @click="router.push('/credit')">
                  <span class="label">信用分</span>
                  <span class="value" :style="{ color: creditInfo.creditLevelColor }">{{ creditInfo.creditScore }}</span>
                  <el-tag size="small" :color="creditInfo.creditLevelColor">{{ creditInfo.creditLevel }}</el-tag>
                </div>
              </div>
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
        </el-col>

        <el-col :xs="24" :md="8">
          <el-card class="menu-card">
            <template #header>
              <h3>我的功能</h3>
            </template>
            <div class="menu-list">
              <div class="menu-item" @click="router.push('/orders')">
                <el-icon :size="20"><List /></el-icon>
                <span>我的订单</span>
              </div>
              <div class="menu-item" @click="router.push('/my-products')">
                <el-icon :size="20"><Goods /></el-icon>
                <span>我的商品</span>
              </div>
              <div class="menu-item" @click="router.push('/address')">
                <el-icon :size="20"><Location /></el-icon>
                <span>地址管理</span>
              </div>
              <div class="menu-item" @click="router.push('/credit')">
                <el-icon :size="20"><Star /></el-icon>
                <span>我的信用</span>
              </div>
              <div class="menu-item" @click="router.push('/transactions')">
                <el-icon :size="20"><Wallet /></el-icon>
                <span>交易流水</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { getUserCredit } from '@/api/rating'
import { updateUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { Goods, List, Location, Star, Wallet } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const creditInfo = ref(null)

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
  loadCreditInfo()
})

async function loadCreditInfo() {
  try {
    const res = await getUserCredit()
    creditInfo.value = res.data
  } catch (e) {
    console.error(e)
  }
}

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
  background: #f5f7fa;
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
}

.user-card {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
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

.balance-info {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed #eee;
}

.balance-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.balance-item:hover {
  transform: translateY(-2px);
}

.balance-item .label {
  font-size: 12px;
  color: #999;
}

.balance-item .value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.menu-card {
  border-radius: 8px;
}

.menu-card h3 {
  margin: 0;
  font-size: 16px;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  color: #333;
}

.menu-item:hover {
  background: #f0f9ff;
  color: #409eff;
}

@media (max-width: 768px) {
  .container {
    padding: 0 12px;
  }

  .user-card {
    padding: 20px;
    margin-bottom: 16px;
  }

  .balance-info {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
