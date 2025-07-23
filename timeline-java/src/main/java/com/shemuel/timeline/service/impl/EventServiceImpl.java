package com.shemuel.timeline.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 查询时间轴事件表分页列表
     */
    @Override
    public IPage<Event> selectPage(Event event) {
        Page<Event> page = PageUtil.getPage(); // 你原本的分页工具
        return this.baseMapper.selectPage(page, event);
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
        wrapper.eq(event.getTitle() != null, Event::getTitle, event.getTitle());
        wrapper.eq(event.getContent() != null, Event::getContent, event.getContent());
        wrapper.eq(event.getTag() != null, Event::getTag, event.getTag());
        wrapper.eq(event.getLocation() != null, Event::getLocation, event.getLocation());
        wrapper.eq(event.getImages() != null, Event::getImages, event.getImages());
        wrapper.eq(event.getEventTime() != null, Event::getEventTime, event.getEventTime());
        wrapper.eq(event.getCreateTime() != null, Event::getCreateTime, event.getCreateTime());
        wrapper.eq(event.getUpdateTime() != null, Event::getUpdateTime, event.getUpdateTime());
        return list(wrapper);
    }

    /**
     * 新增时间轴事件表
     */
    @Override
    public Event insert(Event event) {
        if (event.getEventTime() == null){
            event.setEventTime(LocalDateTime.now());
        }
        save(event);
        return getById(event.getId());
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
