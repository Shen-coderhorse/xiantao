<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>分类管理</span>
        <el-button type="primary" @click="showAddDialog">新增分类</el-button>
      </div>
    </template>
    <el-table :data="categories">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" width="150" />
      <el-table-column prop="icon" label="图标" width="100" />
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增分类" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'

const categories = ref([])
const dialogVisible = ref(false)
const form = ref({ name: '', icon: '', sort: 0 })

onMounted(() => loadData())

async function loadData() {
  try {
    const res = await request.get('/admin/category/list')
    categories.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

function showAddDialog() {
  form.value = { name: '', icon: '', sort: 0 }
  dialogVisible.value = true
}

async function handleAdd() {
  try {
    await request.post('/admin/category', form.value)
    ElMessage.success('添加成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
