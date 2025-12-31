package com.shemuel.timeline.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shemuel.timeline.mapper.TUserMetricMapper;
import com.shemuel.timeline.entity.TUserMetric;
import com.shemuel.timeline.service.TUserMetricService;
import com.shemuel.timeline.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL） 服务实现类
 */
@Service
@RequiredArgsConstructor
public class TUserMetricServiceImpl extends ServiceImpl<TUserMetricMapper, TUserMetric> implements TUserMetricService {

    /**
     * 查询用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）分页列表
     */
    @Override
    public IPage<TUserMetric> selectPage(TUserMetric tUserMetric) {
        LambdaQueryWrapper<TUserMetric> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserMetric.getId() != null, TUserMetric::getId, tUserMetric.getId());
        wrapper.eq(tUserMetric.getUserId() != null, TUserMetric::getUserId, tUserMetric.getUserId());
        wrapper.eq(tUserMetric.getMetricKey() != null, TUserMetric::getMetricKey, tUserMetric.getMetricKey());
        wrapper.eq(tUserMetric.getMetricDate() != null, TUserMetric::getMetricDate, tUserMetric.getMetricDate());
        wrapper.eq(tUserMetric.getMetricValue() != null, TUserMetric::getMetricValue, tUserMetric.getMetricValue());
        wrapper.eq(tUserMetric.getMeta() != null, TUserMetric::getMeta, tUserMetric.getMeta());
        wrapper.eq(tUserMetric.getCreatedAt() != null, TUserMetric::getCreatedAt, tUserMetric.getCreatedAt());
        wrapper.eq(tUserMetric.getUpdatedAt() != null, TUserMetric::getUpdatedAt, tUserMetric.getUpdatedAt());
        wrapper.eq(tUserMetric.getMetricDateKey() != null, TUserMetric::getMetricDateKey, tUserMetric.getMetricDateKey());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）列表
     */
    @Override
    public List<TUserMetric> selectList(TUserMetric tUserMetric) {
        LambdaQueryWrapper<TUserMetric> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserMetric.getId() != null, TUserMetric::getId, tUserMetric.getId());
        wrapper.eq(tUserMetric.getUserId() != null, TUserMetric::getUserId, tUserMetric.getUserId());
        wrapper.eq(tUserMetric.getMetricKey() != null, TUserMetric::getMetricKey, tUserMetric.getMetricKey());
        wrapper.eq(tUserMetric.getMetricDate() != null, TUserMetric::getMetricDate, tUserMetric.getMetricDate());
        wrapper.eq(tUserMetric.getMetricValue() != null, TUserMetric::getMetricValue, tUserMetric.getMetricValue());
        wrapper.eq(tUserMetric.getMeta() != null, TUserMetric::getMeta, tUserMetric.getMeta());
        wrapper.eq(tUserMetric.getCreatedAt() != null, TUserMetric::getCreatedAt, tUserMetric.getCreatedAt());
        wrapper.eq(tUserMetric.getUpdatedAt() != null, TUserMetric::getUpdatedAt, tUserMetric.getUpdatedAt());
        wrapper.eq(tUserMetric.getMetricDateKey() != null, TUserMetric::getMetricDateKey, tUserMetric.getMetricDateKey());
        return list(wrapper);
    }

    /**
     * 新增用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）
     */
    @Override
    public boolean insert(TUserMetric tUserMetric) {
        return save(tUserMetric);
    }

    /**
     * 修改用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）
     */
    @Override
    public boolean update(TUserMetric tUserMetric) {
        return updateById(tUserMetric);
    }

    /**
     * 批量删除用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
