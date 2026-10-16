package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 配件分类（part_category）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("part_category")
public class PartCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父级ID */
    private Long parentId;

    /** 分类名称 */
    private String categoryName;

    /** 排序 */
    private Integer sort;

    /** 状态 1启用 0禁用 */
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 0否 1是 */
    @TableLogic
    @TableField(select = false)
    private Integer deleted;
}
