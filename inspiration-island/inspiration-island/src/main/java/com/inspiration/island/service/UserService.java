package com.inspiration.island.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inspiration.island.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {

    /** 注册用户 */
    String register(User user);

    /** 登录，返回 JWT token */
    String login(User user);

    /** 获取用户主页信息（公开） */
    Map<String, Object> getProfile(Long userId);
}
