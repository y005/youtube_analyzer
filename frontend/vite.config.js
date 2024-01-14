import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/youtube': 'http://localhost:8080'
    },
  },
  build: {
    outDir: "../src/main/resources/static",
    assetsDir: "./asset"
  }
})
