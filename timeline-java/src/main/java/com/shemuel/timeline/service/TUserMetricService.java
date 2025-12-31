package com.shemuel.timeline.service;

import com.shemuel.timeline.entity.TUserMetric;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL） 服务接口
 */
public interface TUserMetricService extends IService<TUserMetric> {
    /**
     * 查询用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）分页列表
     */
    IPage<TUserMetric> selectPage(TUserMetric tUserMetric);

    /**
     * 查询用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）列表
     */
    List<TUserMetric> selectList(TUserMetric tUserMetric);

    /**
     * 新增用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）
     */
    boolean insert(TUserMetric tUserMetric);

    /**
     * 修改用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）
     */
    boolean update(TUserMetric tUserMetric);

    /**
     * 批量删除用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）
     */
    boolean deleteByIds(List<Long> ids);
}
