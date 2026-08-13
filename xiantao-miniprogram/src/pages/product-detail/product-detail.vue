<template>
  <div class="detail-page">
    <div class="swiper" v-if="imageList.length > 0">
      <div class="image-gallery">
        <img
          :src="currentImage"
          class="main-img"
        />
        <div class="thumb-list" v-if="imageList.length > 1">
          <img
            v-for="(img, index) in imageList"
            :key="index"
            :src="img"
            :class="['thumb', { active: currentImage === img }]"
            @click="currentImage = img"
          />
        </div>
      </div>
    </div>
    <div v-else class="no-image">
      <span>暂无图片</span>
    </div>

    <div class="info-section">
      <div class="price-row">
        <span class="price">¥{{ product.price }}</span>
        <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
      </div>
      <span class="title">{{ product.title }}</span>
      <span class="desc">{{ product.description }}</span>
    </div>

    <div class="seller-section">
      <span class="label">卖家：</span>
      <span>{{ product.sellerName || '未知' }}</span>
      <span class="views">{{ product.viewCount }}次浏览</span>
    </div>

    <div class="address-section" @click="showAddressPicker = true">
      <span class="label">配送至：</span>
      <span class="address-text" v-if="selectedAddress">
        {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detailAddress }}
      </span>
      <span class="address-text placeholder" v-else>请选择收货地址</span>
      <span class="arrow">›</span>
    </div>

    <div class="action-bar">
      <button class="btn-contact" @click="contactSeller">联系卖家</button>
      <button class="btn-buy" @click="handleBuy">立即购买</button>
    </div>

    <div v-if="showAddressPicker" class="overlay" @click.self="showAddressPicker = false">
      <div class="address-picker">
        <div class="picker-header">
          <span>选择收货地址</span>
          <span class="close" @click="showAddressPicker = false">✕</span>
        </div>
        <div class="address-options">
          <div
            v-for="addr in addressList"
            :key="addr.id"
            :class="['address-option', { selected: selectedAddress && selectedAddress.id === addr.id }]"
            @click="selectAddress(addr)"
          >
            <div class="addr-name">{{ addr.receiverName }} {{ addr.receiverPhone }}</div>
            <div class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}</div>
            <span v-if="addr.isDefault === 1" class="default-tag">默认</span>
            <span v-if="selectedAddress && selectedAddress.id === addr.id" class="check">✓</span>
          </div>
        </div>
        <div v-if="addressList.length === 0" class="no-address">
          <span>暂无收货地址</span>
          <button class="btn-go-address" @click="showAddressPicker = false; $router.push('/address')">去添加</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getProductDetail, createOrder, getAddressList, getImageUrl, getImageList } from '@/api/index'
import uni from '@/uni-api'

export default {
  name: 'ProductDetailPage',
  data() {
    return {
      product: {},
      imageList: [],
      currentImage: '',
      addressList: [],
      selectedAddress: null,
      showAddressPicker: false
    }
  },
  mounted() {
    const id = this.$route.query.id
    if (id) {
      this.loadProduct(id)
    }
    this.loadAddresses()
  },
  methods: {
    async loadProduct(id) {
      try {
        const res = await getProductDetail(id)
        this.product = res.data || {}
        // 填充图片列表
        if (this.product.imageList && this.product.imageList.length > 0) {
          this.imageList = this.product.imageList.map(url => getImageUrl(url))
        } else if (this.product.images) {
          this.imageList = getImageList(this.product.images)
        }
        this.currentImage = this.imageList[0] || ''
      } catch (e) {
        console.error(e)
      }
    },
    async loadAddresses() {
      const token = uni.getStorageSync('token')
      if (!token) return
      try {
        const res = await getAddressList()
        this.addressList = res.data || []
        const defaultAddr = this.addressList.find(a => a.isDefault === 1)
        if (defaultAddr) {
          this.selectedAddress = defaultAddr
        } else if (this.addressList.length > 0) {
          this.selectedAddress = this.addressList[0]
        }
      } catch (e) {
        if (e.code !== 401) console.error(e)
      }
    },
    selectAddress(addr) {
      this.selectedAddress = addr
      this.showAddressPicker = false
    },
    formatImage(url) {
      return getImageUrl(url)
    },
    contactSeller() {
      uni.showToast({ title: '功能开发中', icon: 'none' })
    },
    async handleBuy() {
      if (!uni.getStorageSync('token')) {
        this.$router.push('/login')
        return
      }
      if (!this.selectedAddress) {
        uni.showToast({ title: '请先选择收货地址', icon: 'none' })
        this.showAddressPicker = true
        return
      }
      try {
        await createOrder({ productId: this.product.id, addressId: this.selectedAddress.id })
        uni.showToast({ title: '下单成功', icon: 'success' })
        this.$router.push('/orders')
      } catch (e) {
        if (e.code !== 401) console.error(e)
      }
    }
  }
}
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: var(--app-canvas);
  background-attachment: fixed;
  padding-bottom: 80px;
}

