package com.inspiration.island.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inspiration.island.entity.Inspiration;
import com.inspiration.island.mapper.InspirationMapper;
import com.inspiration.island.service.InspirationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InspirationServiceImpl extends ServiceImpl<InspirationMapper, Inspiration> implements InspirationService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<Inspiration> listAll() {
        redisTemplate.delete("hot:inspiration:all");
        String cacheKey = "hot:inspiration:all";
        Object cacheData = redisTemplate.opsForValue().get(cacheKey);
        if (cacheData != null) {
            @SuppressWarnings("unchecked")
            List<Inspiration> cached = (List<Inspiration>) cacheData;
            return cached;
        }
        List<Inspiration> list = baseMapper.selectAllWithUser();
        redisTemplate.opsForValue().set(cacheKey, list);
        return list;
    }

    @Override
    public List<Inspiration> listByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public void publish(Inspiration inspiration) {
        baseMapper.insert(inspiration);
        // 清除首页缓存
        redisTemplate.delete("hot:inspiration:all");
    }

    @Override
    public void update(Inspiration inspiration, Long currentUserId) {
        Inspiration db = baseMapper.selectById(inspiration.getId());
        if (db == null) {
            throw new RuntimeException("灵感不存在");
        }
        if (!db.getUserId().equals(currentUserId)) {
            throw new RuntimeException("只能修改自己的灵感");
        }
        // 只更新标题和内容
        db.setTitle(inspiration.getTitle());
        db.setContent(inspiration.getContent());
        baseMapper.updateById(db);
        // 清除首页缓存
        redisTemplate.delete("hot:inspiration:all");
    }

    @Override
    public void delete(Long inspirationId, Long currentUserId) {
        Inspiration db = baseMapper.selectById(inspirationId);
        if (db == null) {
            throw new RuntimeException("灵感不存在");
        }
        if (!db.getUserId().equals(currentUserId)) {
            throw new RuntimeException("只能删除自己的灵感");
        }
        // 软删除
        db.setIsDelete(1);
        baseMapper.updateById(db);
        // 清除首页缓存
        redisTemplate.delete("hot:inspiration:all");
    }
}
