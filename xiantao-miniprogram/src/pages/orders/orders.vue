<template>
  <div class="orders-page">
    <div class="tab-bar">
      <div :class="['tab', { active: activeTab === 'buy' }]" @click="activeTab = 'buy'; loadOrders()">我买的</div>
      <div :class="['tab', { active: activeTab === 'sell' }]" @click="activeTab = 'sell'; loadOrders()">我卖的</div>
    </div>

    <div class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span>订单号：{{ order.orderNo }}</span>
          <span :class="['status', statusClass(order.status)]">{{ statusText(order.status) }}</span>
        </div>
        <div class="order-body">
          <img :src="formatImage(order.productImage)" class="product-img" />
          <div class="product-info">
            <span class="product-title">{{ order.productTitle }}</span>
            <span class="product-price">¥{{ order.productPrice }}</span>
          </div>
        </div>
        <div class="order-actions">
          <button v-if="order.status === 0" class="btn-pay" @click="handlePay(order.id)">去付款</button>
          <button v-if="order.status === 1" class="btn-ship" @click="handleShip(order.id)">发货</button>
          <button v-if="order.status === 2" class="btn-complete" @click="handleComplete(order.id)">确认收货</button>
        </div>
      </div>
    </div>

    <div v-if="orders.length === 0" class="empty">暂无订单</div>
    
    <TabBar />
  </div>
</template>

<script>
import { getOrderList, payOrder, shipOrder, completeOrder } from '@/api/index'
import TabBar from '@/components/TabBar.vue'
import uni from '@/uni-api'

export default {
  name: 'OrdersPage',
  components: { TabBar },
  data() {
    return {
      orders: [],
      activeTab: 'buy'
    }
  },
  mounted() {
    // Check authentication - redirect to login if no token
    const token = uni.getStorageSync('token')
    if (!token) {
      this.$router.push('/login')
      return
    }
    this.loadOrders()
  },
  methods: {
    async loadOrders() {
      try {
        // Backend type: 1=我买的, 2=我卖的
        const typeNum = this.activeTab === 'buy' ? 1 : 2
        const params = { type: typeNum }
        const res = await getOrderList(params)
        const pageData = res.data || {}
        this.orders = pageData.records || pageData.list || pageData || []
      } catch (e) {
        if (e.code !== 401) console.error(e)
      }
    },
    statusText(status) {
      const map = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }
      return map[status] || '未知'
    },
    statusClass(status) {
      const map = { 0: 'status-wait-pay', 1: 'status-wait-ship', 2: 'status-wait-receive', 3: 'status-done', 4: 'status-cancel' }
      return map[status] || ''
    },
    async handlePay(orderId) {
      try {
        await payOrder(orderId)
        uni.showToast({ title: '付款成功', icon: 'success' })
        this.loadOrders()
      } catch (e) {}
    },
    async handleShip(orderId) {
      try {
        await shipOrder(orderId, {
          companyCode: 'SF',
          companyName: '顺丰速运',
          trackingNo: 'SF' + Date.now()
        })
        uni.showToast({ title: '发货成功', icon: 'success' })
        this.loadOrders()
      } catch (e) {}
    },
    async handleComplete(orderId) {
      try {
        await completeOrder(orderId)
        uni.showToast({ title: '确认收货成功', icon: 'success' })
        this.loadOrders()
      } catch (e) {}
    },
    formatImage(url) {
      if (!url) return ''
      if (url.startsWith('http')) return url
      return url
    }
  }
}
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background: var(--app-canvas);
  background-attachment: fixed;
  padding-bottom: 70px;
}

.tab-bar {
  display: flex;
  background: #fff;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 16px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
}

.tab.active {
  color: #409eff;
  border-bottom-color: #409eff;
}

.order-list {
  padding: 12px;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  background: #f9f9f9;
  font-size: 14px;
}

.status { color: #409eff; }
.status-wait-pay { color: #f56c6c; }
.status-wait-ship { color: #e6a23c; }
.status-wait-receive { color: #409eff; }
.status-done { color: #67c23a; }
.status-cancel { color: #999; }

.order-body {
  display: flex;
  padding: 12px;
}

.product-img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
  margin-right: 12px;
}

.product-info {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}

.product-title {
  font-size: 14px;
  color: #333;
}

.product-price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  padding: 12px;
  border-top: 1px solid #f5f5f5;
  gap: 8px;
}

.order-actions button {
  padding: 6px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.btn-pay { background: #f56c6c; color: #fff; }
.btn-ship { background: #409eff; color: #fff; }
.btn-complete { background: #67c23a; color: #fff; }

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
