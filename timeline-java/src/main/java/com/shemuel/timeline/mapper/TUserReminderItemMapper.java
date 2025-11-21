package com.shemuel.timeline.mapper;

import com.shemuel.timeline.entity.TUserReminderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户提醒表子表， 主要记录由主表产生的确切的提示项 Mapper接口
 */
@Mapper
public interface TUserReminderItemMapper extends BaseMapper<TUserReminderItem> {
} 