package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 技师（technician）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("technician")
public class Technician implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联系统用户ID */
    private Long userId;

    /** 门店ID */
    private Long storeId;

    /** 技师姓名 */
    private String techName;

    /** 手机号(加密) */
    private String phone;

    /** 头像 */
    private String avatar;

    /** 工种 机修/钣喷/美容/轮胎/通用 */
    private String techType;

    /** 技师等级 初级/中级/高级/专家 */
    private String level;

    /** 资质证书编号 */
    private String certNo;

    /** 资质证明图片 */
    private String qualification;

    /** 技师简介 */
    private String intro;

    /** 状态 1在职 0离职 */
    private Integer status;

    /** 排序 */
    private Integer sort;

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
