package com.shemuel.timeline.mapper;

import com.shemuel.timeline.dto.UserMetricKV;
import com.shemuel.timeline.entity.TUserMetric;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL） Mapper接口
 */
@Mapper
public interface TUserMetricMapper extends BaseMapper<TUserMetric> {

    /** 提醒触发 +1：同时更新 today_notice + total_notice */
    int incNotice(@Param("userId") long userId,
                  @Param("today") LocalDate today);

    /** 摸鱼累计秒数：同时更新 today_fish_seconds + total_fish_seconds */
    int incFishSeconds(@Param("userId") long userId,
                       @Param("today") LocalDate today,
                       @Param("deltaSeconds") long deltaSeconds);

    /** 通用：今日指标累加（带日期） */
    int incTodayMetric(@Param("userId") long userId,
                       @Param("metricKey") String metricKey,
                       @Param("today") LocalDate today,
                       @Param("delta") long delta);

    /** 通用：总量指标累加（不带日期，metric_date = NULL） */
    int incTotalMetric(@Param("userId") long userId,
                       @Param("metricKey") String metricKey,
                       @Param("delta") long delta);

    /** 查询面板用的 4 个指标（今日 + 总量） */
    List<UserMetricKV> selectBaseMetrics(@Param("userId") long userId,
                                         @Param("today") LocalDate today);

} 