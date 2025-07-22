package com.shemuel.timeline.service;

import com.shemuel.timeline.entity.Timeline;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 时间轴表 服务接口
 */
public interface TimelineService extends IService<Timeline> {
    /**
     * 查询时间轴表分页列表
     */
    IPage<Timeline> selectPage(Timeline timeline);

    /**
     * 查询时间轴表列表
     */
    List<Timeline> selectList(Timeline timeline);

    /**
     * 新增时间轴表
     */
    Timeline insert(Timeline timeline);

    /**
     * 修改时间轴表
     */
    boolean update(Timeline timeline);

    /**
     * 批量删除时间轴表
     */
    boolean deleteByIds(List<Long> ids);
}
