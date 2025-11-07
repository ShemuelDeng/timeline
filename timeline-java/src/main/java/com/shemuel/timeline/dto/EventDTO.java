package com.shemuel.timeline.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Tag(name = "时间轴事件表对象")
public class EventDTO implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "事件ID")
    private Long id;

    @Schema(description = "所属时间轴ID")
    private Long timelineId;

    @Schema(description = "事件标题")
    @NotNull
    private String title;

    @Schema(description = "事件内容，支持富文本或纯文本（HTML 格式）")
    private String content;

    @Schema(description = "事件分类")
    private String tag;

    @Schema(description = "事件地点")
    private String location;

    @Schema(description = "图片文件key集合")
    private List<String> images;

    @Schema(description = "事件发生时间")
    private LocalDateTime eventTime;

    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;
}
