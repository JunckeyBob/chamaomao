import { createApp } from 'vue'
import App from './App.vue'
import router from './index.js'
import './style.css'

const app = createApp(App)


app.config.devtools = false
app.use(router)

app.config.globalProperties.$globalData = {
  cat_id :0,
  user_id :0
};

app.mount('#app')


