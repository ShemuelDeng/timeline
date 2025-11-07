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
@TableName("t_reminder_template_field")
@Tag(name = "模板扩展字段定义表对象")
public class TReminderTemplateField implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "")
    private Long id;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "字段名，如 pay_type, sport_type")
    private String fieldName;

    @Schema(description = "字段显示名，如 缴费类型, 运动类型")
    private String fieldLabel;

    @Schema(description = "字段类型，如 text, select, number, date")
    private String fieldType;

    @Schema(description = "可选项(JSON数组)，")
    private String fieldOptions;

    @Schema(description = "")
    private Integer sortOrder;

    @Schema(description = "")
    private LocalDateTime createTime;
}
