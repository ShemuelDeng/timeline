package com.shemuel.timeline.controller;

import java.util.List;

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

    @GetMapping("/list")
    @Operation(summary = "获取用户通知渠道配置表列表")
    public RestResult<IPage<TUserNotifySetting>> list(TUserNotifySetting tUserNotifySetting) {
        return RestResult.success(tUserNotifySettingService.selectPage(tUserNotifySetting));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户通知渠道配置表详情")
    public RestResult<TUserNotifySetting> getInfo(@PathVariable("id") Long id) {
        return RestResult.success(tUserNotifySettingService.getById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "添加用户通知渠道配置表")
    public RestResult<Object> add(@RequestBody TUserNotifySetting tUserNotifySetting) {
        return RestResult.success(tUserNotifySettingService.insert(tUserNotifySetting));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户通知渠道配置表")
    public RestResult<Object> edit(@RequestBody TUserNotifySetting tUserNotifySetting) {
        return RestResult.success(tUserNotifySettingService.update(tUserNotifySetting));
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除用户通知渠道配置表")
    public RestResult<Object> remove(@PathVariable List<Long> ids) {
        return RestResult.success(tUserNotifySettingService.deleteByIds(ids));
    }
}
