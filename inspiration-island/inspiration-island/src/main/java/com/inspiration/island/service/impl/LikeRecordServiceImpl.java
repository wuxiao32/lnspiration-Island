package com.inspiration.island.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inspiration.island.entity.LikeRecord;
import com.inspiration.island.mapper.LikeRecordMapper;
import com.inspiration.island.service.LikeRecordService;
import org.springframework.stereotype.Service;

@Service
public class LikeRecordServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord> implements LikeRecordService {

    @Override
    public String toggle(Long inspirationId, Long userId) {
        // 检查是否有有效点赞
        LikeRecord validRecord = baseMapper.selectValid(inspirationId, userId);
        if (validRecord != null) {
            // 已点赞，更新为取消
            baseMapper.updateCancel(inspirationId, userId);
            return "取消点赞成功";
        } else {
            // 未点赞，新增记录（默认 is_cancel=0）
            LikeRecord record = new LikeRecord();
            record.setUserId(userId);
            record.setInspirationId(inspirationId);
            record.setIsCancel(0);
            baseMapper.insert(record);
            return "点赞成功";
        }
    }

    @Override
    public int countByInspirationId(Long inspirationId) {
        return baseMapper.countValidByInspirationId(inspirationId);
    }
}
