<template>
  <div class="publish-page">
    <div class="container">
      <div class="publish-card">
        <h2>{{ isEdit ? '编辑商品' : '发布商品' }}</h2>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="商品标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入商品标题" maxlength="100" show-word-limit />
          </el-form-item>
          <el-form-item label="商品分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
              <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="出售价格" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" style="width: 200px" />
          </el-form-item>
          <el-form-item label="原价">
            <el-input-number v-model="form.originalPrice" :min="0" :precision="2" :step="10" style="width: 200px" />
          </el-form-item>
          <el-form-item label="商品描述">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请描述您的商品..." maxlength="500" show-word-limit />
          </el-form-item>
          <el-form-item label="商品图片">
            <div class="upload-area">
              <el-upload
                v-model:file-list="fileList"
                action="/api/upload/images"
                list-type="picture-card"
                :headers="uploadHeaders"
                :on-success="handleUploadSuccess"
                :on-remove="handleRemove"
                :before-upload="beforeUpload"
                :limit="5"
                accept="image/*"
                name="files"
              >
                <el-icon><Plus /></el-icon>
                <template #tip>
                  <div class="el-upload__tip">最多上传5张图片，单张不超过10MB</div>
                </template>
              </el-upload>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleSubmit">
              {{ isEdit ? '保存修改' : '发布商品' }}
            </el-button>
            <el-button @click="router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategoryList } from '@/api/category'
import { createProduct, updateProduct, getProductDetail } from '@/api/product'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.query.id)
const formRef = ref()
const loading = ref(false)
const categories = ref([])
const fileList = ref([])
const uploadedUrls = ref([])

const form = reactive({
  title: '',
  categoryId: null,
  price: null,
  originalPrice: null,
  description: '',
  images: ''
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

onMounted(async () => {
  await loadCategories()
  if (isEdit.value) {
    await loadProduct()
  }
})

async function loadCategories() {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function loadProduct() {
  try {
    const res = await getProductDetail(route.query.id)
    const data = res.data
    form.title = data.title
    form.categoryId = data.categoryId
    form.price = data.price
    form.originalPrice = data.originalPrice
    form.description = data.description
    form.images = data.images
    
    if (data.imageList && data.imageList.length > 0) {
      fileList.value = data.imageList.map((url, index) => ({
        name: `image-${index}`,
        url: url.startsWith('http') ? url : `http://localhost:8080${url}`,
        serverUrl: url
      }))
      uploadedUrls.value = [...data.imageList]
    }
  } catch (e) {
    console.error(e)
  }
}

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB!')
    return false
  }
  return true
}

function handleUploadSuccess(response, file, uploadFileList) {
  if (response.code === 200 && response.data && response.data.length > 0) {
    const newUrl = response.data[0]
    uploadedUrls.value.push(newUrl)
    file.serverUrl = newUrl
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

function handleRemove(file, uploadFileList) {
  if (file.serverUrl) {
    const index = uploadedUrls.value.indexOf(file.serverUrl)
    if (index > -1) {
      uploadedUrls.value.splice(index, 1)
    }
  } else if (file.url && file.url.includes('/uploads/')) {
    const url = file.url.replace('http://localhost:8080', '')
    const index = uploadedUrls.value.indexOf(url)
    if (index > -1) {
      uploadedUrls.value.splice(index, 1)
    }
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  form.images = uploadedUrls.value.join(',')

  loading.value = true
  try {
    if (isEdit.value) {
      await updateProduct(route.query.id, form)
      ElMessage.success('修改成功')
    } else {
      await createProduct(form)
      ElMessage.success('发布成功')
    }
    router.push('/my-products')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.publish-page {
  padding: 20px 0;
}

.publish-card {
  background: #fff;
  padding: 32px;
  border-radius: var(--radius-md);
  max-width: 720px;
  margin: 0 auto;
  box-shadow: var(--shadow-sm);
}

.publish-card h2 {
  margin-bottom: 30px;
  text-align: center;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 1px;
}

.upload-area {
  width: 100%;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 120px;
  height: 120px;
}

:deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
}
</style>
