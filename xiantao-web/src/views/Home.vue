<template>
  <div class="home">
    <div class="container">
      <div class="category-section">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="category-item"
          :class="{ active: selectedCategory === cat.id }"
          @click="selectCategory(cat.id)"
        >
          {{ cat.name }}
        </div>
      </div>

      <div class="product-grid" v-loading="loading">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @click="router.push(`/product/${product.id}`)"
        >
          <div class="product-image">
            <img v-if="product.imageList?.length" :src="getImageUrl(product.imageList[0])" alt="" />
            <div v-else class="no-image">暂无图片</div>
          </div>
          <div class="product-info">
            <h3 class="product-title">{{ product.title }}</h3>
            <div class="product-price">
              <span class="price">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
            </div>
            <div class="product-meta">
              <span>{{ product.sellerName }}</span>
              <span>{{ product.viewCount }} 次浏览</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="products.length === 0 && !loading" class="empty">
        暂无商品
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
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategoryList } from '@/api/category'
import { getProductList } from '@/api/product'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const products = ref([])
const selectedCategory = ref(null)
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)

onMounted(async () => {
  await loadCategories()
  await loadProducts()
})

watch(() => route.query.keyword, () => {
  loadProducts()
})

async function loadCategories() {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function loadProducts() {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: route.query.keyword || ''
    }
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
    }
    const res = await getProductList(params)
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function selectCategory(categoryId) {
  selectedCategory.value = selectedCategory.value === categoryId ? null : categoryId
  pageNum.value = 1
  loadProducts()
}

function getImageUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return 'http://localhost:8080' + url
}
</script>

<style scoped>
.home {
  padding: 20px 0;
}

.category-section {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.category-item {
  padding: 8px 20px;
  background: #fff;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #eee;
}

.category-item:hover,
.category-item.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.product-image {
  width: 100%;
  height: 280px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  box-sizing: border-box;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  object-fit: contain;
}

.no-image {
  color: #999;
}

.product-info {
  padding: 16px;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  margin-bottom: 8px;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
  margin-left: 8px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

.empty {
  text-align: center;
  padding: 60px;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
