<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>商品管理</span>
        <div class="header-filters">
          <el-select v-model="filterCategoryId" placeholder="全部分类" clearable style="width: 150px" @change="loadData">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
          <el-select v-model="filterStatus" placeholder="全部状态" clearable style="width: 120px" @change="loadData">
            <el-option label="在售" :value="1" />
            <el-option label="下架" :value="0" />
            <el-option label="已售" :value="2" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索商品" style="width: 200px" clearable @keyup.enter="loadData" @clear="loadData">
            <template #append>
              <el-button @click="loadData">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </template>
    <el-table :data="products" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="商品标题" min-width="200" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="originalPrice" label="原价" width="100">
        <template #default="{ row }">¥{{ row.originalPrice }}</template>
      </el-table-column>
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="sellerName" label="卖家" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      class="pagination"
      @current-change="loadData"
    />

    <!-- 编辑商品对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑商品" width="600px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="商品标题">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="editForm.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="editForm.originalPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="editForm.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="editForm.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="editForm.images" type="textarea" :rows="2" placeholder="多个URL用逗号分隔" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="在售" :value="1" />
            <el-option label="下架" :value="0" />
            <el-option label="已售" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSave">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'

const products = ref([])
const categories = ref([])
const loading = ref(false)
const keyword = ref('')
const filterCategoryId = ref(null)
const filterStatus = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const editDialogVisible = ref(false)
const editForm = ref({})

onMounted(() => {
  loadCategories()
  loadData()
})

async function loadCategories() {
  try {
    const res = await request.get('/category/list')
    categories.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function loadData() {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value
    }
    if (filterCategoryId.value !== null) params.categoryId = filterCategoryId.value
    if (filterStatus.value !== null) params.status = filterStatus.value

    const res = await request.get('/admin/product/list', { params })
    products.value = res.data.records || []
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function showEditDialog(row) {
  editForm.value = { ...row }
  editDialogVisible.value = true
}

async function handleEditSave() {
  if (!editForm.value.title) {
    ElMessage.warning('请输入商品标题')
    return
  }
  try {
    await request.put(`/admin/product/${editForm.value.id}`, {
      title: editForm.value.title,
      price: editForm.value.price,
      originalPrice: editForm.value.originalPrice,
      categoryId: editForm.value.categoryId,
      description: editForm.value.description,
      images: editForm.value.images,
      status: editForm.value.status
    })
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await request.put(`/admin/product/${row.id}/status`, null, { params: { status: newStatus } })
    ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除此商品吗？删除后不可恢复。', '提示', { type: 'warning' })
    await request.delete(`/admin/product/${id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

function getStatusType(status) {
  return status === 1 ? 'success' : status === 2 ? 'info' : 'danger'
}

function getStatusText(status) {
  return status === 1 ? '在售' : status === 2 ? '已售' : '下架'
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-filters {
  display: flex;
  gap: 10px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
