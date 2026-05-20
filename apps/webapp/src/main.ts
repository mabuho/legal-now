import './assets/main.css'
import 'vue-toastification/dist/index.css'

import Toast, { POSITION, type PluginOptions } from 'vue-toastification'
import { createPinia } from 'pinia'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'


const toastOptions: PluginOptions = {
  position: POSITION.TOP_RIGHT,
  timeout: 5000,
  closeOnClick: true,
  maxToasts: 20,
  newestOnTop: true
}

// Create Vue app
const app = createApp(App)

// Use plugins
const pinia = createPinia();
app.use(pinia); // Usa Pinia
app.use(router)
app.use(Toast, toastOptions)

// Mount app
app.mount('#app')
