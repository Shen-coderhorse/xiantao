<template>
  <div class="orders-page">
    <div class="container">
      <div class="header">
        <h2>我的订单</h2>
        <el-radio-group v-model="orderType" @change="loadOrders">
          <el-radio-button :value="0">全部</el-radio-button>
          <el-radio-button :value="1">我买的</el-radio-button>
          <el-radio-button :value="2">我卖的</el-radio-button>
        </el-radio-group>
      </div>
      <div class="order-list" v-loading="loading">
        <div v-for="order in orders" :key="order.id" class="order-item">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <el-tag :type="getStatusType(order.status)">{{ order.statusText }}</el-tag>
          </div>
          <div class="order-content">
            <div class="product-image">
              <img v-if="order.productImage" :src="order.productImage" alt="" />
              <div v-else class="no-image">暂无图片</div>
            </div>
            <div class="product-info">
              <h3>{{ order.productTitle }}</h3>
              <div class="price">¥{{ order.productPrice }}</div>
            </div>
            <div class="party-info">
              <template v-if="orderType === 1 || orderType === 0">
                <div class="label">卖家</div>
                <div class="name">{{ order.sellerName }}</div>
              </template>
              <template v-if="orderType === 2 || orderType === 0">
                <div class="label">买家</div>
                <div class="name">{{ order.buyerName }}</div>
              </template>
            </div>
            <div class="order-actions">
              <template v-if="order.status === 0 && order.buyerId === userStore.user?.id">
                <el-button type="primary" size="small" @click="handlePay(order)">付款</el-button>
                <el-button size="small" @click="handleCancel(order)">取消</el-button>
              </template>
              <template v-if="order.status === 1 && order.buyerId === userStore.user?.id">
                <el-button type="success" size="small" @click="handleComplete(order)">确认收货</el-button>
              </template>
            </div>
          </div>
          <div class="order-footer">
            <span>创建时间：{{ formatDate(order.createTime) }}</span>
            <span v-if="order.payTime">付款时间：{{ formatDate(order.payTime) }}</span>
          </div>
        </div>
        <div v-if="orders.length === 0 && !loading" class="empty">
          暂无订单
        </div>
      </div>
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadOrders"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, payOrder, completeOrder, cancelOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'

import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const orders = ref([])
const orderType = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  loadOrders()
})

async function loadOrders() {
  loading.value = true
  try {
    const res = await getOrderList({
      type: orderType.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    orders.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handlePay(order) {
  try {
    await ElMessageBox.confirm('确认付款？', '付款', { type: 'info' })
    await payOrder(order.id)
    ElMessage.success('付款成功')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

async function handleComplete(order) {
  try {
    await ElMessageBox.confirm('确认收货？', '收货', { type: 'info' })
    await completeOrder(order.id)
    ElMessage.success('订单完成')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

async function handleCancel(order) {
  try {
    await ElMessageBox.confirm('取消该订单？', '取消订单', { type: 'warning' })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

function getStatusType(status) {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'primary'
    case 2: return 'success'
    default: return 'info'
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}
</script>

<style scoped>
.orders-page {
  padding: 20px 0;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.order-list {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.order-item {
  border-bottom: 1px solid #eee;
}

.order-item:last-child {
  border-bottom: none;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fafafa;
  border-bottom: 1px solid #eee;
}

.order-no {
  font-size: 12px;
  color: #666;
}

.order-content {
  display: flex;
  align-items: center;
  padding: 20px;
  gap: 20px;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  color: #999;
  font-size: 12px;
}

.product-info {
  flex: 1;
}

.product-info h3 {
  font-size: 14px;
  margin-bottom: 8px;
}

.price {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}

.party-info {
  text-align: center;
  min-width: 100px;
}

.party-info .label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.party-info .name {
  font-weight: 500;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.order-footer {
  display: flex;
  gap: 20px;
  padding: 12px 20px;
  font-size: 12px;
  color: #999;
  background: #fafafa;
}

.empty {
  text-align: center;
  padding: 60px;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
