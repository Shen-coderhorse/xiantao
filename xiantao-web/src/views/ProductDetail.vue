<template>
  <div class="product-detail" v-loading="loading">
    <div class="container" v-if="product">
      <div class="detail-card">
        <div class="image-section">
          <div class="main-image">
            <img v-if="product.imageList?.length" :src="currentImage" alt="" />
            <div v-else class="no-image">暂无图片</div>
          </div>
          <div class="image-list" v-if="product.imageList?.length > 1">
            <div
              v-for="(img, index) in product.imageList"
              :key="index"
              class="image-thumb"
              :class="{ active: currentImage === img }"
              @click="currentImage = img"
            >
              <img :src="img" alt="" />
            </div>
          </div>
        </div>
        <div class="info-section">
          <h1 class="title">{{ product.title }}</h1>
          <div class="price-section">
            <span class="price">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
          </div>
          <div class="meta-section">
            <div class="meta-item">
              <span class="label">分类：</span>
              <span>{{ product.categoryName }}</span>
            </div>
            <div class="meta-item">
              <span class="label">浏览：</span>
              <span>{{ product.viewCount }} 次</span>
            </div>
            <div class="meta-item">
              <span class="label">发布时间：</span>
              <span>{{ formatDate(product.createTime) }}</span>
            </div>
          </div>
          <div class="seller-section">
            <div class="seller-info">
              <el-avatar :size="40">{{ product.sellerName?.charAt(0) }}</el-avatar>
              <div class="seller-detail">
                <div class="seller-name">{{ product.sellerName }}</div>
                <div class="seller-label">卖家</div>
              </div>
            </div>
          </div>
          <div class="action-section" v-if="product.status === 1">
            <el-button type="primary" size="large" @click="handleBuy" :disabled="!userStore.isLoggedIn">
              立即购买
            </el-button>
            <p v-if="!userStore.isLoggedIn" class="login-tip">请先登录后购买</p>
          </div>
          <div class="action-section" v-else>
            <el-tag type="info" size="large">{{ product.status === 0 ? '已下架' : '已售出' }}</el-tag>
          </div>
        </div>
      </div>
      <div class="description-card">
        <h3>商品描述</h3>
        <p>{{ product.description || '暂无描述' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductDetail } from '@/api/product'
import { createOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const product = ref(null)
const currentImage = ref('')
const loading = ref(false)

onMounted(async () => {
  await loadProduct()
})

async function loadProduct() {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
    if (res.data?.imageList?.length) {
      currentImage.value = res.data.imageList[0]
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleBuy() {
  if (product.value.sellerId === userStore.user?.id) {
    ElMessage.warning('不能购买自己的商品')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认购买「${product.value.title}」，价格 ¥${product.value.price}？`,
      '确认购买',
      { type: 'info' }
    )
    const res = await createOrder({ productId: product.value.id })
    ElMessage.success('下单成功')
    router.push('/orders')
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}
</script>

<style scoped>
.product-detail {
  padding: 20px 0;
}

.detail-card {
  display: flex;
  gap: 40px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.image-section {
  width: 500px;
}

.main-image {
  width: 100%;
  height: 400px;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  color: #999;
  font-size: 18px;
}

.image-list {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.image-thumb {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
}

.image-thumb.active {
  border-color: #409eff;
}

.image-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-section {
  flex: 1;
}

.title {
  font-size: 24px;
  font-weight: 500;
  color: #333;
  margin-bottom: 20px;
}

.price-section {
  margin-bottom: 20px;
}

.price {
  font-size: 32px;
  font-weight: bold;
  color: #f56c6c;
}

.original-price {
  font-size: 18px;
  color: #999;
  text-decoration: line-through;
  margin-left: 12px;
}

.meta-section {
  padding: 20px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.meta-item .label {
  color: #999;
}

.seller-section {
  margin-bottom: 20px;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.seller-name {
  font-weight: 500;
}

.seller-label {
  font-size: 12px;
  color: #999;
}

.action-section {
  margin-top: 30px;
}

.login-tip {
  color: #999;
  font-size: 12px;
  margin-top: 10px;
}

.description-card {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
}

.description-card h3 {
  margin-bottom: 16px;
  font-size: 18px;
}

.description-card p {
  color: #666;
  line-height: 1.8;
}
</style>
