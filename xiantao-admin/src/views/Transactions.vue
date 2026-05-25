<template>
  <el-card>
    <template #header><span>交易流水</span></template>
    <el-table :data="transactions" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="transactionTypeText" label="类型" width="100" />
      <el-table-column prop="amount" label="金额" width="120">
        <template #default="{ row }">¥{{ row.amount }}</template>
      </el-table-column>
      <el-table-column prop="fromUserName" label="转出" width="120" />
      <el-table-column prop="toUserName" label="转入" width="120" />
      <el-table-column prop="remark" label="备注" min-width="200" />
      <el-table-column prop="createTime" label="时间" width="180" />
    </el-table>
  </el-card>
</template>

<script setup>
import request from '@/utils/request'
import { onMounted, ref } from 'vue'

const transactions = ref([])
const loading = ref(false)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/admin/transaction/list')
    transactions.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>
