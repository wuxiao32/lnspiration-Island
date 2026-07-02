import request from '@/utils/request'

// 收藏/取消收藏
export const toggleCollect = inspirationId => request.post(`/collect/toggle/${inspirationId}`)
// 获取收藏数量
export const getCollectCount = inspirationId => request.get(`/collect/count/${inspirationId}`)