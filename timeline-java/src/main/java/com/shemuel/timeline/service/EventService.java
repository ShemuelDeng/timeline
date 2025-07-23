package com.shemuel.timeline.service;

import com.shemuel.timeline.entity.Event;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 时间轴事件表 服务接口
 */
public interface EventService extends IService<Event> {
    /**
     * 查询时间轴事件表分页列表
     */
    IPage<Event> selectPage(Event event);

    /**
     * 查询时间轴事件表列表
     */
    List<Event> selectList(Event event);

    /**
     * 新增时间轴事件表
     */
    Event insert(Event event);

    /**
     * 修改时间轴事件表
     */
    boolean update(Event event);

    /**
     * 批量删除时间轴事件表
     */
    boolean deleteByIds(List<Long> ids);
}
