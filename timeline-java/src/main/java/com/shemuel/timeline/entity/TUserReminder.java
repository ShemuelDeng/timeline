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
@TableName("t_user_reminder")
@Tag(name = "用户提醒表主表， 只记录用户需要的提醒类型，方式对象")
public class TUserReminder implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "")
    private Long id;

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

    @Schema(description = "重复规则：NONE, DAILY, WEEKLY, MONTHLY, YEARLY,WORKDAY, WEEKEND, CUSTOM")
    private String repeatRule;

    @Schema(description = "当前 支持， BIRTHDAY, ANNIVERSARY 自定义模式：生日，纪念日，他俩的处理方式一模一样")
    private String customMode;

    @Schema(description = "提起几天提醒")
    private Integer advanceDays;

    @Schema(description = "间隔数，如每2天/每2周, 前端 展示没有实现， 后端默认按1处理 ")
    private Integer repeatInterval;

    @Schema(description = "每周的星期几")
    private String repeatWeekdays;

    @Schema(description = "每月的哪几天，逗号分隔")
    private String repeatMonthDays;

    @Schema(description = "每年的哪些天 逗号分隔")
    private String specifyDates;

    @Schema(description = "自定义当天的哪几个时间， 逗号分隔")
    private String specifyTimes;

    @Schema(description = "提醒状态，0：待提醒，1：已过期，2：已完成")
    private Integer status;

    @Schema(description = "是否开启：1：开启， 0：关闭")
    private Integer active;

    @Schema(description = "是否循环：1：开启， 0：关闭")
    private Integer doCircle;

    @Schema(description = "循环开始时间")
    private LocalDateTime circleBegin;

    @Schema(description = "循环结束时间")
    private LocalDateTime circleEnd;

    @Schema(description = "循环间隔，如 20， 单位分钟")
    private Integer circleInterval;

    @Schema(description = "桌面弹窗")
    private Integer notifyDesktop;

    @Schema(description = "微信提醒")
    private Integer notifyWx;

    @Schema(description = "声音提醒")
    private Integer notifySound;

    @Schema(description = "系统通知/托盘气泡")
    private Integer notifySystem;

    @Schema(description = "声音文件")
    private String notifySoundFile;

    @Schema(description = "")
    private LocalDateTime createTime;

    @Schema(description = "")
    private LocalDateTime updateTime;

    @Schema(description = "提醒钩子")
    private String webhook;

    @Schema(description = "")
    private String webhookMethod;
}
