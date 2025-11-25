package com.shemuel.timeline.service;

import com.shemuel.timeline.entity.TUserReminderItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 用户提醒表子表， 主要记录由主表产生的确切的提示项 服务接口
 */
public interface TUserReminderItemService extends IService<TUserReminderItem> {
    /**
     * 查询用户提醒表子表， 主要记录由主表产生的确切的提示项分页列表
     */
    IPage<TUserReminderItem> selectPage(TUserReminderItem tUserReminderItem);

    /**
     * 查询用户提醒表子表， 主要记录由主表产生的确切的提示项列表
     */
    List<TUserReminderItem> selectList(TUserReminderItem tUserReminderItem);

    /**
     * 新增用户提醒表子表， 主要记录由主表产生的确切的提示项
     */
    boolean insert(TUserReminderItem tUserReminderItem);

    /**
     * 修改用户提醒表子表， 主要记录由主表产生的确切的提示项
     */
    boolean update(TUserReminderItem tUserReminderItem);

    /**
     * 批量删除用户提醒表子表， 主要记录由主表产生的确切的提示项
     */
    boolean deleteByIds(List<Long> ids);

    boolean deleteByMainId(Long mainId);
}
