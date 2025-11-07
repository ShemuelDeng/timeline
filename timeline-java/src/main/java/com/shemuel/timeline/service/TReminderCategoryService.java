package com.shemuel.timeline.service;

import com.shemuel.timeline.entity.TReminderCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 提醒分类表 服务接口
 */
public interface TReminderCategoryService extends IService<TReminderCategory> {
    /**
     * 查询提醒分类表分页列表
     */
    IPage<TReminderCategory> selectPage(TReminderCategory tReminderCategory);

    /**
     * 查询提醒分类表列表
     */
    List<TReminderCategory> selectList(TReminderCategory tReminderCategory);

    /**
     * 新增提醒分类表
     */
    boolean insert(TReminderCategory tReminderCategory);

    /**
     * 修改提醒分类表
     */
    boolean update(TReminderCategory tReminderCategory);

    /**
     * 批量删除提醒分类表
     */
    boolean deleteByIds(List<Long> ids);
}
