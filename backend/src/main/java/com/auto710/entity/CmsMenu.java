package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 多端可视化菜单（cms_menu）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("cms_menu")
public class CmsMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 端 mp/public/miniapp/h5 */
    private String platform;

    /** 父级ID */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 菜单英文名称 */
    private String menuNameEn;

    /** view/miniprogram/click/keyword/article/link */
    private String menuType;

    /** 跳转地址 */
    private String url;

    /** 小程序AppID */
    private String appId;

    /** 小程序页面路径 */
    private String pagePath;

    /** click/keyword 关键字 */
    private String keyValue;

    /** 图标 */
    private String icon;

    /** 排序 */
    private Integer sort;

    /** 是否显示 1是 0否 */
    private Integer visible;

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
