<template>
  <view class="address-page">
    <view class="address-list">
      <view
        v-for="addr in addressList"
        :key="addr.id"
        :class="['address-card', { default: addr.isDefault === 1 }]"
        @click="selectAddress(addr)"
      >
        <view class="addr-info">
          <text class="name">{{ addr.receiverName }} {{ addr.receiverPhone }}</text>
          <text class="detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}</text>
        </view>
        <view class="addr-actions">
          <text v-if="addr.isDefault === 1" class="tag">默认</text>
          <text class="btn-edit" @click.stop="editAddress(addr)">编辑</text>
          <text class="btn-del" @click.stop="deleteAddress(addr.id)">删除</text>
        </view>
      </view>
    </view>

    <button class="btn-add" @click="showForm = true; resetForm()">+ 新增地址</button>

    <view v-if="showForm" class="form-overlay">
      <view class="form">
        <text class="form-title">新增/编辑地址</text>
        <input v-model="form.receiverName" placeholder="收货人姓名" class="input" />
        <input v-model="form.receiverPhone" placeholder="手机号" class="input" />
        <input v-model="form.province" placeholder="省" class="input" />
        <input v-model="form.city" placeholder="市" class="input" />
        <input v-model="form.district" placeholder="区" class="input" />
        <input v-model="form.detailAddress" placeholder="详细地址" class="input" />
        <view class="form-actions">
          <button class="btn-cancel" @click="showForm = false">取消</button>
          <button class="btn-save" @click="saveAddress">保存</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getAddressList, createAddress, updateAddress, deleteAddress as delAddress } from '@/api/index'

export default {
  data() {
    return {
      addressList: [],
      showForm: false,
      editId: null,
      form: { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '' }
    }
  },
  onShow() {
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
      this.editId = null
      this.form = { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '' }
    },
    editAddress(addr) {
      this.editId = addr.id
      this.form = { ...addr }
      this.showForm = true
    },
    async saveAddress() {
      if (!this.form.receiverName || !this.form.receiverPhone || !this.form.detailAddress) {
        uni.showToast({ title: '请填写完整地址', icon: 'none' })
        return
      }
      try {
        if (this.editId) {
          await updateAddress(this.editId, this.form)
        } else {
          await createAddress(this.form)
        }
        this.showForm = false
        this.loadAddresses()
        uni.showToast({ title: '保存成功', icon: 'success' })
      } catch (e) {
        console.error(e)
      }
    },
    async deleteAddress(id) {
      try {
        await delAddress(id)
        this.loadAddresses()
        uni.showToast({ title: '删除成功', icon: 'success' })
      } catch (e) {
        console.error(e)
      }
    },
    selectAddress(addr) {
      uni.$emit('selectAddress', addr)
      uni.navigateBack()
    }
  }
}
</script>

<style scoped>
.address-page { padding: 12px; background: #f5f7fa; min-height: 100vh; }
.address-card { background: #fff; padding: 14px; margin-bottom: 8px; border-radius: 8px; border-left: 3px solid #eee; }
.address-card.default { border-left-color: #409eff; }
.addr-info .name { font-size: 16px; font-weight: bold; display: block; margin-bottom: 4px; }
.addr-info .detail { font-size: 13px; color: #666; }
.addr-actions { display: flex; align-items: center; margin-top: 8px; }
.tag { background: #409eff; color: #fff; font-size: 11px; padding: 2px 6px; border-radius: 3px; margin-right: 8px; }
.btn-edit, .btn-del { font-size: 13px; margin-right: 16px; }
.btn-edit { color: #409eff; } .btn-del { color: #f56c6c; }
.btn-add { width: 100%; background: #fff; color: #409eff; border-radius: 8px; font-size: 15px; margin-top: 12px; }
.form-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; }
.form { background: #fff; padding: 20px; border-radius: 12px; width: 90%; }
.form-title { font-size: 16px; font-weight: bold; display: block; margin-bottom: 12px; }
.input { width: 100%; height: 38px; border: 1px solid #eee; border-radius: 6px; padding: 0 10px; margin-bottom: 10px; font-size: 13px; box-sizing: border-box; }
.form-actions { display: flex; gap: 10px; }
.btn-cancel { flex: 1; background: #f5f5f5; border-radius: 20px; font-size: 14px; }
.btn-save { flex: 1; background: #409eff; color: #fff; border-radius: 20px; font-size: 14px; }
</style>
