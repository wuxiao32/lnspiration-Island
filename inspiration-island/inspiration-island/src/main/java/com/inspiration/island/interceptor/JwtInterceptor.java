package com.inspiration.island.interceptor;

import com.inspiration.island.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行登录、注册
        String uri = request.getRequestURI();
        if (uri.contains("/user/login") || uri.contains("/user/register")) {
            return true;
        }
        // 获取请求头 token
        String token = request.getHeader("token");
        if (token == null || "".equals(token)) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先登录\"}");
            return false;
        }
        try {
            Claims claims = jwtUtil.parseToken(token);
            if (jwtUtil.isExpire(token)) {
                response.getWriter().write("{\"code\":401,\"msg\":\"token已过期，请重新登录\"}");
                return false;
            }
            // 验证 Redis 中是否存在该 token（被踢下线则不存在）
            Integer userIdInt = claims.get("userId", Integer.class);
            Long userId = userIdInt.longValue();
            String cachedToken = (String) redisTemplate.opsForValue().get("user:login:" + userId);
            if (cachedToken == null) {
                response.getWriter().write("{\"code\":401,\"msg\":\"token已失效，请重新登录\"}");
                return false;
            }
            // 把用户信息放入 request
            request.setAttribute("userId", userId);
            request.setAttribute("username", claims.getSubject());
        } catch (Exception e) {
            response.getWriter().write("{\"code\":401,\"msg\":\"token非法\"}");
            return false;
        }
        return true;
    }
}
