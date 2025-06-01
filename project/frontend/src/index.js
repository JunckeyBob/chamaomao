import { createRouter, createWebHistory } from 'vue-router'
import Login from './login.vue'
import Home from './home.vue'
import Profile from './info.vue'

const routes = [
  { path: '/', name: 'Login', component: Login },
  { path: '/home', name: 'Home', component: Home },
  { path: '/profile', name: 'Profile', component: Profile }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router