package com.shemuel.timeline.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
@TableName("t_user_reminder")
@Tag(name = "用户提醒表对象")
public class TUserReminder implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "")
    private Long id;

    @Schema(description = "用户微信openID")
    private Long userId;

    @Schema(description = "引用的模板ID，可为空")
    private Long templateId;

    @Schema(description = "提醒标题")
    @Size(max = 100, message = "内容长度不能超过100个字符")
    private String title;

    @Schema(description = "提醒内容")
    @Size(max = 100, message = "内容长度不能超过100个字符")
    private String content;

    @Schema(description = "提醒时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime remindTime;

    @Schema(description = "重复规则，如 DAILY, WEEKLY, MONTHLY")
    private String repeatRule;

    @Schema(description = "提醒状态，0：待提醒，1：已过期，2：已完成")
    private Integer status;


    @Schema(description = "是否可见， 1： 可见， 0： 不可见")
    private Integer visible;

    @Schema(description = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
