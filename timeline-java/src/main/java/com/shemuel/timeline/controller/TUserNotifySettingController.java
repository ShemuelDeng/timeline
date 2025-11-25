package com.shemuel.timeline.controller;

import java.util.List;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.TUserNotifySetting;
import com.shemuel.timeline.service.TUserNotifySettingService;
import com.shemuel.timeline.common.RestResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 用户通知渠道配置表 控制器
 */
@RestController
@RequestMapping("/api/t-user-notify-setting")
@RequiredArgsConstructor
@Tag(name = "用户通知渠道配置表管理")
public class TUserNotifySettingController {

    private final TUserNotifySettingService tUserNotifySettingService;


    @GetMapping("/mySetting")
    @Operation(summary = "获取用户通知渠道配置表详情")
    public RestResult<TUserNotifySetting> getInfo() {
        return RestResult.success(tUserNotifySettingService
                .getOne(new LambdaQueryWrapper<TUserNotifySetting>()
                        .eq(TUserNotifySetting::getUserId, StpUtil.getLoginIdAsLong())));
    }

    @PostMapping("/add")
    @Operation(summary = "添加用户通知渠道配置表")
    public RestResult<Object> add(@RequestBody TUserNotifySetting tUserNotifySetting) {
        tUserNotifySetting.setUserId(StpUtil.getLoginIdAsLong());
        return RestResult.success(tUserNotifySettingService.insert(tUserNotifySetting));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户通知渠道配置表")
    public RestResult<Object> edit(@RequestBody TUserNotifySetting tUserNotifySetting) {
        return RestResult.success(tUserNotifySettingService.update(tUserNotifySetting));
    }

}
