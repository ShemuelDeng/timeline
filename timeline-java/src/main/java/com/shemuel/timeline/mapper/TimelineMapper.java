package com.shemuel.timeline.mapper;

import com.shemuel.timeline.entity.Timeline;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 时间轴表 Mapper接口
 */
@Mapper
public interface TimelineMapper extends BaseMapper<Timeline> {
} 