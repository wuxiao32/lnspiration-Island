package com.inspiration.island.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inspiration.island.entity.User;
import com.inspiration.island.mapper.UserMapper;
import com.inspiration.island.service.UserService;
import com.inspiration.island.util.JwtUtil;
import com.inspiration.island.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String register(User user) {
        User oldUser = baseMapper.selectByUsername(user.getUsername());
        if (oldUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        String md5Pwd = PasswordUtil.md5Encrypt(user.getPassword());
        user.setPassword(md5Pwd);
        baseMapper.insert(user);
        return "注册成功";
    }

    @Override
    public String login(User user) {
        User dbUser = baseMapper.selectByUsername(user.getUsername());
        if (dbUser == null) {
            throw new RuntimeException("用户名不存在");
        }
        String inputMd5 = PasswordUtil.md5Encrypt(user.getPassword());
        if (!inputMd5.equals(dbUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        String token = jwtUtil.createToken(dbUser.getId(), dbUser.getUsername());
        redisTemplate.opsForValue().set("user:login:" + dbUser.getId(), token);
        return token;
    }

    @Override
    public Map<String, Object> getProfile(Long userId) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        int inspirationCount = baseMapper.countInspirationByUserId(userId);
        int commentCount = baseMapper.countCommentByUserId(userId);

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        // 不返回密码
        profile.put("inspirationCount", inspirationCount);
        profile.put("commentCount", commentCount);
        return profile;
    }
}
