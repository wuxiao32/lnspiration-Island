import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path' // 必须导入path

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // 把@映射到src文件夹，这行是解决@报错的关键
      '@': resolve(__dirname, 'src')
    }
  }
})