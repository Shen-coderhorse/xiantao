<template>
  <div class="order-detail-page">
    <div class="container" v-loading="loading">
      <div class="page-header">
        <h2>订单详情</h2>
        <el-button @click="$router.push('/orders')">返回订单列表</el-button>
      </div>

      <div v-if="order" class="order-content">
        <el-card class="order-status-card">
          <div class="status-info">
            <el-icon v-if="order.status === 0" :size="40" color="#E6A23C"><Clock /></el-icon>
            <el-icon v-else-if="order.status === 1" :size="40" color="#409EFF"><Van /></el-icon>
            <el-icon v-else-if="order.status === 2" :size="40" color="#67C23A"><CircleCheck /></el-icon>
            <el-icon v-else :size="40" color="#909399"><CircleClose /></el-icon>
            <div class="status-text">
              <h3>{{ order.statusText }}</h3>
              <p v-if="order.status === 0">请尽快完成支付</p>
              <p v-else-if="order.status === 1">等待卖家发货</p>
              <p v-else-if="order.status === 2">交易已完成</p>
              <p v-else>订单已取消</p>
            </div>
          </div>
        </el-card>

        <el-card class="order-info-card">
          <h3>订单信息</h3>
          <div class="info-row">
            <span class="label">订单编号</span>
            <span class="value">{{ order.orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="label">下单时间</span>
            <span class="value">{{ order.createTime }}</span>
          </div>
          <div class="info-row" v-if="order.payTime">
            <span class="label">支付时间</span>
            <span class="value">{{ order.payTime }}</span>
          </div>
          <div class="info-row" v-if="order.completeTime">
            <span class="label">完成时间</span>
            <span class="value">{{ order.completeTime }}</span>
          </div>
        </el-card>

        <el-card class="product-card">
          <h3>商品信息</h3>
          <div class="product-item" @click="$router.push(`/product/${order.productId}`)">
            <img v-if="order.productImage" :src="getImageUrl(order.productImage)" alt="" />
            <div class="product-info">
              <div class="product-title">{{ order.productTitle }}</div>
              <div class="product-price">¥{{ order.productPrice }}</div>
            </div>
          </div>
        </el-card>

        <el-card v-if="order.address" class="address-card">
          <h3>收货地址</h3>
          <div class="address-info">
            <div class="receiver">
              <span>{{ order.receiverName }}</span>
              <span>{{ order.receiverPhone }}</span>
            </div>
            <div class="address-text">{{ order.address }}</div>
          </div>
        </el-card>

        <el-card v-if="order.status >= 1" class="logistics-card">
          <h3>物流信息</h3>
          <div v-if="logistics" class="logistics-info">
            <div class="logistics-header">
              <span class="company">{{ logistics.companyName }}</span>
              <span class="tracking-no">单号: {{ logistics.trackingNo }}</span>
            </div>
            <div class="logistics-status">
              <el-tag :type="getLogisticsStatusType(logistics.status)">
                {{ logistics.statusText }}
              </el-tag>
            </div>
            <div v-if="logistics.tracks && logistics.tracks.length > 0" class="logistics-tracks">
              <el-timeline>
                <el-timeline-item
                  v-for="(track, index) in logistics.tracks"
                  :key="index"
                  :timestamp="track.trackTime"
                  placement="top"
                  :color="index === 0 ? '#0ea5e9' : '#ddd'"
                >
                  <div class="track-item">
                    <div class="track-description">{{ track.description }}</div>
                    <div class="track-location">{{ track.location }}</div>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </div>
          <el-empty v-else description="暂无物流信息" />
        </el-card>

        <el-card v-if="order.status >= 1" class="transaction-card">
          <h3>交易流水</h3>
          <div v-if="transactions.length > 0" class="transaction-list">
            <div v-for="tx in transactions" :key="tx.id" class="transaction-item">
              <div class="tx-type">
                <el-tag :type="getTransactionTypeColor(tx.transactionType)">
                  {{ tx.transactionTypeText }}
                </el-tag>
              </div>
              <div class="tx-amount">¥{{ tx.amount }}</div>
              <div class="tx-info">
                <span>{{ tx.fromUserName }} → {{ tx.toUserName }}</span>
              </div>
              <div class="tx-time">{{ tx.createTime }}</div>
              <div class="tx-remark">{{ tx.remark }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无交易记录" />
        </el-card>

        <div class="order-actions" v-if="order.status < 2">
          <el-button v-if="order.status === 0" type="primary" size="large" @click="handlePay">
            立即支付
          </el-button>
          <el-button v-if="order.status === 1 && isBuyer" type="success" size="large" @click="handleReceive">
            确认收货
          </el-button>
          <el-button v-if="order.status === 1 && isSeller" type="warning" size="large" @click="showShipDialog">
            发货
          </el-button>
          <el-button v-if="order.status === 0" type="danger" @click="handleCancel">
            取消订单
          </el-button>
        </div>
      </div>

      <el-dialog v-model="shipDialogVisible" title="发货" width="500px">
        <el-form :model="shipForm" :rules="shipRules" ref="shipFormRef" label-width="100px">
          <el-form-item label="物流公司" prop="companyCode">
            <el-select v-model="shipForm.companyCode" placeholder="请选择物流公司" style="width: 100%">
              <el-option label="顺丰速运" value="SF" />
              <el-option label="中通快递" value="ZTO" />
              <el-option label="圆通速递" value="YTO" />
              <el-option label="韵达快递" value="YUNDA" />
              <el-option label="申通快递" value="STO" />
              <el-option label="极兔速递" value="JT" />
            </el-select>
          </el-form-item>
          <el-form-item label="物流单号" prop="trackingNo">
            <el-input v-model="shipForm.trackingNo" placeholder="请输入物流单号" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="shipDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleShip">确定发货</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { getLogistics } from '@/api/logistics'
import { cancelOrder, getOrderDetail, getOrderTransactions, payTransaction, receiveOrder, shipOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'
import { getImageUrl } from '@/utils/image'
import { CircleCheck, CircleClose, Clock, Van } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const order = ref(null)
const logistics = ref(null)
const transactions = ref([])
const loading = ref(false)
const shipDialogVisible = ref(false)
const shipFormRef = ref(null)

const shipForm = ref({
  companyCode: '',
  companyName: '',
  trackingNo: ''
})

const shipRules = {
  companyCode: [{ required: true, message: '请选择物流公司', trigger: 'change' }],
  trackingNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
}

const isBuyer = computed(() => order.value && order.value.buyerId === userStore.user?.id)
const isSeller = computed(() => order.value && order.value.sellerId === userStore.user?.id)

onMounted(() => {
  loadOrderDetail()
})

async function loadOrderDetail() {
  loading.value = true
  try {
    const orderId = route.params.id
    const res = await getOrderDetail(orderId)
    order.value = res.data

    if (order.value.status >= 1) {
      try {
        const logisticsRes = await getLogistics(orderId)
        logistics.value = logisticsRes.data
      } catch (e) {
        console.log('暂无物流信息')
      }

      try {
        const txRes = await getOrderTransactions(orderId)
        transactions.value = txRes.data || []
      } catch (e) {
        console.log('暂无交易记录')
      }
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handlePay() {
  try {
    await ElMessageBox.confirm('确认支付此订单？', '支付确认', { type: 'info' })
    await payTransaction(order.value.id)
    ElMessage.success('支付成功')
    loadOrderDetail()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

async function handleReceive() {
  try {
    await ElMessageBox.confirm('确认已收到货物？', '确认收货', { type: 'info' })
    await receiveOrder(order.value.id)
    ElMessage.success('确认收货成功')
    loadOrderDetail()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

async function handleCancel() {
  try {
    await ElMessageBox.confirm('确定取消此订单吗？', '取消订单', { type: 'warning' })
    await cancelOrder(order.value.id)
    ElMessage.success('订单已取消')
    router.push('/orders')
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

function showShipDialog() {
  shipForm.value = {
    companyCode: '',
    companyName: '',
    trackingNo: ''
  }
  shipDialogVisible.value = true
}

async function handleShip() {
  try {
    await shipFormRef.value.validate()

    const companyNames = {
      SF: '顺丰速运',
      ZTO: '中通快递',
      YTO: '圆通速递',
      YUNDA: '韵达快递',
      STO: '申通快递',
      JT: '极兔速递'
    }

    shipForm.value.companyName = companyNames[shipForm.value.companyCode]

    await shipOrder(order.value.id, shipForm.value)
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    loadOrderDetail()
  } catch (e) {
    console.error(e)
  }
}

function getLogisticsStatusType(status) {
  switch (status) {
    case 0: return 'info'
    case 1: return ''
    case 2: return 'warning'
    case 3: return 'primary'
    case 4: return 'success'
    default: return ''
  }
}

function getTransactionTypeColor(type) {
  switch (type) {
    case 1: return 'primary'
    case 2: return 'warning'
    case 3: return 'success'
    case 4: return 'info'
    default: return ''
  }
}

</script>

<style scoped>
.order-detail-page {
  padding: 20px 0;
  background: transparent;
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
  color: #f1f5f9;
}

.order-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-status-card {
  background: #fff;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.status-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-text h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: var(--text-primary);
}

.status-text p {
  margin: 0;
  font-size: 14px;
  color: var(--text-light);
}

.order-info-card,
.product-card,
.address-card,
.logistics-card,
.transaction-card {
  background: #fff;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: var(--text-primary);
  font-weight: 600;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 8px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px dashed var(--border-color);
}

.info-row:last-child {
  border-bottom: none;
}

.info-row .label {
  color: var(--text-light);
  font-size: 14px;
}

.info-row .value {
  color: var(--text-primary);
  font-size: 14px;
}

.product-item {
  display: flex;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.product-item:hover {
  opacity: 0.8;
}

.product-item img {
  width: 80px;
  height: 80px;
  object-fit: contain;
  background: #f8fafc;
  border-radius: var(--radius-sm);
}

.product-info {
  flex: 1;
}

.product-title {
  font-size: 14px;
  color: var(--text-primary);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: var(--danger-color);
}

.address-info .receiver {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  font-size: 14px;
  color: var(--text-primary);
}

.address-text {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.logistics-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.company {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.tracking-no {
  font-size: 14px;
  color: #999;
}

.logistics-status {
  margin-bottom: 16px;
}

.track-item {
  font-size: 14px;
}

.track-description {
  color: #333;
  margin-bottom: 4px;
}

.track-location {
  color: #999;
  font-size: 12px;
}

.transaction-item {
  padding: 12px;
  border-bottom: 1px dashed #eee;
}

.transaction-item:last-child {
  border-bottom: none;
}

.tx-type {
  margin-bottom: 8px;
}

.tx-amount {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 4px;
}

.tx-info {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.tx-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.tx-remark {
  font-size: 12px;
  color: #909399;
  background: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
}

.order-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 16px 0;
}

@media (max-width: 768px) {
  .container {
    padding: 0 12px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .page-header h2 {
    font-size: 18px;
  }

  .status-info {
    flex-direction: column;
    text-align: center;
  }

  .info-row {
    flex-direction: column;
    gap: 4px;
  }

  .product-item {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .product-item img {
    width: 100%;
    height: auto;
  }

  .logistics-header {
    flex-direction: column;
    gap: 8px;
  }

  .order-actions {
    flex-direction: column;
  }

  .order-actions .el-button {
    width: 100%;
  }
}
</style>
