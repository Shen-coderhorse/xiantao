<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>分类管理</span>
        <el-button type="primary" @click="showAddDialog">新增分类</el-button>
      </div>
    </template>
    <el-table :data="categories" v-loading="loading">
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
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingCategory.id ? '编辑分类' : '新增分类'" width="500px">
      <el-form :model="editingCategory" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="editingCategory.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="editingCategory.icon" placeholder="如：📱" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editingCategory.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="editingCategory.status" :active-value="1" :inactive-value="0" active-text="正常" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'

const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingCategory = ref({ name: '', icon: '', sort: 0, status: 1 })

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/admin/category/list')
    categories.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function showAddDialog() {
  editingCategory.value = { name: '', icon: '', sort: 0, status: 1 }
  dialogVisible.value = true
}

function showEditDialog(row) {
  editingCategory.value = { ...row }
  dialogVisible.value = true
}

async function handleSave() {
  if (!editingCategory.value.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  try {
    if (editingCategory.value.id) {
      await request.put(`/admin/category/${editingCategory.value.id}`, editingCategory.value)
      ElMessage.success('修改成功')
    } else {
      await request.post('/admin/category', editingCategory.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}

async function toggleStatus(row) {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await request.put(`/admin/category/${row.id}`, { ...row, status: newStatus })
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？删除后不可恢复。`, '提示', { type: 'warning' })
    await request.delete(`/admin/category/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
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
