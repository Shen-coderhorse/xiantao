import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { setRouter } from './uni-api'
import './uni-api' // 挂载全局 uni 对象

const app = createApp(App)
app.use(router)
setRouter(router) // 设置路由实例供 uni API 使用
app.mount('#app')
