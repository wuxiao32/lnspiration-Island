package com.inspiration.island.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inspiration.island.entity.Inspiration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InspirationMapper extends BaseMapper<Inspiration> {

    /** 根据用户ID查询自己发布的灵感（按时间倒序） */
    List<Inspiration> selectByUserId(@Param("userId") Long userId);

    /** 联查用户表查询所有灵感（用于首页展示） */
    List<Inspiration> selectAllWithUser();
}
