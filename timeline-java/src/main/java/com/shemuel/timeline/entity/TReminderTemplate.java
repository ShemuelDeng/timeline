package com.shemuel.timeline.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
@TableName("t_reminder_template")
@Tag(name = "提醒模板表对象")
public class TReminderTemplate implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "")
    private Long id;

    @Schema(description = "所属分类ID")
    private Long categoryId;

    @Schema(description = "模板名称，如喝水提醒、运动提醒")
    private String name;

    @Schema(description = "模板描述")
    private String description;

    @Schema(description = "图标URL")
    private String icon;

    @Schema(description = "是否系统模板，1系统，0用户自定义模板")
    private Integer isSystem;

    @Schema(description = "创建人（系统模板为空）")
    private Long userId;

    @Schema(description = "")
    private LocalDateTime createTime;

    @Schema(description = "")
    private LocalDateTime updateTime;
}
