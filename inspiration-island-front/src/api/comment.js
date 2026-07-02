import request from '@/utils/request'

// 发布评论
export const publishComment = data => request.post('/comment/publish', data)
// 查询评论
export const getCommentList = inspirationId => request.get(`/comment/list/${inspirationId}`)
// 删除评论
export const deleteComment = id => request.delete(`/comment/delete/${id}`)
