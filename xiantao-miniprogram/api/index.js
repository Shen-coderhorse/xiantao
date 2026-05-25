const BASE_URL = 'http://localhost:8080/api'

function request(options) {
    return new Promise((resolve, reject) => {
        const token = uni.getStorageSync('token')
        uni.request({
            url: BASE_URL + options.url,
            method: options.method || 'GET',
            data: options.data || {},
            header: {
                'Content-Type': 'application/json',
                'Authorization': token ? `Bearer ${token}` : ''
            },
            success: (res) => {
                if (res.data.code === 200) {
                    resolve(res.data)
                } else {
                    uni.showToast({ title: res.data.message || '请求失败', icon: 'none' })
                    reject(res.data)
                }
            },
            fail: (err) => {
                uni.showToast({ title: '网络错误', icon: 'none' })
                reject(err)
            }
        })
    })
}

export function login(data) {
    return request({ url: '/auth/login', method: 'POST', data })
}

export function register(data) {
    return request({ url: '/auth/register', method: 'POST', data })
}

export function getUserInfo() {
    return request({ url: '/user/info' })
}

export function updateUserInfo(data) {
    return request({ url: '/user/info', method: 'PUT', data })
}

export function getUserCredit() {
    return request({ url: '/rating/credit' })
}

export function getProductList(params) {
    return request({ url: '/product/list', data: params })
}

export function getProductDetail(id) {
    return request({ url: `/product/${id}` })
}

export function getCategories() {
    return request({ url: '/category/list' })
}

export function createProduct(data) {
    return request({ url: '/product', method: 'POST', data })
}

export function getMyProducts(params) {
    return request({ url: '/product/my', data: params })
}

export function deleteProduct(id) {
    return request({ url: `/product/${id}`, method: 'DELETE' })
}

export function createOrder(data) {
    return request({ url: '/order', method: 'POST', data })
}

export function getOrderList(params) {
    return request({ url: '/order/list', data: params })
}

export function getOrderDetail(id) {
    return request({ url: `/order/${id}` })
}

export function payOrder(id) {
    return request({ url: `/order/${id}/pay`, method: 'PUT' })
}

export function completeOrder(id) {
    return request({ url: `/order/${id}/complete`, method: 'PUT' })
}

export function cancelOrder(id) {
    return request({ url: `/order/${id}/cancel`, method: 'PUT' })
}

export function getAddressList() {
    return request({ url: '/address/list' })
}

export function createAddress(data) {
    return request({ url: '/address', method: 'POST', data })
}

export function updateAddress(id, data) {
    return request({ url: `/address/${id}`, method: 'PUT', data })
}

export function deleteAddress(id) {
    return request({ url: `/address/${id}`, method: 'DELETE' })
}

export function setDefaultAddress(id) {
    return request({ url: `/address/${id}/default`, method: 'PUT' })
}

export function getLogistics(orderId) {
    return request({ url: `/logistics/${orderId}` })
}

export function shipOrder(id, data) {
    return request({ url: `/order/${id}/ship`, method: 'PUT', data })
}
