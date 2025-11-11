import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import dns from 'node:dns'

// 解决DNS解析顺序问题，确保localhost解析正确
dns.setDefaultResultOrder('verbatim')

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000, 
    open: true,
    host: true, // 监听所有IP地址
    strictPort: true, // 使用严格端口，避免自动切换
    hmr: {
      protocol: 'ws',
      host: 'localhost',
      
      overlay: true, // 显示错误覆盖层
    },
    // 代理配置，用于连接后端服务
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    minify: 'terser',
    sourcemap: false
  },
  // 环境变量配置
  envDir: '.',
  envPrefix: 'VITE_'
})