package com.inspiration.island.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inspiration.island.entity.CollectRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CollectRecordMapper extends BaseMapper<CollectRecord> {

    /** 更新为取消收藏 */
    int updateCancel(@Param("inspirationId") Long inspirationId,
                     @Param("userId") Long userId);

    /** 查询是否存在有效收藏记录 */
    CollectRecord selectValid(@Param("inspirationId") Long inspirationId,
                              @Param("userId") Long userId);

    /** 统计收藏数量（只算 is_cancel=0 的） */
    int countValidByInspirationId(@Param("inspirationId") Long inspirationId);
}
