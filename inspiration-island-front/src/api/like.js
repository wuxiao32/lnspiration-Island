import request from '@/utils/request'

// 点赞/取消点赞
export const toggleLike = inspirationId => request.post(`/like/toggle/${inspirationId}`)
// 获取点赞数量
export const getLikeCount = inspirationId => request.get(`/like/count/${inspirationId}`)