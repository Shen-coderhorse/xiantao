<template>
  <div class="credit-page">
    <div class="container">
      <div class="page-header">
        <h2>我的信用</h2>
        <el-button @click="$router.back()">返回</el-button>
      </div>

      <div v-loading="loading" class="credit-content">
        <el-card class="credit-score-card">
          <div class="score-display">
            <div class="score-circle" :style="{ borderColor: creditInfo?.creditLevelColor }">
              <div class="score-value" :style="{ color: creditInfo?.creditLevelColor }">
                {{ creditInfo?.creditScore || 500 }}
              </div>
            </div>
            <div class="score-level">
              <el-tag size="large" :color="creditInfo?.creditLevelColor" effect="dark">
                {{ creditInfo?.creditLevel }}
              </el-tag>
            </div>
          </div>
        </el-card>

        <el-card class="credit-stats-card">
          <h3>信用统计</h3>
          <el-row :gutter="20">
            <el-col :xs="12" :md="6">
              <div class="stat-item">
                <div class="stat-value">{{ creditInfo?.totalTransactions || 0 }}</div>
                <div class="stat-label">总交易次数</div>
              </div>
            </el-col>
            <el-col :xs="12" :md="6">
              <div class="stat-item">
                <div class="stat-value" style="color: #67C23A">{{ creditInfo?.completedTransactions || 0 }}</div>
                <div class="stat-label">成功交易</div>
              </div>
            </el-col>
            <el-col :xs="12" :md="6">
              <div class="stat-item">
                <div class="stat-value" style="color: #F56C6C">{{ creditInfo?.cancelledTransactions || 0 }}</div>
                <div class="stat-label">取消订单</div>
              </div>
            </el-col>
            <el-col :xs="12" :md="6">
              <div class="stat-item">
                <div class="stat-value" style="color: #E6A23C">{{ creditInfo?.violationCount || 0 }}</div>
                <div class="stat-label">违规次数</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <el-card class="ratings-card">
          <h3>收到的评价</h3>
          <div v-if="receivedRatings.length > 0" class="ratings-list">
            <div v-for="rating in receivedRatings" :key="rating.id" class="rating-item">
              <div class="rating-header">
                <el-avatar :size="40">{{ rating.reviewerName?.charAt(0) || 'U' }}</el-avatar>
                <div class="rating-user-info">
                  <div class="reviewer-name">{{ rating.reviewerName }}</div>
                  <div class="rating-time">{{ rating.createTime }}</div>
                </div>
                <el-tag :type="getRatingType(rating.rating)">
                  {{ rating.ratingText }}
                </el-tag>
              </div>
              <div class="rating-content">{{ rating.content }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无评价" />
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getUserCredit, getUserRatings } from '@/api/rating'
import { onMounted, ref } from 'vue'

const creditInfo = ref(null)
const receivedRatings = ref([])
const loading = ref(false)

onMounted(() => {
  loadCreditInfo()
  loadReceivedRatings()
})

async function loadCreditInfo() {
  loading.value = true
  try {
    const res = await getUserCredit()
    creditInfo.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function loadReceivedRatings() {
  try {
    const res = await getUserRatings()
    receivedRatings.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

function getRatingType(rating) {
  switch (rating) {
    case 3: return 'success'
    case 2: return 'warning'
    case 1: return 'danger'
    default: return 'info'
  }
}
</script>

<style scoped>
.credit-page {
  padding: 20px 0;
  background: #f5f7fa;
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
  color: #333;
}

.credit-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.credit-score-card {
  background: #fff;
  border-radius: 8px;
  text-align: center;
  padding: 20px;
}

.score-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 6px solid #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: scoreAnimation 1s ease-in-out;
}

.score-value {
  font-size: 40px;
  font-weight: bold;
  color: #409eff;
}

.score-level .el-tag {
  padding: 8px 16px;
}

@keyframes scoreAnimation {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.credit-stats-card {
  background: #fff;
  border-radius: 8px;
}

.credit-stats-card h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 12px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.ratings-card {
  background: #fff;
  border-radius: 8px;
}

.ratings-card h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.ratings-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rating-item {
  padding: 16px;
  border-bottom: 1px dashed #eee;
}

.rating-item:last-child {
  border-bottom: none;
}

.rating-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.rating-user-info {
  flex: 1;
}

.reviewer-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.rating-time {
  font-size: 12px;
  color: #999;
}

.rating-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  padding-left: 52px;
}

@media (max-width: 768px) {
  .container {
    padding: 0 12px;
  }

  .page-header h2 {
    font-size: 18px;
  }

  .score-circle {
    width: 100px;
    height: 100px;
    border-width: 4px;
  }

  .score-value {
    font-size: 32px;
  }

  .stat-item {
    padding: 12px;
  }

  .stat-value {
    font-size: 20px;
  }

  .rating-content {
    padding-left: 0;
  }
}
</style>
