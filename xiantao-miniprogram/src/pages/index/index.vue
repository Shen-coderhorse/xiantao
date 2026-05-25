<template>
  <div class="index-page">
    <div class="search-bar">
      <input
        v-model="keyword"
        placeholder="搜索商品"
        @keyup.enter="handleSearch"
      />
    </div>

    <div class="category-tabs">
      <div class="scroll-container">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="tab-item"
          :class="{ active: currentCategory === cat.id }"
          @click="selectCategory(cat.id)"
        >
          {{ cat.name }}
        </div>
      </div>
    </div>

    <div class="product-list">
      <div
        v-for="product in products"
        :key="product.id"
        class="product-card"
        @click="goToDetail(product.id)"
      >
        <img
          :src="getImageUrl(product.images)"
          class="product-image"
        />
        <div class="product-info">
          <div class="product-title">{{ product.title }}</div>
          <div class="product-price">
            <span class="price">¥{{ product.price }}</span>
            <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
          </div>
          <div class="product-meta">
            <span class="seller">{{ product.sellerName }}</span>
            <span class="views">{{ product.viewCount }}次浏览</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-if="!hasMore && products.length > 0" class="no-more">没有更多了</div>
    <div v-if="products.length === 0 && !loading" class="empty">
      <span>暂无商品</span>
    </div>
    
    <TabBar />
  </div>
</template>

<script>
import { getCategories, getProductList } from '@/api/index'
import TabBar from '@/components/TabBar.vue'

export default {
  name: 'IndexPage',
  components: { TabBar },
  data() {
    return {
      products: [],
      categories: [],
      keyword: '',
      currentCategory: null,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      hasMore: true
    }
  },
  mounted() {
    this.loadCategories()
    this.loadProducts()
  },
  methods: {
    async loadCategories() {
      try {
        const res = await getCategories()
        this.categories = res.data || []
      } catch (e) {
        console.error(e)
      }
    },
    async loadProducts() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          keyword: this.keyword || undefined,
          categoryId: this.currentCategory
        }
        const res = await getProductList(params)
        console.log('Product list response:', res)
        // Backend returns { records: [...], total: N } (MyBatis Plus page format)
        const pageData = res.data || {}
        const list = pageData.records || pageData.list || []
        const total = pageData.total || 0
        if (this.pageNum === 1) {
          this.products = list
        } else {
          this.products = [...this.products, ...list]
        }
        this.total = total
        this.hasMore = this.products.length < total
      } catch (e) {
        console.error('loadProducts error:', e)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pageNum = 1
      this.loadProducts()
    },
    selectCategory(categoryId) {
      this.currentCategory = this.currentCategory === categoryId ? null : categoryId
      this.pageNum = 1
      this.loadProducts()
    },
    goToDetail(id) {
      this.$router.push(`/product-detail?id=${id}`)
    },
    getImageUrl(images) {
      if (!images) return ''
      const urls = images.split(',')
      let url = urls[0].trim()
      if (!url) return ''
      if (url.startsWith('http')) return url
      // Use proxy path for Vite dev server
      return url
    }
  }
}
</script>

<style scoped>
.index-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 70px;
}

.search-bar {
  background: #fff;
  padding: 12px;
}

.search-bar input {
  width: 100%;
  background: #f5f7fa;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 14px;
  border: none;
}

.category-tabs {
  background: #fff;
  padding: 12px 0;
  margin-bottom: 12px;
}

.scroll-container {
  display: flex;
  overflow-x: auto;
  padding: 0 12px;
  white-space: nowrap;
}

.tab-item {
  display: inline-block;
  padding: 6px 16px;
  margin-right: 8px;
  background: #f5f7fa;
  border-radius: 16px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  flex-shrink: 0;
}

.tab-item.active {
  background: #409eff;
  color: #fff;
}

.product-list {
  padding: 0 12px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.product-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.product-info {
  padding: 12px;
}

.product-title {
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 8px;
  height: 40px;
}

.product-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
  margin-right: 8px;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

.loading, .no-more, .empty {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 14px;
}
</style>
