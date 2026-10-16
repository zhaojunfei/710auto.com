package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 门店信息（store_info）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("store_info")
public class StoreInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 门店名称 */
    private String storeName;

    /** 门店英文名称 */
    private String storeNameEn;

    /** 门店Logo */
    private String logo;

    /** 客服电话 */
    private String phone;

    /** 门店地址 */
    private String address;

    /** 门店英文地址 */
    private String addressEn;

    /** 经度 */
    private String longitude;

    /** 纬度 */
    private String latitude;

    /** 营业时间 */
    private String businessHours;

    /** 门店简介 */
    private String intro;

    /** 门店英文简介 */
    private String introEn;

    /** 营业执照 */
    private String businessLicense;

    /** 机动车维修经营备案 */
    private String repairCert;

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
