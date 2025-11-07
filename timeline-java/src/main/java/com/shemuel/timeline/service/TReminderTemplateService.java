package com.shemuel.timeline.service;

import com.shemuel.timeline.entity.TReminderTemplate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 提醒模板表 服务接口
 */
public interface TReminderTemplateService extends IService<TReminderTemplate> {
    /**
     * 查询提醒模板表分页列表
     */
    IPage<TReminderTemplate> selectPage(TReminderTemplate tReminderTemplate);

    /**
     * 查询提醒模板表列表
     */
    List<TReminderTemplate> selectList(TReminderTemplate tReminderTemplate);

    /**
     * 新增提醒模板表
     */
    boolean insert(TReminderTemplate tReminderTemplate);

    /**
     * 修改提醒模板表
     */
    boolean update(TReminderTemplate tReminderTemplate);

    /**
     * 批量删除提醒模板表
     */
    boolean deleteByIds(List<Long> ids);
}
