<template>
  <div class="address-page">
    <div class="address-list">
      <div
        v-for="addr in addressList"
        :key="addr.id"
        :class="['address-card', { default: addr.isDefault === 1 }]"
      >
        <div class="addr-info">
          <span class="name">{{ addr.receiverName }} {{ addr.receiverPhone }}</span>
          <span class="detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}</span>
        </div>
        <div class="addr-actions">
          <span v-if="addr.isDefault === 1" class="tag">默认</span>
          <span class="btn-edit" @click="editAddress(addr)">编辑</span>
          <span class="btn-del" @click="deleteAddress(addr.id)">删除</span>
        </div>
      </div>
    </div>

    <button class="btn-add" @click="showForm = true; resetForm()">+ 新增地址</button>

    <div v-if="showForm" class="form-overlay" @click.self="showForm = false">
      <div class="form">
        <span class="form-title">新增/编辑地址</span>
        <input v-model="form.receiverName" placeholder="收货人姓名" class="input" />
        <input v-model="form.receiverPhone" placeholder="手机号" class="input" />
        <input v-model="form.province" placeholder="省" class="input" />
        <input v-model="form.city" placeholder="市" class="input" />
        <input v-model="form.district" placeholder="区" class="input" />
        <input v-model="form.detailAddress" placeholder="详细地址" class="input" />
        <div class="form-actions">
          <button class="btn-cancel" @click="showForm = false">取消</button>
          <button class="btn-save" @click="saveAddress">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAddressList, createAddress, updateAddress, deleteAddress } from '@/api/index'
import uni from '@/uni-api'

export default {
  name: 'AddressPage',
  data() {
    return {
      addressList: [],
      showForm: false,
      editingId: null,
      form: {
        receiverName: '',
        receiverPhone: '',
        province: '',
        city: '',
        district: '',
        detailAddress: ''
      }
    }
  },
  mounted() {
    const token = uni.getStorageSync('token')
    if (!token) {
      this.$router.push('/login')
      return
    }
    this.loadAddresses()
  },
  methods: {
    async loadAddresses() {
      try {
        const res = await getAddressList()
        this.addressList = res.data || []
      } catch (e) {
        console.error(e)
      }
    },
    resetForm() {
      this.editingId = null
      this.form = {
        receiverName: '',
        receiverPhone: '',
        province: '',
        city: '',
        district: '',
        detailAddress: ''
      }
    },
    editAddress(addr) {
      this.editingId = addr.id
      this.form = { ...addr }
      this.showForm = true
    },
    async saveAddress() {
      if (!this.form.receiverName || !this.form.receiverPhone) {
        uni.showToast({ title: '请填写完整信息', icon: 'none' })
        return
      }
      try {
        if (this.editingId) {
          await updateAddress(this.editingId, this.form)
        } else {
          await createAddress(this.form)
        }
        uni.showToast({ title: '保存成功', icon: 'success' })
        this.showForm = false
        this.loadAddresses()
      } catch (e) {
        console.error(e)
      }
    },
    async deleteAddress(id) {
      if (!confirm('确定删除该地址？')) return
      try {
        await deleteAddress(id)
        uni.showToast({ title: '删除成功', icon: 'success' })
        this.loadAddresses()
      } catch (e) {
        console.error(e)
      }
    }
  }
}
</script>

<style scoped>
.address-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 16px;
  padding-bottom: 70px;
}

.address-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.address-card.default {
  border: 2px solid #409eff;
}

.addr-info {
  margin-bottom: 12px;
}

.addr-info .name {
  display: block;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 4px;
}

.addr-info .detail {
  font-size: 14px;
  color: #666;
}

.addr-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.tag {
  background: #409eff;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.btn-edit, .btn-del {
  cursor: pointer;
  font-size: 14px;
}

.btn-edit { color: #409eff; }
.btn-del { color: #f56c6c; }

.btn-add {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.form-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: flex-end;
  z-index: 1000;
}

.form {
  background: #fff;
  padding: 20px;
  border-radius: 12px 12px 0 0;
  width: 100%;
}

.form-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 16px;
  display: block;
}

.input {
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-actions {
  display: flex;
  gap: 12px;
}

.btn-cancel, .btn-save {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-cancel { background: #f5f5f5; }
.btn-save { background: #409eff; color: #fff; }
</style>
