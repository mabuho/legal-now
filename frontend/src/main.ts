import './assets/main.css'
import 'vue-toastification/dist/index.css'

import Toast, { POSITION, type PluginOptions } from 'vue-toastification'
import { createPinia } from 'pinia'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Import Radix Vue components
import {
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuRoot,
  NavigationMenuTrigger,
} from 'radix-vue'

import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'

const vuetify = createVuetify({
  components,
  directives,
})

const toastOptions: PluginOptions = {
  position: POSITION.TOP_RIGHT,
  timeout: 5000,
  closeOnClick: true,
  maxToasts: 20,
  newestOnTop: true
}

// Create Vue app
const app = createApp(App)

// Register Radix Vue components globally
app.component('NavigationMenuContent', NavigationMenuContent)
app.component('NavigationMenuItem', NavigationMenuItem)
app.component('NavigationMenuLink', NavigationMenuLink)
app.component('NavigationMenuList', NavigationMenuList)
app.component('NavigationMenuRoot', NavigationMenuRoot)
app.component('NavigationMenuTrigger', NavigationMenuTrigger)

// Use plugins
const pinia = createPinia();
app.use(pinia); // Usa Pinia
app.use(router)
app.use(Toast, toastOptions)
app.use(vuetify)

// Mount app
app.mount('#app')