.swiper {
  width: 100%;
  background: #fff;
}

.image-gallery {
  background: #fff;
}

.main-img {
  width: 100%;
  height: 300px;
  object-fit: cover;
  background: #f5f5f5;
}

.thumb-list {
  display: flex;
  gap: 8px;
  padding: 8px;
  overflow-x: auto;
}

.thumb {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  object-fit: cover;
  border: 2px solid transparent;
  flex-shrink: 0;
}

.thumb.active {
  border-color: #409eff;
}

.no-image {
  width: 100%;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
}

.info-section {
  background: #fff;
  padding: 16px;
  margin-bottom: 12px;
}

.price-row {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.price {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
  margin-right: 12px;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.title {
  display: block;
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.desc {
  font-size: 14px;
  color: #666;
}

.seller-section {
  background: #fff;
  padding: 16px;
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.label {
  font-size: 14px;
  color: #999;
  margin-right: 8px;
}

.views {
  margin-left: auto;
  font-size: 12px;
  color: #999;
}

.address-section {
  background: #fff;
  padding: 16px;
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  cursor: pointer;
}

.address-text {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.address-text.placeholder {
  color: #999;
}

.arrow {
  font-size: 18px;
  color: #999;
  margin-left: 8px;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  padding: 12px;
  background: #fff;
  border-top: 1px solid #eee;
  gap: 12px;
}

.btn-contact, .btn-buy {
  flex: 1;
  height: 44px;
  border: none;
  border-radius: 22px;
  font-size: 16px;
  cursor: pointer;
}

.btn-contact {
  background: #fff;
  color: #409eff;
  border: 1px solid #409eff;
}

.btn-buy {
  background: #409eff;
  color: #fff;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
}

.address-picker {
  background: #fff;
  width: 100%;
  border-radius: 12px 12px 0 0;
  max-height: 60vh;
  overflow-y: auto;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
  font-size: 16px;
  font-weight: bold;
}

.close {
  cursor: pointer;
  font-size: 18px;
  color: #999;
}

.address-options {
  padding: 8px 0;
}

.address-option {
  padding: 12px 16px;
  cursor: pointer;
  position: relative;
  border-bottom: 1px solid #f5f5f5;
}

.address-option.selected {
  background: #f0f7ff;
}

.addr-name {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 4px;
}

.addr-detail {
  font-size: 13px;
  color: #666;
}

.default-tag {
  position: absolute;
  top: 12px;
  right: 40px;
  background: #409eff;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.check {
  position: absolute;
  top: 16px;
  right: 16px;
  color: #409eff;
  font-size: 18px;
  font-weight: bold;
}

.no-address {
  text-align: center;
  padding: 30px;
  color: #999;
}

.btn-go-address {
  display: block;
  margin: 12px auto 0;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 8px 24px;
  cursor: pointer;
}
</style>
