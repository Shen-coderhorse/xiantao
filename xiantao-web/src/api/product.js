import request from '@/utils/request'

export function getProductList(params) {
  return request.get('/product/list', { params })
}

export function getProductDetail(id) {
  return request.get(`/product/${id}`)
}

export function createProduct(data) {
  return request.post('/product', data)
}

export function updateProduct(id, data) {
  return request.put(`/product/${id}`, data)
}

export function deleteProduct(id) {
  return request.delete(`/product/${id}`)
}

export function getMyProducts(params) {
  return request.get('/product/my', { params })
}
