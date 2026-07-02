import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Publish from '../views/Publish.vue'
import MyInspiration from '../views/MyInspiration.vue'
import UserProfile from '../views/UserProfile.vue'
import { ElMessage } from 'element-plus'

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/home', component: Home },
  { path: '/login', component: Login },
  { path: '/publish', component: Publish, meta: { needLogin: true } },
  { path: '/my', component: MyInspiration, meta: { needLogin: true } },
  { path: '/user/:id', component: UserProfile }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.needLogin && !token) {
    ElMessage.warning('请先登录')
    next('/login')
  } else {
    next()
  }
})

export default router
