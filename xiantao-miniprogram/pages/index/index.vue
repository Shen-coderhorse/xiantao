<template>
  <view class="index-page">
    <view class="search-bar">
      <input
        v-model="keyword"
        placeholder="搜索商品"
        @confirm="handleSearch"
      />
    </view>

    <view class="category-tabs">
      <scroll-view scroll-x>
        <view
          v-for="cat in categories"
          :key="cat.id"
          class="tab-item"
          :class="{ active: currentCategory === cat.id }"
          @click="selectCategory(cat.id)"
        >
          {{ cat.name }}
        </view>
      </scroll-view>
    </view>

    <view class="product-list">
      <view
        v-for="product in products"
        :key="product.id"
        class="product-card"
        @click="goToDetail(product.id)"
      >
        <image
          :src="getImageUrl(product.images)"
          mode="aspectFill"
          class="product-image"
        />
        <view class="product-info">
          <view class="product-title">{{ product.title }}</view>
          <view class="product-price">
            <text class="price">¥{{ product.price }}</text>
            <text class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</text>
          </view>
          <view class="product-meta">
            <text class="seller">{{ product.sellerName }}</text>
            <text class="views">{{ product.viewCount }}次浏览</text>
          </view>
        </view>
      </view>
    </view>

    <view v-if="loading" class="loading">加载中...</view>
    <view v-if="!hasMore && products.length > 0" class="no-more">没有更多了</view>
    <view v-if="products.length === 0 && !loading" class="empty">
      <text>暂无商品</text>
    </view>
  </view>
</template>

<script>
import { getCategories, getProductList } from '@/api/product';

export default {
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
  onLoad() {
    this.loadCategories()
    this.loadProducts()
  },
  onReachBottom() {
    if (!this.loading && this.hasMore) {
      this.pageNum++
      this.loadProducts()
    }
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
        const { list, total } = res.data
        if (this.pageNum === 1) {
          this.products = list
        } else {
          this.products = [...this.products, ...list]
        }
        this.total = total
        this.hasMore = this.products.length < total
      } catch (e) {
        console.error(e)
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
      uni.navigateTo({ url: `/pages/product-detail/product-detail?id=${id}` })
    },
    getImageUrl(images) {
      if (!images) return '/static/placeholder.png'
      const urls = images.split(',')
      const url = urls[0]
      if (url.startsWith('http')) return url
      return 'http://localhost:8080' + url
    }
  }
}
</script>

<style scoped>
.index-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 20px;
}

.search-bar {
  background: #fff;
  padding: 12px;
}

.search-bar input {
  background: #f5f7fa;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 14px;
}

.category-tabs {
  background: #fff;
  padding: 12px 0;
  margin-bottom: 12px;
}

.category-tabs scroll-view {
  white-space: nowrap;
  padding: 0 12px;
}

.tab-item {
  display: inline-block;
  padding: 6px 16px;
  margin-right: 8px;
  background: #f5f7fa;
  border-radius: 16px;
  font-size: 14px;
  color: #666;
}

.tab-item.active {
  background: #409eff;
  color: #fff;
}

.product-list {
  padding: 0 12px;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.product-card {
  width: 48%;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 180px;
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
