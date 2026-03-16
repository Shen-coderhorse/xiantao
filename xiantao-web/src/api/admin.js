import request from '@/utils/request'

export function getAdminProductList(params) {
  return request.get('/admin/product/list', { params })
}

export function adminCreateProduct(data) {
  return request.post('/admin/product', data)
}

export function adminUpdateProduct(id, data) {
  return request.put(`/admin/product/${id}`, data)
}

export function adminDeleteProduct(id) {
  return request.delete(`/admin/product/${id}`)
}

export function adminUpdateStatus(id, status) {
  return request.put(`/admin/product/${id}/status`, null, { params: { status } })
}
