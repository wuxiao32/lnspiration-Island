package com.inspiration.island.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inspiration.island.entity.CollectRecord;
import com.inspiration.island.mapper.CollectRecordMapper;
import com.inspiration.island.service.CollectRecordService;
import org.springframework.stereotype.Service;

@Service
public class CollectRecordServiceImpl extends ServiceImpl<CollectRecordMapper, CollectRecord> implements CollectRecordService {

    @Override
    public String toggle(Long inspirationId, Long userId) {
        // 检查是否有有效收藏
        CollectRecord validRecord = baseMapper.selectValid(inspirationId, userId);
        if (validRecord != null) {
            // 已收藏，更新为取消
            baseMapper.updateCancel(inspirationId, userId);
            return "取消收藏成功";
        } else {
            // 未收藏，新增记录（默认 is_cancel=0）
            CollectRecord record = new CollectRecord();
            record.setUserId(userId);
            record.setInspirationId(inspirationId);
            record.setIsCancel(0);
            baseMapper.insert(record);
            return "收藏成功";
        }
    }

    @Override
    public int countByInspirationId(Long inspirationId) {
        return baseMapper.countValidByInspirationId(inspirationId);
    }
}
