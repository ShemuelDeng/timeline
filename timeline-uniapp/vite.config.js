import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  css: {
    preprocessorOptions: {
      scss: {
        // 用 @import 注入变量表，保证 node_modules 里的 uview 组件也拿得到 $u-*
        additionalData: '@import "uview-plus/theme.scss"; @import "@/uni.scss";'
      }
    }
  }
})
