import request from '@/utils/request'

export function getLogistics(orderId) {
    return request.get(`/logistics/${orderId}`)
}

export function getLogisticsTracks(orderId) {
    return request.get(`/logistics/${orderId}/track`)
}

export function getLogisticsLocation(orderId) {
    return request.get(`/logistics/${orderId}/location`)
}

export function simulateLogisticsUpdate(orderId) {
    return request.post(`/logistics/simulate/${orderId}`)
}
