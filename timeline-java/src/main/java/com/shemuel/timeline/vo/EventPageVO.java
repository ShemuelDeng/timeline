package com.shemuel.timeline.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.shemuel.timeline.entity.Event;
import com.shemuel.timeline.entity.Timeline;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 事件分页视图对象，包含事件分页数据和关联的时间轴信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EventPageVO<T>  {

    private final IPage<T> page;
    private Timeline timeline;

    public EventPageVO(IPage<T> page) {
        this.page = page;
    }

    public EventPageVO(IPage<T> page, Timeline timeline) {
        this.page = page;
        this.timeline = timeline;
    }

}