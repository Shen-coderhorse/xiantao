<template>
  <div class="address-management">
    <div class="page-header">
      <h2>收货地址管理</h2>
      <el-button type="primary" @click="showAddDialog">新增地址</el-button>
    </div>

    <div class="address-list">
      <div
        v-for="addr in addressList"
        :key="addr.id"
        class="address-card"
        :class="{ 'is-default': addr.isDefault === 1 }"
      >
        <div class="address-info">
          <div class="receiver">
            <span class="name">{{ addr.receiverName }}</span>
            <span class="phone">{{ addr.receiverPhone }}</span>
            <el-tag v-if="addr.isDefault === 1" size="small" type="primary">默认</el-tag>
          </div>
          <div class="full-address">{{ addr.fullAddress }}</div>
        </div>
        <div class="address-actions">
          <el-button v-if="addr.isDefault !== 1" size="small" @click="setDefault(addr.id)">设为默认</el-button>
          <el-button size="small" @click="showEditDialog(addr)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(addr.id)">删除</el-button>
        </div>
      </div>
    </div>

    <el-empty v-if="addressList.length === 0" description="暂无收货地址" />

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑地址' : '新增地址'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="form.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" type="textarea" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { createAddress, deleteAddress, getAddressList, setDefaultAddress, updateAddress } from '@/api/address'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'

const addressList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)

const form = ref({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})

const rules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

onMounted(() => {
  loadAddresses()
})

async function loadAddresses() {
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

function showAddDialog() {
  isEdit.value = false
  editId.value = null
  form.value = {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: 0
  }
  dialogVisible.value = true
}

function showEditDialog(addr) {
  isEdit.value = true
  editId.value = addr.id
  form.value = {
    receiverName: addr.receiverName,
    receiverPhone: addr.receiverPhone,
    province: addr.province,
    city: addr.city,
    district: addr.district,
    detailAddress: addr.detailAddress,
    isDefault: addr.isDefault
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateAddress(editId.value, form.value)
      ElMessage.success('地址更新成功')
    } else {
      await createAddress(form.value)
      ElMessage.success('地址添加成功')
    }
    dialogVisible.value = false
    loadAddresses()
  } catch (e) {
    console.error(e)
  }
}

async function setDefault(id) {
  try {
    await setDefaultAddress(id)
    ElMessage.success('已设为默认地址')
    loadAddresses()
  } catch (e) {
    console.error(e)
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除此地址吗？', '提示', { type: 'warning' })
    await deleteAddress(id)
    ElMessage.success('地址已删除')
    loadAddresses()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}
</script>

<style scoped>
.address-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #f1f5f9;
  font-weight: 700;
  position: relative;
  padding-left: 14px;
}

.page-header h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  border-radius: 2px;
  background: var(--gradient-primary);
}

.address-list {
  display: grid;
  gap: 16px;
}

.address-card {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 20px;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  transition: var(--transition-base);
}

.address-card:hover {
  transform: translateY(-4px);
  border-color: rgba(14, 165, 233, 0.25);
  box-shadow: 0 12px 28px rgba(14, 165, 233, 0.14);
}

.address-card.is-default {
  border-color: var(--brand-primary);
  background: #f0f9ff;
}

.address-info {
  margin-bottom: 16px;
}

.receiver {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.phone {
  font-size: 14px;
  color: var(--text-secondary);
}

.full-address {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.address-actions {
  display: flex;
  gap: 8px;
  border-top: 1px solid var(--border-color);
  padding-top: 12px;
}

@media (max-width: 768px) {
  .address-management {
    padding: 12px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .page-header h2 {
    font-size: 18px;
  }

  .address-card {
    padding: 16px;
  }

  .receiver {
    flex-wrap: wrap;
    gap: 8px;
  }

  .name {
    font-size: 15px;
  }

  .address-actions {
    flex-wrap: wrap;
  }
}
</style>
