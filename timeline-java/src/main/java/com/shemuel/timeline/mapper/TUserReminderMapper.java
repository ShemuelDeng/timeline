package com.shemuel.timeline.mapper;

import com.shemuel.timeline.entity.TUserReminder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户提醒表主表， 只记录用户需要的提醒类型，方式 Mapper接口
 */
@Mapper
public interface TUserReminderMapper extends BaseMapper<TUserReminder> {
} 