import request from '@/utils/request'

// 获取全部灵感
export const getInspirationList = () => request.get('/inspiration/list')
// 发布灵感
export const publishInspiration = data => request.post('/inspiration/publish', data)
// 我的灵感
export const getMyInspiration = () => request.get('/inspiration/myList')
// 修改灵感
export const updateInspiration = data => request.put('/inspiration/update', data)
// 删除灵感
export const deleteInspiration = id => request.delete(`/inspiration/delete/${id}`)
