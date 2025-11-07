package com.shemuel.timeline.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shemuel.timeline.mapper.TReminderCategoryMapper;
import com.shemuel.timeline.entity.TReminderCategory;
import com.shemuel.timeline.service.TReminderCategoryService;
import com.shemuel.timeline.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 提醒分类表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class TReminderCategoryServiceImpl extends ServiceImpl<TReminderCategoryMapper, TReminderCategory> implements TReminderCategoryService {

    /**
     * 查询提醒分类表分页列表
     */
    @Override
    public IPage<TReminderCategory> selectPage(TReminderCategory tReminderCategory) {
        LambdaQueryWrapper<TReminderCategory> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tReminderCategory.getId() != null, TReminderCategory::getId, tReminderCategory.getId());
        wrapper.eq(tReminderCategory.getName() != null, TReminderCategory::getName, tReminderCategory.getName());
        wrapper.eq(tReminderCategory.getCode() != null, TReminderCategory::getCode, tReminderCategory.getCode());
        wrapper.eq(tReminderCategory.getSortOrder() != null, TReminderCategory::getSortOrder, tReminderCategory.getSortOrder());
        wrapper.eq(tReminderCategory.getCreateTime() != null, TReminderCategory::getCreateTime, tReminderCategory.getCreateTime());
        wrapper.eq(tReminderCategory.getUpdateTime() != null, TReminderCategory::getUpdateTime, tReminderCategory.getUpdateTime());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询提醒分类表列表
     */
    @Override
    public List<TReminderCategory> selectList(TReminderCategory tReminderCategory) {
        LambdaQueryWrapper<TReminderCategory> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tReminderCategory.getId() != null, TReminderCategory::getId, tReminderCategory.getId());
        wrapper.eq(tReminderCategory.getName() != null, TReminderCategory::getName, tReminderCategory.getName());
        wrapper.eq(tReminderCategory.getCode() != null, TReminderCategory::getCode, tReminderCategory.getCode());
        wrapper.eq(tReminderCategory.getSortOrder() != null, TReminderCategory::getSortOrder, tReminderCategory.getSortOrder());
        wrapper.eq(tReminderCategory.getCreateTime() != null, TReminderCategory::getCreateTime, tReminderCategory.getCreateTime());
        wrapper.eq(tReminderCategory.getUpdateTime() != null, TReminderCategory::getUpdateTime, tReminderCategory.getUpdateTime());
        return list(wrapper);
    }

    /**
     * 新增提醒分类表
     */
    @Override
    public boolean insert(TReminderCategory tReminderCategory) {
        return save(tReminderCategory);
    }

    /**
     * 修改提醒分类表
     */
    @Override
    public boolean update(TReminderCategory tReminderCategory) {
        return updateById(tReminderCategory);
    }

    /**
     * 批量删除提醒分类表
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
