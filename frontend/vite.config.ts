import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

import vueDevTools from 'vite-plugin-vue-devtools'
import tailwindcss from '@tailwindcss/vite'

import electron from 'vite-plugin-electron'


export default defineConfig({

  plugins: [

    vue(),

    vueDevTools(),

    tailwindcss(),



    electron([

      {

        entry: 'src-electron/main.ts'

      },

      {

        entry: 'src-electron/preload.ts',

        onstart(options) {

          options.reload()

        }

      }

    ])

  ],

  server: {
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/ws': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true,
        secure: false
      }
    },
    allowedHosts: [
      't-gzvfllwu.tunn.dev',
      '1co1161532vp7.vicp.fun'
    ]
  },

  resolve: {

    alias: {

      '@':
        fileURLToPath(
          new URL('./src', import.meta.url)
        )

    }

  }

})