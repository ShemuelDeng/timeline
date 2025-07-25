package com.shemuel.timeline.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.shemuel.timeline.config.FastjsonListStringTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

@Data
@TableName("event")
@Tag(name = "时间轴事件表对象")
public class Event implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "事件ID")
    private Long id;

    @Schema(description = "所属时间轴ID")
    @NotNull
    private Long timelineId;

    @Schema(description = "事件标题")
    private String title;

    @Schema(description = "事件内容，支持富文本或纯文本（HTML 格式）")
    private String content;

    @Schema(description = "是否是富文本； 0:普通文本， 1： 富文本")
    private int isRich;

    @Schema(description = "事件分类")
    private String tag;

    @Schema(description = "事件地点")
    private String location;

    @Schema(description = "图片")
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = FastjsonListStringTypeHandler.class)
    private List<String> images;

    @Schema(description = "事件发生时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime eventTime;

    @Schema(description = "记录创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
