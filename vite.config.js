import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  publicDir: 'static',
  server: {
    port: 5000,
    host: '0.0.0.0',
    allowedHosts: true,
    proxy: {
      '/api/auth': {
        target: 'http://localhost:8282',
        changeOrigin: true,
        secure: false
      },
      '/api': {
        target: 'http://localhost:8282',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
    }
  },
  build: {
    outDir: '../dist'
  }
})
