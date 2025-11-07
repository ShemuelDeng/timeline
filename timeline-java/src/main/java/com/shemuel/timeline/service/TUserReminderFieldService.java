package com.shemuel.timeline.service;

import com.shemuel.timeline.entity.TUserReminderField;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 用户提醒的扩展字段值表 服务接口
 */
public interface TUserReminderFieldService extends IService<TUserReminderField> {
    /**
     * 查询用户提醒的扩展字段值表分页列表
     */
    IPage<TUserReminderField> selectPage(TUserReminderField tUserReminderField);

    /**
     * 查询用户提醒的扩展字段值表列表
     */
    List<TUserReminderField> selectList(TUserReminderField tUserReminderField);

    /**
     * 新增用户提醒的扩展字段值表
     */
    boolean insert(TUserReminderField tUserReminderField);

    /**
     * 修改用户提醒的扩展字段值表
     */
    boolean update(TUserReminderField tUserReminderField);

    /**
     * 批量删除用户提醒的扩展字段值表
     */
    boolean deleteByIds(List<Long> ids);
}
