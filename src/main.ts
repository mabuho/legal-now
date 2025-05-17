import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
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
app.use(createPinia())
app.use(router)

// Mount app
app.mount('#app')
