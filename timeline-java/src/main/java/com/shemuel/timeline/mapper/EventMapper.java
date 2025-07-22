package com.shemuel.timeline.mapper;

import com.shemuel.timeline.entity.Event;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 时间轴事件表 Mapper接口
 */
@Mapper
public interface EventMapper extends BaseMapper<Event> {
} 