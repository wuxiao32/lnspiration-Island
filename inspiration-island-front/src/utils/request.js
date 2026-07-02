import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// 请求拦截：自动带上token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.token = token // 和后端JwtInterceptor读取header字段保持一致
  }
  return config
}, err => Promise.reject(err))

// 响应拦截：统一处理返回码
service.interceptors.response.use(res => {
  const data = res.data
  if (data.code !== 200) {
    ElMessage.error(data.msg)
    // 401未登录/过期，跳转登录
    if (data.code === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    return Promise.reject(data)
  }
  return data
}, err => {
  ElMessage.error('服务器异常，请稍后重试')
  return Promise.reject(err)
})

export default service