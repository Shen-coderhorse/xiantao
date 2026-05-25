import request from '@/utils/request'

export function createRating(data) {
    return request.post('/rating', data)
}

export function getMyRatings() {
    return request.get('/rating/my')
}

export function getUserRatings(userId) {
    return request.get(`/rating/user/${userId}`)
}

export function getUserCredit() {
    return request.get('/rating/credit')
}
