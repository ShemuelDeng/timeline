package com.shemuel.timeline.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.service.TUserReminderService;
import com.shemuel.timeline.common.RestResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 用户提醒表 控制器
 */
@RestController
@RequestMapping("/api/t-user-reminder")
@RequiredArgsConstructor
@Tag(name = "用户提醒表管理")
public class TUserReminderController {

    private final TUserReminderService tUserReminderService;

    @GetMapping("/list")
    @Operation(summary = "获取用户提醒表列表")
    public RestResult<IPage<TUserReminder>> list(TUserReminder tUserReminder) {
        return RestResult.success(tUserReminderService.selectPage(tUserReminder));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户提醒表详情")
    public RestResult<TUserReminder> getInfo(@PathVariable("id") Long id) {
        return RestResult.success(tUserReminderService.getById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "添加用户提醒表")
    public RestResult<Object> add(@RequestBody TUserReminder tUserReminder) {
        return RestResult.success(tUserReminderService.insert(tUserReminder));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户提醒表")
    public RestResult<Object> edit(@RequestBody TUserReminder tUserReminder) {
        return RestResult.success(tUserReminderService.update(tUserReminder));
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除用户提醒表")
    public RestResult<Object> remove(@PathVariable List<Long> ids) {
        return RestResult.success(tUserReminderService.deleteByIds(ids));
    }
}
