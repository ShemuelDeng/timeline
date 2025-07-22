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
@TableName("event")
@Tag(name = "时间轴事件表对象")
public class Event implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "事件ID")
    private Long id;

    @Schema(description = "所属时间轴ID")
    private Long timelineId;

    @Schema(description = "事件内容，支持富文本或纯文本（HTML 格式）")
    private String content;

    @Schema(description = "是否富文本：0=否，1=是")
    private Integer isRich;

    @Schema(description = "事件发生时间")
    private LocalDateTime eventTime;

    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;
}
