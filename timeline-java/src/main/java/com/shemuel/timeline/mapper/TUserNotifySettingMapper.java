package com.shemuel.timeline.mapper;

import com.shemuel.timeline.entity.TUserNotifySetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户通知渠道配置表 Mapper接口
 */
@Mapper
public interface TUserNotifySettingMapper extends BaseMapper<TUserNotifySetting> {
} 