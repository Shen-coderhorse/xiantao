<template>
  <div class="transactions-page">
    <div class="container">
      <div class="page-header">
        <h2>交易流水</h2>
        <el-button @click="$router.back()">返回</el-button>
      </div>

      <div v-loading="loading" class="transactions-content">
        <div v-if="transactions.length > 0" class="transactions-list">
          <div v-for="tx in transactions" :key="tx.id" class="transaction-item">
            <div class="transaction-header">
              <div class="tx-type">
                <el-tag :type="getTransactionTypeColor(tx.transactionType)" effect="dark">
                  {{ tx.transactionTypeText }}
                </el-tag>
              </div>
              <div class="tx-amount" :class="{ 'income': tx.toUserId === userId, 'expense': tx.fromUserId === userId }">
                {{ tx.toUserId === userId ? '+' : '-' }}¥{{ tx.amount }}
              </div>
            </div>
            <div class="transaction-body">
              <div class="tx-info">
                <div class="tx-users">
                  <span>{{ tx.fromUserName }}</span>
                  <el-icon><Right /></el-icon>
                  <span>{{ tx.toUserName }}</span>
                </div>
                <div class="tx-order">
                  订单号: {{ tx.orderNo }}
                </div>
              </div>
              <div class="tx-time">{{ tx.createTime }}</div>
            </div>
            <div class="tx-remark">{{ tx.remark }}</div>
            <div class="tx-status">
              <el-tag size="small" :type="tx.status === 1 ? 'success' : 'info'">
                {{ tx.statusText }}
              </el-tag>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无交易记录" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { getMyTransactions } from '@/api/transaction'
import { useUserStore } from '@/stores/user'
import { Right } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()
const userId = userStore.user?.id
const transactions = ref([])
const loading = ref(false)

onMounted(() => {
  loadTransactions()
})

async function loadTransactions() {
  loading.value = true
  try {
    const res = await getMyTransactions()
    transactions.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function getTransactionTypeColor(type) {
  switch (type) {
    case 1: return 'primary'
    case 2: return 'warning'
    case 3: return 'success'
    case 4: return 'info'
    default: return ''
  }
}
</script>

<style scoped>
.transactions-page {
  padding: 20px 0;
  background: transparent;
  min-height: 100vh;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #f1f5f9;
  font-weight: 700;
  position: relative;
  padding-left: 14px;
}

.page-header h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  border-radius: 2px;
  background: var(--gradient-primary);
}

.transactions-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.transactions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.transaction-item {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 16px;
  box-shadow: var(--shadow-sm);
  border: 1px solid transparent;
  transition: var(--transition-base);
}

.transaction-item:hover {
  transform: translateY(-3px);
  border-color: rgba(14, 165, 233, 0.22);
  box-shadow: 0 10px 26px rgba(14, 165, 233, 0.12);
}

.transaction-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px dashed var(--border-color);
}

.tx-amount {
  font-size: 18px;
  font-weight: bold;
}

.tx-amount.income {
  color: var(--success-color);
}

.tx-amount.expense {
  color: var(--danger-color);
}

.transaction-body {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.tx-info {
  flex: 1;
}

.tx-users {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.tx-order {
  font-size: 12px;
  color: var(--text-light);
}

.tx-time {
  font-size: 12px;
  color: var(--text-light);
  white-space: nowrap;
}

.tx-remark {
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-light);
  padding: 6px 10px;
  border-radius: var(--radius-sm);
  margin-bottom: 8px;
}

.tx-status {
  text-align: right;
}

@media (max-width: 768px) {
  .container {
    padding: 0 12px;
  }

  .page-header h2 {
    font-size: 18px;
  }

  .transaction-item {
    padding: 12px;
  }

  .tx-amount {
    font-size: 16px;
  }

  .transaction-body {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
