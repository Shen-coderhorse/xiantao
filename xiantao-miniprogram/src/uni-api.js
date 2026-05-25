// uni API 兼容层 - 用于 H5 浏览器模式
import { useRouter } from 'vue-router'

// 全局 router 实例（在 main.js 中设置）
let routerInstance = null

export function setRouter(router) {
  routerInstance = router
}

// 存储
export function getStorageSync(key) {
  try {
    return localStorage.getItem(key)
  } catch (e) {
    return ''
  }
}

export function setStorageSync(key, value) {
  try {
    localStorage.setItem(key, value)
  } catch (e) {
    console.error('setStorageSync failed:', e)
  }
}

export function removeStorageSync(key) {
  try {
    localStorage.removeItem(key)
  } catch (e) {
    console.error('removeStorageSync failed:', e)
  }
}

// 导航
export function navigateTo({ url }) {
  if (routerInstance) {
    routerInstance.push(url)
  } else {
    console.warn('Router not initialized')
  }
}

export function redirectTo({ url }) {
  if (routerInstance) {
    routerInstance.replace(url)
  } else {
    console.warn('Router not initialized')
  }
}

export function switchTab({ url }) {
  if (routerInstance) {
    routerInstance.replace(url)
  } else {
    console.warn('Router not initialized')
  }
}

export function navigateBack() {
  if (routerInstance) {
    routerInstance.go(-1)
  } else {
    window.history.back()
  }
}

// 提示
export function showToast({ title, icon = 'success', duration = 1500 }) {
  // 使用浏览器原生提示
  if (icon === 'error' || icon === 'none') {
    alert(title)
  } else {
    // 创建简单的 toast 提示
    const toast = document.createElement('div')
    toast.style.cssText = `
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      background: rgba(0,0,0,0.8);
      color: white;
      padding: 10px 20px;
      border-radius: 4px;
      z-index: 9999;
      font-size: 14px;
      pointer-events: none;
    `
    toast.textContent = title
    document.body.appendChild(toast)
    setTimeout(() => {
      document.body.removeChild(toast)
    }, duration)
  }
}

export function showLoading({ title = '加载中...' }) {
  const loading = document.createElement('div')
  loading.id = 'uni-loading'
  loading.style.cssText = `
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: rgba(0,0,0,0.8);
    color: white;
    padding: 15px 25px;
    border-radius: 4px;
    z-index: 9999;
    font-size: 14px;
  `
  loading.textContent = title
  document.body.appendChild(loading)
}

export function hideLoading() {
  const loading = document.getElementById('uni-loading')
  if (loading) {
    document.body.removeChild(loading)
  }
}

export function showModal({ title, content, showCancel = true }) {
  return new Promise((resolve) => {
    if (window.confirm(content)) {
      resolve({ confirm: true, cancel: false })
    } else {
      resolve({ confirm: false, cancel: true })
    }
  })
}

// 请求
export function request(options) {
  const { url, method = 'GET', data = {}, header = {} } = options
  
  return new Promise((resolve, reject) => {
    const token = getStorageSync('token')
    const headers = {
      'Content-Type': 'application/json',
      ...header,
      'Authorization': token ? `Bearer ${token}` : ''
    }
    
    let fullUrl = url
    
    if (method === 'GET' && data && Object.keys(data).length > 0) {
      const params = new URLSearchParams()
      Object.keys(data).forEach(key => {
        if (data[key] !== undefined && data[key] !== null && data[key] !== '') {
          params.append(key, data[key])
        }
      })
      const queryString = params.toString()
      if (queryString) {
        fullUrl += (fullUrl.includes('?') ? '&' : '?') + queryString
      }
    }
    
    fetch(fullUrl, {
      method,
      headers,
      body: method !== 'GET' ? JSON.stringify(data) : undefined
    })
    .then(res => res.json())
    .then(data => {
      if (data.code === 200) {
        resolve(data)
      } else if (data.code === 401) {
        removeStorageSync('token')
        removeStorageSync('userInfo')
        if (routerInstance) {
          routerInstance.push('/login')
        }
        reject(data)
      } else {
        showToast({ title: data.message || '请求失败', icon: 'none' })
        reject(data)
      }
    })
    .catch(err => {
      showToast({ title: '网络错误', icon: 'none' })
      reject(err)
    })
  })
}

// 导出全局 uni 对象
const uni = {
  getStorageSync,
  setStorageSync,
  removeStorageSync,
  navigateTo,
  redirectTo,
  switchTab,
  navigateBack,
  showToast,
  showLoading,
  hideLoading,
  showModal,
  request
}

// 挂载到全局
if (typeof window !== 'undefined') {
  window.uni = uni
}

export default uni
