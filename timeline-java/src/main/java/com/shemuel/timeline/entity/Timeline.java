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
@TableName("timeline")
@Tag(name = "时间轴表对象")
public class Timeline implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "时间轴ID")
    private Long id;

    @Schema(description = "所属用户ID")
    private Long userId;

    @Schema(description = "时间轴标题，例如“孩子成长”、“恋爱历程”")
    private String title;

    @Schema(description = "时间轴简介或备注")
    private String description;

    @Schema(description = "事件分类")
    private String tag;

    @Schema(description = "封面图URL（用于首页展示）")
    private String coverUrl;

    @Schema(description = "事件数量")
    private Integer eventCount;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
