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
@TableName("t_user_reminder_item")
@Tag(name = "用户提醒表子表， 主要记录由主表产生的确切的提示项对象")
public class TUserReminderItem implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "")
    private Long id;

    @Schema(description = "主表ID")
    private Long mainId;

    @Schema(description = "用户微信openID")
    private Long userId;

    @Schema(description = "引用的模板ID，可为空")
    private Long templateId;

    @Schema(description = "提醒标题")
    private String title;

    @Schema(description = "提醒内容")
    private String content;

    @Schema(description = "提醒时间")
    private LocalDateTime remindTime;

    @Schema(description = "间隔数，如每2天/每2周")
    private Integer repeatInterval;

    @Schema(description = "重复规则，如 DAILY, WEEKLY, MONTHLY")
    private String repeatRule;

    @Schema(description = "")
    private LocalDateTime createTime;

    @Schema(description = "")
    private LocalDateTime updateTime;


    @Schema(description = "提醒状态，0：待提醒，1：已过期，2：已完成")
    private Integer status;

}
