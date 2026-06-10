// postcss.config.js (ESM 格式)
import tailwindcss from '@tailwindcss/postcss'
import autoprefixer from 'autoprefixer'

export default {
  plugins: [
    tailwindcss(), // 新版插件调用方式
    autoprefixer()
  ],
}