package com.inspiration.island.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inspiration.island.entity.CollectRecord;

public interface CollectRecordService extends IService<CollectRecord> {

    /** 收藏/取消收藏切换，返回操作结果描述 */
    String toggle(Long inspirationId, Long userId);

    /** 统计某个灵感的收藏数 */
    int countByInspirationId(Long inspirationId);
}
