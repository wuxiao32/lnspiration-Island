package com.inspiration.island.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inspiration.island.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {

    /** 更新为取消点赞 */
    int updateCancel(@Param("inspirationId") Long inspirationId,
                     @Param("userId") Long userId);

    /** 查询是否存在有效点赞记录 */
    LikeRecord selectValid(@Param("inspirationId") Long inspirationId,
                           @Param("userId") Long userId);

    /** 统计点赞数量（只算 is_cancel=0 的） */
    int countValidByInspirationId(@Param("inspirationId") Long inspirationId);
}
