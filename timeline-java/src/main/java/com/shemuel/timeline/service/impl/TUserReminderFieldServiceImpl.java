package com.shemuel.timeline.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shemuel.timeline.mapper.TUserReminderFieldMapper;
import com.shemuel.timeline.entity.TUserReminderField;
import com.shemuel.timeline.service.TUserReminderFieldService;
import com.shemuel.timeline.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 用户提醒的扩展字段值表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class TUserReminderFieldServiceImpl extends ServiceImpl<TUserReminderFieldMapper, TUserReminderField> implements TUserReminderFieldService {

    /**
     * 查询用户提醒的扩展字段值表分页列表
     */
    @Override
    public IPage<TUserReminderField> selectPage(TUserReminderField tUserReminderField) {
        LambdaQueryWrapper<TUserReminderField> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserReminderField.getId() != null, TUserReminderField::getId, tUserReminderField.getId());
        wrapper.eq(tUserReminderField.getUserReminderId() != null, TUserReminderField::getUserReminderId, tUserReminderField.getUserReminderId());
        wrapper.eq(tUserReminderField.getFieldName() != null, TUserReminderField::getFieldName, tUserReminderField.getFieldName());
        wrapper.eq(tUserReminderField.getFieldValue() != null, TUserReminderField::getFieldValue, tUserReminderField.getFieldValue());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询用户提醒的扩展字段值表列表
     */
    @Override
    public List<TUserReminderField> selectList(TUserReminderField tUserReminderField) {
        LambdaQueryWrapper<TUserReminderField> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserReminderField.getId() != null, TUserReminderField::getId, tUserReminderField.getId());
        wrapper.eq(tUserReminderField.getUserReminderId() != null, TUserReminderField::getUserReminderId, tUserReminderField.getUserReminderId());
        wrapper.eq(tUserReminderField.getFieldName() != null, TUserReminderField::getFieldName, tUserReminderField.getFieldName());
        wrapper.eq(tUserReminderField.getFieldValue() != null, TUserReminderField::getFieldValue, tUserReminderField.getFieldValue());
        return list(wrapper);
    }

    /**
     * 新增用户提醒的扩展字段值表
     */
    @Override
    public boolean insert(TUserReminderField tUserReminderField) {
        return save(tUserReminderField);
    }

    /**
     * 修改用户提醒的扩展字段值表
     */
    @Override
    public boolean update(TUserReminderField tUserReminderField) {
        return updateById(tUserReminderField);
    }

    /**
     * 批量删除用户提醒的扩展字段值表
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
