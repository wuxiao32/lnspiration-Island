package com.inspiration.island.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inspiration.island.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /** 根据灵感ID联查用户表获取评论者昵称 */
    List<Comment> selectCommentWithUser(@Param("inspirationId") Long inspirationId);
}
