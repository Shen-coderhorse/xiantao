<template>
  <div class="publish-page">
    <div class="form">
      <label class="label">商品标题</label>
      <input v-model="form.title" placeholder="请输入商品标题" class="input" />

      <label class="label">商品描述</label>
      <textarea v-model="form.description" placeholder="请描述商品详情" class="textarea" rows="4"></textarea>

      <label class="label">售价（元）</label>
      <input v-model="form.price" type="number" placeholder="请输入售价" class="input" />

      <label class="label">原价（元）</label>
      <input v-model="form.originalPrice" type="number" placeholder="请输入原价（可选）" class="input" />

      <label class="label">商品分类</label>
      <select v-model="form.categoryId" class="input">
        <option value="">请选择分类</option>
        <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
      </select>

      <button class="btn" @click="handleSubmit">发布商品</button>
    </div>
  </div>
</template>

<script>
import { createProduct, getCategories } from '@/api/index'
import uni from '@/uni-api'

export default {
  name: 'PublishPage',
  data() {
    return {
      form: {
        title: '',
        description: '',
        price: '',
        originalPrice: '',
        categoryId: ''
      },
      categories: []
    }
  },
  mounted() {
    const token = uni.getStorageSync('token')
    if (!token) {
      this.$router.push('/login')
      return
    }
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
    async handleSubmit() {
      if (!this.form.title || !this.form.price) {
        uni.showToast({ title: '请填写完整信息', icon: 'none' })
        return
      }
      try {
        await createProduct(this.form)
        uni.showToast({ title: '发布成功', icon: 'success' })
        this.$router.push('/index')
      } catch (e) {
        console.error(e)
      }
    }
  }
}
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  background: var(--app-canvas);
  background-attachment: fixed;
  padding: 20px 16px;
}

.form {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}

.input, .textarea {
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.textarea {
  resize: vertical;
}

.btn {
  width: 100%;
  height: 44px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}
</style>
