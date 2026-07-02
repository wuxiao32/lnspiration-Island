package com.inspiration.island.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("collect_record")
public class CollectRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long inspirationId;
    private LocalDateTime createTime;
    /** 0:正常收藏 1:取消收藏 */
    private Integer isCancel;
}
