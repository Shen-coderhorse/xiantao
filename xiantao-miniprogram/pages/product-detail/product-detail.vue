<template>
  <view class="detail-page">
    <swiper class="swiper" indicator-dots autoplay v-if="product.images">
      <swiper-item v-for="(img, index) in product.images.split(',')" :key="index">
        <image :src="formatImage(img)" mode="aspectFill" class="swiper-img" />
      </swiper-item>
    </swiper>

    <view class="info-section">
      <view class="price-row">
        <text class="price">¥{{ product.price }}</text>
        <text class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</text>
      </view>
      <text class="title">{{ product.title }}</text>
      <text class="desc">{{ product.description }}</text>
    </view>

    <view class="seller-section">
      <text class="label">卖家：</text>
      <text>{{ product.sellerName || '未知' }}</text>
      <text class="views">{{ product.viewCount }}次浏览</text>
    </view>

    <view class="action-bar">
      <button class="btn-contact" @click="contactSeller">联系卖家</button>
      <button class="btn-buy" @click="handleBuy">立即购买</button>
    </view>
  </view>
</template>

<script>
import { getProductDetail, createOrder } from '@/api/index'

export default {
  data() {
    return {
      id: '',
      product: {}
    }
  },
  onLoad(options) {
    this.id = options.id
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      try {
        const res = await getProductDetail(this.id)
        this.product = res.data || {}
      } catch (e) {
        console.error(e)
      }
    },
    formatImage(url) {
      if (!url) return ''
      return url.startsWith('http') ? url : 'http://localhost:8080' + url
    },
    contactSeller() {
      uni.showToast({ title: '请联系卖家获取联系方式', icon: 'none' })
    },
    async handleBuy() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        setTimeout(() => {
          uni.navigateTo({ url: '/pages/login/login' })
        }, 1500)
        return
      }
      try {
        uni.showLoading({ title: '创建订单中...' })
        const res = await createOrder({ productId: this.id })
        uni.hideLoading()
        uni.showToast({ title: '订单创建成功', icon: 'success' })
        setTimeout(() => {
          uni.switchTab({ url: '/pages/orders/orders' })
        }, 1500)
      } catch (e) {
        uni.hideLoading()
        console.error(e)
      }
    }
  }
}
</script>

<style scoped>
.detail-page { padding-bottom: 80px; }
.swiper { height: 300px; }
.swiper-img { width: 100%; height: 100%; }
.info-section { padding: 16px; background: #fff; margin-bottom: 8px; }
.price-row { display: flex; align-items: baseline; margin-bottom: 8px; }
.price { font-size: 24px; font-weight: bold; color: #f56c6c; }
.original-price { font-size: 14px; color: #999; text-decoration: line-through; margin-left: 8px; }
.title { font-size: 18px; font-weight: bold; display: block; margin-bottom: 8px; }
.desc { font-size: 14px; color: #666; line-height: 1.6; }
.seller-section { padding: 16px; background: #fff; font-size: 14px; color: #666; margin-bottom: 8px; }
.label { font-weight: bold; }
.views { float: right; color: #999; }
.action-bar { position: fixed; bottom: 0; left: 0; right: 0; display: flex; padding: 8px 16px; background: #fff; border-top: 1px solid #eee; }
.btn-contact { flex: 1; background: #f5f7fa; color: #666; border-radius: 20px; font-size: 14px; }
.btn-buy { flex: 1; background: #f56c6c; color: #fff; border-radius: 20px; font-size: 14px; margin-left: 12px; }
</style>
