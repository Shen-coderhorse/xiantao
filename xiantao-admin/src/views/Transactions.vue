<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>交易流水</span>
        <div class="header-filters">
          <el-select v-model="filterType" placeholder="全部类型" clearable style="width: 120px" @change="loadData">
            <el-option label="付款" :value="1" />
            <el-option label="托管" :value="2" />
            <el-option label="解冻" :value="3" />
            <el-option label="退款" :value="4" />
          </el-select>
          <el-select v-model="filterStatus" placeholder="全部状态" clearable style="width: 120px" @change="loadData">
            <el-option label="处理中" :value="0" />
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="2" />
          </el-select>
        </div>
      </div>
    </template>
    <el-table :data="transactions" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="transactionTypeText" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getTypeColor(row.transactionType)">{{ row.transactionTypeText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="120">
        <template #default="{ row }">¥{{ row.amount }}</template>
      </el-table-column>
      <el-table-column prop="fromUserName" label="转出" width="120" />
      <el-table-column prop="toUserName" label="转入" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'warning' : 'danger'">
            {{ row.statusText }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="200" />
      <el-table-column prop="createTime" label="时间" width="180" />
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

const transactions = ref([])
const loading = ref(false)
const filterType = ref(null)
const filterStatus = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (filterType.value !== null) params.type = filterType.value
    if (filterStatus.value !== null) params.status = filterStatus.value

    const res = await request.get('/admin/transaction/list', { params })
    transactions.value = res.data.records || []
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function getTypeColor(type) {
  const colors = { 1: 'primary', 2: 'warning', 3: 'success', 4: 'danger' }
  return colors[type] || 'info'
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-filters {
  display: flex;
  gap: 10px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
