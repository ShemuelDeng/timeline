package com.shemuel.timeline.vo;

import com.shemuel.timeline.entity.Event;
import com.shemuel.timeline.entity.Timeline;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 事件视图对象，包含事件和关联的时间轴信息
 */
@Data
public class EventVO implements Serializable {

    @Schema(description = "事件ID")
    private Long id;

    @Schema(description = "所属时间轴ID")
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
    private List<String> images;

    @Schema(description = "事件发生时间")
    private LocalDateTime eventTime;

    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;
    
    // 时间轴信息
    @Schema(description = "时间轴标题")
    private String timelineTitle;
    
    @Schema(description = "时间轴简介")
    private String timelineDescription;
    
    @Schema(description = "时间轴封面URL")
    private String timelineCoverUrl;
    
    /**
     * 从Event和Timeline对象创建EventVO
     */
    public static EventVO fromEventAndTimeline(Event event, Timeline timeline) {
        EventVO vo = new EventVO();
        
        // 复制Event属性
        vo.setId(event.getId());
        vo.setTimelineId(event.getTimelineId());
        vo.setTitle(event.getTitle());
        vo.setContent(event.getContent());
        vo.setIsRich(event.getIsRich());
        vo.setTag(event.getTag());
        vo.setLocation(event.getLocation());
        vo.setImages(event.getImages());
        vo.setEventTime(event.getEventTime());
        vo.setCreateTime(event.getCreateTime());
        vo.setUpdateTime(event.getUpdateTime());
        
        // 设置Timeline属性
        if (timeline != null) {
            vo.setTimelineTitle(timeline.getTitle());
            vo.setTimelineDescription(timeline.getDescription());
            vo.setTimelineCoverUrl(timeline.getCoverUrl());
        }
        
        return vo;
    }
}