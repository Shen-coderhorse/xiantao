<template>
  <view class="orders-page">
    <view class="tab-bar">
      <view :class="['tab', { active: activeTab === 'buy' }]" @click="activeTab = 'buy'; loadOrders()">我买的</view>
      <view :class="['tab', { active: activeTab === 'sell' }]" @click="activeTab = 'sell'; loadOrders()">我卖的</view>
    </view>

    <view class="order-list">
      <view v-for="order in orders" :key="order.id" class="order-card">
        <view class="order-header">
          <text>订单号：{{ order.orderNo }}</text>
          <text :class="['status', statusClass(order.status)]">{{ statusText(order.status) }}</text>
        </view>
        <view class="order-body">
          <image :src="formatImage(order.productImage)" mode="aspectFill" class="product-img" />
          <view class="product-info">
            <text class="product-title">{{ order.productTitle }}</text>
            <text class="product-price">¥{{ order.productPrice }}</text>
          </view>
        </view>
        <view class="order-actions">
          <button v-if="order.status === 0" class="btn-pay" size="mini" @click="handlePay(order.id)">去付款</button>
          <button v-if="order.status === 1" class="btn-ship" size="mini" @click="handleShip(order.id)">发货</button>
          <button v-if="order.status === 2" class="btn-complete" size="mini" @click="handleComplete(order.id)">确认收货</button>
        </view>
      </view>
    </view>

    <view v-if="orders.length === 0" class="empty">暂无订单</view>
  </view>
</template>

<script>
import { getOrderList, payOrder, shipOrder, completeOrder } from '@/api/index'

export default {
  data() {
    return {
      activeTab: 'buy',
      orders: []
    }
  },
  onShow() {
    this.loadOrders()
  },
  methods: {
    async loadOrders() {
      try {
        const res = await getOrderList({})
        const all = res.data || []
        this.orders = all
      } catch (e) {
        console.error(e)
      }
    },
    statusText(s) {
      return { 0: '待付款', 1: '待发货', 2: '已发货', 3: '已完成', 4: '已取消' }[s] || '未知'
    },
    statusClass(s) {
      return { 0: 'text-orange', 1: 'text-blue', 2: 'text-green', 3: 'text-gray', 4: 'text-red' }[s] || ''
    },
    formatImage(url) {
      if (!url) return ''
      return url.startsWith('http') ? url : 'http://localhost:8080' + url
    },
    async handlePay(id) {
      try {
        await payOrder(id)
        uni.showToast({ title: '付款成功', icon: 'success' })
        this.loadOrders()
      } catch (e) {
        console.error(e)
      }
    },
    async handleShip(id) {
      try {
        await shipOrder(id, { logisticsCompany: '顺丰速运', logisticsNo: 'SF' + Date.now() })
        uni.showToast({ title: '发货成功', icon: 'success' })
        this.loadOrders()
      } catch (e) {
        console.error(e)
      }
    },
    async handleComplete(id) {
      try {
        await completeOrder(id)
        uni.showToast({ title: '已确认收货', icon: 'success' })
        this.loadOrders()
      } catch (e) {
        console.error(e)
      }
    }
  }
}
</script>

<style scoped>
.tab-bar { display: flex; background: #fff; border-bottom: 1px solid #eee; }
.tab { flex: 1; text-align: center; padding: 14px; font-size: 15px; color: #666; }
.tab.active { color: #409eff; border-bottom: 2px solid #409eff; }
.order-card { background: #fff; margin: 8px; padding: 12px; border-radius: 8px; }
.order-header { display: flex; justify-content: space-between; font-size: 12px; color: #999; margin-bottom: 8px; }
.status { font-weight: bold; }
.text-orange { color: #e6a23c; } .text-blue { color: #409eff; } .text-green { color: #67c23a; } .text-gray { color: #909399; } .text-red { color: #f56c6c; }
.order-body { display: flex; margin-bottom: 8px; }
.product-img { width: 80px; height: 80px; border-radius: 4px; margin-right: 12px; }
.product-title { font-size: 14px; display: block; margin-bottom: 4px; }
.product-price { font-size: 16px; color: #f56c6c; font-weight: bold; }
.order-actions { display: flex; justify-content: flex-end; gap: 8px; }
.btn-pay { background: #e6a23c; color: #fff; } .btn-ship { background: #409eff; color: #fff; } .btn-complete { background: #67c23a; color: #fff; }
.empty { text-align: center; padding: 40px; color: #999; }
</style>
