package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 文件上传记录（sys_file）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("sys_file")
public class SysFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 存储文件名 */
    private String fileName;

    /** 原始文件名 */
    private String originalName;

    /** 访问地址 */
    private String fileUrl;

    /** 文件类型 */
    private String fileType;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 存储驱动 local/s3/oss/cos/minio */
    private String storageDriver;

    /** 存储桶 */
    private String bucket;

    /** 文件MD5 */
    private String md5;

    /** 业务类型 */
    private String bizType;

    /** 上传会员ID */
    private Long memberId;

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
