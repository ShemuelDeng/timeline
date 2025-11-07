package com.shemuel.timeline.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shemuel.timeline.mapper.TReminderTemplateFieldMapper;
import com.shemuel.timeline.entity.TReminderTemplateField;
import com.shemuel.timeline.service.TReminderTemplateFieldService;
import com.shemuel.timeline.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 模板扩展字段定义表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class TReminderTemplateFieldServiceImpl extends ServiceImpl<TReminderTemplateFieldMapper, TReminderTemplateField> implements TReminderTemplateFieldService {

    /**
     * 查询模板扩展字段定义表分页列表
     */
    @Override
    public IPage<TReminderTemplateField> selectPage(TReminderTemplateField tReminderTemplateField) {
        LambdaQueryWrapper<TReminderTemplateField> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tReminderTemplateField.getId() != null, TReminderTemplateField::getId, tReminderTemplateField.getId());
        wrapper.eq(tReminderTemplateField.getTemplateId() != null, TReminderTemplateField::getTemplateId, tReminderTemplateField.getTemplateId());
        wrapper.eq(tReminderTemplateField.getFieldName() != null, TReminderTemplateField::getFieldName, tReminderTemplateField.getFieldName());
        wrapper.eq(tReminderTemplateField.getFieldLabel() != null, TReminderTemplateField::getFieldLabel, tReminderTemplateField.getFieldLabel());
        wrapper.eq(tReminderTemplateField.getFieldType() != null, TReminderTemplateField::getFieldType, tReminderTemplateField.getFieldType());
        wrapper.eq(tReminderTemplateField.getFieldOptions() != null, TReminderTemplateField::getFieldOptions, tReminderTemplateField.getFieldOptions());
        wrapper.eq(tReminderTemplateField.getSortOrder() != null, TReminderTemplateField::getSortOrder, tReminderTemplateField.getSortOrder());
        wrapper.eq(tReminderTemplateField.getCreateTime() != null, TReminderTemplateField::getCreateTime, tReminderTemplateField.getCreateTime());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询模板扩展字段定义表列表
     */
    @Override
    public List<TReminderTemplateField> selectList(TReminderTemplateField tReminderTemplateField) {
        LambdaQueryWrapper<TReminderTemplateField> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tReminderTemplateField.getId() != null, TReminderTemplateField::getId, tReminderTemplateField.getId());
        wrapper.eq(tReminderTemplateField.getTemplateId() != null, TReminderTemplateField::getTemplateId, tReminderTemplateField.getTemplateId());
        wrapper.eq(tReminderTemplateField.getFieldName() != null, TReminderTemplateField::getFieldName, tReminderTemplateField.getFieldName());
        wrapper.eq(tReminderTemplateField.getFieldLabel() != null, TReminderTemplateField::getFieldLabel, tReminderTemplateField.getFieldLabel());
        wrapper.eq(tReminderTemplateField.getFieldType() != null, TReminderTemplateField::getFieldType, tReminderTemplateField.getFieldType());
        wrapper.eq(tReminderTemplateField.getFieldOptions() != null, TReminderTemplateField::getFieldOptions, tReminderTemplateField.getFieldOptions());
        wrapper.eq(tReminderTemplateField.getSortOrder() != null, TReminderTemplateField::getSortOrder, tReminderTemplateField.getSortOrder());
        wrapper.eq(tReminderTemplateField.getCreateTime() != null, TReminderTemplateField::getCreateTime, tReminderTemplateField.getCreateTime());
        return list(wrapper);
    }

    /**
     * 新增模板扩展字段定义表
     */
    @Override
    public boolean insert(TReminderTemplateField tReminderTemplateField) {
        return save(tReminderTemplateField);
    }

    /**
     * 修改模板扩展字段定义表
     */
    @Override
    public boolean update(TReminderTemplateField tReminderTemplateField) {
        return updateById(tReminderTemplateField);
    }

    /**
     * 批量删除模板扩展字段定义表
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
