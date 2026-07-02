package com.inspiration.island.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("inspiration")
public class Inspiration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String createTime;
    /** 逻辑删除：0=正常 1=已删除 */
    private Integer isDelete;

    /** 联查字段，非数据库列 */
    @TableField(exist = false)
    private String username;

    /** 评论集合，非数据库列 */
    @TableField(exist = false)
    private List<Comment> commentList;
}
