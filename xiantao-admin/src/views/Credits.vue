<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>信用管理</span>
        <el-select v-model="filterLevel" placeholder="全部等级" clearable style="width: 150px" @change="loadData">
          <el-option label="极好(900+)" :value="1" />
          <el-option label="优秀(700+)" :value="2" />
          <el-option label="良好(500+)" :value="3" />
        </el-select>
      </div>
    </template>
    <el-table :data="credits" v-loading="loading">
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="creditScore" label="信用分" width="100">
        <template #default="{ row }">
          <span :style="{ color: getLevelColor(row.creditScore), fontWeight: 'bold' }">{{ row.creditScore }}</span>
        </template>
      </el-table-column>
      <el-table-column label="信用等级" width="120">
        <template #default="{ row }">
          <el-tag :color="getLevelColor(row.creditScore)" style="color: #fff;">{{ getLevel(row.creditScore) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalTransactions" label="总交易" width="100" />
      <el-table-column prop="completedTransactions" label="成功" width="80" />
      <el-table-column prop="cancelledTransactions" label="取消" width="80" />
      <el-table-column prop="goodRatingCount" label="好评" width="80" />
      <el-table-column prop="mediumRatingCount" label="中评" width="80" />
      <el-table-column prop="badRatingCount" label="差评" width="80" />
      <el-table-column prop="violationCount" label="违规" width="80" />
    </el-table>
    <el-pagination
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      class="pagination"
      @current-change="loadData"
    />
  </el-card>
</template>

<script setup>
import request from '@/utils/request'
import { onMounted, ref } from 'vue'

const credits = ref([])
const loading = ref(false)
const filterLevel = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (filterLevel.value !== null) params.creditLevel = filterLevel.value

    const res = await request.get('/admin/credit/list', { params })
    credits.value = res.data.records || []
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function getLevel(score) {
  if (score >= 900) return '极好'
  if (score >= 700) return '优秀'
  if (score >= 500) return '良好'
  if (score >= 350) return '一般'
  return '较差'
}

function getLevelColor(score) {
  if (score >= 900) return '#67C23A'
  if (score >= 700) return '#409EFF'
  if (score >= 500) return '#E6A23C'
  if (score >= 350) return '#F56C6C'
  return '#909399'
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
