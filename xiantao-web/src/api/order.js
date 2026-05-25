import request from '@/utils/request'

export function createOrder(data) {
  return request.post('/order', data)
}

export function getOrderList(params) {
  return request.get('/order/list', { params })
}

export function getOrderDetail(id) {
  return request.get(`/order/${id}`)
}

export function payOrder(id) {
  return request.put(`/order/${id}/pay`)
}

export function completeOrder(id) {
  return request.put(`/order/${id}/complete`)
}

export function cancelOrder(id) {
  return request.put(`/order/${id}/cancel`)
}

export function shipOrder(id, data) {
  return request.put(`/order/${id}/ship`, data)
}

export function receiveOrder(id) {
  return request.put(`/order/${id}/receive`)
}

export function getOrderTransactions(orderId) {
  return request.get(`/transaction/order/${orderId}`)
}

export function payTransaction(orderId) {
  return request.post(`/transaction/pay/${orderId}`)
}

export function releaseTransaction(orderId) {
  return request.post(`/transaction/release/${orderId}`)
}
