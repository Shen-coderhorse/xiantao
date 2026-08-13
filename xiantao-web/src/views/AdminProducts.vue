<template>
  <div class="admin-products">
    <div class="container">
      <div class="header">
        <h2>商品管理</h2>
        <el-button type="primary" @click="handleAdd">新增商品</el-button>
      </div>

      <div class="filter-section">
        <el-select v-model="filter.categoryId" placeholder="选择分类" clearable style="width: 150px" @change="loadProducts">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
        <el-select v-model="filter.status" placeholder="选择状态" clearable style="width: 120px; margin-left: 10px" @change="loadProducts">
          <el-option label="在售" :value="1" />
          <el-option label="已下架" :value="0" />
          <el-option label="已售" :value="2" />
        </el-select>
        <el-input v-model="filter.keyword" placeholder="搜索商品" clearable style="width: 200px; margin-left: 10px" @keyup.enter="loadProducts" />
        <el-button type="primary" style="margin-left: 10px" @click="loadProducts">搜索</el-button>
      </div>

      <el-table :data="products" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image v-if="row.imageList?.length" :src="row.imageList[0]" style="width: 60px; height: 60px" fit="cover" />
            <span v-else>暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="商品标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sellerName" label="卖家" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="80" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-popconfirm title="确定删除该商品吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadProducts"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'" width="600px">
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
        <el-form-item label="商品状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">在售</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
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
              multiple
            >
              <el-icon><Plus /></el-icon>
              <template #tip>
                <div class="el-upload__tip">最多上传5张图片，单张不超过10MB</div>
              </template>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategoryList } from '@/api/category'
import { getAdminProductList, adminCreateProduct, adminUpdateProduct, adminDeleteProduct, adminUpdateStatus } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const products = ref([])
const categories = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const fileList = ref([])

const filter = reactive({
  categoryId: null,
  status: null,
  keyword: ''
})

const form = reactive({
  id: null,
  title: '',
  categoryId: null,
  price: null,
  originalPrice: null,
  status: 1,
  description: '',
  images: ''
})

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

onMounted(async () => {
  await loadCategories()
  await loadProducts()
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
    const res = await getAdminProductList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      categoryId: filter.categoryId,
      status: filter.status,
      keyword: filter.keyword
    })
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  resetForm()
  fileList.value = []
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  form.id = row.id
  form.title = row.title
  form.categoryId = row.categoryId
  form.price = row.price
  form.originalPrice = row.originalPrice
  form.status = row.status
  form.description = row.description
  form.images = row.images

  if (row.imageList && row.imageList.length > 0) {
    fileList.value = row.imageList.map((url, index) => ({
      name: `image-${index}`,
      url: url.startsWith('http') ? url : `/api${url}`
    }))
  } else {
    fileList.value = []
  }

  dialogVisible.value = true
}

function resetForm() {
  form.id = null
  form.title = ''
  form.categoryId = null
  form.price = null
  form.originalPrice = null
  form.status = 1
  form.description = ''
  form.images = ''
}

async function handleDelete(id) {
  try {
    await adminDeleteProduct(id)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (e) {
    console.error(e)
  }
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await adminUpdateStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '上架成功' : '下架成功')
    loadProducts()
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

function handleUploadSuccess(response) {
  if (response.code === 200 && response.data) {
    const urls = fileList.value
      .filter(f => f.response && f.response.data)
      .flatMap(f => f.response.data)
      .concat(fileList.value.filter(f => f.url && !f.response).map(f => {
        if (f.url.startsWith('/api')) {
          return f.url.replace('/api', '')
        }
        return f.url
      }))

    const newUrls = response.data || []
    form.images = [...new Set([...urls, ...newUrls])].join(',')
  }
}

function handleRemove() {
  const urls = fileList.value
    .filter(f => {
      if (f.response && f.response.data) {
        return true
      }
      if (f.url && !f.response) {
        return true
      }
      return false
    })
    .flatMap(f => {
      if (f.response && f.response.data) {
        return f.response.data
      }
      if (f.url) {
        if (f.url.startsWith('/api')) {
          return f.url.replace('/api', '')
        }
        return f.url
      }
      return []
    })

  form.images = [...new Set(urls)].join(',')
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const imageUrls = fileList.value
    .flatMap(f => {
      if (f.response && f.response.data) {
        return f.response.data
      }
      if (f.url) {
        if (f.url.startsWith('/api')) {
          return f.url.replace('/api', '')
        }
        return f.url
      }
      return []
    })

  form.images = [...new Set(imageUrls)].join(',')

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await adminUpdateProduct(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await adminCreateProduct(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadProducts()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
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
.admin-products {
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

.filter-section {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.upload-area {
  width: 100%;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}
</style>
