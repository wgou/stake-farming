import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    host: true, // 监听所有地址，包括localhost和IP地址
    port: 5173,
    proxy: {
      '/stake': {
        target: 'https://index.cloudko.org',
        changeOrigin: true,
        rewrite: (path) => path // 保持 /stake 路径
      }
    }
  }
})
