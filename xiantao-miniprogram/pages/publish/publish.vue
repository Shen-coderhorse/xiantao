<template>
  <view class="publish-page">
    <view class="form">
      <text class="label">商品标题</text>
      <input v-model="form.title" placeholder="请输入商品标题" class="input" />

      <text class="label">商品描述</text>
      <textarea v-model="form.description" placeholder="请描述商品详情" class="textarea" />

      <text class="label">售价（元）</text>
      <input v-model="form.price" type="digit" placeholder="请输入售价" class="input" />

      <text class="label">原价（元）</text>
      <input v-model="form.originalPrice" type="digit" placeholder="请输入原价（可选）" class="input" />

      <text class="label">商品分类</text>
      <picker :range="categories" range-key="name" @change="onCategoryChange">
        <view class="picker">{{ categories[categoryIndex] ? categories[categoryIndex].name : '请选择分类' }}</view>
      </picker>

      <button class="btn" @click="handleSubmit">发布商品</button>
    </view>
  </view>
</template>

<script>
import { getCategories, createProduct } from '@/api/index'

export default {
  data() {
    return {
      form: { title: '', description: '', price: '', originalPrice: '' },
      categories: [],
      categoryIndex: 0
    }
  },
  onLoad() {
    this.loadCategories()
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
    onCategoryChange(e) {
      this.categoryIndex = e.detail.value
    },
    async handleSubmit() {
      if (!this.form.title || !this.form.description || !this.form.price) {
        uni.showToast({ title: '请填写完整信息', icon: 'none' })
        return
      }
      const cat = this.categories[this.categoryIndex]
      const data = {
        ...this.form,
        categoryId: cat ? cat.id : 1,
        price: parseFloat(this.form.price),
        originalPrice: this.form.originalPrice ? parseFloat(this.form.originalPrice) : null
      }
      try {
        uni.showLoading({ title: '发布中...' })
        await createProduct(data)
        uni.hideLoading()
        uni.showToast({ title: '发布成功', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 1500)
      } catch (e) {
        uni.hideLoading()
        console.error(e)
      }
    }
  }
}
</script>

<style scoped>
.publish-page { padding: 16px; background: #f5f7fa; min-height: 100vh; }
.form { background: #fff; padding: 16px; border-radius: 8px; }
.label { font-size: 14px; font-weight: bold; margin-bottom: 6px; display: block; }
.input { width: 100%; height: 40px; border: 1px solid #eee; border-radius: 6px; padding: 0 10px; margin-bottom: 14px; font-size: 14px; box-sizing: border-box; }
.textarea { width: 100%; height: 100px; border: 1px solid #eee; border-radius: 6px; padding: 10px; margin-bottom: 14px; font-size: 14px; box-sizing: border-box; }
.picker { height: 40px; border: 1px solid #eee; border-radius: 6px; padding: 0 10px; margin-bottom: 14px; font-size: 14px; line-height: 40px; }
.btn { width: 100%; background: #409eff; color: #fff; border-radius: 20px; font-size: 16px; margin-top: 10px; }
</style>
