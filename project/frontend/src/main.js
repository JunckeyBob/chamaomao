import { createApp } from 'vue'
import App from './App.vue'
import './style.css'
const app = createApp(App)

app.config.devtools = false
app.mount('#app')
