import request from '@/utils/request'

// 注册
export const register = data => request.post('/user/register', data)
// 登录
export const login = data => request.post('/user/login', data)
// 用户主页
export const getUserProfile = id => request.get(`/user/profile/${id}`)
