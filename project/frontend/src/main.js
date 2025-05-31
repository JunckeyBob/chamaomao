import { createApp } from 'vue'
import App from './App.vue'
import router from './index.js'
import './style.css'
const app = createApp(App)

app.config.devtools = false
app.use(router)
app.mount('#app')
