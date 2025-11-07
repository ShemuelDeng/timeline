package com.shemuel.timeline.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shemuel.timeline.mapper.TUserReminderMapper;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.service.TUserReminderService;
import com.shemuel.timeline.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 用户提醒表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class TUserReminderServiceImpl extends ServiceImpl<TUserReminderMapper, TUserReminder> implements TUserReminderService {

    /**
     * 查询用户提醒表分页列表
     */
    @Override
    public IPage<TUserReminder> selectPage(TUserReminder tUserReminder) {
        LambdaQueryWrapper<TUserReminder> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserReminder.getId() != null, TUserReminder::getId, tUserReminder.getId());
        wrapper.eq(tUserReminder.getUserId() != null, TUserReminder::getUserId, tUserReminder.getUserId());
        wrapper.eq(tUserReminder.getTemplateId() != null, TUserReminder::getTemplateId, tUserReminder.getTemplateId());
        wrapper.eq(tUserReminder.getTitle() != null, TUserReminder::getTitle, tUserReminder.getTitle());
        wrapper.eq(tUserReminder.getContent() != null, TUserReminder::getContent, tUserReminder.getContent());
        wrapper.eq(tUserReminder.getRemindTime() != null, TUserReminder::getRemindTime, tUserReminder.getRemindTime());
        wrapper.eq(tUserReminder.getRepeatRule() != null, TUserReminder::getRepeatRule, tUserReminder.getRepeatRule());
        wrapper.eq(tUserReminder.getIsActive() != null, TUserReminder::getIsActive, tUserReminder.getIsActive());
        wrapper.eq(tUserReminder.getCreateTime() != null, TUserReminder::getCreateTime, tUserReminder.getCreateTime());
        wrapper.eq(tUserReminder.getUpdateTime() != null, TUserReminder::getUpdateTime, tUserReminder.getUpdateTime());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询用户提醒表列表
     */
    @Override
    public List<TUserReminder> selectList(TUserReminder tUserReminder) {
        LambdaQueryWrapper<TUserReminder> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserReminder.getId() != null, TUserReminder::getId, tUserReminder.getId());
        wrapper.eq(tUserReminder.getUserId() != null, TUserReminder::getUserId, tUserReminder.getUserId());
        wrapper.eq(tUserReminder.getTemplateId() != null, TUserReminder::getTemplateId, tUserReminder.getTemplateId());
        wrapper.eq(tUserReminder.getTitle() != null, TUserReminder::getTitle, tUserReminder.getTitle());
        wrapper.eq(tUserReminder.getContent() != null, TUserReminder::getContent, tUserReminder.getContent());
        wrapper.eq(tUserReminder.getRemindTime() != null, TUserReminder::getRemindTime, tUserReminder.getRemindTime());
        wrapper.eq(tUserReminder.getRepeatRule() != null, TUserReminder::getRepeatRule, tUserReminder.getRepeatRule());
        wrapper.eq(tUserReminder.getIsActive() != null, TUserReminder::getIsActive, tUserReminder.getIsActive());
        wrapper.eq(tUserReminder.getCreateTime() != null, TUserReminder::getCreateTime, tUserReminder.getCreateTime());
        wrapper.eq(tUserReminder.getUpdateTime() != null, TUserReminder::getUpdateTime, tUserReminder.getUpdateTime());
        return list(wrapper);
    }

    /**
     * 新增用户提醒表
     */
    @Override
    public boolean insert(TUserReminder tUserReminder) {
        return save(tUserReminder);
    }

    /**
     * 修改用户提醒表
     */
    @Override
    public boolean update(TUserReminder tUserReminder) {
        return updateById(tUserReminder);
    }

    /**
     * 批量删除用户提醒表
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
