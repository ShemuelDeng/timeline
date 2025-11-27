package com.shemuel.timeline.service;

import com.shemuel.timeline.entity.TUserReminder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户提醒表主表， 只记录用户需要的提醒类型，方式 服务接口
 */
public interface TUserReminderService extends IService<TUserReminder> {
    /**
     * 查询用户提醒表主表， 只记录用户需要的提醒类型，方式分页列表
     */
    IPage<TUserReminder> selectPage(TUserReminder tUserReminder);

    /**
     * 查询用户提醒表主表， 只记录用户需要的提醒类型，方式列表
     */
    List<TUserReminder> selectList(TUserReminder tUserReminder);

    /**
     * 新增用户提醒表主表， 只记录用户需要的提醒类型，方式
     */
    TUserReminder insert(TUserReminder tUserReminder);

    LocalDateTime getNextRemindTime(Long id);


    /**
     * 修改用户提醒表主表， 只记录用户需要的提醒类型，方式
     */
    boolean update(TUserReminder tUserReminder);

    /**
     * 批量删除用户提醒表主表， 只记录用户需要的提醒类型，方式
     */
    boolean deleteByIds(List<Long> ids);
}
