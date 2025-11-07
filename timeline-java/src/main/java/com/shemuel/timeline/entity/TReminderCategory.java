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
@TableName("t_reminder_category")
@Tag(name = "提醒分类表对象")
public class TReminderCategory implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "")
    private Long id;

    @Schema(description = "分类名称，如健康、生活、工作")
    private String name;

    @Schema(description = "分类编码，如 HEALTH, LIFE, WORK")
    private String code;

    @Schema(description = "排序字段")
    private Integer sortOrder;

    @Schema(description = "")
    private LocalDateTime createTime;

    @Schema(description = "")
    private LocalDateTime updateTime;
}
