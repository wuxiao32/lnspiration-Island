package com.inspiration.island.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inspiration.island.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /** 发布评论 */
    void publish(Comment comment);

    /** 查看某个灵感的所有评论（带用户昵称） */
    List<Comment> listByInspirationId(Long inspirationId);

    /** 删除评论（仅作者本人） */
    void delete(Long commentId, Long currentUserId);
}
