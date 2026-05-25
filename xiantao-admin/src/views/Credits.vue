<template>
  <el-card>
    <template #header><span>信用管理</span></template>
    <el-table :data="credits" v-loading="loading">
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="creditScore" label="信用分" width="100">
        <template #default="{ row }">
          <span :style="{ color: getLevelColor(row.creditScore) }">{{ row.creditScore }}</span>
        </template>
      </el-table-column>
      <el-table-column label="信用等级" width="120">
        <template #default="{ row }">
          <el-tag :color="getLevelColor(row.creditScore)">{{ getLevel(row.creditScore) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalTransactions" label="总交易" width="100" />
      <el-table-column prop="completedTransactions" label="成功" width="80" />
      <el-table-column prop="goodRatingCount" label="好评" width="80" />
      <el-table-column prop="badRatingCount" label="差评" width="80" />
    </el-table>
  </el-card>
</template>

<script setup>
import request from '@/utils/request'
import { onMounted, ref } from 'vue'

const credits = ref([])
const loading = ref(false)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/admin/credit/list')
    credits.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function getLevel(score) {
  if (score >= 800) return '极好'
  if (score >= 700) return '优秀'
  if (score >= 600) return '良好'
  if (score >= 500) return '一般'
  return '较差'
}

function getLevelColor(score) {
  if (score >= 800) return '#F5A623'
  if (score >= 700) return '#67C23A'
  if (score >= 600) return '#409EFF'
  if (score >= 500) return '#E6A23C'
  return '#F56C6C'
}
</script>
