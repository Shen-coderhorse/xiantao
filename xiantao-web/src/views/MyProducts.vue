<template>
  <div class="my-products">
    <div class="container">
      <div class="header">
        <h2>我的商品</h2>
        <el-button type="primary" @click="router.push('/publish')">发布新商品</el-button>
      </div>
      <div class="product-list" v-loading="loading">
        <div v-for="product in products" :key="product.id" class="product-item">
          <div class="product-image">
            <img v-if="product.imageList?.length" :src="product.imageList[0]" alt="" />
            <div v-else class="no-image">暂无图片</div>
          </div>
          <div class="product-info">
            <h3>{{ product.title }}</h3>
            <div class="price">¥{{ product.price }}</div>
            <div class="meta">
              <el-tag :type="getStatusType(product.status)">{{ getStatusText(product.status) }}</el-tag>
              <span>{{ product.viewCount }} 次浏览</span>
            </div>
          </div>
          <div class="product-actions">
            <el-button size="small" @click="handleEdit(product)" :disabled="product.status === 2">编辑</el-button>
            <el-popconfirm title="确定删除该商品吗？" @confirm="handleDelete(product.id)">
              <template #reference>
                <el-button size="small" type="danger" :disabled="product.status === 2">删除</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
        <div v-if="products.length === 0 && !loading" class="empty">
          暂无商品
        </div>
      </div>
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadProducts"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyProducts, deleteProduct } from '@/api/product'

defineOptions({
  name: 'MyProducts'
})

const router = useRouter()
const loading = ref(false)
const products = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  loadProducts()
})

onActivated(() => {
  loadProducts()
})

async function loadProducts() {
  loading.value = true
  try {
    const res = await getMyProducts({ pageNum: pageNum.value, pageSize: pageSize.value })
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function handleEdit(product) {
  router.push(`/publish?id=${product.id}`)
}

async function handleDelete(id) {
  try {
    await deleteProduct(id)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (e) {
    console.error(e)
  }
}

function getStatusType(status) {
  switch (status) {
    case 1: return 'success'
    case 2: return 'info'
    default: return 'warning'
  }
}

function getStatusText(status) {
  switch (status) {
    case 1: return '在售'
    case 2: return '已售'
    default: return '已下架'
  }
}
</script>

<style scoped>
.my-products {
  padding: 20px 0;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #f1f5f9;
}

.product-list {
  background: #fff;
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.product-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  transition: var(--transition-base);
}

.product-item:last-child {
  border-bottom: none;
}

.product-item:hover {
  background: #f8fbff;
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-sm);
  overflow: hidden;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.no-image {
  color: var(--text-light);
  font-size: 12px;
}

.product-info {
  flex: 1;
  padding: 0 20px;
}

.product-info h3 {
  font-size: 16px;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: var(--danger-color);
  margin-bottom: 8px;
}

.meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--text-light);
}

.product-actions {
  display: flex;
  gap: 8px;
}

.empty {
  text-align: center;
  padding: 60px;
  color: var(--text-light);
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
