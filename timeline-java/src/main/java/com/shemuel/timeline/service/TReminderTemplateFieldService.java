package com.shemuel.timeline.service;

import com.shemuel.timeline.entity.TReminderTemplateField;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 模板扩展字段定义表 服务接口
 */
public interface TReminderTemplateFieldService extends IService<TReminderTemplateField> {
    /**
     * 查询模板扩展字段定义表分页列表
     */
    IPage<TReminderTemplateField> selectPage(TReminderTemplateField tReminderTemplateField);

    /**
     * 查询模板扩展字段定义表列表
     */
    List<TReminderTemplateField> selectList(TReminderTemplateField tReminderTemplateField);

    /**
     * 新增模板扩展字段定义表
     */
    boolean insert(TReminderTemplateField tReminderTemplateField);

    /**
     * 修改模板扩展字段定义表
     */
    boolean update(TReminderTemplateField tReminderTemplateField);

    /**
     * 批量删除模板扩展字段定义表
     */
    boolean deleteByIds(List<Long> ids);
}
