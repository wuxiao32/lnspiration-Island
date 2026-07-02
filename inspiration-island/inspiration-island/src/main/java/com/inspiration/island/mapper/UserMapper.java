package com.inspiration.island.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inspiration.island.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /** 根据用户名查询用户 */
    User selectByUsername(@Param("username") String username);

    /** 登录用：根据用户名和密码查询用户 */
    User selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /** 查询用户灵感数量 */
    int countInspirationByUserId(@Param("userId") Long userId);

    /** 查询用户评论数量 */
    int countCommentByUserId(@Param("userId") Long userId);
}
