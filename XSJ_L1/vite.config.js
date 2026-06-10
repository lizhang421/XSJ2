import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

function printUrlsPlugin() {
  return {
    name: 'print-urls',
    configureServer(server) {
      const originalPrintUrls = server.printUrls.bind(server)
      server.printUrls = () => {
        originalPrintUrls()
        const host = server.resolvedUrls.local[0]
        console.log()
        console.log('  ➜  用户系统:   ' + host)
        console.log('  ➜  管理员系统: ' + host + 'sys-x7k9m2-admin.html')
        console.log()
      }
    }
  }
}

export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    printUrlsPlugin()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/api': {
        target: "http://localhost:8080",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  build: {
    rollupOptions: {
      input: {
        main: fileURLToPath(new URL('./index.html', import.meta.url)),
        admin: fileURLToPath(new URL('./sys-x7k9m2-admin.html', import.meta.url))
      }
    }
  }
})
