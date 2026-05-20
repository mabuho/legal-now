import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import tailwindcss from "@tailwindcss/vite";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    tailwindcss()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    devSourcemap: true
  },
  /*hmr: {
      protocol: 'wss',
      //host: 'a305-2806-2f0-9f80-eebe-edd4-2f04-3d01-133f.ngrok-free.app',
      host: '306ed936fce1.ngrok-free.app',
      port: 443,
  },*/
  server: {
    host: true,
    cors: true,
    port: 5173,
    allowedHosts: [
      //'5e63-2806-2f0-9f80-eebe-edd4-2f04-3d01-133f.ngrok-free.app'
      'e0b27e45c80f.ngrok-free.app', // vue
      'http://localhost:5173',
    ]
  },
  build: {
    sourcemap: true,
    cssCodeSplit: true
  }
})
