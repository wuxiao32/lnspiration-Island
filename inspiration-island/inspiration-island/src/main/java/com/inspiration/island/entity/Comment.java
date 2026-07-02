package com.inspiration.island.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long inspirationId;
    private Long userId;
    private String content;
    private String createTime;

    /** 联查字段，非数据库列 */
    @TableField(exist = false)
    private String username;
}
