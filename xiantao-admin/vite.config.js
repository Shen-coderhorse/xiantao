import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import { defineConfig } from 'vite'

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': resolve(__dirname, 'src')
        }
    },
    server: {
        port: 5174,
        strictPort: true,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true
            },
            '/uploads': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        }
    },
    build: {
        chunkSizeWarningLimit: 1500,
        rollupOptions: {
            output: {
                // 体积大的依赖单独分块，消除 500KB chunk 警告并提升缓存命中率
                manualChunks: {
                    'vue-vendor': ['vue', 'vue-router'],
                    'element-plus': ['element-plus'],
                    echarts: ['echarts']
                }
            }
        }
    }
})
