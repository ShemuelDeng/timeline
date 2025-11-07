package com.shemuel.timeline.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shemuel.timeline.mapper.TReminderTemplateMapper;
import com.shemuel.timeline.entity.TReminderTemplate;
import com.shemuel.timeline.service.TReminderTemplateService;
import com.shemuel.timeline.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 提醒模板表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class TReminderTemplateServiceImpl extends ServiceImpl<TReminderTemplateMapper, TReminderTemplate> implements TReminderTemplateService {

    /**
     * 查询提醒模板表分页列表
     */
    @Override
    public IPage<TReminderTemplate> selectPage(TReminderTemplate tReminderTemplate) {
        LambdaQueryWrapper<TReminderTemplate> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tReminderTemplate.getId() != null, TReminderTemplate::getId, tReminderTemplate.getId());
        wrapper.eq(tReminderTemplate.getCategoryId() != null, TReminderTemplate::getCategoryId, tReminderTemplate.getCategoryId());
        wrapper.eq(tReminderTemplate.getName() != null, TReminderTemplate::getName, tReminderTemplate.getName());
        wrapper.eq(tReminderTemplate.getDescription() != null, TReminderTemplate::getDescription, tReminderTemplate.getDescription());
        wrapper.eq(tReminderTemplate.getIcon() != null, TReminderTemplate::getIcon, tReminderTemplate.getIcon());
        wrapper.eq(tReminderTemplate.getIsSystem() != null, TReminderTemplate::getIsSystem, tReminderTemplate.getIsSystem());
        wrapper.eq(tReminderTemplate.getUserId() != null, TReminderTemplate::getUserId, tReminderTemplate.getUserId());
        wrapper.eq(tReminderTemplate.getCreateTime() != null, TReminderTemplate::getCreateTime, tReminderTemplate.getCreateTime());
        wrapper.eq(tReminderTemplate.getUpdateTime() != null, TReminderTemplate::getUpdateTime, tReminderTemplate.getUpdateTime());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询提醒模板表列表
     */
    @Override
    public List<TReminderTemplate> selectList(TReminderTemplate tReminderTemplate) {
        LambdaQueryWrapper<TReminderTemplate> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tReminderTemplate.getId() != null, TReminderTemplate::getId, tReminderTemplate.getId());
        wrapper.eq(tReminderTemplate.getCategoryId() != null, TReminderTemplate::getCategoryId, tReminderTemplate.getCategoryId());
        wrapper.eq(tReminderTemplate.getName() != null, TReminderTemplate::getName, tReminderTemplate.getName());
        wrapper.eq(tReminderTemplate.getDescription() != null, TReminderTemplate::getDescription, tReminderTemplate.getDescription());
        wrapper.eq(tReminderTemplate.getIcon() != null, TReminderTemplate::getIcon, tReminderTemplate.getIcon());
        wrapper.eq(tReminderTemplate.getIsSystem() != null, TReminderTemplate::getIsSystem, tReminderTemplate.getIsSystem());
        wrapper.eq(tReminderTemplate.getUserId() != null, TReminderTemplate::getUserId, tReminderTemplate.getUserId());
        wrapper.eq(tReminderTemplate.getCreateTime() != null, TReminderTemplate::getCreateTime, tReminderTemplate.getCreateTime());
        wrapper.eq(tReminderTemplate.getUpdateTime() != null, TReminderTemplate::getUpdateTime, tReminderTemplate.getUpdateTime());
        return list(wrapper);
    }

    /**
     * 新增提醒模板表
     */
    @Override
    public boolean insert(TReminderTemplate tReminderTemplate) {
        return save(tReminderTemplate);
    }

    /**
     * 修改提醒模板表
     */
    @Override
    public boolean update(TReminderTemplate tReminderTemplate) {
        return updateById(tReminderTemplate);
    }

    /**
     * 批量删除提醒模板表
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
