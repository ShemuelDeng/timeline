package com.shemuel.timeline.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shemuel.timeline.mapper.TUserReminderItemMapper;
import com.shemuel.timeline.entity.TUserReminderItem;
import com.shemuel.timeline.service.TUserReminderItemService;
import com.shemuel.timeline.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 用户提醒表子表， 主要记录由主表产生的确切的提示项 服务实现类
 */
@Service
@RequiredArgsConstructor
public class TUserReminderItemServiceImpl extends ServiceImpl<TUserReminderItemMapper, TUserReminderItem> implements TUserReminderItemService {

    /**
     * 查询用户提醒表子表， 主要记录由主表产生的确切的提示项分页列表
     */
    @Override
    public IPage<TUserReminderItem> selectPage(TUserReminderItem tUserReminderItem) {
        LambdaQueryWrapper<TUserReminderItem> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserReminderItem.getId() != null, TUserReminderItem::getId, tUserReminderItem.getId());
        wrapper.eq(tUserReminderItem.getMainId() != null, TUserReminderItem::getMainId, tUserReminderItem.getMainId());
        wrapper.eq(tUserReminderItem.getUserId() != null, TUserReminderItem::getUserId, tUserReminderItem.getUserId());
        wrapper.eq(tUserReminderItem.getTemplateId() != null, TUserReminderItem::getTemplateId, tUserReminderItem.getTemplateId());
        wrapper.eq(tUserReminderItem.getTitle() != null, TUserReminderItem::getTitle, tUserReminderItem.getTitle());
        wrapper.eq(tUserReminderItem.getContent() != null, TUserReminderItem::getContent, tUserReminderItem.getContent());
        wrapper.eq(tUserReminderItem.getRemindTime() != null, TUserReminderItem::getRemindTime, tUserReminderItem.getRemindTime());
        wrapper.eq(tUserReminderItem.getRepeatRule() != null, TUserReminderItem::getRepeatRule, tUserReminderItem.getRepeatRule());
        wrapper.eq(tUserReminderItem.getCreateTime() != null, TUserReminderItem::getCreateTime, tUserReminderItem.getCreateTime());
        wrapper.eq(tUserReminderItem.getUpdateTime() != null, TUserReminderItem::getUpdateTime, tUserReminderItem.getUpdateTime());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询用户提醒表子表， 主要记录由主表产生的确切的提示项列表
     */
    @Override
    public List<TUserReminderItem> selectList(TUserReminderItem tUserReminderItem) {
        LambdaQueryWrapper<TUserReminderItem> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserReminderItem.getId() != null, TUserReminderItem::getId, tUserReminderItem.getId());
        wrapper.eq(tUserReminderItem.getMainId() != null, TUserReminderItem::getMainId, tUserReminderItem.getMainId());
        wrapper.eq(tUserReminderItem.getUserId() != null, TUserReminderItem::getUserId, tUserReminderItem.getUserId());
        wrapper.eq(tUserReminderItem.getTemplateId() != null, TUserReminderItem::getTemplateId, tUserReminderItem.getTemplateId());
        wrapper.eq(tUserReminderItem.getTitle() != null, TUserReminderItem::getTitle, tUserReminderItem.getTitle());
        wrapper.eq(tUserReminderItem.getContent() != null, TUserReminderItem::getContent, tUserReminderItem.getContent());
        wrapper.eq(tUserReminderItem.getRemindTime() != null, TUserReminderItem::getRemindTime, tUserReminderItem.getRemindTime());
        wrapper.eq(tUserReminderItem.getRepeatRule() != null, TUserReminderItem::getRepeatRule, tUserReminderItem.getRepeatRule());
        wrapper.eq(tUserReminderItem.getCreateTime() != null, TUserReminderItem::getCreateTime, tUserReminderItem.getCreateTime());
        wrapper.eq(tUserReminderItem.getUpdateTime() != null, TUserReminderItem::getUpdateTime, tUserReminderItem.getUpdateTime());
        return list(wrapper);
    }

    /**
     * 新增用户提醒表子表， 主要记录由主表产生的确切的提示项
     */
    @Override
    public boolean insert(TUserReminderItem tUserReminderItem) {
        return save(tUserReminderItem);
    }

    /**
     * 修改用户提醒表子表， 主要记录由主表产生的确切的提示项
     */
    @Override
    public boolean update(TUserReminderItem tUserReminderItem) {
        return updateById(tUserReminderItem);
    }

    /**
     * 批量删除用户提醒表子表， 主要记录由主表产生的确切的提示项
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public boolean deleteByMainId(Long mainId) {
        LambdaQueryWrapper<TUserReminderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TUserReminderItem::getMainId, mainId);
        return remove(wrapper);
    }
}
