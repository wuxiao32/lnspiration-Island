package com.inspiration.island.interceptor;

import com.inspiration.island.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行登录、注册
        String uri = request.getRequestURI();
        if(uri.contains("/user/login") || uri.contains("/user/register")){
            return true;
        }
        // 获取请求头token
        String token = request.getHeader("token");
        if(token == null || "".equals(token)){
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先登录\"}");
            return false;
        }
        try {
            Claims claims = jwtUtil.parseToken(token);
            if(jwtUtil.isExpire(token)){
                response.getWriter().write("{\"code\":401,\"msg\":\"token已过期，请重新登录\"}");
                return false;
            }
            // 把用户信息放入request
            Integer userId = claims.get("userId", Integer.class);
            request.setAttribute("userId", userId.longValue());
            request.setAttribute("username",claims.getSubject());
        }catch (Exception e){
            response.getWriter().write("{\"code\":401,\"msg\":\"token非法\"}");
            return false;
        }
        return true;
    }
}