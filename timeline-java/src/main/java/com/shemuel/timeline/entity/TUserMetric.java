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
@TableName("t_user_metric")
@Tag(name = "用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）对象")
public class TUserMetric implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "指标类型，如 today_notice / total_notice / today_fish_seconds / total_fish_seconds")
    private String metricKey;

    @Schema(description = "指标日期（东八区）。今日类填当天；总量类填 NULL")
    private LocalDateTime metricDate;

    @Schema(description = "指标值（次数/秒等，统一用整数）")
    private Long metricValue;

    @Schema(description = "扩展信息（可选）")
    private String meta;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "用于唯一索引的日期键")
    private LocalDateTime metricDateKey;
}
