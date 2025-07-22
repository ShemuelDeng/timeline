package com.shemuel.timeline.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.shemuel.timeline.entity.Timeline;
import com.shemuel.timeline.exception.ServiceException;
import com.shemuel.timeline.service.TimelineService;
import org.springframework.stereotype.Service;
import com.shemuel.timeline.mapper.EventMapper;
import com.shemuel.timeline.entity.Event;
import com.shemuel.timeline.service.EventService;
import com.shemuel.timeline.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 时间轴事件表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {

    private final TimelineService timelineService;

    /**
     * 查询时间轴事件表分页列表
     */
    @Override
    public IPage<Event> selectPage(Event event) {
        LambdaQueryWrapper<Event> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(event.getId() != null, Event::getId, event.getId());
        wrapper.eq(event.getTimelineId() != null, Event::getTimelineId, event.getTimelineId());
        wrapper.eq(event.getContent() != null, Event::getContent, event.getContent());
        wrapper.eq(event.getIsRich() != null, Event::getIsRich, event.getIsRich());
        wrapper.eq(event.getEventTime() != null, Event::getEventTime, event.getEventTime());
        wrapper.eq(event.getCreateTime() != null, Event::getCreateTime, event.getCreateTime());
        wrapper.eq(event.getUpdateTime() != null, Event::getUpdateTime, event.getUpdateTime());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询时间轴事件表列表
     */
    @Override
    public List<Event> selectList(Event event) {
        LambdaQueryWrapper<Event> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(event.getId() != null, Event::getId, event.getId());
        wrapper.eq(event.getTimelineId() != null, Event::getTimelineId, event.getTimelineId());
        wrapper.eq(event.getContent() != null, Event::getContent, event.getContent());
        wrapper.eq(event.getIsRich() != null, Event::getIsRich, event.getIsRich());
        wrapper.eq(event.getEventTime() != null, Event::getEventTime, event.getEventTime());
        wrapper.eq(event.getCreateTime() != null, Event::getCreateTime, event.getCreateTime());
        wrapper.eq(event.getUpdateTime() != null, Event::getUpdateTime, event.getUpdateTime());
        return list(wrapper);
    }

    /**
     * 新增时间轴事件表
     */
    @Override
    public boolean insert(Event event) {

        Timeline timeline = timelineService.getById(event.getTimelineId());
        if (Objects.isNull(timeline)){
            throw new ServiceException("时间轴不存在");
        }

        if(Objects.isNull(event.getEventTime())){
            event.setEventTime(LocalDateTime.now());
        }
        return save(event);
    }

    /**
     * 修改时间轴事件表
     */
    @Override
    public boolean update(Event event) {
        return updateById(event);
    }

    /**
     * 批量删除时间轴事件表
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
