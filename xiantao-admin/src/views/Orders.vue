<template>
  <el-card>
    <template #header><span>订单管理</span></template>
    <el-table :data="orders" v-loading="loading">
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="productTitle" label="商品" min-width="150" />
      <el-table-column prop="productPrice" label="金额" width="100">
        <template #default="{ row }">¥{{ row.productPrice }}</template>
      </el-table-column>
      <el-table-column prop="buyerName" label="买家" width="100" />
      <el-table-column prop="sellerName" label="卖家" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
    </el-table>
  </el-card>
</template>

<script setup>
import request from '@/utils/request'
import { onMounted, ref } from 'vue'

const orders = ref([])
const loading = ref(false)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/admin/order/list', { params: { pageNum: 1, pageSize: 100 } })
    orders.value = res.data.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function getStatusType(status) {
  const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return types[status] || 'info'
}
</script>
