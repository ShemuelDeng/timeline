package com.shemuel.timeline.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shemuel.timeline.mapper.TUserNotifySettingMapper;
import com.shemuel.timeline.entity.TUserNotifySetting;
import com.shemuel.timeline.service.TUserNotifySettingService;
import com.shemuel.timeline.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 用户通知渠道配置表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class TUserNotifySettingServiceImpl extends ServiceImpl<TUserNotifySettingMapper, TUserNotifySetting> implements TUserNotifySettingService {

    /**
     * 查询用户通知渠道配置表分页列表
     */
    @Override
    public IPage<TUserNotifySetting> selectPage(TUserNotifySetting tUserNotifySetting) {
        LambdaQueryWrapper<TUserNotifySetting> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserNotifySetting.getId() != null, TUserNotifySetting::getId, tUserNotifySetting.getId());
        wrapper.eq(tUserNotifySetting.getUserId() != null, TUserNotifySetting::getUserId, tUserNotifySetting.getUserId());
        wrapper.eq(tUserNotifySetting.getWecomBotUrl() != null, TUserNotifySetting::getWecomBotUrl, tUserNotifySetting.getWecomBotUrl());
        wrapper.eq(tUserNotifySetting.getDingdingBotUrl() != null, TUserNotifySetting::getDingdingBotUrl, tUserNotifySetting.getDingdingBotUrl());
        wrapper.eq(tUserNotifySetting.getBarkUrl() != null, TUserNotifySetting::getBarkUrl, tUserNotifySetting.getBarkUrl());
        wrapper.eq(tUserNotifySetting.getBarkEnabledDefault() != null, TUserNotifySetting::getBarkEnabledDefault, tUserNotifySetting.getBarkEnabledDefault());
        wrapper.eq(tUserNotifySetting.getWecomEnabledDefault() != null, TUserNotifySetting::getWecomEnabledDefault, tUserNotifySetting.getWecomEnabledDefault());
        wrapper.eq(tUserNotifySetting.getDingdingEnabledDefault() != null, TUserNotifySetting::getDingdingEnabledDefault, tUserNotifySetting.getDingdingEnabledDefault());
        wrapper.eq(tUserNotifySetting.getWebhookEnabledDefault() != null, TUserNotifySetting::getWebhookEnabledDefault, tUserNotifySetting.getWebhookEnabledDefault());
        wrapper.eq(tUserNotifySetting.getDingdingSecret() != null, TUserNotifySetting::getDingdingSecret, tUserNotifySetting.getDingdingSecret());
        wrapper.eq(tUserNotifySetting.getCreateTime() != null, TUserNotifySetting::getCreateTime, tUserNotifySetting.getCreateTime());
        wrapper.eq(tUserNotifySetting.getUpdateTime() != null, TUserNotifySetting::getUpdateTime, tUserNotifySetting.getUpdateTime());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询用户通知渠道配置表列表
     */
    @Override
    public List<TUserNotifySetting> selectList(TUserNotifySetting tUserNotifySetting) {
        LambdaQueryWrapper<TUserNotifySetting> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(tUserNotifySetting.getId() != null, TUserNotifySetting::getId, tUserNotifySetting.getId());
        wrapper.eq(tUserNotifySetting.getUserId() != null, TUserNotifySetting::getUserId, tUserNotifySetting.getUserId());
        wrapper.eq(tUserNotifySetting.getWecomBotUrl() != null, TUserNotifySetting::getWecomBotUrl, tUserNotifySetting.getWecomBotUrl());
        wrapper.eq(tUserNotifySetting.getDingdingBotUrl() != null, TUserNotifySetting::getDingdingBotUrl, tUserNotifySetting.getDingdingBotUrl());
        wrapper.eq(tUserNotifySetting.getBarkUrl() != null, TUserNotifySetting::getBarkUrl, tUserNotifySetting.getBarkUrl());
        wrapper.eq(tUserNotifySetting.getBarkEnabledDefault() != null, TUserNotifySetting::getBarkEnabledDefault, tUserNotifySetting.getBarkEnabledDefault());
        wrapper.eq(tUserNotifySetting.getWecomEnabledDefault() != null, TUserNotifySetting::getWecomEnabledDefault, tUserNotifySetting.getWecomEnabledDefault());
        wrapper.eq(tUserNotifySetting.getDingdingEnabledDefault() != null, TUserNotifySetting::getDingdingEnabledDefault, tUserNotifySetting.getDingdingEnabledDefault());
        wrapper.eq(tUserNotifySetting.getWebhookEnabledDefault() != null, TUserNotifySetting::getWebhookEnabledDefault, tUserNotifySetting.getWebhookEnabledDefault());
        wrapper.eq(tUserNotifySetting.getDingdingSecret() != null, TUserNotifySetting::getDingdingSecret, tUserNotifySetting.getDingdingSecret());
        wrapper.eq(tUserNotifySetting.getCreateTime() != null, TUserNotifySetting::getCreateTime, tUserNotifySetting.getCreateTime());
        wrapper.eq(tUserNotifySetting.getUpdateTime() != null, TUserNotifySetting::getUpdateTime, tUserNotifySetting.getUpdateTime());
        return list(wrapper);
    }

    /**
     * 新增用户通知渠道配置表
     */
    @Override
    public boolean insert(TUserNotifySetting tUserNotifySetting) {
        return save(tUserNotifySetting);
    }

    /**
     * 修改用户通知渠道配置表
     */
    @Override
    public boolean update(TUserNotifySetting tUserNotifySetting) {
        return updateById(tUserNotifySetting);
    }

    /**
     * 批量删除用户通知渠道配置表
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }
}
