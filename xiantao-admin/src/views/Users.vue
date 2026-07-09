<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>用户管理</span>
        <el-input v-model="keyword" placeholder="搜索用户名/昵称/手机号" style="width: 300px" clearable @keyup.enter="loadData" @clear="loadData">
          <template #append>
            <el-button @click="loadData">搜索</el-button>
          </template>
        </el-input>
      </div>
    </template>
    <el-table :data="users" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'">
            {{ row.role === 'admin' ? '管理员' : '用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="balance" label="余额" width="100">
        <template #default="{ row }">¥{{ row.balance }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" :type="row.role === 'admin' ? 'info' : 'danger'" @click="toggleRole(row)">
            {{ row.role === 'admin' ? '降为用户' : '升为管理员' }}
          </el-button>
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
  </el-card>
</template>

<script setup>
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'

const users = ref([])
const loading = ref(false)
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/admin/user/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value }
    })
    users.value = res.data.records || []
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await ElMessageBox.confirm(`确定${newStatus === 1 ? '启用' : '禁用'}用户「${row.nickname}」吗？`, '提示', { type: 'warning' })
    await request.put(`/admin/user/${row.id}/status`, null, { params: { status: newStatus } })
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

async function toggleRole(row) {
  const newRole = row.role === 'admin' ? 'user' : 'admin'
  try {
    await ElMessageBox.confirm(`确定将用户「${row.nickname}」${newRole === 'admin' ? '设为管理员' : '降为普通用户'}吗？`, '提示', { type: 'warning' })
    await request.put(`/admin/user/${row.id}/role`, null, { params: { role: newRole } })
    ElMessage.success('操作成功')
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
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
