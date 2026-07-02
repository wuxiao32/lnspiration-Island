package com.inspiration.island.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    // 秘钥
    private static final String SECRET = "inspirationIslandSecretKey1234567890";
    // 过期时间 1小时
    private static final long EXPIRE_TIME = 3600 * 1000;
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 生成token
    public String createToken(Long userId, String username){
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_TIME);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .claim("userId",userId)
                .signWith(KEY)
                .compact();
    }

    // 解析token
    public Claims parseToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 判断是否过期
    public boolean isExpire(String token){
        return parseToken(token).getExpiration().before(new Date());
    }
}