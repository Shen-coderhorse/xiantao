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

      <div class="filter-section">
        <div class="sort-bar">
          <span class="sort-label">排序：</span>
          <span class="sort-item" :class="{ active: sortBy === 'time_desc' }" @click="changeSort('time_desc')">最新</span>
          <span class="sort-item" :class="{ active: sortBy === 'price_asc' }" @click="changeSort('price_asc')">价格↑</span>
          <span class="sort-item" :class="{ active: sortBy === 'price_desc' }" @click="changeSort('price_desc')">价格↓</span>
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
import { getImageUrl } from '@/utils/image'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const products = ref([])
const selectedCategory = ref(null)
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const sortBy = ref('time_desc')

onMounted(async () => {
  await loadCategories()
  await loadProducts()
})

watch(() => route.query.keyword, () => {
  pageNum.value = 1
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
      keyword: route.query.keyword || '',
      sortBy: sortBy.value
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

function changeSort(sort) {
  sortBy.value = sort
  pageNum.value = 1
  loadProducts()
}
</script>

<style scoped>
.home {
  padding: 20px 0;
}

.category-section {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.category-item {
  padding: 8px 20px;
  background: #fff;
  border-radius: 20px;
  cursor: pointer;
  transition: var(--transition-base);
  border: 1px solid var(--border-color);
  font-weight: 500;
}

.category-item:hover {
  color: var(--brand-primary);
  border-color: var(--brand-primary);
}

.category-item.active {
  background: var(--gradient-primary);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(14, 165, 233, 0.32);
}

.filter-section {
  margin-bottom: 20px;
}

.sort-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 0;
}

.sort-label {
  font-size: 14px;
  color: #cbd5e1;
}

.sort-item {
  font-size: 14px;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px 14px;
  border-radius: 8px;
  transition: var(--transition-base);
}

.sort-item:hover {
  color: #5eead4;
}

.sort-item.active {
  color: #fff;
  background: var(--gradient-primary);
  box-shadow: 0 3px 10px rgba(14, 165, 233, 0.3);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.product-card {
  background: #fff;
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  border: 1px solid transparent;
  box-shadow: var(--shadow-sm);
  transition: var(--transition-base);
}

.product-card:hover {
  transform: translateY(-8px);
  border-color: rgba(14, 165, 233, 0.25);
  box-shadow: 0 16px 40px rgba(14, 165, 233, 0.16);
}

.product-image {
  width: 100%;
  height: 240px;
  background: #f8fafc;
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
  color: var(--text-light);
  font-size: 13px;
}

.product-info {
  padding: 16px;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 8px;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: var(--danger-color);
}

.original-price {
  font-size: 14px;
  color: var(--text-light);
  text-decoration: line-through;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-light);
}

.empty {
  text-align: center;
  padding: 60px;
  color: #94a3b8;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
