import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import './style.css'
import './assets/fonts.css'

// Import pages
import HomePage from './pages/HomePage.vue'
import TransactionPage from './pages/TransactionPage.vue'
import SharePage from './pages/SharePage.vue'
import AccountPage from './pages/AccountPage.vue'
import ChatPage from './pages/ChatPage.vue'
import WhitepaperPage from './pages/WhitepaperPage.vue'

const routes = [
  { path: '/', component: HomePage },
  { path: '/transaction', component: TransactionPage },
  { path: '/share', component: SharePage },
  { path: '/account', component: AccountPage },
  { path: '/chat', component: ChatPage },
  { path: '/whitepaper', component: WhitepaperPage },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

const app = createApp(App)
app.use(router)
app.mount('#app')
