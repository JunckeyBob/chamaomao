import { createRouter, createWebHistory } from 'vue-router'
import Login from './login.vue'
import Home from './home.vue'
import Profile from './info.vue'
import Application from './application.vue'
import Map from './map.vue'
import Cat from './cat.vue'
import Edit from './edit.vue'
import Adopt from './adopt.vue'

const routes = [
  { path: '/', name: 'Login', component: Login },
  { path: '/home', name: 'Home', component: Home },
  { path: '/profile', name: 'Profile', component: Profile },
  { path: '/application', name: 'Application', component: Application },
  { path: '/map', name: 'Map', component: Map },
  { path: '/cat', name: 'Cat', component: Cat },
  { path: '/edit', name: 'Edit', component: Edit },
  {  path: '/adopt', name: 'Adopt', component: Adopt }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router