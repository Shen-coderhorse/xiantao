import request from '@/utils/request'

export function getMyTransactions() {
    return request.get('/transaction/my')
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

export function refundTransaction(orderId) {
    return request.post(`/transaction/refund/${orderId}`)
}
