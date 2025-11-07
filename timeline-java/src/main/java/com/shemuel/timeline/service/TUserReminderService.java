package com.shemuel.timeline.service;

import com.shemuel.timeline.entity.TUserReminder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 用户提醒表 服务接口
 */
public interface TUserReminderService extends IService<TUserReminder> {
    /**
     * 查询用户提醒表分页列表
     */
    IPage<TUserReminder> selectPage(TUserReminder tUserReminder);

    /**
     * 查询用户提醒表列表
     */
    List<TUserReminder> selectList(TUserReminder tUserReminder);

    /**
     * 新增用户提醒表
     */
    boolean insert(TUserReminder tUserReminder);

    /**
     * 修改用户提醒表
     */
    boolean update(TUserReminder tUserReminder);

    /**
     * 批量删除用户提醒表
     */
    boolean deleteByIds(List<Long> ids);
}
