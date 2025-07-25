package com.shemuel.timeline.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shemuel.timeline.mapper.TimelineMapper;
import com.shemuel.timeline.entity.Timeline;
import com.shemuel.timeline.service.TimelineService;
import com.shemuel.timeline.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 时间轴表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class TimelineServiceImpl extends ServiceImpl<TimelineMapper, Timeline> implements TimelineService {

    /**
     * 查询时间轴表分页列表
     */
    @Override
    public IPage<Timeline> selectPage(Timeline timeline) {
        LambdaQueryWrapper<Timeline> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(timeline.getId() != null, Timeline::getId, timeline.getId());
        wrapper.eq(timeline.getUserId() != null, Timeline::getUserId, timeline.getUserId());
        wrapper.eq(timeline.getTitle() != null, Timeline::getTitle, timeline.getTitle());
        wrapper.eq(timeline.getDescription() != null, Timeline::getDescription, timeline.getDescription());
        wrapper.eq(timeline.getTag() != null, Timeline::getTag, timeline.getTag());
        wrapper.eq(timeline.getCoverUrl() != null, Timeline::getCoverUrl, timeline.getCoverUrl());
        wrapper.eq(timeline.getCreateTime() != null, Timeline::getCreateTime, timeline.getCreateTime());
        wrapper.eq(timeline.getUpdateTime() != null, Timeline::getUpdateTime, timeline.getUpdateTime());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询时间轴表列表
     */
    @Override
    public List<Timeline> selectList(Timeline timeline) {
        LambdaQueryWrapper<Timeline> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(timeline.getId() != null, Timeline::getId, timeline.getId());
        wrapper.eq(timeline.getUserId() != null, Timeline::getUserId, timeline.getUserId());
        wrapper.eq(timeline.getTitle() != null, Timeline::getTitle, timeline.getTitle());
        wrapper.eq(timeline.getDescription() != null, Timeline::getDescription, timeline.getDescription());
        wrapper.eq(timeline.getTag() != null, Timeline::getTag, timeline.getTag());
        wrapper.eq(timeline.getCoverUrl() != null, Timeline::getCoverUrl, timeline.getCoverUrl());
        wrapper.eq(timeline.getCreateTime() != null, Timeline::getCreateTime, timeline.getCreateTime());
        wrapper.eq(timeline.getUpdateTime() != null, Timeline::getUpdateTime, timeline.getUpdateTime());
        return list(wrapper);
    }

    /**
     * 新增时间轴表
     */
    @Override
    public Timeline insert(Timeline timeline) {
        timeline.setEventCount(0);
        save(timeline);
        return getById(timeline.getId());
    }

    /**
     * 修改时间轴表
     */
    @Override
    public boolean update(Timeline timeline) {
        return updateById(timeline);
    }

    /**
     * 批量删除时间轴表
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
