package com.shemuel.timeline.service;

import com.shemuel.timeline.entity.TUserNotifySetting;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 用户通知渠道配置表 服务接口
 */
public interface TUserNotifySettingService extends IService<TUserNotifySetting> {
    /**
     * 查询用户通知渠道配置表分页列表
     */
    IPage<TUserNotifySetting> selectPage(TUserNotifySetting tUserNotifySetting);

    /**
     * 查询用户通知渠道配置表列表
     */
    List<TUserNotifySetting> selectList(TUserNotifySetting tUserNotifySetting);

    /**
     * 新增用户通知渠道配置表
     */
    boolean insert(TUserNotifySetting tUserNotifySetting);

    /**
     * 修改用户通知渠道配置表
     */
    boolean update(TUserNotifySetting tUserNotifySetting);

    /**
     * 批量删除用户通知渠道配置表
     */
    boolean deleteByIds(List<Long> ids);
}
