package com.inspiration.island.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inspiration.island.entity.Comment;
import com.inspiration.island.mapper.CommentMapper;
import com.inspiration.island.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public void publish(Comment comment) {
        baseMapper.insert(comment);
    }

    @Override
    public List<Comment> listByInspirationId(Long inspirationId) {
        return baseMapper.selectCommentWithUser(inspirationId);
    }

    @Override
    public void delete(Long commentId, Long currentUserId) {
        Comment db = baseMapper.selectById(commentId);
        if (db == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!db.getUserId().equals(currentUserId)) {
            throw new RuntimeException("只能删除自己的评论");
        }
        baseMapper.deleteById(commentId);
    }
}
