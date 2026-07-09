<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>订单管理</span>
        <el-select v-model="filterStatus" placeholder="全部状态" clearable style="width: 150px" @change="loadData">
          <el-option label="待付款" :value="0" />
          <el-option label="待发货" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
      </div>
    </template>
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
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showDetailDialog(row)">详情</el-button>
          <el-button v-if="row.status === 1" size="small" type="primary" @click="openShipDialog(row)">发货</el-button>
          <el-button v-if="row.status === 0 || row.status === 1" size="small" type="warning" @click="cancelOrder(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      class="pagination"
      @current-change="loadData"
    />

    <!-- 发货对话框 -->
    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="订单号">
          <span>{{ currentOrder?.orderNo }}</span>
        </el-form-item>
        <el-form-item label="物流公司">
          <el-select v-model="shipForm.companyCode" placeholder="选择物流公司" style="width: 100%">
            <el-option label="顺丰速运" value="SF" />
            <el-option label="中通快递" value="ZTO" />
            <el-option label="圆通速递" value="YTO" />
            <el-option label="申通快递" value="STO" />
            <el-option label="韵达快递" value="YD" />
            <el-option label="京东物流" value="JD" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号">
          <el-input v-model="shipForm.trackingNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleShip">确认发货</el-button>
      </template>
    </el-dialog>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px">
      <el-descriptions :column="1" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="商品">{{ currentOrder.productTitle }}</el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ currentOrder.productPrice }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ currentOrder.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="买家ID">{{ currentOrder.buyerId }}</el-descriptions-item>
        <el-descriptions-item label="卖家ID">{{ currentOrder.sellerId }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.payTime" label="支付时间">{{ currentOrder.payTime }}</el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.completeTime" label="完成时间">{{ currentOrder.completeTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </el-card>
</template>

<script setup>
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'

const orders = ref([])
const loading = ref(false)
const filterStatus = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const shipDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentOrder = ref(null)
const shipForm = ref({ companyCode: '', companyName: '', trackingNo: '' })

const companyMap = {
  SF: '顺丰速运', ZTO: '中通快递', YTO: '圆通速递',
  STO: '申通快递', YD: '韵达快递', JD: '京东物流'
}

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (filterStatus.value !== null) params.status = filterStatus.value

    const res = await request.get('/admin/order/list', { params })
    orders.value = res.data.records || []
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function showDetailDialog(row) {
  currentOrder.value = row
  detailDialogVisible.value = true
}

function openShipDialog(row) {
  currentOrder.value = row
  shipForm.value = { companyCode: '', companyName: '', trackingNo: '' }
  shipDialogVisible.value = true
}

async function handleShip() {
  if (!shipForm.value.companyCode) {
    ElMessage.warning('请选择物流公司')
    return
  }
  if (!shipForm.value.trackingNo) {
    ElMessage.warning('请输入物流单号')
    return
  }
  shipForm.value.companyName = companyMap[shipForm.value.companyCode] || shipForm.value.companyCode

  try {
    await request.put(`/admin/order/${currentOrder.value.id}/ship`, {
      companyCode: shipForm.value.companyCode,
      companyName: shipForm.value.companyName,
      trackingNo: shipForm.value.trackingNo
    })
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}

async function cancelOrder(row) {
  try {
    await ElMessageBox.confirm(`确定取消订单「${row.orderNo}」吗？`, '提示', { type: 'warning' })
    await request.put(`/admin/order/${row.id}/cancel`)
    ElMessage.success('取消成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

function getStatusType(status) {
  const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return types[status] || 'info'
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
