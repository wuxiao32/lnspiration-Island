package com.inspiration.island.controller;

import com.inspiration.island.common.Result;
import com.inspiration.island.entity.User;
import com.inspiration.island.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /** 注册 */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        try {
            String msg = userService.register(user);
            return Result.success(msg);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        try {
            String token = userService.login(user);
            return Result.success(token);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /** 退出登录（需登录） */
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        redisTemplate.delete("user:login:" + userId);
        return Result.success("已退出登录");
    }

    /** 用户主页（公开） */
    @GetMapping("/profile/{id}")
    public Result<?> profile(@PathVariable Long id) {
        try {
            Map<String, Object> profile = userService.getProfile(id);
            return Result.success(profile);
        } catch (RuntimeException e) {
            return Result.error(404, e.getMessage());
        }
    }
}
